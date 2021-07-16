import java.net.*;
import java.io.*;
import java.util.*;
 
public class FClient {
 
	public static void main(String[] args) {
	 
	    DatagramSocket clientSocket = null;
		FileOutputStream fos = null;
		DatagramPacket receivedPacket, sendPacket;
		String reply;
		InetAddress ip;
		int port;

		try {
	    	clientSocket = new DatagramSocket();
	    	System.out.println("Server is up........");

			byte[] receivedData, sendData;

			int count=0;
			boolean end = false;

			String filename = "demoText.html";
			// write received data into demoText-received.html
			fos = new FileOutputStream(filename.split("\\.")[0] + "-received." + filename.split("\\.")[1]);

			String requestMessage = "REQUEST" + filename + " \r\n";
			System.out.println(requestMessage);

			sendData = requestMessage.getBytes();

			sendPacket = new DatagramPacket(
					sendData,
					sendData.length,
					InetAddress.getByName(args[0]),
					Integer.parseInt(args[1])
			);

			clientSocket.send(sendPacket);

			while(!end)
			{
			    receivedData = new byte[1024];
			    receivedPacket = new DatagramPacket(receivedData,receivedData.length);
			    clientSocket.receive(receivedPacket);

			    reply = receivedPacket.getData().toString();

			    String[] split = reply.split(" ");

			    if (reply.trim().endsWith("END")){
			    	end = true;
			    	break;
				}

			    //sending acknowlegdement
				String ackMessage = "ACK" + (split[1]).trim() + "\r\n";

				sendData = ackMessage.getBytes();
				sendPacket = new DatagramPacket(
						sendData,
						sendData.length,
						InetAddress.getByName(args[0]),
						Integer.parseInt(args[1])
				);

				clientSocket.send(sendPacket);

				System.out.println(reply.substring(5+split[1].length(), reply.length()-2));
				fos.write(reply.substring(5+split[1].length(), reply.length()-2).getBytes());

				count++;
			}

		} catch (IOException ex) {
			System.out.println(ex.getMessage());

		} finally {

			try {
				if (fos != null)
					fos.close();
				if (clientSocket != null)
					clientSocket.close();
			} catch (IOException ex) {
				System.out.println(ex.getMessage());
			}
		}
	}
}
