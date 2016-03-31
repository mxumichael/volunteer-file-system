package indexServer;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;

import lib.NetworkAddress;
import lib.Packet;
import lib.SenderReceiver;

public class Server extends Thread implements Runnable{
	
	protected Socket clientSocket = null;
	protected static int serverPort = 65423; // changed by Vicky
	static Map<String, Integer> globalSet = new HashMap<String, Integer>();
	private Thread runningThread = null;

	Server(Socket socketToClient){
		clientSocket = socketToClient;
	}
	
	@Override
	public void run() {
		// Exchange messages with the client to share data.
		while(true) {
			//Receive Payload sent from the client
			Packet payloadFromClient = new Packet(new SenderReceiver().receiveMessageViaTCPOn(clientSocket));
			String[] clientInformation = payloadFromClient.getData().split(":");
			
			String clientIPaddress = clientInformation[0];
			int clientPortNumber = Integer.parseInt(clientInformation[1]);
			//Server should have the updated hashmap of portnumbers and ip addresses at this point 
			addToHash(clientIPaddress, clientPortNumber);
			viewCurrentHash();
			
			//Send current list of active peers to requestng client
			
		}
		
		
	}
	
	public void addToHash(String clientIPaddress, int clientPortNumber){
		globalSet.put(clientIPaddress, clientPortNumber);
	}
	
	public void emptyHash(){
		
	}
	
	public void updateHash() {
		
	}
	
	public void viewCurrentHash(){
		
		for (Map.Entry<String, Integer> entry : globalSet.entrySet()) {
		    System.out.println(entry.getKey()+" : "+entry.getValue());
		}
	}
	
	public static void main(String[] args) {
		
		
		try (ServerSocket ssock = new ServerSocket(serverPort)) { 
			
			Socket connectionSocket;
			//Get IP Address of 'enp0s8'
			String ipAddress = NetworkAddress.getIPAddress("enp0s8");
			//String ipAddress = "192.168.0.36"; //hardcoded for mac
			// Upload IPAddress:Port number at http://www4.ncsu.edu/~vpkatara/server.txt
			new UploadToServer().upload(ipAddress, serverPort);
			
			System.out.println("Server Started!! at "+ipAddress+":"+serverPort);
			
            while (true) {
            	Socket sock = ssock.accept();
				new Thread(new Server(sock)).start();
	        }
	    } catch (IOException e) {
            System.err.println("Could not listen on port " + serverPort);
            System.exit(-1);
        }
		
	}
}
