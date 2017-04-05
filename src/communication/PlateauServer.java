package communication;

import java.awt.Point;
import java.util.ArrayList;
import java.util.List;

public class PlateauServer extends Plateau {

	public static final int HORIZONTAL = 0;
	public static final int VERTICAL = 1;
	public static final int NO_DIRECTION = 2;
	public boolean empty;
	
	public PlateauServer(int taillePlateau) {
		super(taillePlateau);
		empty = true;
	}
	
	public void gestionPlacement(Plateau plateau_joueur) throws ExceptionPlateau{
		List<Point> points = placementValide(plateau_joueur);
		 int alignement = verificationAlignement(points);
		 if(alignement == NO_DIRECTION){
			 throw new ExceptionPlateau("Alignement des lettres non respecté.", "POS");
		 }
		System.out.println(findMainWord(points, alignement));
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
						points.add(new Point(i, j));
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
	
	public String findMainWord(List<Point> points, int alignement) throws ExceptionPlateau{
		if(points.isEmpty())
			 throw new ExceptionPlateau("Aucune lettre !", "POS");
		switch (alignement) {
			case VERTICAL:
				return searchUpDown(points.get(0));
			case HORIZONTAL:
				return searchLeftRight(points.get(0));
			default:
				break;
			}
		return null;
	}
	
	public List<String> searchVertical(List<Point> points){
		if(empty){
			
		}
		return null;
	}
	
	
	public List<String> chercherAutreMots(List<Point> points, int alignement){
		switch (alignement) {
		case VERTICAL:
				return searchLeftRight(points);
		case HORIZONTAL:
				return searchUpDown(points);
		default:
			break;
		}
		return null;
	}

	private ArrayList<String> searchLeftRight(List<Point> points) {
		ArrayList<String> mots = new ArrayList<String>();
		for(Point pt: points){
			mots.add(searchLeftRight(pt));
		}
		return mots;
	}
	private String searchLeftRight(Point pt){
		String str ="";
		int x = (int) pt.getX() -1;
		while(x >= 0 && plateau[x][(int) pt.getY()] != char_empty){
			str+= plateau[x][(int) pt.getY()];
			x--;
		}
		if(str.length() > 0){
			str = new StringBuilder(str).reverse().toString();
		}
		x = (int) pt.getX();
		while(x < taille_plateau && plateau[x][(int) pt.getY()] != char_empty){
			str+= plateau[x][(int) pt.getY()];
			x++;
		}
		return str;
	}
	private ArrayList<String> searchUpDown(List<Point> points) {
		ArrayList<String> mots = new ArrayList<String>();
		for(Point pt: points){
			mots.add(searchUpDown(pt));
		}
		return mots;
	}
	
	
	private String searchUpDown(Point pt) {
			String str ="";
			int y = (int) pt.getY() -1;
			while(y >= 0 && plateau[(int) pt.getX()][y] != char_empty){
				str+=plateau[(int) pt.getX()][y];
				y--;
			}
			if(str.length() > 0){
				str = new StringBuilder(str).reverse().toString();
			}
			y = (int) pt.getY();
			while(y < taille_plateau && plateau[(int) pt.getX()][y] != char_empty){
				str+=  plateau[(int) pt.getX()][y];
				y++;
			}
			return str;
	}

}

