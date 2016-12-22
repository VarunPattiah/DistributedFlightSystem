package front;

import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;

public class LeaderNotifyThread extends Thread{
	
	private static final int FE_NOTIFY_PORT = 5555;
	
	/**
	 * Start group leader notify thread which will wait for message from LE
	 */
	@Override
	public void run(){
		DatagramSocket socket = null;
		DatagramPacket packet = null;
		byte[] buffer = null;
		try{
			socket = new DatagramSocket(FE_NOTIFY_PORT);
			buffer = new byte[1024];
			while(true){
				packet = new DatagramPacket(buffer, buffer.length);
				socket.receive(packet);
				String message = new String(packet.getData());
				System.out.println(message);
				String[] split = message.split(":");
				if("LCHANGED".equalsIgnoreCase(split[0]))
					updateLeaderInfo(split[1].trim(), 7878);
			}
		}catch(Exception e){
			e.printStackTrace();
			System.out.println("IO:"+e.getMessage());
		}finally{
			if(null != socket)
				socket.close();
		}
	}

	/**
	 * after notified by the election system update the group leader details
	 * @param address
	 * @param port
	 * @throws IOException
	 */
	private void updateLeaderInfo(String address, int port) throws IOException {
		try {
			FileWriter file = new FileWriter("Leader.txt",false);
			file.write(address+":"+port);
			file.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}

}
