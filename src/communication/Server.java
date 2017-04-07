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
	
	public void sendToAllWhithoutHim(String message, ServiceClient notHim){
		for(ServiceClient sc:this.clients){
			if(sc == notHim)
				continue;
			sc.sendMessage(message);
		}
	}
	private void sendToAllWhithoutHim(Protocole p, ServiceClient notHim, String message) {
		sendToAllWhithoutHim(ProtocoleCreator.create(p, message),notHim) ;
		
	}
	public void sendToHim(Protocole p, String message, String name){
		for(ServiceClient sc: this.clients){
			if(sc.getPseudo().equals(name)){
				sc.sendMessage(p ,message);
			}
		}
	}
	
	public void sendToAll(Protocole p, String message){
		this.sendToAll(ProtocoleCreator.create(p, message));
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
	
	@Override
	public void deconnexion(ServiceClient sc) {
		// TODO Auto-generated method stub
		this.sendToAll(ProtocoleCreator.create(Protocole.DECONNEXION, sc.getPseudo()));	
	}
	
	@Override
	public void debutSession() {
		logger.info("Start of new session !");
		this.sendToAll(ProtocoleCreator.create(Protocole.SESSION));
		for(ServiceClient sc : this.clients){
			sc.reset();
		}
	}

	@Override
	public void vainqueur() {
		sendToAll(Protocole.VAINQUEUR, session.getScore());
	}

	@Override
	public void tour() {
		logger.info("New turn");
		this.sendToAll(ProtocoleCreator.create(Protocole.TOUR, this.session.getPlateau().toString(), this.session.getTirageCourant()));
	}

	@Override
	public void rValide(ServiceClient sc) {
		sc.sendMessage(ProtocoleCreator.create(Protocole.RVALIDE));
		rATrouve(sc);
		logger.info(sc.getPseudo() + " est le premier a avoir trouvé un mot valide !");
		synchronized (obj) {
			obj.notifyAll();
		}
	}

	@Override
	public void rInValide(ServiceClient sc, String message) {
		sc.sendMessage(ProtocoleCreator.create(Protocole.RINVALIDE, message));
	}

	@Override
	public void rATrouve(ServiceClient sc) {
		this.sendToAll(Protocole.RATROUVE, sc.getPseudo());	
	}
	public synchronized void validation(ServiceClient sc){
		if(session.getStep_actuel() == Session.STEP_RECHERCHE){
			rValide(sc);
		}else{
			sValide(sc);
		}
	}
	
	public void invalidation(ServiceClient sc, String message){
		if(session.getStep_actuel() == Session.STEP_RECHERCHE){
			rInValide(sc, message);
		}else{
			sInValide(sc, message);
		}
	}
	@Override
	public void rFin() {
		logger.info("End of reflexion phase");
		this.sendToAll(ProtocoleCreator.create(Protocole.RFIN));
	}

	@Override
	public void sValide(ServiceClient sc) {
		sc.sendMessage(ProtocoleCreator.create(Protocole.SVALIDE));
	}

	@Override
	public void sFin() {
		logger.info("End of soumission phase");
		this.sendToAll(ProtocoleCreator.create(Protocole.SFIN));
	}

	@Override
	public void sInValide(ServiceClient sc, String message) {
		sc.sendMessage(ProtocoleCreator.create(Protocole.SINVALIDE, message));
	}
	
	@Override
	public void bilan() {
		logger.info("Bilan du tour" + this.getSession().getScore());
		String message =  this.session.getPlateau().getVainqueur();
		this.sendToAll(Protocole.BILAN, message+this.getSession().getScore());
	}

	@Override
	public void refus(ServiceClient sc) {
		sc.sendMessage(ProtocoleCreator.create(Protocole.REFUS));
	}

	@Override
	public void connecte(ServiceClient sc) {
		this.sendToAllWhithoutHim(ProtocoleCreator.create(Protocole.CONNECTE, sc.getPseudo()), sc);		
	}
	

	public ServerSocket getServerSocket() {
		return serverSocket;
	}

	public void setServerSocket(ServerSocket serverSocket) {
		this.serverSocket = serverSocket;
	}

	public ArrayList<ServiceClient> getClients() {
		return clients;
	}

	public void setClients(ArrayList<ServiceClient> clients) {
		this.clients = clients;
	}

	public Session getSession() {
		return session;
	}

	public void setSession(Session session) {
		this.session = session;
	}

	@Override
	public void meilleurMot(ServiceClient meilleur) {
		meilleur.sendMessage(Protocole.MEILLEUR, "1");
		this.sendToAllWhithoutHim(Protocole.MEILLEUR, meilleur, "0");
		
	}



	@Override
	public void ancienMeilleur(ServiceClient oldBest, ServiceClient newBest) {
		oldBest.sendMessage(Protocole.MEILLEUR, "0");
		newBest.sendMessage(Protocole.MEILLEUR, "0");
	}

	public void gestionFinDeTour() {
		
		session.getPlateau().reset();
		for(ServiceClient sc: this.clients){
			sc.ajouterScore();
			sc.setPlateau(new Plateau(session.getPlateau()));
		}
		
	}

}
