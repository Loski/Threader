package communication;

import java.util.List;
import java.util.Map;

public class Session implements Runnable {

	public final static int TAILLE_PLATEAU = 15;
	public final static int TAILLE_TIRAGE = 7;

	// temps en mlsecondes pour chaque phase
	public final static int PHASE_DE_RECHERCHE = 30 * 1 *1000; //30sec => need 5mn
	public final static int PHASE_DE_SOUMISSION = 30 * 1000; //30 seco >need 2mn
	public final static int PHASE_DE_RESULTAT = 10 * 1000; //10sec
	public final static int STEP_RECHERCHE = 1;
	public final static int STEP_SOUMISSION = 2;
	public final static int STEP_RESULTAT = 3;
	public final static int NO_STEP = 0;

	private Alphabet tirage;
	private PlateauServer plateau;
	private List<ServiceClient> joueurs; // R�f�rence tous les joueurs de la
											// session, actif ou non
	private int tour;
	private Server server;
	private int step_actuel;

	public Session(List<ServiceClient> clients, Server s) {
		this.tirage = new Alphabet(TAILLE_TIRAGE);
		this.plateau = new PlateauServer(TAILLE_PLATEAU);
		this.joueurs = clients;
		this.tour = 1;
		this.server = s;
		this.step_actuel = NO_STEP;
	}

	@Override
	public void run() {
		while (this.joueurs.size() > 0) {
			switch (step_actuel) {
				case NO_STEP:
					this.server.debutSession();
					this.step_actuel = STEP_RECHERCHE;
				break;
				case STEP_RECHERCHE:
					this.server.tour();
					try {
						wait(PHASE_DE_RECHERCHE);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					this.server.rFin();
					this.step_actuel = STEP_SOUMISSION;
					break;
				case STEP_SOUMISSION:
					try {
						Thread.sleep(PHASE_DE_SOUMISSION);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					this.server.sFin();
					this.step_actuel = STEP_RESULTAT;
					break;
				case STEP_RESULTAT:
					this.server.bilan();
					try {
						Thread.sleep(PHASE_DE_RESULTAT);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					tour++;
					this.step_actuel = NO_STEP;
					break;
			default:
				break;
			}
		}
	}

	public Alphabet getTirage() {
		return tirage;
	}

	public void setTirage(Alphabet tirage) {
		this.tirage = tirage;
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
	public String toString(){
		return this.plateau.toString()+"/"+this.getTirageCourant()+"/"+this.getScore();
	}
	public String getTirageCourant() {
		// TODO Auto-generated method stub
		return "AAAAAAA";
	}

}
