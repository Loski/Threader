package communication;

import java.awt.Point;
import java.util.ArrayList;
import java.util.List;

public class PlateauServer extends Plateau {

	public PlateauServer(int taillePlateau) {
		super(taillePlateau);
	}
	
	public String placementValide(Plateau plateau_joueur) throws ExceptionPlateau{
		boolean somethingChange = false;
		List<Point> points = new ArrayList<Point>();
		for(int i = 0; i < taille_plateau; i++){
			for(int j = 0; j < taille_plateau; j++){
				if(this.plateau[i][j] != plateau_joueur.plateau[i][j]){
					if(this.plateau[i][j] != char_empty){
						throw new ExceptionPlateau("un joueur tente de tricher Michel", "POS");
					}else{
						somethingChange = true;
						points.add(new Point(i, j));
					 }
				 }
			 }
		 }
		 if(!somethingChange){
			 throw new ExceptionPlateau("Le changement, c'est pas maintenant !", "POS");
		 }
		 if(!verificationAlignement(points)){
			 throw new ExceptionPlateau("Alignement des lettres non respecté.", "POS");
		 }
		String s = "";
		for(Point pt : points){
			s+= plateau_joueur.plateau[(int) pt.getX()][(int)pt.getY()];
		}
		return s;
	}
	/**
	 * Vérifie que les nouvelles lettres sont bien alignées
	 * @param point Coordonnés des lettres dans le tableau
	 * @return
	 */
	public boolean verificationAlignement(List<Point> points){
		// on suposse alignement en X
		if(points.size() <= 1)
			return true;
		if(points.get(0).getX() == points.get(1).getX()){
			double x = points.get(0).getX();
			for(Point pt : points){
				if(pt.getX() != x)
					return false;
			}
		}else{
			double y = points.get(0).getY();
			for(Point pt : points){
				if(pt.getY() != y)
					return false;
			}
		}
		return true;
	}
	


}

