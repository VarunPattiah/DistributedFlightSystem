package server;

public class Constants {
	/**
	 * @author Shreyas Movva
	 * Constants for RMI
	 */

	public static final String RMI_SERVER_1_ADDRESS = "localhost";
	public static final String RMI_SERVER_2_ADDRESS = "localhost";
	public static final String RMI_SERVER_3_ADDRESS = "localhost";

	public static final String RMI_SERVER_1_PORT = "10991";
	public static final String RMI_SERVER_2_PORT = "10992";
	public static final String RMI_SERVER_3_PORT = "10993";

	public static final String RMI_SERVER_1_NAME = "Montreal";
	public static final String RMI_SERVER_2_NAME = "Washington";
	public static final String RMI_SERVER_3_NAME = "NewDelhi";
	public static final String ECONOMY_CLASS = "ECONOMY";
	public static final String BUISNESS_CLASS = "BUISNESS";
	public static final String FIRST_CLASS = "FIRST";
	public static final String DEPARTURE ="Departure";
	public static final String DESTINATION ="Destination";
	public static final String FLIGHTDATE ="flightDate";
	public static final String FLIGHTTIME ="flightTime";
	public static final String ECONOMYSEATS ="economySeats";
	public static final String BUSINESSSEATS ="businessSeats";
	public static final String FIRSTCLASSSEATS ="firstClassSeats";
	public static final String AVAILABILITY ="Delete/Create - True/false";
	
	
	
	public static final int UDP_PORT_1_NUMBER = 9996;
	public static final int UDP_PORT_2_NUMBER = 9995;
	public static final int UDP_PORT_3_NUMBER = 9994;
	public static final int MAX_REPLICA = 3;
	public static final int FE_NOTIFY_PORT = 5555;
	public static final int MC_FD_PORT1 = 6001;
	public static final int MC_FD_PORT2 = 6002;
	public static final int MC_FD_PORT3 = 6003;
	public static final int MC_OPERATION_PORT = 9008;
	public static final int MC_LE_PORT = 9003;
	public static final int MTL_NUMBEROFFLIGHT = 10;
	public static final int WST_NUMBEROFFLIGHT = 20;
	public static final int NDL_NUMBEROFFLIGHT = 30;
	public static final int FIRST_NUMBEROFSEATS = 10;
	public static final int BUISNESS_NUMBEROFSEATS = 20;
	public static final int ECONOMY_NUMBEROFSEATS = 30;

	public static final String MANAGER_USERNAME = "MANAGER";
	public static final String MANAGER_PASSWORD = "MANAGER";
	public static final int BOOKING_DATE_LIMIT = 3;
	public static final String PASSENGER_EXISTS = "Passenger Exists with same username(First Name and Last Name)";
	public static final String PASSENGER_SUCCESS = "Passenger Flight booked Successfuly.";
	public static final int CUSTOMER_PWD_LENGTH = 6;
	
	public enum REQUEST_TYPE {
		 FLIGHT_COUNT,TRANSFER_RESERVATION, ROLLBACK_RESERVATION
	}

	public enum PASSENGER_STATUS {
		CREATED, DELETED, ERROR, ROLLED_BACK, ROLLBACK_ERROR, RESTORED
	}

	public static final String LOG_EXTENSION = ".txt";
	public static final String IOR_PATH = ".\\IOR\\";
	public static final String SERVER_LOG_PATH = ".\\LOGS\\SERVER\\";
	public static final String CLIENT_MANAGER_LOG_PATH = ".\\LOGS\\CLIENT\\MANAGER\\";
	public static final String PASSENGER_LOG_PATH = ".\\LOGS\\CLIENT\\PASSENGER\\";
	public static final String FLIGHT_RECORD= ".\\LOGS\\SERVER\\RECORDS\\FlightRecords";
	public static final String FLIGHT_RECORD_OUTPUT= ".\\LOGS\\SERVER\\RECORDS\\FlightRecordsUpdated";
	public static final String PASSENGER_RECORD_OUTPUT= ".\\LOGS\\SERVER\\RECORDS\\PassengerRecordsUpdated";
	public static final int PROCESS_ID = 2;
	public static final String REQUEST_HANDLER_OUTPUT = ".\\LOGS\\SERVER\\RECORDS\\RequestLog";

}
