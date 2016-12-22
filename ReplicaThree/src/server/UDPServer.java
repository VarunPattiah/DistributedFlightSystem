package server;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;


public class UDPServer extends Thread {
	MainServer dfrsServer;

	public UDPServer(MainServer dfrsServer) {
		this.dfrsServer = dfrsServer;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Thread#run()
	 */
	@Override
	public void run() {
		System.out.println(this.dfrsServer.getServerName());
		DatagramSocket datagramSocket = null;
		try {
			datagramSocket = new DatagramSocket(dfrsServer.getUdpPort());
			while (true) {
				byte[] message = new byte[5000];
				DatagramPacket requestPacket = new DatagramPacket(message, message.length);
				datagramSocket.receive(requestPacket);
				ByteArrayInputStream byteStream = new ByteArrayInputStream(message);
				ObjectInputStream is = new ObjectInputStream(new BufferedInputStream(byteStream));
				PassengerWrapperVO passengerWrapperVO = (PassengerWrapperVO) is.readObject();
				is.close();
				ByteArrayOutputStream baos = new ByteArrayOutputStream(5000);
				ObjectOutputStream objectOS = new ObjectOutputStream(new BufferedOutputStream(baos));
				if (passengerWrapperVO.getRequestType() == configuration.REQUEST_TYPE.FLIGHT_COUNT) {
					int bookedFlightCount = dfrsServer.getBookedFlightRecordCount(passengerWrapperVO.getRecordType());
					passengerWrapperVO.setRecordType(String.valueOf(bookedFlightCount));
					objectOS.flush();
					objectOS.writeObject(passengerWrapperVO);
					objectOS.flush();
				} else if (passengerWrapperVO.getRequestType() == configuration.REQUEST_TYPE.TRANSFER_RESERVATION) {
					passengerWrapperVO = dfrsServer.receivePassengerReservation(passengerWrapperVO);
					objectOS.flush();
					objectOS.writeObject(passengerWrapperVO);
					objectOS.flush();
				} else if (passengerWrapperVO.getRequestType() == configuration.REQUEST_TYPE.ROLLBACK_RESERVATION) {
					passengerWrapperVO = dfrsServer.deleteReservation(passengerWrapperVO);
					objectOS.flush();
					objectOS.writeObject(passengerWrapperVO);
					objectOS.flush();
				}

				byte[] response = baos.toByteArray();
				DatagramPacket replyPacket = new DatagramPacket(response, response.length, requestPacket.getAddress(),
						requestPacket.getPort());
				datagramSocket.send(replyPacket);
				baos.close();
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		} finally {
			datagramSocket.close();
		}
	}
}
