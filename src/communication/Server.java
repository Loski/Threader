package communication;

import java.awt.Point;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.logging.FileHandler;
import java.util.logging.Handler;
import java.util.logging.Logger;

import protocole.Protocole;
import protocole.ProtocoleCreator;

public class Server implements Communication {
	
    public static final int LANGUE_FR = 0;
    public static final int LANGUE_EN = 1;
	public final static int port = 2017;
	private ServerSocket serverSocket;
	private ArrayList<ServiceClient> clients;
    private Session session;
    public static Logger logger = Logger.getLogger(Server.class.getName());
    public final static Object obj = new Object();
    public  int langue;

	public Server(String args){
		if(args.equals("en")){
			langue = LANGUE_EN;
		}else{
			langue = LANGUE_FR;
		}
		this.clients = new ArrayList<>();
		Handler fh = null; 
		try {
			fh = new FileHandler("Logging.xml");
		} catch (SecurityException | IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		logger.addHandler(fh);
		this.session = new Session(clients, this, langue);
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
	public String langueToString(){
		if(langue == LANGUE_EN)
			return "EN";
		return "FR";
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
		if(this.clients.remove(sc)){
			System.out.println("Remove");
		}else{
			System.out.println("bad remove");
		}
		if(clients.size() < 1){
			synchronized (obj) {
				obj.notifyAll();
			}
			return;
		}
			
		if(sc == session.getPlateau().getMeilleur_joueur()){
			session.findNewBestPlayer();
			if(session.getPlateau().getMeilleur_joueur() != null){
				meilleurMot(session.getPlateau().getMeilleur_joueur());
			}
		}
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
		if(args.length > 0)
		   new Server(args[0]);
		else
			new Server("fr");
	}
	
	@Override
	public synchronized void bienvenue(ServiceClient sc){
		if(clients.size() == 1)
			new Thread(this.session).start();
		sc.sendMessage(ProtocoleCreator.create(Protocole.BIENVENUE, this.session.getPlateau().toString(), this.session.getTirageCourant(),
				this.session.getScore(), this.session.getPhaseActuelleString(), String.valueOf(this.session.getTempsRestant()), this.langueToString()));
		logger.info("Bienvenue à " + sc.getPseudo());
	}
	
    
	public int getLangue() {
		return langue;
	}

	public void setLangue(int langue) {
		this.langue = langue;
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
		newBest.sendMessage(Protocole.MEILLEUR, "1");
	}

	public void gestionFinDeTour() {
		for(ServiceClient sc: clients){
			sc.ajouterScore();
		}
		if(session.getListe_letters().canTirage()){
			try {
				ServiceClient best_player = session.getPlateau().getMeilleur_joueur();
				if(best_player!=null)
				{
					Plateau best = best_player.getPlateau();
					ArrayList<Point> pts = (ArrayList<Point>) session.getPlateau().getLettresJouees(best);
					for(Point pt: pts){
						session.viderTirage(session.getPlateau().getMeilleur_joueur().getPlateau().plateau[(int) pt.getX()][(int) pt.getY()]);
					}
				}
			} catch (ExceptionPlateau e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			session.majLetter();
		}else{
			session.setSession_over(true);
		}

		
		
	}

	public void resetPlayer() {
		session.getPlateau().reset();
		for(ServiceClient sc: this.clients){
			sc.setPlateau(new Plateau(session.getPlateau()));
		}
		
	}

	public void endSession() {
		logger.info("End of session");
		vainqueur();
		session.reset();
		if(clients.size() > 1)
			new Thread(session).start();
		
	}

	public boolean isPseudoDisponible(String string) {
		for(ServiceClient sc: clients){
			if(sc.getPseudo().equals(string))
				return false;
		}
		return true;
	}

}
