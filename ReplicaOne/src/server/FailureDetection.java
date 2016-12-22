package server;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

public class FailureDetection extends Thread {

	/**
	 * @author VarunPattiah
	 * start failure detection thread 
	 */
	@Override
	public void run() {
		new MulticastMessageReceiver(Constants.MC_FD_PORT2);
		new MulticastMessageReceiver(Constants.MC_FD_PORT3);
		new DFRSServer().setGroupLeaderProcessID(Constants.PROCESS_ID);
		while(true){
			List<String> activeProcesses = new MulticastMessageSender().multicastRequest("FD", Constants.MC_FD_PORT1);
			int currentGroupLeader = new DFRSServer().getGroupLeaderProcessID();
			System.out.println("Current FD-Replica1 GroupLeader: "+currentGroupLeader);
			System.out.println("activeProcesses.size() : "+activeProcesses.size());
			for(String str:activeProcesses)
				System.out.println(str);
			
			if (activeProcesses.size() == 0) {
				try {
					notifyFrontEnd(InetAddress.getLocalHost().getHostAddress());
				} catch (UnknownHostException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}else{
				List<Integer> processes =  new ArrayList<Integer>();
				for(String process: activeProcesses){
					processes.add(Integer.parseInt(process.split(":")[0]));
				}
				
				if(!processes.contains(currentGroupLeader)){
					System.out.println("Leader Election System started...");
					new LeaderElection(activeProcesses).start();
				}
				else{
					new MulticastMessageSender().multicastRequest("LE:"+Constants.PROCESS_ID, Constants.MC_FD_PORT1);
					new DFRSServer().setGroupLeaderProcessID(Constants.PROCESS_ID);
					try {
						notifyFrontEnd("LCHANGED:"+InetAddress.getLocalHost().getHostAddress());
					} catch (UnknownHostException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
			try {
				Thread.currentThread().sleep(5000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}	
	}

	/**
	 * method which notifies the front end about new group leader
	 * @param address
	 */
	private void notifyFrontEnd(String address) {
		DatagramSocket socket = null;
		DatagramPacket packet = null;
		try{
			socket = new DatagramSocket();
			byte[] bytes = address.getBytes();
			packet = new DatagramPacket(bytes, bytes.length, InetAddress.getByName(new DFRSServer().getFrontEndAddress()), Constants.FE_NOTIFY_PORT);
			socket.send(packet);
		}catch(Exception e){
			System.out.println("IO:"+e.getMessage());
		}finally{
			if(null != socket)
				socket.close();
		}
		
	}
	
	

}
