import java.net.*;
import java.io.*;
import java.util.*;
 
public class SecretClient {
 
	public static void main(String[] args) {
	 
	    DatagramSocket cs = null;

		try {
			cs = new DatagramSocket();

			byte[] rd, sd;
			String GREETING = "HELLO";
			String reply;
			DatagramPacket sp,rp;
			boolean end = false;

			while(!end)
			{   	  
				// send Greeting      
			    sd=GREETING.getBytes();	 
			    sp=new DatagramPacket(sd,sd.length, 
									InetAddress.getByName(args[0]),
  									Integer.parseInt(args[1]));	 
				cs.send(sp);	
				System.out.println("sent Greeting");

				// get next consignment
				rd=new byte[512];
				rp=new DatagramPacket(rd,rd.length); 
			    cs.receive(rp);	

				// print SECRET
				reply=new String(rp.getData());	 
				System.out.println(reply);

				if (reply.trim().equals("END")) // last consignment
					end = true;

			}
		 
			cs.close();

		} catch (IOException ex) {
			System.out.println(ex.getMessage());
		}
	}
 
}
