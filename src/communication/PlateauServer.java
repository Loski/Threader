package communication;

import java.awt.Point;
import java.util.ArrayList;
import java.util.List;

public class PlateauServer extends Plateau {

	public PlateauServer(int taillePlateau) {
		super(taillePlateau);
	}
	
	public boolean placementValide(Plateau plateau_joueur) throws ExceptionPlateau{
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
		
		 if(!somethingChange){
			 throw new ExceptionPlateau("Le changement, c'est pas maintenant !", "POS");
		 }
		 if(!verificationAlignement(point)){
			 throw new ExceptionPlateau("Alignement des lettres non respecté.", "POS");
		 }
		 return true;
	}
	
	public boolean verificationAlignement(List<Point> point){
		// on suposse alignement en X
		if(point.get(0).getX() == point.get(1).getY()){
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

