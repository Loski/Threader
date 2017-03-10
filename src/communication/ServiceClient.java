package communication;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

import protocole.Protocole;

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
	}
	
	
	 	public void sendMessage(String message) throws IOException{
			this.output.println(message);
			if(this.output.checkError()){
				throw new IOException("erreur d'envoie de donnée pour "+this.pseudo);
			}
		}

		@Override
		public void run() {
			try {
				while(isConnected){
					readFromJoueur();
				}
			} catch (Exception e) {
				System.out.println("(Joueur run) Exception : "+e.toString());
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
