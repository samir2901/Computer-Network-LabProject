/*
*@author Mistuk Roy
*/
import java.net.*;
import java.io.*;
import java.util.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.table.*;

public class Receiver extends JPanel implements Runnable{
    public static final int BYTES_PER_PACKET = 3;
    public static final int PORT = 7609;
    public static final String LOST = "LOST";
    public static final String RCVD = "RCVD";
    public static final String SENT = "SENT";
    public static final String RSNT = "RSNT";
    

    Thread t;
    JTextField prob, N;
    JTextArea text;
    JTable table, ackTable;
    Vector packets, ackPackets;

    DatagramSocket socket;
    volatile boolean done;

    public Receiver() throws IOException{
        super(new BorderLayout());

        packets = new Vector();
        ackPackets = new Vector();

        //set up UI
        JPanel p1 = new JPanel(new FlowLayout());
        p1.add(new JLabel("Packet Loss (0..1)"));
        prob = new JTextField(10);
        p1.add(prob);
        prob.setText("0.5");
        p1.add(new JLabel(" MAX SEQ NUM"));
        N = new JTextField(10);
        p1.add(N);
        N.setText("20");
        add("North", p1);

        //column header for the table
        Vector labels = new Vector();
        labels.add("Status");
        labels.add("Host");
        labels.add("Port");
        labels.add("Seq. Num");
        labels.add("Data");

        //user interface items
        JTabbedPane tabs = new JTabbedPane();

        table = new JTable(packets, labels);
        tabs.add("Received", new JScrollPane(table));

        ackTable = new JTable(ackPackets, labels);
        tabs.add("Sent", new JScrollPane(ackTable));

        add("Center", tabs);

        text = new JTextArea(10, 80);

        add("South", new JScrollPane(text));

        //set up network stuff
        socket = new DatagramSocket(PORT);

        //start thread
        done = false;
    }

    public void start(){
        if(t == null){
            t = new Thread(this);
            t.start();        
        }        
    }

    public void stop(){
        done = true;
    }

    public void run(){
        byte expectedSequenceNumber = 1;
        //ack for packet 0 (not initially sent)
        DatagramPacket ack = new DatagramPacket(new byte[]{0}, 1);
        while(!done){
            //get the next packet
            byte[] data = new byte[BYTES_PER_PACKET];
            DatagramPacket rp = new DatagramPacket(data,
                                                   BYTES_PER_PACKET);
            try{
                //receive the packet
                //System.err.println("Waiting.");
                socket.receive(rp);
                //System.err.println("Got One");

                //give others a chance to run
                Thread.yield();
            }
            catch(IOException ioe){
                System.err.println("IOException occurred while receiving!");
            }
            //determine if the packet gets lost (somewhere)
            double d = rng.nextDouble();
            double p = Double.parseDouble(prob.getText());
            boolean success = (d > p);
            String msg = null;
            if(success){ //lose it
                msg = RCVD;
            }
            else{ //don't lose it
                msg = LOST;
            }
            //put the packet in a pocket.
            PacketPocket pack = new PacketPocket(rp, msg);
            packets.add(pack);

            if(success){
                String ackMsg = RSNT;

                //if we got the packet we expected
                if(pack.id == expectedSequenceNumber){ 
                    //set the appropriate acknowledgment id
                    ack.setData(new byte[] {pack.id});
                    //increase the expectedSeqNum mod N
                    //get the value of N
                    byte n = Byte.parseByte(N.getText());                    
                    expectedSequenceNumber = 
                        (byte)((expectedSequenceNumber + 1) % n);
                    //update the text area (send data to application)
                    text.append("" + pack.c);
                    ackMsg = SENT;
                }

                //send ACK within a probability
                double dack = rng.nextDouble();
                if(dack >= p){

                    //SEND acknowledgement
                    //send ASK back to host that sent the packet.
                    ack.setAddress(rp.getAddress());
                    ack.setPort(rp.getPort());

                    try{
                        socket.send(ack);
                        //put the packet in a pocket.
                        PacketPocket ackPack = new PacketPocket(ack, ackMsg);
                        ackPackets.add(ackPack);
                    }
                    catch(IOException ioe){
                        System.err.println("IOException occurred " + 
                                           "while sending!");
                    }
                }
                else{
                    //LOST ACK                    
                    ackPackets.add(new PacketPocket(ack, LOST));
                }
            }

            
            //Tell the table it's model has changed
            TableModel model = table.getModel();
            if(model instanceof AbstractTableModel){
                ((AbstractTableModel)model).fireTableDataChanged();
            }

            //Tell the ackTable it's model has changed
            model = ackTable.getModel();
            if(model instanceof AbstractTableModel){
                ((AbstractTableModel)model).fireTableDataChanged();
            }
        }
    }

    static Random rng = new Random();
    public static void main(String[] args) throws IOException{
        String filename = null;

        //set up random number generator w/ non random seed
        if(args.length >= 1){
            int n = Integer.parseInt(args[0]);
            rng = new Random(n);
        }

        JFrame f = new JFrame("Packet receiver!");
        Receiver r = new Receiver();

        f.getContentPane().add("Center", r);
        f.addWindowListener(new WindowAdapter(){
                public void windowClosing(WindowEvent e){
                    System.exit(0);
                }
            });
        
        f.setSize(500,500);
        f.show();
        r.start();
    }
}

//represents a row in the table
class PacketPocket extends Vector{
    
    DatagramPacket packet;
    byte id;
    char c;

    public PacketPocket(DatagramPacket p, String status){
        this.packet = p;
        add(status);
        add(packet.getAddress());
        add(new Integer(packet.getPort()));
        byte[] data = packet.getData();
        id = data[0];
        add(new Integer(id));
        if(data.length >= 3){
            int hi = data[1];
            int lo = data[2];
            c = (char)((hi << 8) | lo);
            add(new Character(c));
        }
        else{
            add("---");
        }
    }
}