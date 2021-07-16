import java.net.*;
import java.io.*;
import java.util.*;
 
public class FServer {

	public static void main(String[] args) {
 
		DatagramSocket serverSocket = null;
		FileInputStream fis = null;
		DatagramPacket receivedPacket, sendPacket;
		byte[] receivedData, sendData;
		String reply;
		InetAddress ip;
		int port;
		
		try {
			serverSocket = new DatagramSocket(Integer.parseInt(args[0]));
			System.out.println("Server is up....");
			receivedData = new byte[100];
			sendData = new byte[512];


			receivedPacket = new DatagramPacket(receivedData,receivedData.length);
			serverSocket.receive(receivedPacket);

			port = receivedPacket.getPort();
			ip = receivedPacket.getAddress();
			reply = receivedPacket.getData().toString();
			System.out.println(reply);
			//String filename = reply.substring(7, reply.length()).trim();
			String filename = "demoText.html";
			// read file into buffer
			fis = new FileInputStream(filename);

			int consignment;
			String strConsignment;
			int result = 0; // number of bytes read
			int count = 0;
	 
			while(true && result!=-1){
	 
				receivedData = new byte[100];
				sendData = new byte[512];

				// get client's consignment request from DatagramPacket
				ip = receivedPacket.getAddress();
				port =receivedPacket.getPort();
				System.out.println("Client IP Address = " + ip);
				System.out.println("Client port = " + port);

				result = fis.read(sendData);
				if (result==-1){
				    sendData = new String("RTD " + count + " END\r\n").getBytes();
                }else{
				    sendData = new String("RTD" + count + new String(sendData) + " \r\n").getBytes();
                }

				sendPacket = new DatagramPacket(sendData,sendData.length,ip,port);
				serverSocket.send(sendPacket);

				receivedPacket = new DatagramPacket(receivedData,receivedData.length);
				serverSocket.receive(receivedPacket);

				String ack = receivedPacket.getData().toString();
				System.out.println(ack);
				count++;
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

