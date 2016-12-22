package server;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;

import org.omg.CORBA.ORB;

import front.Message;
import idl.interfaces.DFRSInterface;
import idl.interfaces.DFRSInterfaceHelper;
import idl.interfaces.FlightVO;
import idl.interfaces.PassengerVO;


public class ReplicaManager extends Thread {

	private int port;
	private static HashMap<String, Message> requestHandler = new HashMap<String,Message>();
	public ReplicaManager() {
	}

	public ReplicaManager(int port) {
		this.port = port;
		start();
	}

	/**
	 * called by each thread and wait for requests to come in
	 */
	@Override
	public void run() {
		DatagramSocket socket = null;
		try {
			socket = new DatagramSocket(port);
			while(true){
				byte[] message = new byte[5000];
				DatagramPacket request = new DatagramPacket(message, message.length);
				socket.receive(request);

				ByteArrayInputStream bais = new ByteArrayInputStream(message);
				ObjectInputStream is = new ObjectInputStream(new BufferedInputStream(bais));
		
				MulticastMessageSender sender = new MulticastMessageSender();
				Message msg = (Message) is.readObject();
				System.out.println("#######################requestHandler.size() : "+requestHandler.size());
				/*if(requestHandler.size()!=0 && ((requestHandler.size() + 1) < msg.getMessageID()) )
				{System.out.println("**********************requestHandler.size()!=0 && requestHandler.size()!=msg.getMessageID()"+msg.getMessageID());
					msg.setMessageType("backup");
					msg.setMessageID((long) requestHandler.size());
					List<HashMap<String, Message> > results = sender.multicastBackUpRecord(msg);
					System.out.println("results size : "+results.size());
					HashMap<String, Message> temp = new HashMap<String, Message>();
					if(results.get(0).size() > results.get(1).size())
						temp = results.get(0);
					else
						temp = results.get(1);
					System.out.println(temp);
					for (int i = 0; i < temp.size(); i++) {
						getResultFromLocalReplica(temp.get(i));
						
					}
				}*/
				if(msg!=null && msg.getMessageID()!=null)
				requestHandler.put(String.valueOf(msg.getMessageID()), msg);
				new ReplicaManager().setRequestHandler(requestHandler);
				is.close();
				bais.close();
				System.out.println("RM Line noi 52");
				//send multicast message to all other active replicas
				//MulticastMessageSender sender = new MulticastMessageSender();
				List<Message> results = sender.multicastMessage(msg);
				
				//get the result from local replica
				//results.add(getResultFromLocalReplica(msg));
				//String resp =  getResultFromLocalReplica(msg).getMessage();
				//byte[] outputBytes =resp.getBytes();
				//compare the results from all replicas and choose the result with majority
				byte[] outputBytes = compareResults(results).getBytes();
				System.out.println("Sending respomndser to client from replica manager : "+request.getAddress()+"   port: "+request.getPort());
				//send the response to client
				DatagramPacket response = new DatagramPacket(outputBytes,
						outputBytes.length, request.getAddress(), request.getPort());
				//System.out.println("rm rESPNSE : "+resp);
				socket.send(response);
				updaterRequests();
			}
		} catch (SocketException e) {
			System.out.println("SOCKET:" + e.getMessage());
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("IO:" + e.getMessage());
		} finally {
			if (socket != null)
				socket.close();
		}
	}

