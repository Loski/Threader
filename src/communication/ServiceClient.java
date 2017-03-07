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
	private boolean hasQuit; // To stop listening and stop the thread
	private boolean isWaiting;
	
	public ServiceClient(Socket s, Server server) {
		this.socket = s;
		this.server = server;
		this.hasQuit = false;
		
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


		@Override
		public void run() {
			try {
				while(!hasQuit){
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
			 
			if (Protocole.CONNEXION.toString().equals(cmd)) {
				try{
					this.pseudo = msgs[1];
				}catch (Exception e) {
					System.err.println("Aucun pseudonyme fournis.");
				}
			} else if (Protocole.TROUVE.toString().equals(cmd)){
				
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


		public boolean isHasQuit() {
			return hasQuit;
		}


		public void setHasQuit(boolean hasQuit) {
			this.hasQuit = hasQuit;
		}


		public boolean isWaiting() {
			return isWaiting;
		}


		public void setWaiting(boolean isWaiting) {
			this.isWaiting = isWaiting;
		}

}
