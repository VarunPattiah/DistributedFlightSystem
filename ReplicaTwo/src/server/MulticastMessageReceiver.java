package server;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.MulticastSocket;

import front.Message;

public class MulticastMessageReceiver extends Thread{

	private int port;
	
	public MulticastMessageReceiver(int port) {
		this.port = port;
		start();
	}
	
	
	@Override
	public void run(){
		MulticastSocket socket = null;
		DatagramPacket incomingPacket = null;
		try{
			byte[] receiveData = new byte[1024];
			socket = new MulticastSocket(port);
			InetAddress address = InetAddress.getByName("224.2.2.3");
			socket.joinGroup(address);
			System.out.println("PORT:"+port);
			while(true){
				incomingPacket = new DatagramPacket(receiveData, receiveData.length);
				socket.receive(incomingPacket);
				Message msg = null; String request = "";
				if(incomingPacket.getLength() >50){
					msg = deserializeMessge(incomingPacket);
					if("operation".equalsIgnoreCase(msg.getMessageType())){
						Message message = new ReplicaManager().getResultFromLocalReplica(msg);
						new MainServerProject().setFrontEndAddress(msg.getMessage());
						DatagramSocket socket2 = new DatagramSocket();
						byte[] serializeMessge = serializeMessge(message);
						DatagramPacket packet = new DatagramPacket(serializeMessge, serializeMessge.length,incomingPacket.getAddress(),incomingPacket.getPort());
						socket2.send(packet);
						System.out.println("response sent from Replica2"+message.getMessageID());
					}	
					else if("backup".equalsIgnoreCase(msg.getMessageType()))
					{System.out.println("Request for BackUp");
						DatagramSocket socket2 = new DatagramSocket();
						ByteArrayOutputStream bos = new ByteArrayOutputStream();
						ObjectOutput oo = new ObjectOutputStream(bos);
						System.out.println("Sending log of size : "+new ReplicaManager().getRequestHandler().size());
						oo.writeObject(new ReplicaManager().getRequestHandler());
						oo.close();
					 
						byte[] serializeMessge = bos.toByteArray();
						DatagramPacket packet = new DatagramPacket(serializeMessge, serializeMessge.length,incomingPacket.getAddress(),incomingPacket.getPort());
						socket2.send(packet);
					}
				}else{
					 request = new String(incomingPacket.getData());
					 System.out.println("************request**********"+request);
					 if(request.startsWith("FD")){
						     String reply = ""+Constants.PROCESS_ID;
						     byte[] replyBuf = reply.getBytes();
                             incomingPacket = new DatagramPacket(replyBuf, replyBuf.length,incomingPacket.getAddress(),incomingPacket.getPort());
                             socket.send(incomingPacket);
                             System.out.println("packet sent");
                     }else if(request.startsWith("LE")){
                    	 new MainServerProject().setGroupLeaderProcessID(Integer.parseInt(request.split(":")[1].trim()));
                     }
				}
			}
		}catch(Exception e){
			System.out.println("SOCKET:"+e.getMessage());
		}finally{
			if(null!= socket)
				socket.close();
		}
	}

	/**
	 * Deserialize message object from input stream
	 * @param inPacket
	 * @return
	 * @throws IOException
	 * @throws ClassNotFoundException
	 */
	private Message deserializeMessge(DatagramPacket inPacket)
			throws IOException, ClassNotFoundException {
		ObjectInputStream inStream = new ObjectInputStream(new ByteArrayInputStream(inPacket.getData()));
		Message msg = (Message)inStream.readObject();
		inStream.close();
		return msg;
	}
	
	
	/**
	 * Serialize message object to send over the network
	 * @param inPacket
	 * @return
	 * @throws IOException
	 * @throws ClassNotFoundException
	 */
	private byte[] serializeMessge(Message msg)
			throws IOException, ClassNotFoundException {
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		ObjectOutput oo = new ObjectOutputStream(bos);
		oo.writeObject(msg);
		oo.close();
		return bos.toByteArray();
	}
}