	/**
	 * 
	 * @param msg
	 * @return
	 */
	public Message getResultFromLocalReplica(Message msg) {
		DFRSInterface object = null;
		Message message = new Message();
		String response = "";
		if (null != msg) {
			String method = msg.getMethod();
			String mgrId = msg.getManagerId();
			String serverName = mgrId.replaceAll("[0-9]", "");
			if(serverName.equalsIgnoreCase("MTL"))
				serverName = configuration.RMI_SERVER_1_NAME;
			if(serverName.equalsIgnoreCase("WST"))
				serverName = configuration.RMI_SERVER_2_NAME;
			if(serverName.equalsIgnoreCase("NDL"))
				serverName = configuration.RMI_SERVER_3_NAME;
			PassengerVO pVO = null;
			FlightVO fVO = null;
			System.out.println("serverName : "+serverName);
			if (null != (object = getRemoteObjectStub(serverName))) {
				if ("BF".equalsIgnoreCase(method)){
					pVO = object.bookFlight(msg.getFirstName(), msg.getLastName(), msg.getAddress(), msg.getPhone(), msg.getDestination(), msg.getDateofBooking(), msg.getClassType());
					if(pVO!=null)
						response = pVO.message;
				}else if ("MA".equalsIgnoreCase(method))
					response = String.valueOf(object.managerAuthentication(msg.getManagerId()));
				else if ("GB".equalsIgnoreCase(method))
					response =Arrays.toString( object.getBookedFlightCount(msg.getRecordType()));
				else if ("ER".equalsIgnoreCase(method)){
					fVO = object.editFlightRecord(msg.getRecordId(), msg.getFieldName(), msg.getNewValue());
					if(fVO!=null)
						response = fVO.message;
				}else if ("TR".equalsIgnoreCase(method)){
					pVO = object.transferReservation(msg.getPassengerID(), msg.getCurrentCity(), msg.getOtherCity());
					if(pVO!=null)
						response = pVO.message;
				}
			}
		}

		message.setProcessID(configuration.PROCESS_ID);
		message.setMessage(response);
		message.setMessageID(msg.getMessageID());
		message.setMethod(msg.getMethod());
		return message;
	}

	/**
	 * get the remote object reference
	 * 
	 * @param clinicName
	 * @return
	 * @throws IOException
	 */
	private static DFRSInterface getRemoteObjectStub(String serverName) {
		String[] args = null;
		DFRSInterface serverObj = null;
		// initialize ORB
		ORB orb = ORB.init(args, null);

		String property = System.getProperty("user.dir");
		try {
			// get the IOR string from contact file based on clinic
			BufferedReader br = new BufferedReader(new FileReader(configuration.IOR_PATH+serverName+configuration.LOG_EXTENSION));
			String ior = br.readLine();
			br.close();

			// Get the CORBA object from IOR
			org.omg.CORBA.Object obj = orb.string_to_object(ior);

			// Convert CORBA object to JAVA object
			serverObj = DFRSInterfaceHelper.narrow(obj);
		} catch (Exception e) {
			System.out.println("IO:" + e.getMessage());
		}

		return serverObj;
	}

	/**
	 * method for comparing results from different replicas and return single result based on majoriry
	 * @param results
	 * @return
	 */
	public String compareResults(List<Message> results) {
		String response = "", positiveRes = "", negResponse = "";
		int successCounter = 0;
		int majority = 0;
		if (results.size() != 0) {
			majority = results.size() / 2 + 1;
			for (Message msg : results) {
				if (!msg.getMethod().equalsIgnoreCase("GR")) {
					if (msg.getMessage().split(":")[0]
					.equalsIgnoreCase("SUCCESS")) {
						positiveRes = msg.getMessage();
						successCounter++;
					}else{
						negResponse = msg.getMessage();
					}
				}else{
					response = msg.getMessage();
					break;
				}
			}
		}

		if (successCounter >= majority)
			response = positiveRes;
		else
			response = negResponse;

		return response;
	}
	private static void updaterRequests() {
		BufferedWriter out = null;
		try  
		{
		    FileWriter fstream = new FileWriter(configuration.REQUEST_HANDLER_OUTPUT + configuration.LOG_EXTENSION, false); //true tells to append data.
		    out = new BufferedWriter(fstream);
		    for (Entry<String, Message> entry : requestHandler.entrySet()) { 
		    	
					out.write("\nMessageID\t\tMethod\t\tResponse\n"+entry.getKey()+"\t\t"+entry.getValue().getMethod()+"\t\t"+entry.getValue().getStatus()+"\n");

		    	}
		    
		}
		catch (IOException e)
		{
		    System.err.println("Error: " + e.getMessage());
		}
		finally
		{
		    if(out != null) {
		        try {
					out.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
		    }
		}
		
	}
	
	public static HashMap<String, Message> getRequestHandler() {
		return requestHandler;
	}

	public static void setRequestHandler(HashMap<String, Message> requestHandler) {
		ReplicaManager.requestHandler = requestHandler;
	}

	public  static void main(String args[])
	{
		new ReplicaManager(7878);
		System.out.println("ReplicaManger Started.......");
	}
}
