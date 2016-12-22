package front;

import java.io.FileNotFoundException;
import java.io.PrintWriter;

import org.omg.CORBA.ORB;
import org.omg.CORBA.ORBPackage.InvalidName;
import org.omg.PortableServer.POA;
import org.omg.PortableServer.POAHelper;
import org.omg.PortableServer.POAManagerPackage.AdapterInactive;
import org.omg.PortableServer.POAPackage.ObjectNotActive;
import org.omg.PortableServer.POAPackage.ServantAlreadyActive;
import org.omg.PortableServer.POAPackage.WrongPolicy;

public class FrontEnd {
	/**
	 * start front end server
	 * @param args
	 * @throws InvalidName
	 * @throws ServantAlreadyActive
	 * @throws WrongPolicy
	 * @throws ObjectNotActive
	 * @throws FileNotFoundException
	 * @throws AdapterInactive
	 */
	public static void main(String[] args) throws InvalidName, ServantAlreadyActive, WrongPolicy, ObjectNotActive, FileNotFoundException, AdapterInactive{
		
		org.omg.CORBA.ORB orb = ORB.init(args, null);
		POA rootPOA = POAHelper.narrow(orb.resolve_initial_references("RootPOA"));
		
		FrontEndInterfaceImpl frontEndInterfaceImpl = new FrontEndInterfaceImpl();
		byte[] id = rootPOA.activate_object(frontEndInterfaceImpl);
		org.omg.CORBA.Object ref = rootPOA.id_to_reference(id);
		
		String ior = orb.object_to_string(ref);
		System.out.println(ior);
		
		//write contact details to file
		PrintWriter file = new PrintWriter("FrontEnd.txt");
		file.println(ior);
		file.close();
		
		System.out.println("Front End Server Started...");

		new LeaderNotifyThread().start();
		
		rootPOA.the_POAManager().activate();
		orb.run();
		
	}

	

	
}
