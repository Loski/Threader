package communication;

import java.util.List;
import java.util.Map;

public class Session implements Runnable {

	public final static int TAILLE_PLATEAU = 15;
	public final static int TAILLE_TIRAGE = 7;

	// temps en mlsecondes pour chaque phase
	public final static int TEMPS_PHASE_DE_RECHERCHE = 3000 * 1 *10000; //30sec => need 5mn
	public final static int TEMPS_PHASE_DE_SOUMISSION = 30 * 1000; //30 seco >need 2mn
	public final static int TEMPS_PHASE_DE_RESULTAT = 10 * 1000; //10sec
	public final static int STEP_RECHERCHE = 1;
	public final static int STEP_SOUMISSION = 2;
	public final static int STEP_RESULTAT = 3;
	public final static int STEP_SESSION = 0;


	private Alphabet liste_letters;
	private PlateauServer plateau;
	private List<ServiceClient> joueurs; // R�f�rence tous les joueurs de la
											// session, actif ou non
	private int tour;
	private Server server;
	private int step_actuel;
	
	private long debut_phase;


	public Session(List<ServiceClient> clients, Server s) {
		this.liste_letters = new Alphabet(TAILLE_TIRAGE);
		this.plateau = new PlateauServer(TAILLE_PLATEAU);
		this.joueurs = clients;
		this.tour = 1;
		this.server = s;
		this.step_actuel = STEP_SESSION;
		this.debut_phase = -1;
	}

	@Override
	public void run() {
		while (this.joueurs.size() > 0) {
			switch (step_actuel) {
				case STEP_SESSION:
					this.step_actuel = STEP_RECHERCHE;
					try {
						Thread.sleep(5*1000);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					this.server.debutSession();
					this.debut_phase = System.currentTimeMillis();
				break;
				case STEP_RECHERCHE:
					this.server.tour();
					this.debut_phase = System.currentTimeMillis();
					synchronized(Server.obj){
						try {
							Server.obj.wait(TEMPS_PHASE_DE_RECHERCHE);
						} catch (InterruptedException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
					this.server.rFin();
					System.out.println("Notify");
					this.step_actuel = STEP_SOUMISSION;
					break;
				case STEP_SOUMISSION:
					this.debut_phase = System.currentTimeMillis();
					try {
						Thread.sleep(TEMPS_PHASE_DE_SOUMISSION);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					this.server.sFin();
					this.step_actuel = STEP_RESULTAT;
					break;
				case STEP_RESULTAT:
					this.debut_phase = System.currentTimeMillis();
					this.server.bilan();
					try {
						Thread.sleep(TEMPS_PHASE_DE_RESULTAT);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					tour++;
					this.step_actuel = STEP_SESSION;
					break;
			default:
				break;
			}
		}
	}
	public Alphabet getListe_letters() {
		return liste_letters;
	}

	public void setListe_letters(Alphabet tirage) {
		this.liste_letters = tirage;
	}

	public PlateauServer getPlateau() {
		return plateau;
	}

	public void setPlateau(PlateauServer plateau) {
		this.plateau = plateau;
	}

	public List<ServiceClient> getJoueurs() {
		return joueurs;
	}

	public void setJoueurs(List<ServiceClient> joueurs) {
		this.joueurs = joueurs;
	}

	public String getScore() {
		// TODO Auto-generated method stub
		String s = "" + tour + "*";
		for (ServiceClient sc : this.joueurs) {
			s += sc.getPseudo() + "*" + sc.getScore() + "*";
		}
		return s;
	}
	public int getStep_actuel() {
		return step_actuel;
	}

	public void setStep_actuel(int step_actuel) {
		this.step_actuel = step_actuel;
	}

	public String toString(){
		return this.plateau.toString()+"/"+this.getTirageCourant()+"/"+this.getScore()+"/"+getPhaseActuelleString()+"/"+getTempsRestant() ;
	}
	public String getTirageCourant() {
		String str = "";
		for(Letter c : this.liste_letters.tirage()){
			str += c.getLetter();
		}
		return str;
	}
	
	public String getPhaseActuelleString(){
		if(this.step_actuel == STEP_RECHERCHE)
			return "REC";
		else if(this.step_actuel == STEP_RESULTAT)
			return "RES";
		else if(this.step_actuel == STEP_SOUMISSION)
			return "SOU";
		return "DEB";
	}
	
	public long getTempsRestant(){
		long time = 0;
		switch (step_actuel) {
		case STEP_RECHERCHE:
			time = TEMPS_PHASE_DE_RECHERCHE;
			break;
		case STEP_SOUMISSION:
			time = TEMPS_PHASE_DE_SOUMISSION;
		case STEP_RESULTAT:
			time = TEMPS_PHASE_DE_RESULTAT;
		case STEP_SESSION:
			time = 0;
		default:
			break;
		}
		return (time - (this.debut_phase - System.currentTimeMillis()))/1000;
	}

}
