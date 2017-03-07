package communication;

public class Session {
	
	public final static int taille_plateau = 15;
	public final static int taille_tirage = 7;
	
	
	//temps en secondes pour chaque phase
	public final static int phase_de_recherche = 5 * 60;
	public final static int phase_de_soumission = 2 * 60;
	public final static int phase_de_resultat = 10;
	
	
	private Alphabet tirage;
	private PlateauServer plateau;
	private Map<String, Joueur> mapPseudo_Joueur; // Référence tous les joueurs de la session, actif ou non
	
	public Session() {
		this.tirage = new Alphabet(taille_tirage);
		this.plateau = new PlateauServer(taille_plateau);
	}

}
