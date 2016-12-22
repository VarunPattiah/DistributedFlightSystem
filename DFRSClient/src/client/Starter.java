package client;

import java.util.Scanner;


/*
 * author : Varun Pattiah Sankaralingam
 * Program: This is the main console which call the Manager or client console based on user input
 */
public class Starter {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		ManagerClient managerClient = new ManagerClient();
		PassengerClient passengerClient = new PassengerClient();
		Scanner scanner = new Scanner(System.in);
		int value =0;
		boolean isValid = false;
		while (!isValid) {
			System.out.println("\t\tWelcome to DFRS System\t\t");
			System.out.println("\t\t======================\t\t\n");
			System.out.println("Select your role: ");
			System.out.println("1.Passenger");
			System.out.println("2.Manager");
			int menuOption = 0;
			menuOption = scanner.nextInt();
			switch (menuOption) {
			case 1:
				value = passengerClient.Starter(args);
				//isValid = true;
				break;
			case 2:
				value = managerClient.Starter(args);
				////isValid = true;//
				break;
			default:
				System.out.println("Invalid Input.Enter a valid menu option\n\n");
			}
		}
	}

}
