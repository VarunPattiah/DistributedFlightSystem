package server;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.List;



public class LeaderElection extends Thread{
	
	private List<String> activeProcesses = null; 
	
	public LeaderElection(List<String> activeProcesses){
		this.activeProcesses = activeProcesses;
	}
	
	@Override
	public void run(){
		electLeader();
		
	}

	/**
	 * choose the process with the lowest process id among the active replicas
	 */
	private void electLeader() {
		int minProcessID = configuration.MAX_REPLICA;
		if(activeProcesses.size() != 0){
			for(String process: activeProcesses){
				 int processId = Integer.parseInt(process.split(":")[0]);
				 if(processId < minProcessID)
					minProcessID = processId;
			}
		}
		
		getAddress(minProcessID);
	}

	/**
	 * method for getting address of front end
	 * @param process
	 */
	private void getAddress(int process) {
		String address = "LCHANGED";
		if(activeProcesses.size() != 0){
			for(String proc: activeProcesses){
				 String[] leaderInfo = proc.split(":");
				 if(process == Integer.parseInt(leaderInfo[0])){
					 address += ":"+leaderInfo[1];
					 break;
				 }	 
			}
		}
		notifyFrontEnd(address);
		notifyActiveReplicas(process);
	}

	/**
	 * After election notify to other active replicas about new group leader
	 * @param process
	 */
	private void notifyActiveReplicas(int process) {
		new MulticastMessageSender().multicastRequest("LE:"+process, configuration.MC_FD_PORT3);
		System.out.println("New Group Leader Replica: "+process);
		new MainServer().setGroupLeaderProcessID(process);
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
			packet = new DatagramPacket(bytes, bytes.length, InetAddress.getByName(new MainServer().getFrontEndAddress()), configuration.FE_NOTIFY_PORT);
			socket.send(packet);
		}catch(Exception e){
			System.out.println("IO:"+e.getMessage());
		}finally{
			if(null != socket)
				socket.close();
		}
		
	}

	/**
	 * @return the activeProcesses
	 */
	public List<String> getActiveProcesses() {
		return activeProcesses;
	}

	/**
	 * @param activeProcesses the activeProcesses to set
	 */
	public void setActiveProcesses(List<String> activeProcesses) {
		this.activeProcesses = activeProcesses;
	}
	
}
