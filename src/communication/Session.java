package communication;

import java.util.List;
import java.util.Map;

public class Session implements Runnable {

	public final static int TAILLE_PLATEAU = 15;
	public final static int TAILLE_TIRAGE = 7;

	// temps en mlsecondes pour chaque phase
	public final static int TEMPS_PHASE_DE_RECHERCHE = 60 * 5 * 1000; //30sec => need 5mn
	public final static int TEMPS_PHASE_DE_SOUMISSION = 50 * 1000; //30 seco >need 2mn
	public final static int TEMPS_PHASE_DE_RESULTAT = 10 * 100; //10sec
	public final static int STEP_RECHERCHE = 1;
	public final static int STEP_SOUMISSION = 2;
	public final static int STEP_RESULTAT = 3;
	public final static int STEP_SESSION = 0;


	private Alphabet liste_letters;
	private PlateauServer plateau;
	private List<ServiceClient> joueurs; // R�f�rence tous les joueurs de la
	VerificationMot verification;										// session, actif ou non
	private int tour;
	private Server server;
	private int step_actuel;
	private boolean session_lancer, session_over;
	private long debut_phase;


	public Session(List<ServiceClient> clients, Server s, int langue) {
		this.liste_letters = new Alphabet(TAILLE_TIRAGE);
		this.plateau = new PlateauServer(TAILLE_PLATEAU);
		this.joueurs = clients;
		this.tour = 0;
		this.server = s;
		this.step_actuel = STEP_SESSION;
		this.debut_phase = -1;
		this.session_lancer = true;
		session_over = false;
		if(langue == Server.LANGUE_EN){
			verification = new VerificationEN();
		}else{
			verification = new VerificationFR();
		}
	}

	@Override
	public void run() {
		while (this.joueurs.size() > 0 && !session_over) {
			switch (step_actuel) {
				case STEP_SESSION:
					synchronized(Server.obj){
						try {
							Server.obj.wait(TEMPS_PHASE_DE_RESULTAT);
						} catch (InterruptedException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
					if(session_lancer){
						this.server.debutSession();
						session_lancer = false;
					}
					this.step_actuel = STEP_RECHERCHE;
					this.debut_phase = System.currentTimeMillis();
				break;
				case STEP_RECHERCHE:
					tour++;
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
					this.step_actuel = STEP_SOUMISSION;
					break;
				case STEP_SOUMISSION:
					this.debut_phase = System.currentTimeMillis();
					synchronized(Server.obj){
						try {
							Server.obj.wait(TEMPS_PHASE_DE_SOUMISSION);
						} catch (InterruptedException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
					this.server.sFin();
					this.step_actuel = STEP_RESULTAT;
					break;
				case STEP_RESULTAT:
					this.debut_phase = System.currentTimeMillis();
					server.gestionFinDeTour();
					this.server.bilan();
					if(plateau.hadWiner())
						setPlateau(new PlateauServer(plateau.getMeilleur_joueur().getPlateau()));
					server.resetPlayer();
					this.step_actuel = STEP_SESSION;
					break;
			default:
				break;
			}
		}
		server.endSession();
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
		if(this.step_actuel  == STEP_SESSION || this.step_actuel  == STEP_SOUMISSION)
			return str;
		for(Letter c : this.liste_letters.tirage()){
			str += c.getLetter();
		}
		return str;
	}
	
	public int getTour()
	{
		return this.tour;
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
	
	public boolean isSession_over() {
		return session_over;
	}

	public void setSession_over(boolean session_over) {
		this.session_over = session_over;
	}

	public int getTempsRestant(){
		long time = 0;
		switch (step_actuel) {
		case STEP_RECHERCHE:
			time = TEMPS_PHASE_DE_RECHERCHE;
			break;
		case STEP_SOUMISSION:
			time = TEMPS_PHASE_DE_SOUMISSION;
			break;
		case STEP_RESULTAT:
			time = TEMPS_PHASE_DE_RESULTAT;
			break;
		case STEP_SESSION:
			time = 5 * 1000;
			break;
		default:
			return 0;
		}
		
		if(this.debut_phase==-1 ||time < 0)
			this.debut_phase=System.currentTimeMillis();
		
		int restant = (int) (time - (System.currentTimeMillis() - this.debut_phase))/1000;
		
		return restant;
	}

	public boolean verificationSessionForTrouve() {
		if(step_actuel == STEP_RECHERCHE ||  step_actuel == STEP_SOUMISSION)
			return true;
		return false;
		
	}

	public void majLetter() {
		liste_letters.majLetter();	
	}

	public void viderTirage(char c) {
		liste_letters.delete(c);
	}

	public void findNewBestPlayer() {
		int score = 0;
		ServiceClient best = null;
		for(ServiceClient sc: joueurs){
			int tmp = sc.getPlateau().getScore();
			if(tmp > score){
				best = sc;
			}
		}
		plateau.setMeilleur_joueur(best);
		plateau.setScore(score);
		
	}

	public void reset() {
		plateau = new PlateauServer(15);
		session_lancer = true;
		session_over = false;
		tour = 0;
		step_actuel = STEP_SESSION;
		debut_phase = -1;
		liste_letters = new Alphabet(TAILLE_TIRAGE);
		for(ServiceClient sc: joueurs){
			sc.setScore(0);
		}
		
	}

	public boolean isRealWord(String s) {
		return verification.isRealWord(s);
	}

}
