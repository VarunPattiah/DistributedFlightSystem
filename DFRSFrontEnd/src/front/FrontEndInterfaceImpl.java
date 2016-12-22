package front;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileReader;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;

import FrontEnd.FrontEndInterfacePOA;

public class FrontEndInterfaceImpl extends FrontEndInterfacePOA {

	private static Long sequence = 1L;
	private Object lock = new Object();
	
	/**
	 * read the group leader info from file
	 * @return
	 */
	private String getLeaderInfo() {
		String info = "";
		String property = System.getProperty("user.dir");
		BufferedReader br;
		try {
			br = new BufferedReader(new FileReader(property + File.separator
					+ "Leader.txt"));
			info = br.readLine();
			br.close();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return info;
	}

	/**
	 * construct Message object from method parameters 
	 * @param method
	 * @param managerId
	 * @param firstName
	 * @param lastName
	 * @param description
	 * @param status
	 * @param address
	 * @param phone
	 * @param lastLocation
	 * @param recordId
	 * @param remoteServerName
	 * @return
	 */
	private synchronized Message constructRequest(String method, String managerId, String firstName, String lastName,
			String phoneNumber, String destination, String address, String dateofBooking, String classType,
			String recordType, String recordId, String fieldName, String passengerID, String currentCity,
			String otherCity, String newValue, String remoteServerName) {System.out.println("Inside constructRequest"+managerId);
		Message msg = new Message();
		msg.setMessageID(sequence);
		msg.setMethod(method);
		msg.setFirstName(firstName);
		msg.setLastName(lastName);
		msg.setManagerId(managerId);
		//msg.setStatus(status);
		msg.setPhone(phoneNumber);
		msg.setDateofBooking(dateofBooking);
		msg.setClassType(classType);
		msg.setRecordType(recordType);
		msg.setFieldName(fieldName);
		msg.setPassengerID(passengerID);
		msg.setCurrentCity(currentCity);
		msg.setOtherCity(otherCity);
		msg.setNewValue(newValue);
		
		if (null != address || !"".equalsIgnoreCase(address))
			msg.setAddress(address);
		if (null != destination || !"".equalsIgnoreCase(destination))
			msg.setDestination(destination);
		if (null != recordId || !"".equalsIgnoreCase(recordId))
			msg.setRecordId(recordId);
		if (null != remoteServerName || !"".equalsIgnoreCase(remoteServerName))
			msg.setRemoteServerName(remoteServerName);
		msg.setMessageType("operation");
		try {
			msg.setMessage(InetAddress.getLocalHost().getHostAddress());
		} catch (UnknownHostException e) {
			e.printStackTrace();
		}
		sequence++;

		return msg;
	}

	/**
	 * synchronized method which receives method name and arguments from client
	 */
	@Override
	public String executeOperation(String method, String managerId, String firstName, String lastName,
			String phoneNumber, String destination, String address, String dateofBooking, String classType,
			String recordType, String recordId, String fieldName, String passengerID, String currentCity,
			String otherCity, String newValue, String remoteServerName) {
		System.out.println("inside executeOperation ");
		// TODO Auto-generated method stub
		String leaderInfo = "", result = "";
		DatagramSocket socket = null;
		try {
			synchronized (lock) {
				do {
					leaderInfo = getLeaderInfo();
				} while (null == leaderInfo && "".equalsIgnoreCase(leaderInfo));

				Message msg = constructRequest(method, managerId, firstName, lastName, phoneNumber, destination, address, dateofBooking, classType, recordType, recordId, fieldName, passengerID, currentCity, otherCity, newValue, remoteServerName);
				String[] split = leaderInfo.split(":");
				
				//serialize the message object
				ByteArrayOutputStream baos = new ByteArrayOutputStream(5000);
				ObjectOutputStream objectOS = new ObjectOutputStream(new BufferedOutputStream(baos));
				objectOS.flush();
				objectOS.writeObject(msg);
				objectOS.flush();
				socket = new DatagramSocket();
				InetAddress host = InetAddress.getByName(split[0]);
				byte[] serializedMsg = baos.toByteArray();
				DatagramPacket request = new DatagramPacket(serializedMsg,
						serializedMsg.length, host, Integer.parseInt(split[1]));
				socket.send(request);
				baos.close();
				System.out.println("Request sent to "+split[0]+":"+split[1]);
				//send reply back to client
				byte[] buffer = new byte[100];
				DatagramPacket reply = new DatagramPacket(buffer, buffer.length);
				socket.receive(reply);
				
				result = new String(reply.getData());
				System.out.println("Got Reply : "+result);
			}

		} catch (SocketException s) {
			System.out.println("Socket: " + s.getMessage());
		} catch (Exception e) {
			System.out.println("IO: " + e.getMessage());
		} finally {
			if (socket != null) {
				socket.close();
			}
		}
		return result;
	}

}
