package communication;

import java.awt.Point;
import java.util.ArrayList;
import java.util.List;

public class PlateauServer extends Plateau {


	public boolean empty;
	private ServiceClient meilleur_joueur;
	public ServiceClient getMeilleur_joueur() {
		return meilleur_joueur;
	}


	
	public String gestionPlacement(Plateau plateau_joueur) throws ExceptionPlateau{
		List<Point> points = placementValide(plateau_joueur);
		 int alignement = verificationAlignement(points);
		 if(alignement == NO_DIRECTION){
			 throw new ExceptionPlateau("Alignement des lettres non respecté.", "POS");
		 }
		return plateau_joueur.findMainWord(points, alignement);
	}


	public List<Point> placementValide(Plateau plateau_joueur) throws ExceptionPlateau{
		boolean somethingChange = false;
		List<Point> points = new ArrayList<Point>();
		for(int i = 0; i < taille_plateau; i++){
			for(int j = 0; j < taille_plateau; j++){
				if(this.plateau[i][j] != plateau_joueur.plateau[i][j]){
					if(this.plateau[i][j] != char_empty){
						throw new ExceptionPlateau("un joueur tente de tricher Michel", "POS");
					}else{
						somethingChange = true;
						points.add(new Point(j, i));
					 }
				 }
			 }
		 }
		 if(!somethingChange){
			 throw new ExceptionPlateau("Le changement, c'est pas maintenant !", "POS");
		 }
		 return points;
	}
	
	/**
	 * Vérifie que les nouvelles lettres sont bien alignées
	 * @param point Coordonnés des lettres dans le tableau
	 * @return
	 */
	public int verificationAlignement(List<Point> points){
		if(points.size() <= 1)
			return VERTICAL;
		// on suposse alignement en X
		if(points.get(0).getX() == points.get(1).getX()){
			double x = points.get(0).getX();
			for(Point pt : points){
				if(pt.getX() != x)
					return NO_DIRECTION;
			}
			return HORIZONTAL;
		}else{
			double y = points.get(0).getY();
			for(Point pt : points){
				if(pt.getY() != y)
					return NO_DIRECTION;
			}
		}
		return HORIZONTAL;
	}

	public synchronized int askSwitch(ServiceClient serviceClient) {
		if(meilleur_joueur == null){
			meilleur_joueur = serviceClient;
			score = serviceClient.getPlateau().getScore();
			return NOTIFY_ALL;
		}else if(serviceClient.getPlateau().getScore() > this.score){
			score = serviceClient.getPlateau().getScore();
			if(meilleur_joueur == serviceClient){
				return NO_NOTIFY;
			}else{
				return NOTIFY_ONLY_OLD;
			}
		}
		return NO_NOTIFY;
	}
	
	public void setMeilleur_joueur(ServiceClient meilleur_joueur) {
		this.meilleur_joueur = meilleur_joueur;
	}

	public PlateauServer(int taillePlateau) {
		super(taillePlateau);
		empty = true;
	}



	public void reset() {
		score = 0;
		meilleur_joueur = null;
	}



	public String getVainqueur() {
		if(meilleur_joueur == null){
			return "//";
		}else
			return meilleur_joueur.getPseudo() + "/" + score+ "/";
	}
	

}

