package client;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;
import java.util.logging.FileHandler;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

import org.omg.CORBA.ORB;

import FrontEnd.FrontEndInterface;
import FrontEnd.FrontEndInterfaceHelper;
import FrontEnd.FlightVO;
import FrontEnd.PassengerVO;
import util.Constants;

/*
 * author: Varun Pattiah Sankaralingam
 * Program: Perform all manager action
 */
public class ManagerClient {

	FrontEndInterface mtlInterface = null;
	FrontEndInterface wstInterface = null;
	FrontEndInterface ndlInterface = null;
	String serverName = null;
	String managerID = null;
	private Logger logger;
	private enum City {
		A("MONTREAL"),
		B("WASHINGTON"),
		C("NEWDELHI");
		private String text;

		City(String text) {
		    this.text = text;
	}
	}
	
	private void setLogger(String managerID) {
		String logFile = Constants.CLIENT_MANAGER_LOG_PATH + managerID + Constants.LOG_EXTENSION;
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

	/**
	 * initializeServers - initializes all the 3 server instances
	 * 
	 * @throws MalformedURLException
	 * @throws RemoteException
	 * @throws NotBoundException
	 */
	private void initializeServers(String args[]) throws IOException {
		ORB orb = ORB.init(args,null);
		BufferedReader br;
		String ior = null;
		br = new BufferedReader(new FileReader(Constants.IOR_PATH + Constants.FRONTEND_NAME + Constants.LOG_EXTENSION));
		ior = br.readLine();
		br.close();
		
		org.omg.CORBA.Object o = orb.string_to_object(ior);
		
		mtlInterface = wstInterface = ndlInterface = FrontEndInterfaceHelper.narrow(o);
	}
	
	/*
	 * Description: Based on departure city , interface is found
	 */
	private FrontEndInterface getManagerInterface(String managerID) {
		int serverNumber = 0;
		if(managerID.substring(0, 3).equalsIgnoreCase("MTL"))
			serverNumber = 1;
		if(managerID.substring(0, 3).equalsIgnoreCase("WST"))
			serverNumber = 2;
		if(managerID.substring(0, 3).equalsIgnoreCase("NDL"))
			serverNumber = 3;
		
		boolean invalidServer = true;
		while (invalidServer) {
			
			switch (serverNumber) {
			case 1:
				
				serverName = Constants.RMI_SERVER_1_NAME;
				invalidServer = false;
				return mtlInterface;
			case 2:
				serverName = Constants.RMI_SERVER_2_NAME;
				invalidServer = false;
				return wstInterface;
			case 3:
				serverName = Constants.RMI_SERVER_3_NAME;
				invalidServer = false;
				return ndlInterface;
			default:
				System.out.println("\nInvalid Manager ID");
				return null;
			}
		}
		return null;
	}

	/**
	 * showMenu() - function to show menu options
	 */
	private void showMenu() {
		System.out.println("Distributed Flight Reservation System");
		System.out.println("Manager Operations");
		System.out.println("Please select an option");
		System.out.println("1. Booked Flight Count.");
		System.out.println("2. Edit Flight Record");
		System.out.println("3. Transfer Reservation");
		System.out.println("4. Exit");
		System.out.println("5. Exit to Main Menu");
	}


	/**
	 * main function - the control starts here
	 * 
	 * @param args
	 * @return 
	 */
	public int Starter(String args[]) {
		ManagerClient managerClient = new ManagerClient();
		try {
			managerClient.initializeServers(args);
		} catch (IOException e1) {
			System.out.println("Error Starting servers");
			e1.printStackTrace();
		}		
		while (true) {
			try {
				boolean isChangeServer = false;
				while (!isChangeServer) {
					System.out.println("Enter the MANAGER credentials");
					Scanner scanner1 = new Scanner(System.in);
					String userName = scanner1.nextLine().toUpperCase();
					boolean isAuthorized = managerClient.managerAuthentication(userName);
					if (!isAuthorized)
						System.out.println("Invalid MANAGER Credentials");
					else
						managerID = userName;
					while (isAuthorized) {
						FrontEndInterface FrontEndInterface = managerClient.getManagerInterface(userName);
						managerClient.setLogger(userName);
						managerClient.logger.info(userName+" Manager Credentials Authorized");
						int menuOption = 0;
						managerClient.showMenu();
						Scanner scanner = new Scanner(System.in);
						menuOption = scanner.nextInt();
						switch (menuOption) {
						case 1:
							managerClient.getBookedFlightCount(managerClient, FrontEndInterface, userName);
							break;
						case 2:
							managerClient.editFlightBookRecords(FrontEndInterface, userName);
							break;
						case 3:
							managerClient.transferReservation(managerClient, FrontEndInterface, userName);
							break;
						case 4:
							isAuthorized = false;
							isChangeServer = true;
							break;
						case 5:
							return 3;
						default:
							System.out.println("Invalid Input.Enter a valid menu option");
							managerClient.logger.info("Invalid Input.Enter a valid menu option");
						}
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

	}

	private void transferReservation(ManagerClient managerClient, FrontEndInterface FrontEndInterface, String userName) {
		Scanner scanner = new Scanner(System.in);
		String passengerID = null, otherCity = null;
		System.out.println("Provide the following Details to transferReservation of the record:\t");
		System.out.println("1. PassengerID\t");
		System.out.println("2. OtherCity\t");
		System.out.println("Enter the PassengerID");
		passengerID = scanner.nextLine();
		System.out.println("Select Other City:");
		int i =0;
		ArrayList<City> cities = new ArrayList<City>();
		for (City b : City.values()) {
	        if (serverName.equalsIgnoreCase(b.text)) {
	          continue;
	        }
	        else{
	        	i++;cities.add(b);
	        	System.out.println(i+"."+b.text+"\t");
	        }
	      }
		boolean isValid1 = false;
		while (!isValid1) {
			int menuOption = 0;
			menuOption = scanner.nextInt();
			switch (menuOption) {
			case 1:
				otherCity = (cities.get(0).text);
				isValid1 = true;
				break;
			case 2:
				otherCity = (cities.get(1).text);
				isValid1 = true;
				break;
			default:
				System.out.println("Invalid Input.Enter a valid menu option");
			}
		}
		String message = FrontEndInterface.executeOperation("TR", userName, "", "", "", "", "", "", "", "", "", "", passengerID, "", otherCity, "", serverName);
		System.out.println(message);
		logger.info(message);
	}

	private void editFlightBookRecords(FrontEndInterface FrontEndInterface, String username) {
		String recordID = null, fieldName = null, newValue = null;
		FrontEnd.FlightVO flightVO;
		Scanner scanner = new Scanner(System.in);
		System.out.println("Provide the following Details to edit the record:\t");
		System.out.println("1. RecordId\t");
		System.out.println("2. FieldName\t");
		System.out.println("3. NewValue\t");
		System.out.println("Enter the RecordID");
		recordID = String.valueOf(scanner.nextInt());System.out.println(recordID);
		System.out.println("Select the FieldName");
		System.out.println("1."+Constants.DEPARTURE);
		System.out.println("2."+Constants.DESTINATION);
		System.out.println("3."+Constants.ECONOMYSEATS);
		System.out.println("4."+Constants.BUSINESSSEATS);
		System.out.println("5."+Constants.FIRSTCLASSSEATS);
		System.out.println("6."+Constants.FLIGHTDATE);
		System.out.println("7."+Constants.AVAILABILITY);
		boolean isValid = false;
		while (!isValid) {
			int menuOption = 0;
			menuOption = scanner.nextInt();
			switch (menuOption) {
			case 1:
				fieldName = Constants.DEPARTURE;
				isValid = true;
				break;
			case 2:
				fieldName = Constants.DESTINATION;
				isValid = true;
				break;
			case 3:
				fieldName = Constants.ECONOMYSEATS;
				isValid = true;
				break;
			case 4:
				fieldName = Constants.BUSINESSSEATS;
				isValid = true;
				break;
			case 5:
				fieldName = Constants.FIRSTCLASSSEATS;
				isValid = true;
				break;
			case 6:
				fieldName = Constants.FLIGHTDATE;
				isValid = true;
				break;
			case 7:
				fieldName = Constants.AVAILABILITY;
				isValid = true;
				break;
			default:
				System.out.println("Invalid Input.Enter a valid menu option");
			}
		}

		System.out.println("Enter the FieldValue");
		newValue = scanner.next();
		String message = FrontEndInterface.executeOperation("ER", username, "", "", "", "", "", "", "", "", recordID+","+username, fieldName, "", "", "", newValue, serverName);
		System.out.println(message);
		/*System.out.println("\n============================================================\nFlightDetails Details:\n============================================================" + "\n\tRecordId:\t" + flightVO.flightID + "\n\tDeparture:\t" + flightVO.Departure+ "\n\tDestination:\t" + flightVO.Destination + "\n\tflightDate:\t" + flightVO.flightDate + "\n\teconomySeats:\t"
				+ flightVO.economySeats + "\n\tbusinessSeats:\t" + flightVO.businessSeats + "\n\tfirstClassSeats:\t" + flightVO.firstClassSeats + "\n\tEditedBy:\t" + flightVO.editedBy +"\n");
		*/logger.info(message);
		/*logger.info("\n============================================================\nFlightDetails Details:\n============================================================" + "\n\tRecordId:\t" + flightVO.flightID + "\n\tDeparture:\t" + flightVO.Departure+ "\n\tDestination:\t" + flightVO.Destination + "\n\tflightDate:\t" + flightVO.flightDate + "\n\teconomySeats:\t"
				+ flightVO.economySeats + "\n\tbusinessSeats:\t" + flightVO.businessSeats + "\n\tfirstClassSeats:\t" + flightVO.firstClassSeats + "\n\tEditedBy:\t" + flightVO.editedBy +"\n");
	*/}

	private void getBookedFlightCount(ManagerClient managerClient, FrontEndInterface FrontEndInterface, String userName) {
		HashMap<String, Integer>  mapdata = new HashMap<String, Integer>();
		Character[] recordCount = new Character[3];
		String recordType = "";
		Scanner scanner = new Scanner(System.in);
		System.out.println("Select Passenger CLASS OF FLIGHT:\t");
		System.out.println("1. Economy\t");
		System.out.println("2. Buisness\t");
		System.out.println("3. First\t");
		boolean isValid = false;
		while (!isValid) {
			int menuOption = 0;
			menuOption = scanner.nextInt();
			switch (menuOption) {
			case 1:
				recordType = "ECONOMY";
				isValid = true;
				break;
			case 2:
				recordType = "BUISNESS";
				isValid = true;
				break;
			case 3:
				recordType = "FIRST";
				isValid = true;
				break;
			default:
				System.out.println("Invalid Input.Enter a valid menu option");
			}
		}
		String count = FrontEndInterface.executeOperation("GB", userName, "", "", "", "", "", "", "", recordType, "", "", "", "", "", "", serverName);
		System.out.println("["+Constants.RMI_SERVER_1_NAME+","+Constants.RMI_SERVER_2_NAME+","+Constants.RMI_SERVER_3_NAME+"]");

		System.out.println(count);
		managerClient.logger.info(" Booked Flight Count : "+mapdata);
		
	}

	/**
	 * @param FrontEndInterface
	 * @return manager authorization statu
	 * s - true or false
	 * @throws RemoteException
	 */
	private boolean managerAuthentication(String userName) throws RemoteException {
		FrontEndInterface FrontEndInterface = this.getManagerInterface(userName);
		boolean isAuthorized = false;
		if(FrontEndInterface!=null){
			String result = FrontEndInterface.executeOperation("MA", userName, "", "", "", "", "", "", "", "", "", "", "", "", "", "", serverName);
			if(result.trim().equalsIgnoreCase("true"))
			isAuthorized = true;
		}
		return isAuthorized;
	}

	
}
