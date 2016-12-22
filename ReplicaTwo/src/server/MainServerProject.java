package server;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Scanner;
import java.util.logging.FileHandler;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

import org.omg.CORBA.ORB;
import org.omg.CORBA.ORBPackage.InvalidName;
import org.omg.PortableServer.POA;
import org.omg.PortableServer.POAHelper;
import org.omg.PortableServer.POAManagerPackage.AdapterInactive;
import org.omg.PortableServer.POAPackage.ObjectNotActive;
import org.omg.PortableServer.POAPackage.ServantAlreadyActive;
import org.omg.PortableServer.POAPackage.WrongPolicy;

import idl.interfaces.DFRSInterface;
import idl.interfaces.DFRSInterfacePOA;
import idl.interfaces.FlightVO;
import idl.interfaces.PassengerVO;

public class MainServerProject extends DFRSInterfacePOA implements Runnable{
	
	private String serverName;
	private String serverAddress;
	private static int groupLeaderProcessID = 1;
	private static String frontEndAddress;
	private int udpPort;
	private int economy_numberOfSeats;
	private int numberOfFlights;
	private int buisness_numberOfSeats;
	private int first_numberOfSeats;
	private Logger logger;
	private UDPServer transferlistener;
	private ArrayList<PassengerWrapperVO> list = new ArrayList<PassengerWrapperVO>();
	private static ArrayList<MainServerProject> DFRSServers = new ArrayList<MainServerProject>();
	private static HashMap<String, ArrayList<FlightVO>> flightRecordMap = new HashMap<String, ArrayList<FlightVO>>();
	private HashMap<Character, ArrayList<PassengerVO>> recordMap = new HashMap<Character, ArrayList<PassengerVO>>();
	public static MainServerProject mtlServer = null;
	public static MainServerProject wstServer = null;
	public static MainServerProject ndlServer = null;
	public static int passengerRecordID;
	public String getServerName() {
		return serverName;
	}

	public void setServerName(String serverName) {
		this.serverName = serverName;
	}

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
	public Integer getEconomy_numberOfSeats() {
		return economy_numberOfSeats;
	}

	public void setEconomy_numberOfSeats(int economy_numberOfSeats) {
		this.economy_numberOfSeats = economy_numberOfSeats;
	}

	public int getBuisness_numberOfSeats() {
		return buisness_numberOfSeats;
	}

	public void setBuisness_numberOfSeats(int buisness_numberOfSeats) {
		this.buisness_numberOfSeats = buisness_numberOfSeats;
	}

	public int getFirst_numberOfSeats() {
		return first_numberOfSeats;
	}

	public void setFirst_numberOfSeats(int first_numberOfSeats) {
		this.first_numberOfSeats = first_numberOfSeats;
	}
	public void setNumberOfFlights(int numberOfFlights) {
		this.numberOfFlights = numberOfFlights;
	}

	public int getNumberOfFlights() {
		return numberOfFlights;
	}
/*
 * Descripton: The below method set the logger for the server for each thread based on City
 */
	private void setLogger(String serverName) {
		String logFile = Constants.SERVER_LOG_PATH + serverName + Constants.LOG_EXTENSION;
		logger = Logger.getLogger(logFile);
		if (logger.getHandlers().length == 0) {
			FileHandler fileHandler = null;
				try {
					fileHandler = new FileHandler(logFile, true);
					SimpleFormatter textFormatter = new SimpleFormatter();
					fileHandler.setFormatter(textFormatter);
					logger.addHandler(fileHandler);
					logger.setUseParentHandlers(false);
				} catch (SecurityException e) {
					System.out.println("Logger initialization error. Check File Permissions!!!");
					e.printStackTrace();
				} catch (IOException e) {
					System.out.println("Logger initialization error. IO problems opening the files");
					e.printStackTrace();
				}			
		}
	}
	public MainServerProject(){}
	/**
	 * @param args
	 */
	public MainServerProject(String serverName, int udpPort, String serverAddress) {
		this.setServerName(serverName);
		this.setUdpPort(udpPort);
		this.setServerAddress(serverAddress);
		setLogger(serverName);
	}
	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Thread#run()
	 */
	public void run() {
		DatagramSocket aSocket = null;
		this.transferlistener = new UDPServer(this);
		transferlistener.start();
	}
	
	 public int getBookedFlightRecordCount(String data) {
		 
         int count = 0;
     for (Entry<Character, ArrayList<PassengerVO>> entry : this.recordMap.entrySet()) {
         ArrayList<PassengerVO> tempList = entry.getValue();
         for (Iterator iterator = tempList.iterator(); iterator.hasNext();) {
             PassengerVO passengerVO = (PassengerVO) iterator.next();
             if(passengerVO.classOfFlight.equalsIgnoreCase(data))
                 count++;
         }


     }
         
         return count;

     }


