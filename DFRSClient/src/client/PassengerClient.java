package client;

import FrontEnd.FrontEndInterface;
import FrontEnd.FrontEndInterfaceHelper;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Scanner;
import java.util.logging.FileHandler;
import java.util.logging.Handler;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

import org.omg.CORBA.ORB;

import FrontEnd.PassengerVO;

import util.Constants;


/*
 * @author : Varun Pattiah Sankaralingam
 * Description: this program perform all passenger action to book flight
 * 
 */
public class PassengerClient extends Thread{

	FrontEndInterface mtlInterface = null;
	FrontEndInterface wstInterface = null;
	FrontEndInterface ndlInterface = null;
	String serverName = null;
	private Logger logger;
	private static String departure;
	private enum City {
		A("MONTREAL"),
		B("WASHINGTON"),
		C("NEWDELHI");
		private String text;

		City(String text) {
		    this.text = text;
	}
	}
	
	/*
	 * Setlogger set the log for client
	 */
	private void setLogger(String serverName) {
		String logFile = Constants.PASSENGER_LOG_PATH + serverName + Constants.LOG_EXTENSION;
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
				finally{
					for(Handler h:logger.getHandlers())
					{
					    h.close();   //must call h.close or a .LCK file will remain.
					}
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
	private void initializeServers(String[] args) throws IOException  {
		
			ORB orb = ORB.init(args,null);
			BufferedReader br;
			String ior = null;
			br = new BufferedReader(new FileReader(Constants.IOR_PATH + Constants.FRONTEND_NAME + Constants.LOG_EXTENSION));
			ior = br.readLine();
			br.close();
			
			org.omg.CORBA.Object o = orb.string_to_object(ior);
			
			mtlInterface = wstInterface = ndlInterface = FrontEndInterfaceHelper.narrow(o);
			
	}

	/**
	 * getDepartureCity - function implemented to select banking
	 * institution
	 * 
	 * @return FrontEndInterface
	 */
	private FrontEndInterface getDepartureCity() {
		Scanner scanner = new Scanner(System.in);
		int serverNumber = 0;
		boolean invalidServer = true;
		// System.setSecurityManager(new RMISecurityManager());
		while (invalidServer) {
			System.out.println("Select the Departure City:\n");
			System.out.println("\t\t[1]\t" + Constants.RMI_SERVER_1_NAME);
			System.out.println("\t\t[2]\t" + Constants.RMI_SERVER_2_NAME);
			System.out.println("\t\t[3]\t" + Constants.RMI_SERVER_3_NAME);
			serverNumber = scanner.nextInt();
			switch (serverNumber) {
			case 1:
				serverName = Constants.RMI_SERVER_1_NAME;
				invalidServer = false;
				departure = serverName;
				return mtlInterface;
			case 2:
				serverName = Constants.RMI_SERVER_2_NAME;
				invalidServer = false;
				departure = serverName;
				return wstInterface;
			case 3:
				serverName = Constants.RMI_SERVER_3_NAME;
				invalidServer = false;
				departure = serverName;
				return ndlInterface;
			default:
				System.out.println("\nInvalid Selection");
			}
			
			setLogger(serverName);
		}
		return null;
	}

	/**
	 * showMenu() - function to show menu options
	 */
	private void showMenu() {
		System.out.println("Distributed Flight Reservation System");
		System.out.println("Passenger Operations");
		System.out.println("Please select an option");
		System.out.println("1. Book Flight.");;
		System.out.println("2. Exit");
		System.out.println("3. Exit to Main Menu");
	}

	/**
	 * get input from customer and set to CustomerVO
	 * 
	 * @return CustomerVO
	 */
	@SuppressWarnings("deprecation")
	private PassengerVO setPassengerVO() {
		boolean flag = false;
		PassengerVO passengerVO = new PassengerVO();
		Scanner scanner = new Scanner(System.in);
		System.out.println("Enter Passenger FIRST NAME:\t");
		passengerVO.firstName = (scanner.nextLine().toUpperCase());
		System.out.println("Enter Passenger LAST NAME:\t");
		passengerVO.lastName = (scanner.nextLine().toUpperCase());
		System.out.println("Enter Passenger ADDRESS:\t");
		passengerVO.address = (scanner.nextLine().toUpperCase());
		System.out.println("Enter Passenger PHONE NUMBER:\t");
		while(!flag){
		String text = scanner.nextLine();
		if(text.matches("[0-9]+")){
			passengerVO.phoneNumber = (text);
			flag = true;
		}
		else
			System.out.println("Phone number can only contain digits");
		}
		System.out.println("Select Passenger DESTINATION:\t");
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
				passengerVO.destination = (cities.get(0).text);
				isValid1 = true;
				break;
			case 2:
				passengerVO.destination = (cities.get(1).text);
				isValid1 = true;
				break;
			default:
				System.out.println("Invalid Input.Enter a valid menu option");
			}
		}
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
				passengerVO.classOfFlight = (Constants.ECONOMY_CLASS);
				isValid = true;
				break;
			case 2:
				passengerVO.classOfFlight = (Constants.BUISNESS_CLASS);
				isValid = true;
				break;
			case 3:
				passengerVO.classOfFlight = (Constants.FIRST_CLASS);
				isValid = true;
				break;
			default:
				System.out.println("Invalid Input.Enter a valid menu option");
			}
		}
		System.out.println("Enter Passenger DATE OF FLIGHT within next "+Constants.BOOKING_DATE_LIMIT+" days (MM/dd/yyyy):\t");
		flag = false;
		Calendar now = Calendar.getInstance();
		now.add(Calendar.DATE,-1);
		Calendar futureDate = Calendar.getInstance(); 
	    futureDate.add(Calendar.DATE, Constants.BOOKING_DATE_LIMIT);

	    System.out.println(now.getTime());
		while(!flag){
			String date = scanner.next();
			SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy");
			Date date2=null;
			try {
			    date2 = dateFormat.parse(date);
			    if(date2.before(now.getTime()) || date2.after(futureDate.getTime()))
			    {
			    	System.out.println("Flight not available , try some other date");
			    	continue;
			    }
			    date2 = new Date(date);
			    now.setTime(date2);
			    System.out.println("Select the TIME OF FLIGHT:\t");
				System.out.println("1. 3:00 PM\t");
				System.out.println("2. 6:00 PM\t");
				System.out.println("3. 9:00 PM\t");
				now.set(Calendar.AM_PM, Calendar.PM);
				now.set(Calendar.MINUTE, 0);
				now.set(Calendar.SECOND, 0);
				
				boolean isValid2 = false;
				while (!isValid2) {
					int menuOption = 0;
					menuOption = scanner.nextInt();
					switch (menuOption) {
					case 1:
						now.set(Calendar.HOUR_OF_DAY, 15);
						isValid2 = true;
						break;
					case 2:
						now.set(Calendar.HOUR_OF_DAY, 18);
						isValid2 = true;
						break;
					case 3:
						now.set(Calendar.HOUR_OF_DAY, 21);
						isValid2 = true;
						break;
					default:
						System.out.println("Invalid Input.Enter a valid menu option");
					}
				}
			    passengerVO.dateOfFilight = (now.getTime().toString());
				flag = true;
			} catch (java.text.ParseException e) {
				System.out.println("Invalid input it should be in MM/dd/yyyy format");
				//e.printStackTrace();
			}
		}
		return passengerVO;
	}

	
	private void bookFlight(PassengerClient passengerClient, FrontEndInterface FrontEndInterface,String serverName, PassengerVO passengerVO)
			throws RemoteException {
		setLogger(serverName);
		String managerId = "";
		//System.out.println("this.serverName :"+serverName);
		if(serverName!=null){
		if(serverName.equalsIgnoreCase(Constants.RMI_SERVER_1_NAME))
			managerId="MTL1111";
		else if(serverName.equalsIgnoreCase(Constants.RMI_SERVER_2_NAME))
			managerId="WST1111";
		else if(serverName.equalsIgnoreCase(Constants.RMI_SERVER_3_NAME))
			managerId="NDL1111";
		String resultVO = FrontEndInterface.executeOperation("BF", managerId, passengerVO.firstName, passengerVO.lastName, passengerVO.phoneNumber, passengerVO.destination, passengerVO.address, passengerVO.dateOfFilight, passengerVO.classOfFlight,"", "", "", "", "", "", "", serverName);
		System.out.println(resultVO);
		logger.info(resultVO);
		}
	/*	if (resultVO.isFlightBooked) {
			System.out.println(resultVO.message);
			System.out.println("\n============================================================\nPassenger Details:\n============================================================" + "\n\tRecordId:\t" + resultVO.recordID + "\n\tFirst Name:\t" + resultVO.firstName+ "\n\tLast Name:\t" + resultVO.lastName + "\n\tAddress:\t" + resultVO.address + "\n\tPhone Number:\t"
					+ resultVO.phoneNumber + "\n\tDestination:\t" + resultVO.destination + "\n\tClassOfFlight:\t" + resultVO.classOfFlight
					+ "\n\tDateOfFlight:\t" + resultVO.dateOfFilight +"\n");
			
			logger.info(resultVO.message);
			logger.info("\n============================================================\nPassenger Details:\n============================================================" + "\n\tRecordId:\t" + resultVO.recordID + "\n\tFirst Name:\t" + resultVO.firstName+ "\n\tLast Name:\t" + resultVO.lastName + "\n\tAddress:\t" + resultVO.address + "\n\tPhone Number:\t"
					+ resultVO.phoneNumber + "\n\tDestination:\t" + resultVO.destination + "\n\tClassOfFlight:\t" + resultVO.classOfFlight
					+ "\n\tDateOfFlight:\t" + resultVO.dateOfFilight +"\n");
		} else {
			System.out.println(resultVO.message + resultVO.firstName + "  " + resultVO.lastName);
			logger.info(resultVO.message + " " + resultVO.firstName + "  " + resultVO.lastName);
		}*/
	}

	
	public int Starter(String args[]) {
		PassengerVO passengerVO = new PassengerVO();
		PassengerClient passengerClient = new PassengerClient();
		
			try {
				passengerClient.initializeServers(args);
				passengerClient.createPassengers(passengerClient);
			} catch (Exception e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		

		while (true) {
			try {
				boolean isChangeServer = false;
				FrontEndInterface FrontEndInterface;
				while (!isChangeServer) {
					int menuOption = 0;
					passengerClient.showMenu();
					Scanner scanner = new Scanner(System.in);
					menuOption = scanner.nextInt();
					switch (menuOption) {
					case 1:
						FrontEndInterface = passengerClient.getDepartureCity();
						passengerVO = passengerClient.setPassengerVO();
						passengerClient.bookFlight(passengerClient, FrontEndInterface,departure, passengerVO);
						break;
					case 2:
						isChangeServer = true;
						break;
					case 3:
						return 3;
					default:
						System.out.println("Invalid Input.Enter a valid menu option");
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				System.out.println("Changing Server\n");
			}
		}
	}
	private PassengerVO setPassengerVODetails(String firstName, String lastName, String address,
			String phoneNumber, String destination, Date dateOfFilight, String classOfFlight) {
		PassengerVO passengerVO = new PassengerVO();
		passengerVO.firstName = (firstName);
		passengerVO.lastName = (lastName);
		passengerVO.address = (address);
		passengerVO.phoneNumber = (phoneNumber);
		passengerVO.destination = (destination);
		passengerVO.dateOfFilight = (dateOfFilight).toString();
		passengerVO.classOfFlight = (classOfFlight);
		return passengerVO;
	}

	/**
	 * Function to test Passenger Creation functionality
	 * 
	 * @param customerAction
	 * @throws MalformedURLException
	 * @throws RemoteException
	 * @throws NotBoundException
	 */
	private void createPassengers(PassengerClient passengerClient)
			throws MalformedURLException, RemoteException, NotBoundException {
		String serverName="";
		passengerClient.start();
		FrontEndInterface FrontEndInterface = mtlInterface;
		Calendar now = Calendar.getInstance();
		now.add(Calendar.DATE, 1);
		now.set(Calendar.HOUR_OF_DAY, 15);
		now.set(Calendar.MINUTE, 0);
		now.set(Calendar.SECOND, 0);
		now.add(Calendar.DATE,-1);
		serverName = Constants.RMI_SERVER_1_NAME;
		System.out.println(now.getTime());
		passengerClient.bookFlight(passengerClient, FrontEndInterface,serverName,
				setPassengerVODetails("AA", "AA","apt 16 montreal","5144498754","WASHINGTON", now.getTime(), Constants.ECONOMY_CLASS));
		passengerClient.bookFlight(passengerClient, FrontEndInterface,serverName,
				setPassengerVODetails("BB", "BB","apt 16 montreal","5144498754","WASHINGTON", now.getTime(), Constants.BUISNESS_CLASS));
	
		PassengerClient passengerClient1 = new PassengerClient();
		PassengerClient passengerClient2 = new PassengerClient();
		passengerClient1.start();
		FrontEndInterface = wstInterface;
		serverName = Constants.RMI_SERVER_2_NAME;
		passengerClient1.bookFlight(passengerClient, FrontEndInterface,serverName,
				setPassengerVODetails("CC", "CC","apt 16 washington","5144498754","NEWDELHI", now.getTime(), Constants.ECONOMY_CLASS));
		passengerClient1.bookFlight(passengerClient, FrontEndInterface,serverName,
				setPassengerVODetails("DD", "DD","apt 16 washington","5144498754","NEWDELHI",now.getTime(), Constants.ECONOMY_CLASS));
		
		/*passengerClient2.start();
		FrontEndInterface = ndlInterface;
		serverName = Constants.RMI_SERVER_3_NAME;
		passengerClient2.bookFlight(passengerClient, FrontEndInterface,serverName,
				setPassengerVODetails("EE", "EE","apt 16 delhi","5144498754","MONTREAL", now.getTime(), Constants.ECONOMY_CLASS));
		passengerClient2.bookFlight(passengerClient, FrontEndInterface,serverName,
				setPassengerVODetails("FF", "FF","apt 16 delhi","5144498754","MONTREAL", now.getTime(), Constants.ECONOMY_CLASS));*/
		

	}

		
}
