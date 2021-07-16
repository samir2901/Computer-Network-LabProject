import java.net.*;
import java.io.*;
import java.util.*;
 
public class FServer {
 
	public static void main(String[] args) {
 
		DatagramSocket ss = null;
		FileInputStream fis = null;
		DatagramPacket rp, sp;
		byte[] rd, sd;

		InetAddress ip;
		int port;
		
		try {
			ss = new DatagramSocket(Integer.parseInt(args[0]));
			System.out.println("Server is up....");


			// read file into buffer
			fis = new FileInputStream("demoText.html");

			int consignment;
			String strConsignment;
			int result = 0; // number of bytes read
	 
			while(true && result!=-1){
	 
				rd=new byte[100];
				sd=new byte[512];
				 
				rp = new DatagramPacket(rd,rd.length);
				 
				ss.receive(rp);
				 
				// get client's consignment request from DatagramPacket
				ip = rp.getAddress(); 
				port =rp.getPort();
				System.out.println("Client IP Address = " + ip);
				System.out.println("Client port = " + port);

				strConsignment = new String(rp.getData());
				consignment = Integer.parseInt(strConsignment.trim());
				System.out.println("Client ACK = " + consignment);

				// prepare data
				result = fis.read(sd);
				if (result == -1) {
					sd = new String("END").getBytes();
					consignment = -1;
				}
				sp=new DatagramPacket(sd,sd.length,ip,port);
				 
				ss.send(sp);
				 
				rp=null;
				sp = null;
				 
				System.out.println("Sent Consignment #" + consignment);
	 
			}
			
		} catch (IOException ex) {
			System.out.println(ex.getMessage());

		} finally {
			try {
				if (fis != null)
					fis.close();
			} catch (IOException ex) {
				System.out.println(ex.getMessage());
			}
		}
		
	}
}

