package server;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.ObjectInputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.ArrayList;


/**
 * 
 * @author VarunPattiah
 *
 */
public class UDPClient extends Thread {

	private String serverAddress;
	private int udpPort;
	private byte[] data;
	PassengerWrapperVO resultVO;
	ArrayList<PassengerWrapperVO> serverResponse = new ArrayList<PassengerWrapperVO>();
	Constants.REQUEST_TYPE requestType;

	public String getServerAddress() {
		return serverAddress;
	}

	public void setServerAddress(String serverAddress) {
		this.serverAddress = serverAddress;
	}

	public int getUdpPort() {
		return udpPort;
	}

	public void setUdpPort(int udpPort) {
		this.udpPort = udpPort;
	}

	public byte[] getData() {
		return data;
	}

	public void setSendData(byte[] data) {
		this.data = data;
	}

	public UDPClient(String serverAddress, int udpPort, byte[] data, Constants.REQUEST_TYPE requestType,ArrayList<PassengerWrapperVO> serverResponse) {
		this.setServerAddress(serverAddress);
		this.setUdpPort(udpPort);
		this.setSendData(data);
		this.requestType = requestType;
		this.serverResponse = serverResponse;
	}

	/* (non-Javadoc)
	 * @see java.lang.Thread#run()
	 */
	@Override
	public void run() {
		DatagramSocket datagramSocket = null;
		try {
			datagramSocket = new DatagramSocket();
			InetAddress hostAddress = InetAddress.getByName(this.getServerAddress());
			int serverPort = this.getUdpPort();
			DatagramPacket request = new DatagramPacket(this.getData(), this.getData().length, hostAddress,
					serverPort);
			datagramSocket.send(request);
			byte[] message = new byte[5000];
			DatagramPacket reply = new DatagramPacket(message, message.length);
			datagramSocket.receive(reply);

			ByteArrayInputStream bais = new ByteArrayInputStream(message);
			ObjectInputStream is = new ObjectInputStream(new BufferedInputStream(bais));
	
			resultVO = (PassengerWrapperVO) is.readObject();
			serverResponse.add(resultVO);
			is.close();
			bais.close();
		} catch (Exception ex) {
			ex.printStackTrace();
		} finally {
			datagramSocket.close();
		}
	}
}
