package communication;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

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
	private Plateau plateau_courant;
	
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
		this.plateau_courant = new Plateau(server.getSession().getPlateau()); 
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
					if(!this.isAuthentified){
						this.pseudo = msgs[1];
						this.isAuthentified = true;
						System.out.println("Connexion de " + this.pseudo);
						this.server.addClient(this);
					}else{
						System.out.println("Tentative de reconnexion" + this.pseudo);
					}
				}catch (Exception e) {
					System.err.println("Aucun pseudonyme fournis.");
				}
			}else if(Protocole.SORT.name().equals(cmd) && isAuthentified){
				isConnected = false;
				this.server.removeJoueur(this);
			}
			else if (Protocole.TROUVE.name().equals(cmd) && isAuthentified){
				if(msgs.length > 1){
					try {
						this.server.getSession().getPlateau().gestionPlacement(new Plateau(msgs[1]));
					} catch (ExceptionPlateau e) {
						server.rInValide(this, e.getCode_erreur() + e.getMessage());
						e.printStackTrace();
					}
				}
			}else if(Protocole.PENVOIE.name().equals(cmd) && isAuthentified){
				if(msgs.length > 2){
					this.server.sendToHim(Protocole.PRECEPTION, msgs[1], msgs[2]);
				}
			}else if(Protocole.ENVOIE.name().equals(cmd) && isAuthentified){
				if(msgs.length > 1){
					this.server.sendToAll(Protocole.RECEPTION, msgs[1]);
				}
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


		public boolean isWaiting() {
			return isWaiting;
		}


		public void setWaiting(boolean isWaiting) {
			this.isWaiting = isWaiting;
		}

}
