package communication;

public interface Communication {
	
	// For all clients
	/**
	 * Annonce à tous les joueurs que sc vient de se connecter
	 * @param sc
	 */
	public void connecte(ServiceClient sc);
	/**
	 * Annonce à tous les joueurs que sc vient de se déconnecter
	 * @param sc
	 */
	public void deconnexion(ServiceClient sc);
	
	/**
	 * Lancement d'une session
	 * Lance le thread d'une session
	 */
	public void debutSession();
	public void vainqueur();
	public void tour();
	public void rATrouve(ServiceClient sc);
	public void rFin();
	public void sFin();
	public void sInvalide();
	public void meilleurMot(ServiceClient meilleur);
	public void ancienMeilleur(ServiceClient oldBest, ServiceClient newBest);
	public void bilan();
	
	//For only one client
	/**
	 * Envoie à sc que sa connexion est refusé.
	 * @param sc
	 */
	public void refus(ServiceClient sc);
	/**
	 * Envoie le plateau courrant ainsi que la liste de joueur.
	 * @param sc
	 */
	public void bienvenue(ServiceClient sc);
	void rInValide(ServiceClient sc, String message);
	void rValide(ServiceClient sc);
	void sValide(ServiceClient sc);

}
