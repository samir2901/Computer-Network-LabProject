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

			byte[] receivedData, sendData;

			int count=0;
			boolean end = false;
			receivedData = new byte[512];
			sendData = new byte[512];

			String filename = "demoText.html";
			// write received data into demoText-received.html
			fos = new FileOutputStream(filename.split("\\.")[0] + "-received." + filename.split("\\.")[1]);

			String requestMessage = "REQUEST " + filename + " \r\n";
			//System.out.println(requestMessage);

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
				receivedData = new byte[512];
				sendData = new byte[512];

				//getting data from server
				receivedPacket = new DatagramPacket(receivedData,receivedData.length);
				clientSocket.receive(receivedPacket);
				reply = new String(receivedPacket.getData());
				System.out.println(reply);

				String[] split = reply.split(" ");

				//sending acknowledgment
				String ackMsg = "ACK " + (split[1]).trim() + " \r\n";
				sendData = ackMsg.getBytes();
				sendPacket = new DatagramPacket(sendData, sendData.length, InetAddress.getByName(args[0]), Integer.parseInt(args[1]));
				clientSocket.send(sendPacket);

				if (reply.contains("END")){
					end = true;
					break;
				}

				String data = reply.substring(5+split[1].length(),reply.length()-2);
				System.out.println(data);
				fos.write(data.getBytes());

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