	public static void main(String[] args) {
		try{
			 mtlServer = new MainServerProject(Constants.RMI_SERVER_1_NAME, Constants.UDP_PORT_1_NUMBER,
					Constants.RMI_SERVER_1_ADDRESS);
			 wstServer = new MainServerProject(Constants.RMI_SERVER_2_NAME, Constants.UDP_PORT_2_NUMBER,
					Constants.RMI_SERVER_2_ADDRESS);
			 ndlServer = new MainServerProject(Constants.RMI_SERVER_3_NAME, Constants.UDP_PORT_3_NUMBER,
					Constants.RMI_SERVER_3_ADDRESS);

			mtlServer.setNumberOfFlights(Constants.MTL_NUMBEROFFLIGHT);
			wstServer.setNumberOfFlights(Constants.WST_NUMBEROFFLIGHT);
			ndlServer.setNumberOfFlights(Constants.NDL_NUMBEROFFLIGHT);
			
			mtlServer.setEconomy_numberOfSeats(Constants.ECONOMY_NUMBEROFSEATS);
			wstServer.setEconomy_numberOfSeats(Constants.ECONOMY_NUMBEROFSEATS);
			ndlServer.setEconomy_numberOfSeats(Constants.ECONOMY_NUMBEROFSEATS);

			mtlServer.setBuisness_numberOfSeats(Constants.BUISNESS_NUMBEROFSEATS);
			wstServer.setBuisness_numberOfSeats(Constants.BUISNESS_NUMBEROFSEATS);
			ndlServer.setBuisness_numberOfSeats(Constants.BUISNESS_NUMBEROFSEATS);
			
			mtlServer.setFirst_numberOfSeats(Constants.FIRST_NUMBEROFSEATS);
			wstServer.setFirst_numberOfSeats(Constants.FIRST_NUMBEROFSEATS);
			ndlServer.setFirst_numberOfSeats(Constants.FIRST_NUMBEROFSEATS);
			DFRSServers.add(mtlServer);
			DFRSServers.add(wstServer);
			DFRSServers.add(ndlServer);
			initiateFlightRecords();
			ORB orb = ORB.init(args, null);
			POA rootPOA = null;
			Iterator<MainServerProject> DFRSIterator = DFRSServers.iterator();
			while (DFRSIterator.hasNext()) {
				try {
					MainServerProject dfrsServer = DFRSIterator.next();
					new Thread(dfrsServer).start();	
					System.out.println(dfrsServer.getServerName() + " server started");
					rootPOA = POAHelper.narrow(orb.resolve_initial_references("RootPOA"));
					byte[] id = rootPOA.activate_object(dfrsServer);
					org.omg.CORBA.Object ref = rootPOA.id_to_reference(id);
					
					String ior = orb.object_to_string(ref);
					System.out.println(ior);
					
					PrintWriter file = new PrintWriter(Constants.IOR_PATH+dfrsServer.getServerName()+Constants.LOG_EXTENSION);
					file.println(ior);
					file.close();
				} catch (InvalidName e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (ServantAlreadyActive e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (WrongPolicy e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (ObjectNotActive e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (FileNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} 
			}
			try{

				new ReplicaManager(7878);
				new MulticastMessageReceiver(Constants.MC_OPERATION_PORT);
				
				new FailureDetection().start();
				rootPOA.the_POAManager().activate();
				orb.run();
				}catch (AdapterInactive e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			System.out.println("Server is Up and Running!");
		}catch(Exception e){
			e.printStackTrace();
		}
	}


	@Override
	public PassengerVO bookFlight(String firstName, String lastName, String address, String phoneNumber,
			String destination, String date, String classType) {
		int seat=0;boolean flightavailable = false;
		String recordID = "Record-"+passengerRecordID;
		ArrayList<PassengerVO> temperory;
		PassengerVO passengerVO = new PassengerVO();
		passengerVO.recordID = (recordID);
		passengerVO.firstName = (firstName);
		passengerVO.message = "";
		passengerVO.lastName = (lastName);
		passengerVO.address = (address);
		passengerVO.phoneNumber = (phoneNumber);
		passengerVO.destination = (destination);
		passengerVO.dateOfFilight = (date).toString();
		passengerVO.classOfFlight = (classType);
		passengerVO.isFlightBooked = (true);
		if(this.recordMap.get(lastName.charAt(0))== null){
			temperory = new ArrayList<PassengerVO>();
		}
		else{
			temperory = this.recordMap.get(lastName.charAt(0));
			for (int i = 0; i < temperory.size(); i++) {
				PassengerVO passengerObj = temperory.get(i);
				if(passengerObj.firstName.equalsIgnoreCase(passengerVO.firstName) && passengerObj.firstName.equalsIgnoreCase(passengerVO.lastName)){
					passengerVO.isFlightBooked = (false);
					passengerVO.message = (Constants.PASSENGER_EXISTS);
					logger.info("Failure : "+Constants.PASSENGER_EXISTS + "with FIRST NAME: " + firstName + " and LAST NAME: " + lastName);
					return passengerVO;
				}
			}
		}
		ArrayList<FlightVO> listOfFlight = flightRecordMap.get(this.serverName);
		for (Iterator iterator = listOfFlight.iterator(); iterator.hasNext();) {
			FlightVO flightVO = (FlightVO) iterator.next();
			if(flightVO.flightDate.equalsIgnoreCase(passengerVO.dateOfFilight) && flightVO.Destination.equalsIgnoreCase(passengerVO.destination))
			{	
				if(passengerVO.classOfFlight.equalsIgnoreCase(Constants.BUISNESS_CLASS)){
					seat = flightVO.businessSeats;
					if(seat!=0)
					flightVO.businessSeats = seat - 1;
				}
				else if(passengerVO.classOfFlight.equalsIgnoreCase(Constants.ECONOMY_CLASS)){
					seat = flightVO.economySeats;
					if(seat!=0)
					flightVO.economySeats = seat - 1;
				}
				else{
					seat = flightVO.firstClassSeats;
					if(seat!=0)
					flightVO.firstClassSeats = seat - 1;
				}
				seat=-1;
				if(seat<=0){
					passengerVO.message = ("FAILURE: Seats are not avilable");
					passengerVO.isFlightBooked = false;
					logger.info("Seats are not avilable");
					return passengerVO;
				}
				else{
				passengerVO.message = ("SUCCESS: "+Constants.PASSENGER_SUCCESS+ "RecordID : "+passengerVO.recordID);
				logger.info(Constants.PASSENGER_SUCCESS +"\n============================================================\nPassenger Details:\n============================================================" + "\n\tRecordId:\t" + passengerVO.recordID + "\n\tFirst Name:\t" + passengerVO.firstName+ "\n\tLast Name:\t" + passengerVO.lastName + "\n\tAddress:\t" + passengerVO.address + "\n\tPhone Number:\t"
						+ passengerVO.phoneNumber + "\n\tDestination:\t" + passengerVO.destination + "\n\tClassOfFlight:\t" + passengerVO.classOfFlight
						+ "\n\tDateOfFlight:\t" + passengerVO.dateOfFilight +"\n");
				flightavailable = true;
				}
				//System.out.println("Available"+flightVO.flightDate);
				
				break;
			}
		}
		if(!flightavailable)
		{
			passengerVO.isFlightBooked  =false;
			if(passengerVO.message.equals(""))
				passengerVO.message = "Failure : Flight is Not available";
			return passengerVO;
		}
		updateFLightRecords();
		synchronized(this){
			temperory.add(passengerVO);
		}
		passengerRecordID++;
		this.recordMap.put(lastName.charAt(0), temperory);
		if(passengerVO.classOfFlight.equalsIgnoreCase(Constants.BUISNESS_CLASS)){
			this.setBuisness_numberOfSeats(this.getBuisness_numberOfSeats() - 1);
		}
		else if(passengerVO.classOfFlight.equalsIgnoreCase(Constants.ECONOMY_CLASS)){
			this.setEconomy_numberOfSeats(this.getEconomy_numberOfSeats() - 1);
		}
		else{
			this.setFirst_numberOfSeats(this.getFirst_numberOfSeats() - 1);
		}
		updatePassengerRecords(this);
		return passengerVO;
	}
	
	private static void updatePassengerRecords(MainServerProject dfrsServer) {
		BufferedWriter out = null;
		try  
		{
		    FileWriter fstream = new FileWriter(Constants.PASSENGER_RECORD_OUTPUT+ "-" + dfrsServer.serverName + Constants.LOG_EXTENSION, false); //true tells to append data.
		    out = new BufferedWriter(fstream);
		    for (Entry<Character, ArrayList<PassengerVO>> entry : dfrsServer.recordMap.entrySet()) { 
		    	ArrayList<PassengerVO> temp = entry.getValue();
		    	for (Iterator<PassengerVO> iterator = temp.iterator(); iterator.hasNext();) {
					PassengerVO passengerVO = (PassengerVO) iterator.next();
					out.write("\n============================================================\nPassenger Details:\n============================================================" + "\n\tRecordId:\t" + passengerVO.recordID + "\n\tFirst Name:\t" + passengerVO.firstName+ "\n\tLast Name:\t" + passengerVO.lastName + "\n\tAddress:\t" + passengerVO.address + "\n\tPhone Number:\t"
							+ passengerVO.phoneNumber + "\n\tDestination:\t" + passengerVO.destination + "\n\tClassOfFlight:\t" + passengerVO.classOfFlight
							+ "\n\tDateOfFlight:\t" + passengerVO.dateOfFilight +"\n");

					
				}
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
	private static void updateFLightRecords() {
		BufferedWriter out = null;
		try  
		{
		    FileWriter fstream = new FileWriter(Constants.FLIGHT_RECORD_OUTPUT + Constants.LOG_EXTENSION, false); //true tells to append data.
		    out = new BufferedWriter(fstream);
		    Iterator it = flightRecordMap.entrySet().iterator();
		    for (Entry<String, ArrayList<FlightVO>> entry : flightRecordMap.entrySet()) { 
		    	ArrayList<FlightVO> temp = entry.getValue();
		    	for (Iterator iterator = temp.iterator(); iterator.hasNext();) {
					FlightVO flightVO = (FlightVO) iterator.next();
					out.write("\n\tRecordId:\t" + flightVO.flightID + "\n\tDeparture:\t" + flightVO.Departure+ "\n\tDestination:\t" + flightVO.Destination + "\n\tflightDate:\t" + flightVO.flightDate + "\n\teconomySeats:\t"
							+ flightVO.economySeats + "\n\tbusinessSeats:\t" + flightVO.businessSeats + "\n\tfirstClassSeats:\t" + flightVO.firstClassSeats + "\n\tEditedBy:\t" + flightVO.editedBy +"\n");

					
				}
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

	private static void initiateFlightRecords() {
		int i = 1, id = 1, j = 0;
		Calendar now = Calendar.getInstance();
		Iterator<MainServerProject> dFRSIterator = DFRSServers.iterator();
		while (dFRSIterator.hasNext()) {
			MainServerProject dfrsServer = (MainServerProject) dFRSIterator.next();
			ArrayList<FlightVO> temporary = new ArrayList<FlightVO>();
			j=0;
			while(j<3){i=1;
			while(i<=Constants.BOOKING_DATE_LIMIT){
				if(i==1)
					 now = Calendar.getInstance();
				else
					now.add(Calendar.DATE, 1);
				FlightVO flightVO = new FlightVO();
				flightVO.flightID = id++;
				flightVO.Departure = (dfrsServer.serverName);
				now.set(Calendar.AM_PM, Calendar.PM);
				now.set(Calendar.MINUTE, 0);
				now.set(Calendar.SECOND, 0);
				if( j==0 )
				now.set(Calendar.HOUR_OF_DAY, 15);
				else if( j==1 )
				now.set(Calendar.HOUR_OF_DAY, 18);
				else if( j==2 )
				now.set(Calendar.HOUR_OF_DAY, 21);
				flightVO.flightDate = (now.getTime().toString());
				flightVO.economySeats = (Constants.ECONOMY_NUMBEROFSEATS);
				flightVO.businessSeats = (Constants.BUISNESS_NUMBEROFSEATS);
				flightVO.firstClassSeats = (Constants.FIRST_NUMBEROFSEATS);
				flightVO.editedBy = ("NONE");
				if(dfrsServer.serverName.equalsIgnoreCase(Constants.RMI_SERVER_1_NAME))
				{
					flightVO.Destination = (Constants.RMI_SERVER_2_NAME);
				}else if(dfrsServer.serverName.equalsIgnoreCase(Constants.RMI_SERVER_2_NAME))
				{
					flightVO.Destination = (Constants.RMI_SERVER_1_NAME);
				}else if(dfrsServer.serverName.equalsIgnoreCase(Constants.RMI_SERVER_3_NAME))
				{
					flightVO.Destination = (Constants.RMI_SERVER_2_NAME);
				}
				temporary.add(flightVO);
				i++;
			}
			i = 1;
			while(i<=Constants.BOOKING_DATE_LIMIT){
				if(i==1)
					 now = Calendar.getInstance();
				else
					now.add(Calendar.DATE, 1);
				FlightVO flightVO = new FlightVO();
				flightVO.flightID = id++;
				flightVO.Departure = (dfrsServer.serverName);
				now.set(Calendar.AM_PM, Calendar.PM);
				now.set(Calendar.MINUTE, 0);
				now.set(Calendar.SECOND, 0);
				if( j==0 )
				now.set(Calendar.HOUR_OF_DAY, 15);
				else if( j==1 )
				now.set(Calendar.HOUR_OF_DAY, 18);
				else if( j==2 )
				now.set(Calendar.HOUR_OF_DAY, 21);
				flightVO.flightDate = (now.getTime().toString());
				flightVO.economySeats = (Constants.ECONOMY_NUMBEROFSEATS);
				flightVO.businessSeats = (Constants.BUISNESS_NUMBEROFSEATS);
				flightVO.firstClassSeats = (Constants.FIRST_NUMBEROFSEATS);
				flightVO.editedBy = ("NONE");
				if(dfrsServer.serverName.equalsIgnoreCase(Constants.RMI_SERVER_1_NAME))
				{
					flightVO.Destination = (Constants.RMI_SERVER_3_NAME);
				}else if(dfrsServer.serverName.equalsIgnoreCase(Constants.RMI_SERVER_2_NAME))
				{
					flightVO.Destination = (Constants.RMI_SERVER_3_NAME);
				}else if(dfrsServer.serverName.equalsIgnoreCase(Constants.RMI_SERVER_3_NAME))
				{
					flightVO.Destination = (Constants.RMI_SERVER_1_NAME);
				}
				temporary.add(flightVO);i++;
			}
			j++;
		}
			flightRecordMap.put(dfrsServer.serverName, temporary);
		}
		updateFLightRecords();
	}
	@Override
	public int[] getBookedFlightCount(String recordType) {
		logger.info("getBookedFlightCount() function call");
		int[] flightCounts = new int[3];
		int i = 0;
		PassengerWrapperVO passengerWrapperVO = new PassengerWrapperVO();
		passengerWrapperVO.setRequestType(Constants.REQUEST_TYPE.FLIGHT_COUNT);
		passengerWrapperVO.setRecordType(recordType);
		Iterator<MainServerProject> dFRSIterator = DFRSServers.iterator();
		while (dFRSIterator.hasNext()) {
			MainServerProject dfrsServer = (MainServerProject) dFRSIterator.next();
			synchronized (dfrsServer) {
				DatagramSocket datagramSocket = null;
				try {
					ByteArrayOutputStream baos = new ByteArrayOutputStream(5000);
					ObjectOutputStream objectOS;
					objectOS = new ObjectOutputStream(new BufferedOutputStream(baos));
					objectOS.flush();
					objectOS.writeObject(passengerWrapperVO);
					objectOS.flush();
					byte[] sendData = baos.toByteArray();
					datagramSocket = new DatagramSocket();
					InetAddress hostAddress = InetAddress.getByName(dfrsServer.getServerAddress());
					int serverPort = dfrsServer.getUdpPort();
					DatagramPacket request = new DatagramPacket(sendData, sendData.length, hostAddress, serverPort);
					datagramSocket.send(request);
					byte[] message = new byte[5000];
					DatagramPacket reply = new DatagramPacket(message, message.length);
					datagramSocket.receive(reply);

					ByteArrayInputStream bais = new ByteArrayInputStream(message);
					ObjectInputStream is = new ObjectInputStream(new BufferedInputStream(bais));
			
						PassengerWrapperVO pass = (PassengerWrapperVO) is.readObject();
					int serverflightseat = Integer.parseInt(pass.getRecordType());
					flightCounts[i++] = serverflightseat;
					is.close();
					bais.close();
				} catch (Exception ex) {
					ex.printStackTrace();
				} finally {					datagramSocket.close();
				}
			}
		}
		 System.out.println(flightCounts);
		return flightCounts;
	}

	@Override
	public FlightVO editFlightRecord(String recordID, String fieldName, String newValue) {
		boolean available = false;
		Calendar now = Calendar.getInstance();
		String message = "SUCCESS: Your changes got updated successfully";
		Calendar futureDate = Calendar.getInstance(); 
	    futureDate.add(Calendar.DATE, 10);
		String[] data = recordID.split(",");
		ArrayList<FlightVO> listFlight = null;
		if(data[1].substring(0, 3).equalsIgnoreCase("MTL")) 
			listFlight = flightRecordMap.get(Constants.RMI_SERVER_1_NAME);
		if(data[1].substring(0, 3).equalsIgnoreCase("WST"))
			listFlight = flightRecordMap.get(Constants.RMI_SERVER_2_NAME);
		if(data[1].substring(0, 3).equalsIgnoreCase("NDL"))
			listFlight = flightRecordMap.get(Constants.RMI_SERVER_3_NAME);
		FlightVO flightVO = null;
		for (Iterator iterator = listFlight.iterator(); iterator.hasNext();) {
			flightVO = (FlightVO) iterator.next();
			if(flightVO.flightID == Integer.parseInt(data[0])){
				 available =true;
				break;
			}
		}
		if( available){
			flightVO.isFlightModified = true;
			if(fieldName.equalsIgnoreCase(Constants.DEPARTURE)){
				flightVO.Departure = (newValue);
			}
			else if(fieldName.equalsIgnoreCase(Constants.DESTINATION)){
				flightVO.Destination = (newValue);
			}
			else if(fieldName.equalsIgnoreCase(Constants.BUSINESSSEATS)){
				flightVO.businessSeats = (Integer.parseInt(newValue));
			}
			else if(fieldName.equalsIgnoreCase(Constants.ECONOMYSEATS)){
				flightVO.economySeats = (Integer.parseInt(newValue));
			}
			else if(fieldName.equalsIgnoreCase(Constants.FIRSTCLASSSEATS)){
				flightVO.firstClassSeats = (Integer.parseInt(newValue));
			}
			else if(fieldName.equalsIgnoreCase(Constants.AVAILABILITY)){
				if(newValue.equalsIgnoreCase("true")){
					flightVO.isFlightModified = (false);
					listFlight.remove(flightVO);
					message = "SUCCESS: Flight record is deleted successfully" ;
				}
				
			}
			else if(fieldName.equalsIgnoreCase(Constants.FLIGHTDATE)){
				SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy");
				Date date2=null;
				try {
				    date2 = dateFormat.parse(newValue);
				    if(date2.before(now.getTime()) || date2.after(futureDate.getTime()))
				    {
				    	System.out.println("Flight not available , try some other date");
				    	message = "FAILURE: Flight not available , try some other date";
				    	flightVO.isFlightModified = (false);
				    }
				    date2 = new Date(newValue);
				    now.setTime(date2);
				    flightVO.flightDate = (date2).toString();
				} catch (java.text.ParseException e) {
					System.out.println("Invalid input it should be in MM/dd/yyyy format");
					message = "FAILURE: Invalid input it should be in MM/dd/yyyy format";
					flightVO.isFlightModified = (false);
					//e.printStackTrace();
				}
			}
			else if(fieldName.equalsIgnoreCase(Constants.FLIGHTTIME)){
				//flightVO.setIsAvailable(false);
			}
		}else{
			flightVO = new FlightVO();
			flightVO.isFlightModified = (true);
			flightVO.flightID = (Integer.parseInt(data[0]));
			flightVO.economySeats = (Constants.ECONOMY_NUMBEROFSEATS);
			flightVO.businessSeats = (Constants.BUISNESS_NUMBEROFSEATS);
			flightVO.firstClassSeats = (Constants.FIRST_NUMBEROFSEATS);
			flightVO.flightDate = (new Date()).toString();
			if(data[1].startsWith("MTL")){
				flightVO.Departure = (Constants.RMI_SERVER_1_NAME);
				flightVO.Destination = (Constants.RMI_SERVER_2_NAME);
			}else if(data[1].startsWith("NDL"))
			{
				flightVO.Departure = (Constants.RMI_SERVER_3_NAME);
				flightVO.Destination = (Constants.RMI_SERVER_2_NAME);
			}
			else  if(data[1].startsWith("WST")){
				flightVO.Departure = (Constants.RMI_SERVER_2_NAME);
				flightVO.Destination = (Constants.RMI_SERVER_1_NAME);
			}
			if(fieldName.equalsIgnoreCase(Constants.AVAILABILITY)){
				if(newValue.equalsIgnoreCase("true"))
					flightVO.isAvailable = (false);
			}
			message = "New Flight is created";
			flightVO.message = (message);
		}
		flightVO.editedBy = (data[1]);
		if(flightVO.isFlightModified){
			synchronized(this){
			listFlight.add(flightVO);
			}
		}
		flightVO.message = (message);
			
		logger.info(flightVO.message);
		if(flightVO.isFlightModified)
			logger.info("\n============================================================\nFlightDetails Details:\n============================================================" + "\n\tRecordId:\t" + flightVO.flightID + "\n\tDeparture:\t" + flightVO.Departure+ "\n\tDestination:\t" + flightVO.Destination + "\n\tflightDate:\t" + flightVO.flightDate + "\n\teconomySeats:\t"
					+ flightVO.economySeats + "\n\tbusinessSeats:\t" + flightVO.businessSeats + "\n\tfirstClassSeats:\t" + flightVO.firstClassSeats + "\n\tEditedBy:\t" + flightVO.editedBy +"\n");
		updateFLightRecords();
		return flightVO;
	}
	
	public static boolean isNumeric(String str)
	{
	    for (char c : str.toCharArray())
	    {
	        if (!Character.isDigit(c)) return false;
	    }
	    return true;
	}

	private MainServerProject getServerReference(String otherCity) {
		if(otherCity.equalsIgnoreCase(Constants.RMI_SERVER_1_NAME))
			return mtlServer;
		if(otherCity.equalsIgnoreCase(Constants.RMI_SERVER_2_NAME))
			return wstServer;
		if(otherCity.equalsIgnoreCase(Constants.RMI_SERVER_3_NAME))
			return ndlServer;		
	return null;
	}
	@Override
	public boolean managerAuthentication(String userName) {
		if(userName.length() == 7 && (isNumeric(userName.substring(3, 7)))){
			if(userName.substring(0, 3).equalsIgnoreCase("MTL") || userName.substring(0, 3).equalsIgnoreCase("WST") || userName.substring(0, 3).equalsIgnoreCase("NDL")){
				logger.info("Manager succesfully Authorized with ID: "+userName);
				return true;
			}
		}
		logger.info("Manager failed Authorization with ID: "+userName);
		return false;
	}

	@SuppressWarnings("incomplete-switch")
	@Override
	public PassengerVO transferReservation(String passengerID, String currentCity, String otherCity) {
		System.out.println("\n.........................transferingReservation.....................................");
		PassengerWrapperVO passengerWrapperVO = new PassengerWrapperVO();
		PassengerVO passengerVO = new PassengerVO();
		boolean isAvailable = false;
		 for (Entry<Character, ArrayList<PassengerVO>> entry : this.recordMap.entrySet()) { 
		    	ArrayList<PassengerVO> temp = entry.getValue();
		    	for (Iterator iterator = temp.iterator(); iterator.hasNext();) {
					PassengerVO passenger = (PassengerVO) iterator.next();
					if(passenger.recordID.equalsIgnoreCase(passengerID)){
						passengerWrapperVO.setRequestType(Constants.REQUEST_TYPE.TRANSFER_RESERVATION);
						passengerWrapperVO.setRequestId(passengerID);
						passengerWrapperVO.setPassengerVO(passenger);
						isAvailable = true;
						break;
					}
		    	}
		 }
		 if(isAvailable){	
		 	PassengerVO copyPassengerVO = passengerWrapperVO.getPassengerVO();
			MainServerProject dfrsServer = getServerReference(otherCity);
			passengerWrapperVO = sendTransferReservationRequest(dfrsServer, passengerWrapperVO);
			passengerWrapperVO.setPassengerVO(copyPassengerVO);
			passengerWrapperVO = deletefromCurrentServer(passengerWrapperVO, passengerID);
			
			System.out.println("\ttransferReservartion() PASSENGER TRANSFER RESERVATION STATUS:\t" + passengerWrapperVO.getPassengerStatus());
			System.out.println("\ttransferReservartion() PASSENGER DELETE   RESERVATION STATUS:\t" + passengerWrapperVO.getDeletePassengerStatus());
			logger.info("\ttransferReservartion() PASSENGER TRANSFER RESERVATION STATUS:\t" + passengerWrapperVO.getPassengerStatus());
			logger.info("\ttransferReservartion() PASSENGER DELETE   RESERVATION STATUS:\t" + passengerWrapperVO.getDeletePassengerStatus());

			boolean isTransfered = false;
			switch (passengerWrapperVO.getDeletePassengerStatus()) {
			case DELETED:
				switch (passengerWrapperVO.getPassengerStatus()) {
				case CREATED:
					isTransfered = true;
					break;
				case ERROR:
					 rollbackDeletefromCurrentServer(dfrsServer,passengerWrapperVO);
					break;
				}
				break;
			case ERROR:
				switch (passengerWrapperVO.getPassengerStatus()) {
				case CREATED:
					PassengerWrapperVO rollBackPassengerWrapperVO = rollBackPassenger(dfrsServer, passengerWrapperVO);
					break;
				case ERROR:
					break;
				}
				break;
			}

			if (isTransfered) {
				passengerVO = passengerWrapperVO.getPassengerVO();
				passengerVO.message = "SUCCESS: Transfer Reservation from Current City to Other Done Successfully";
			} else {
				passengerVO = passengerWrapperVO.getPassengerVO();
				passengerVO.message = "FAILURE: Transfer Reservation Declined due to ERROR";
			}
		} else {
			passengerVO.recordID="";
			passengerVO.firstName = "";
			passengerVO.lastName = "";
			passengerVO.address = "";
			passengerVO.phoneNumber = "";
			passengerVO.destination = "";
			passengerVO.dateOfFilight = "";
			passengerVO.classOfFlight = "";
			passengerVO.message = "Invalid Passenger ID - Not available for transfer";
		}
		System.out.println("transferLoan STATUS:\t" + passengerVO.message);
		logger.info("transferLoan STATUS:\t" + passengerVO.message);
		System.out.println("\n\n");
		return passengerVO;
	}

	 private void rollbackDeletefromCurrentServer(MainServerProject dfrsServer, PassengerWrapperVO passengerWrapperVO) {
	        System.out.println("rollbackDeletefromCurrentServer");
	        int seat;boolean available = true;
	        PassengerVO passengerVO = passengerWrapperVO.getPassengerVO();
	        Character key = passengerVO.lastName.charAt(0);
	        ArrayList<PassengerVO> temp = null;
	        
	            if(this.recordMap.containsKey(key)){
	                synchronized(this.recordMap.get(passengerVO.lastName.charAt(0))){
	                    temp = this.recordMap.get(passengerVO.lastName.charAt(0));
	                    }
	                synchronized(temp){
	                    if (temp!= null){
	                        if(!temp.contains(passengerVO)){
	                            temp.add(passengerVO);
	                        
	                        }
	                        passengerWrapperVO.setPassengerStatus(Constants.PASSENGER_STATUS.CREATED);
	                    }
	                }
	            }else{
	                temp = new ArrayList<PassengerVO>();
	                
	                temp.add(passengerVO);
	                
	                this.recordMap.put(passengerVO.lastName.charAt(0), temp);
	                passengerWrapperVO.setPassengerStatus(Constants.PASSENGER_STATUS.CREATED);
	            }
	            updatePassengerRecords(this);
	            System.out.println("\t rollbackDeletefromCurrentServer() STATUS:\t" + passengerWrapperVO.getPassengerStatus());
	            logger.info("\t rollbackDeletefromCurrentServer() STATUS:\t" + passengerWrapperVO.getPassengerStatus());
	        
	    }
	
	private PassengerWrapperVO rollBackPassenger(MainServerProject dfrsServer, PassengerWrapperVO passengerWrapperVO) {
			list = new ArrayList<PassengerWrapperVO>();
			passengerWrapperVO.setRequestType(Constants.REQUEST_TYPE.ROLLBACK_RESERVATION);
			try{
			ByteArrayOutputStream baos = new ByteArrayOutputStream(5000);
			ObjectOutputStream objectOS;
			objectOS = new ObjectOutputStream(new BufferedOutputStream(baos));
			objectOS.flush();
			objectOS.writeObject(passengerWrapperVO);
			objectOS.flush();
			byte[] sendData = baos.toByteArray();
			objectOS.close();
			baos.close();
			UDPClient rollBackThread = new UDPClient(dfrsServer.getServerAddress(), dfrsServer.udpPort, sendData,
					passengerWrapperVO.getRequestType(),list);
			rollBackThread.start();
			rollBackThread.join();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			updatePassengerRecords(this);
		System.out.println("\trollBackPassenger() STATUS:\t" + passengerWrapperVO.getPassengerStatus());
		logger.info("\trollBackPassenger() STATUS:\t" + passengerWrapperVO.getPassengerStatus());
		return passengerWrapperVO;
	}

	private PassengerWrapperVO deletefromCurrentServer(PassengerWrapperVO passengerWrapperVO, String passengerID) {
		ArrayList<PassengerVO> temp = null;
		PassengerVO ps= passengerWrapperVO.getPassengerVO();
		System.out.println(ps.lastName);
		if(this.recordMap.containsKey(ps.lastName.charAt(0)))
		synchronized(this.recordMap.get(ps.lastName.charAt(0))){
		temp = this.recordMap.get(ps.lastName.charAt(0));
		}
		PassengerVO passengerVO = null;
		for (Iterator iterator = temp.iterator(); iterator.hasNext();) {
			 passengerVO = (PassengerVO) iterator.next();
			if(passengerID.equalsIgnoreCase(passengerVO.recordID))
				break;
		}
		System.out.println("passengerVO"+passengerVO);
		if(passengerVO!=null)
		{   synchronized(temp){
			temp.remove(passengerVO);
			}
		}
		if(temp.size()==0)
		{
			this.recordMap.remove(passengerWrapperVO.getPassengerVO().lastName.charAt(0));
		}
		else
			this.recordMap.put(passengerWrapperVO.getPassengerVO().lastName.charAt(0),temp);
		
		if (this.recordMap.get(passengerWrapperVO.getPassengerVO().lastName.charAt(0))!=null && this.recordMap.get(passengerWrapperVO.getPassengerVO().lastName.charAt(0)).contains(passengerVO)) {
			passengerWrapperVO.setDeletePassengerStatus(Constants.PASSENGER_STATUS.ERROR);
		} else {
			passengerWrapperVO.setDeletePassengerStatus(Constants.PASSENGER_STATUS.DELETED);
		}
		updatePassengerRecords(this);
		System.out.println("\tdeletefromCurrentServer() PASSENGER DELETE STATUS:\t" + passengerWrapperVO.getDeletePassengerStatus() + "\tPASSENGER ID:\t " + passengerID);
		logger.info("\tdeletefromCurrentServer() PASSENGER DELETE STATUS:\t" + passengerWrapperVO.getDeletePassengerStatus() + "\tPASSENGER ID:\t " + passengerID);
		return passengerWrapperVO;
	}

	private PassengerWrapperVO sendTransferReservationRequest(MainServerProject dfrsServer,
			PassengerWrapperVO passengerWrapperVO) {
		System.out.println("sendTransferReservationRequest");
			try {
			ByteArrayOutputStream baos = new ByteArrayOutputStream(5000);
			ObjectOutputStream objectOS;
			objectOS = new ObjectOutputStream(new BufferedOutputStream(baos));
			objectOS.flush();
			objectOS.writeObject(passengerWrapperVO);
			objectOS.flush();
			byte[] data = baos.toByteArray();
			objectOS.close();
			baos.close();
			UDPClient udpClient = new UDPClient(dfrsServer.getServerAddress(), dfrsServer.udpPort, data,
					passengerWrapperVO.getRequestType(), list);
			udpClient.start();
			udpClient.join();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			passengerWrapperVO = list.get(0);
			updatePassengerRecords(dfrsServer);
		return passengerWrapperVO;
	}

	public PassengerWrapperVO receivePassengerReservation(PassengerWrapperVO passengerWrapperVO) {
		System.out.println("receivePassengerReservation");
		int seat;boolean available = true;
		PassengerVO passengerVO = passengerWrapperVO.getPassengerVO();
		Character key = passengerVO.lastName.charAt(0);
		System.out.println(this.recordMap.containsKey(key)+"8888888888888888888888888888"+passengerVO.dateOfFilight);
		ArrayList<PassengerVO> temp = null;
		ArrayList<FlightVO> listOfFlight = flightRecordMap.get(this.serverName);
		for (Iterator iterator = listOfFlight.iterator(); iterator.hasNext();) {
			FlightVO flightVO = (FlightVO) iterator.next();
			if(flightVO.flightDate.equalsIgnoreCase(passengerVO.dateOfFilight) && flightVO.Destination.equalsIgnoreCase(passengerVO.destination))
			{
				System.out.println("Flight Dates are same");
				if(passengerVO.classOfFlight.equalsIgnoreCase(Constants.BUISNESS_CLASS)){
					seat = flightVO.businessSeats;
					if(seat!=0)
					flightVO.businessSeats = (seat - 1);
					else 
						available = false;
				}
				else if(passengerVO.classOfFlight.equalsIgnoreCase(Constants.ECONOMY_CLASS)){
					seat = flightVO.economySeats;
					if(seat!=0)
					flightVO.economySeats = (seat - 1);
					else 
						available = false;
				}
				else{
					seat = flightVO.firstClassSeats;
					if(seat!=0)
					flightVO.firstClassSeats = (seat - 1);
					else 
						available = false;
				}
			}
			
		}
		if(available){
			if(this.recordMap.containsKey(key)){
				synchronized(this.recordMap.get(passengerVO.lastName.charAt(0))){
					temp = this.recordMap.get(passengerVO.lastName.charAt(0));
					}
				synchronized(temp){
					if (temp!= null){
						if(!temp.contains(passengerVO)){
							temp.add(passengerVO);
						
						}
						passengerWrapperVO.setPassengerStatus(Constants.PASSENGER_STATUS.CREATED);
					}
				}
			}else{
				temp = new ArrayList<PassengerVO>();
				
				temp.add(passengerVO);
				
				this.recordMap.put(passengerVO.lastName.charAt(0), temp);
				passengerWrapperVO.setPassengerStatus(Constants.PASSENGER_STATUS.CREATED);
			}
		}
		else
			passengerWrapperVO.setPassengerStatus(Constants.PASSENGER_STATUS.ERROR);
		synchronized(this.recordMap){
		if(this.recordMap.containsKey(key))
		temp = this.recordMap.get(key);
		if (temp!= null ){
			if(!temp.contains(passengerVO))
				passengerWrapperVO.setPassengerStatus(Constants.PASSENGER_STATUS.ERROR);
		}
		else
			passengerWrapperVO.setPassengerStatus(Constants.PASSENGER_STATUS.ERROR);
		}
		updatePassengerRecords(this);
		System.out.println("\t\treceivePassengerReservation() Passenger STATUS:\t" + passengerWrapperVO.getPassengerStatus() + "\tPASSENGER ID:\t"
				+ passengerWrapperVO.getPassengerVO().recordID);
		logger.info("\t\treceivePassengerReservation() Passenger STATUS:\t" + passengerWrapperVO.getPassengerStatus() + "\tPASSENGER ID:\t"
				+ passengerWrapperVO.getPassengerVO().recordID);
		return passengerWrapperVO;
	}

	public PassengerWrapperVO deleteReservation(PassengerWrapperVO passengerWrapperVO) {
		ArrayList<PassengerVO> temp = null;
		PassengerVO ps= passengerWrapperVO.getPassengerVO();
		synchronized(this.recordMap.get(ps.lastName.charAt(0))){
			temp = this.recordMap.get(ps.lastName.charAt(0));
			}
			PassengerVO passengerVO = null;
			for (Iterator iterator = temp.iterator(); iterator.hasNext();) {
				 passengerVO = (PassengerVO) iterator.next();
				if(ps.recordID.equalsIgnoreCase(passengerVO.recordID))
					break;
			}
			if(passengerVO!=null)
			{   synchronized(temp){
				temp.remove(passengerVO);
				}
			}
			if(temp.size()==0)
			{
				this.recordMap.remove(passengerWrapperVO.getPassengerVO().lastName.charAt(0));
			}
			else
				this.recordMap.put(passengerWrapperVO.getPassengerVO().lastName.charAt(0),temp);
			
		if (this.recordMap.get(passengerWrapperVO.getPassengerVO().lastName.charAt(0))!=null && this.recordMap.get(passengerWrapperVO.getPassengerVO().lastName.charAt(0)).contains(ps)) {
			passengerWrapperVO.setDeletePassengerStatus(Constants.PASSENGER_STATUS.ROLLBACK_ERROR);
		} else {
			passengerWrapperVO.setDeletePassengerStatus(Constants.PASSENGER_STATUS.ROLLED_BACK);
		}
		System.out.println("\tdeletefromCurrentServer() PASSENGER DELETE STATUS:\t" + passengerWrapperVO.getPassengerStatus());
		logger.info("\tdeletefromCurrentServer() PASSENGER DELETE STATUS:\t" + passengerWrapperVO.getPassengerStatus());
		updatePassengerRecords(this);
		return passengerWrapperVO;
	}
	/**
	 * @return the groupLeaderProcessID
	 */
	public int getGroupLeaderProcessID() {
		return groupLeaderProcessID;
	}

	/**
	 * @param groupLeaderProcessID the groupLeaderProcessID to set
	 */
	public void setGroupLeaderProcessID(int groupLeaderProcessID) {
		this.groupLeaderProcessID = groupLeaderProcessID;
	}

	/**
	 * @return the froentEndAddress
	 */
	public String getFrontEndAddress() {
		return frontEndAddress;
	}

	/**
	 * @param froentEndAddress the froentEndAddress to set
	 */
	public void setFrontEndAddress(String frontEndAddress) {
		this.frontEndAddress = frontEndAddress;
	}
}
