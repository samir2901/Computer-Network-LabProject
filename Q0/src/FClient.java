import java.net.*;
import java.io.*;
import java.util.*;
 
public class FClient {
 
	public static void main(String[] args) {
	 
	    DatagramSocket cs = null;
		FileOutputStream fos = null;

		try {

	    	cs = new DatagramSocket();
	 
			byte[] rd, sd;
			String reply;
			DatagramPacket sp,rp;
			int count=0;
			boolean end = false;
			
			// write received data into demoText1.html
			fos = new FileOutputStream("demoText1.html");

			while(!end)
			{
			    String ack = "" + count;
			    	  
				// send ACK      
			    sd=ack.getBytes();	
			    sp=new DatagramPacket(sd,sd.length, 
									  InetAddress.getByName(args[0]),
  									  Integer.parseInt(args[1]));	  
				cs.send(sp);	

				// get next consignment
				rd=new byte[512];
				rp=new DatagramPacket(rd,rd.length); 
			    cs.receive(rp);	

				// concat consignment 
			    reply=new String(rp.getData());	 
			    System.out.println(reply);
				fos.write(rp.getData());

				if (reply.trim().equals("END")) // if last consignment
					end = true;

				count++;
			}

		} catch (IOException ex) {
			System.out.println(ex.getMessage());

		} finally {

			try {
				if (fos != null)
					fos.close();
				if (cs != null)
					cs.close();
			} catch (IOException ex) {
				System.out.println(ex.getMessage());
			}
		}
	}
}
