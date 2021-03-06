package communication;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

import protocole.Protocole;
import protocole.ProtocoleCreator;

public class ServiceClient implements Runnable{

	private Socket socket;
	private String pseudo;
	private int score;
	private PrintWriter output;
	private BufferedReader input;
	private Server server;
	private boolean isConnected; // To stop listening and stop the thread
	private boolean isWaiting;
	private boolean isAuthentified;
	private Plateau plateau;
	
	public ServiceClient(Socket s, Server server) {
		
		this.socket = s;
		this.server = server;
		this.isConnected = true;
		this.isAuthentified = false;
		
		try {
			output = new PrintWriter(socket.getOutputStream());
		} catch (IOException e) {
			System.out.println("(Joueur) : Obtiention outputStream de "+pseudo+" impossible.");
		}
		try {
			input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
		} catch (IOException e) {
			System.out.println("(Joueur) : Obtiention inputStream de "+pseudo+" impossible.");
		}
		this.setPlateau(new Plateau(server.getSession().getPlateau())); 
	}
	
	
	 	public void sendMessage(String message){
			this.output.println(message);
			System.out.println(message + " to : \t" + this.getPseudo());
			if(this.output.checkError()){
				isConnected = false;
			}
		}
	 	
		public void sendMessage(Protocole p, String message) {
			this.sendMessage(ProtocoleCreator.create(p, message));
		}

		@Override
		public void run() {
			try {
				while(isConnected){
					readFromJoueur();
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
}


		private void readFromJoueur() {
			String msg ="";
			while(msg.isEmpty() || !msg.contains("/")){
				try {
					msg = input.readLine();
				} catch (IOException e) {
					e.printStackTrace();
				}
				if(msg==null) msg = "";
			
			}
			
			System.out.println("(SERVER) ReadFromJoueur reçoit : "+ msg);
			
			String[] msgs = msg.split("/");
			String cmd = msgs[0];
			 
			if (Protocole.CONNEXION.name().equals(cmd)) {
				try{
					if(msgs[1] == null)
						throw new Exception();
					else if(!server.isPseudoDisponible(msgs[1])){
						Server.logger.warning("Pseudonyme déjà utilisé" + msgs[1]);
						server.refus(this);
						return;
					}
					if(!this.isAuthentified){
						this.pseudo = msgs[1];
						this.pseudo = this.pseudo.replace(' ', '_');
						this.isAuthentified = true;
						this.server.addClient(this);
					}else{
						Server.logger.warning("Tentative de reconnexion" + this.pseudo);
						server.refus(this);
						this.isConnected = false;
					}
				}catch (Exception e) {
					Server.logger.warning("Aucun pseudonyme fournis" + this.pseudo);
					this.isConnected = false;
					server.refus(this);
				}
			}else if(Protocole.SORT.name().equals(cmd) && isAuthentified){
				isConnected = false;
				this.server.removeJoueur(this);
			}
			else if (Protocole.TROUVE.name().equals(cmd) && isAuthentified){
				if(msgs.length > 1){
					try {
						if(!server.getSession().verificationSessionForTrouve())
							throw new ExceptionPlateau("Not in the good phase","INV");
						Plateau plateau_tmp = new Plateau(msgs[1]);

						
						List<String> str = new ArrayList<String>();
						
						if(this.server.getSession().getPlateau().isEmpty())
						{
							str = this.server.getSession().getPlateau().getWordOfFirstTurn(plateau_tmp);
						}
						else
						{
							str = this.server.getSession().getPlateau().verificationMots(plateau_tmp);
						}

						gestionPlateauValide(str, plateau_tmp);
					} catch (ExceptionPlateau e) {
						server.invalidation(this, e.getCode_erreur() + e.getMessage());
						e.printStackTrace();
					}
				}
			}else if(Protocole.PENVOIE.name().equals(cmd) && isAuthentified){
				if(msgs.length > 2){
					this.server.sendToHim(Protocole.PRECEPTION, msgs[2]+"/"+msgs[1], msgs[1]);
				}
			}else if(Protocole.ENVOIE.name().equals(cmd) && isAuthentified){
				if(msgs.length > 1){
					this.server.sendToAll(Protocole.RECEPTION, msgs[1]);
				}
			}

		}
		
		public void gestionPlateauValide(List<String> str, Plateau plateau_tmp) throws ExceptionPlateau{
			for(String s: str){
				if(!server.getSession().isRealWord(s)){
					throw new ExceptionPlateau("Mot non présent dans la langue" + server.langueToString() + " : " + s, "DIC");
				}
			}
			int score = plateau_tmp.calculScore(str, server.getSession().getListe_letters());
			if(score > plateau.getScore()){
				server.validation(this);
				plateau_tmp.setScore(score);
				plateau_tmp.setMy_word(str);
				plateau = new Plateau(plateau_tmp);
				ServiceClient old = server.getSession().getPlateau().getMeilleur_joueur();
				switch (server.getSession().getPlateau().askSwitch(this)) {
				case Plateau.NOTIFY_ALL:
					this.server.meilleurMot(this);
					break;
				case Plateau.NOTIFY_ONLY_OLD:
					this.server.ancienMeilleur(old, this);
				default:
					break;
				}
			}else{
				throw new ExceptionPlateau("Moins bon score", "INF");
			}
		}

		public Socket getSocket() {
			return socket;
		}


		public void setSocket(Socket socket) {
			this.socket = socket;
		}


		public String getPseudo() {
			return pseudo;
		}


		public void setPseudo(String pseudo) {
			this.pseudo = pseudo;
		}


		public int getScore() {
			return score;
		}


		public void setScore(int score) {
			this.score = score;
		}


		public PrintWriter getOutput() {
			return output;
		}


		public void setOutput(PrintWriter output) {
			this.output = output;
		}


		public BufferedReader getInput() {
			return input;
		}


		public void setInput(BufferedReader input) {
			this.input = input;
		}


		public Server getServer() {
			return server;
		}


		public void setServer(Server server) {
			this.server = server;
		}
		
		public void reset() {
			this.setPlateau(new Plateau(15));
			this.score = 0;
		}

		public Plateau getPlateau() {
			return plateau;
		}


		public void setPlateau(Plateau plateau) {
			this.plateau = plateau;
		}


		public void ajouterScore() {
			score+=plateau.getScore();
		}

}
