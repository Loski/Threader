package communication;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import protocole.Protocole;
import protocole.ProtocoleCreator;

public class Server implements Communication {
	
	
	public final static int port = 2017;
	private ServerSocket serverSocket;
	private ArrayList<ServiceClient> clients;
    private Session session;
    
    
	public Server(){
		this.clients = new ArrayList<>();
		this.session = new Session(clients, this);
		this.startServer();
	}
	
	synchronized public int getNbClients(){
		return clients.size();
	}
	
	/**
	 * Envoie un message � tous les joueurs
	 * @param message � envoyer
	 */
	public void sendToAll(String message){
		for(ServiceClient sc:this.clients){
			try {
				sc.sendMessage(message);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	
	public void startServer(){	
		Runnable serverTask = new Runnable() {
            @Override
            public void run() {
                try {
                    serverSocket = new ServerSocket(port);
                    System.out.println(serverSocket.getInetAddress());
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
		this.clients.remove(c);
		// Fin session??
		this.deconnexion(c);
	}
	
	synchronized public void addClient(ServiceClient sc){
		this.clients.add(sc);
		bienvenue(sc);
		connecte(sc);
	}
	
	public static void main (String[] args){
		   new Server();
	}
	
	@Override
	public void bienvenue(ServiceClient sc){
		try {
			sc.sendMessage(ProtocoleCreator.create(Protocole.BIENVENUE, this.session.toString()));
			System.out.println("Bienvenue à " + sc.getPseudo());
			if(clients.size() == 1)
				new Thread(this.session).start();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	@Override
	public void deconnexion(ServiceClient sc) {
		// TODO Auto-generated method stub
		this.sendToAll(ProtocoleCreator.create(Protocole.DECONNEXION, sc.getPseudo()));	
	}
	
	@Override
	public void debutSession() {
		this.sendToAll(ProtocoleCreator.create(Protocole.SESSION));
	}

	@Override
	public void vainqueur() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void tour() {
		// TODO Auto-generated method stub
		this.sendToAll(ProtocoleCreator.create(Protocole.TOUR, this.session.getPlateau().toString(), this.session.getTirageCourant()));
	}

	@Override
	public void rValide() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void rInValide() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void rATrouve(ServiceClient sc) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void rFin() {
		// TODO Auto-generated method stub
		this.sendToAll(ProtocoleCreator.create(Protocole.RFIN));
	}

	@Override
	public void sValide() {
		this.sendToAll(ProtocoleCreator.create(Protocole.SVALIDE));
	}

	@Override
	public void sFin() {
		this.sendToAll(ProtocoleCreator.create(Protocole.SFIN));
	}

	@Override
	public void sInvalide() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void bilan() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void refus(ServiceClient sc) {
		try {
			sc.sendMessage(ProtocoleCreator.create(Protocole.REFUS));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	public void connecte(ServiceClient sc) {
		// TODO Auto-generated method stub
		this.sendToAll(ProtocoleCreator.create(Protocole.CONNECTE, sc.getPseudo()));
		
	}

}
