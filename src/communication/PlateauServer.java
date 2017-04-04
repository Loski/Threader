package communication;

import java.awt.Point;
import java.util.ArrayList;
import java.util.List;

public class PlateauServer extends Plateau {

	public PlateauServer(int taillePlateau) {
		super(taillePlateau);
	}
	
	public void placementValide(Plateau plateau_joueur) throws ExceptionPlateau{
		boolean somethingChange = false;
		List<Point> point = new ArrayList<Point>();
		for(int i = 0; i < taille_plateau; i++){
			for(int j = 0; j < taille_plateau; j++){
				if(this.plateau[i][j] != plateau_joueur.plateau[i][j]){
					if(this.plateau[i][j] != char_empty){
						throw new ExceptionPlateau("un joueur tente de tricher Michel", "POS");
					}else{
						somethingChange = true;
						point.add(new Point(i, j));
					 }
				 }
			 }
		 }
		System.out.println(somethingChange);
		 if(!somethingChange){
			 throw new ExceptionPlateau("Le changement, c'est pas maintenant !", "POS");
		 }
		 if(!verificationAlignement(point)){
			 System.out.println(point);
			 throw new ExceptionPlateau("Alignement des lettres non respecté.", "POS");
		 }
	}
	/**
	 * Vérifie que les nouvelles lettres sont bien alignées
	 * @param point Coordonnés des lettres dans le tableau
	 * @return
	 */
	public boolean verificationAlignement(List<Point> point){
		// on suposse alignement en X
		if(point.size() <= 1)
			return true;
		if(point.get(0).getX() == point.get(1).getX()){
			double x = point.get(0).getX();
			for(Point pt : point){
				if(pt.getX() != x)
					return false;
			}
		}else{
			double y = point.get(0).getY();
			for(Point pt : point){
				if(pt.getY() != y)
					return false;
			}
		}
		return true;
	}

}

