package communication;

public interface Communication {
	
	// For all clients
	public void connecte(ServiceClient sc);
	public void deconnexion(ServiceClient sc);
	public void debutSession();
	public void vainqueur();
	public void tour();
	public void rValide();
	public void rInValide();
	public void rATrouve(ServiceClient sc);
	public void rFin();
	public void sValide();
	public void sFin();
	public void sInvalide();
	public void bilan();
	
	//For only one client
	public void refus(ServiceClient sc);
	public void bienvenue(ServiceClient sc);

}
