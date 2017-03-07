package communication;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Server {
	
	
	public final static int port = 2017;
	private ServerSocket serverSocket;
	private ArrayList<ServiceClient> client;
    private ExecutorService pool;
    
    
	public Server(){
		this.client = new ArrayList<>();
		this.pool = Executors.newFixedThreadPool(4); 
		this.startServer();
	}
	
	synchronized public int getNbClients(){
		return client.size();
	}
	
	
	public void startServer(){
		
		Runnable serverTask = new Runnable() {
            @Override
            public void run() {
                try {
                    serverSocket = new ServerSocket(port);
                    System.out.println("Waiting for clients to connect...");
                    while (true) {
                        Socket clientSocket = serverSocket.accept();
                        new Thread(new ServiceClient(clientSocket, Server.this)).start();
                    }
                } catch (IOException e) {
                    System.err.println("Unable to process client request");
                    e.printStackTrace();
                }finally {
        			if(serverSocket!=null && !serverSocket.isClosed()){
        				try {
        					serverSocket.close();
        				} catch (IOException e) {
        					e.printStackTrace();
        				}
        			}
        }
            }
        };
        Thread serverThread = new Thread(serverTask);
        serverThread.start();
	}

	synchronized public void removeJoueur(ServiceClient c) {
		this.client.remove(c);
	}
	
	synchronized public void addClient(ServiceClient c){
		this.client.add(c);
	}
}
