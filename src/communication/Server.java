package communication;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.FileHandler;
import java.util.logging.Formatter;
import java.util.logging.Handler;
import java.util.logging.Logger;

import protocole.Protocole;
import protocole.ProtocoleCreator;

public class Server implements Communication {
	
	
	public final static int port = 2017;
	private ServerSocket serverSocket;
	private ArrayList<ServiceClient> clients;
    private Session session;
    public static Logger logger = Logger.getLogger(Server.class.getName());
    public final static Object obj = new Object();
    
	public Server(){
		this.clients = new ArrayList<>();
		this.session = new Session(clients, this);
		Handler fh = null; 
		try {
			fh = new FileHandler("TestLogging.log");
		} catch (SecurityException | IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		logger.addHandler(fh);
		this.startServer();
	}
	
	synchronized public int getNbClients(){
		return clients.size();
	}
	
	/**
	 * Envoie un message à tous les joueurs
	 * @param message à envoyer
	 */
	public void sendToAll(String message){
		for(ServiceClient sc:this.clients){
			sc.sendMessage(message);
		}
	}
	
	
	public void startServer(){	
		Runnable serverTask = new Runnable() {
            @Override
            public void run() {
                try {
                    serverSocket = new ServerSocket(port);
                    logger.config("IP address : "+ serverSocket.getInetAddress());
                    logger.info("Waiting for clients to connect...");
                    while (true) {
                        Socket clientSocket = serverSocket.accept();
                        new Thread(new ServiceClient(clientSocket, Server.this)).start();
                    }
                } catch (IOException e) {
                    logger.severe("Unable to process client request");
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

	synchronized public void removeJoueur(ServiceClient sc) {
		logger.info(sc.getPseudo() + " leaving the server.");
		this.clients.remove(sc);
		// Fin session??
		this.deconnexion(sc);
	}
	
	synchronized public void addClient(ServiceClient sc){
		logger.info(sc.getPseudo() + " entering the server.");
		this.clients.add(sc);
		bienvenue(sc);
		connecte(sc);
	}
	
	public static void main (String[] args){
		   new Server();
	}
	
	@Override
	public void bienvenue(ServiceClient sc){
		if(clients.size() == 1)
			new Thread(this.session).start();
		sc.sendMessage(ProtocoleCreator.create(Protocole.BIENVENUE, this.session.getPlateau().toString(), this.session.getTirageCourant(),
				this.session.getScore(), this.session.getPhaseActuelleString(), String.valueOf(this.session.getTempsRestant())));
		logger.info("Bienvenue à " + sc.getPseudo());
	}
	
	public void sendStateActuel(ServiceClient sc){
		int state_actuel = session.getStep_actuel();
		if(state_actuel == Session.TEMPS_PHASE_DE_RECHERCHE){
			sc.sendMessage(ProtocoleCreator.create(Protocole.TOUR, this.session.getPlateau().toString(), this.session.getTirageCourant()));
		}else if(state_actuel == Session.TEMPS_PHASE_DE_SOUMISSION){
			sc.sendMessage(ProtocoleCreator.create(Protocole.TOUR, this.session.getPlateau().toString(), this.session.getTirageCourant()));
		}else{
			
		}
	}
	@Override
	public void deconnexion(ServiceClient sc) {
		// TODO Auto-generated method stub
		this.sendToAll(ProtocoleCreator.create(Protocole.DECONNEXION, sc.getPseudo()));	
	}
	
	@Override
	public void debutSession() {
		logger.info("Start of new session !");
		this.sendToAll(ProtocoleCreator.create(Protocole.SESSION));
	}

	@Override
	public void vainqueur() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void tour() {
		logger.info("New turn");
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
		logger.info("End of reflexion phase");
		this.sendToAll(ProtocoleCreator.create(Protocole.RFIN));
	}

	@Override
	public void sValide() {
		this.sendToAll(ProtocoleCreator.create(Protocole.SVALIDE));
	}

	@Override
	public void sFin() {
		logger.info("End of soumission phase");
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
		sc.sendMessage(ProtocoleCreator.create(Protocole.REFUS));
	}

	@Override
	public void connecte(ServiceClient sc) {
		// TODO Auto-generated method stub
		this.sendToAll(ProtocoleCreator.create(Protocole.CONNECTE, sc.getPseudo()));
		
	}

}
