package communication;

import java.awt.Point;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

public class PlateauServer extends Plateau {



	public boolean empty;

	private ServiceClient meilleur_joueur;

	public PlateauServer(int taillePlateau) {
		super(taillePlateau);
		empty = true;
	}



	public PlateauServer(Plateau plateau) {
		super(plateau);
		this.meilleur_joueur = null;
		empty = isEmpty();
	}
	
	public List<Point> verificationAlignement(Plateau plateau_joueur) throws ExceptionPlateau
	{
		List<Point> points = getLettresJouees(plateau_joueur);
		
		int alignement = getOrientation(points);
		
		if(alignement == NO_DIRECTION){
			throw new ExceptionPlateau("Alignement des lettres non respecté.", "POS");
		}
		
		return points;
	}
	
	public ArrayList<String> getWordOfFirstTurn(Plateau plateau_joueur) throws ExceptionPlateau
	{
		List<Point> points = verificationAlignement(plateau_joueur);
		ArrayList<String> words = new ArrayList<String>();
		
		int alignement = getOrientation(points);
		
		if(alignement==HORIZONTAL || alignement==VERTICAL)
		{
			words.add(Plateau.findWord(points.get(0), alignement,plateau_joueur));
		}
		else if(alignement == NOT_FOUND)
		{
			words.add(""+plateau_joueur.plateau[points.get(0).x][points.get(0).y]);
		}
		return words;
	}
	
	public ArrayList<String> verificationMots(Plateau plateau_joueur) throws ExceptionPlateau
	{
		List<Point> points = verificationAlignement(plateau_joueur);
		
		int alignement = getOrientation(points);
		
		ArrayList<String> words = new ArrayList<String>();
		
		if(alignement==HORIZONTAL || alignement==VERTICAL)
		{
			//Mot complété
			words.add(Plateau.findWord(points.get(0), alignement,plateau_joueur));
		}
		else if(alignement == NOT_FOUND)
		{
			for(String mot:Plateau.getCroisement(points.get(0), plateau_joueur))
				if(mot!="" && mot.length()>1)
					words.add(mot);
		}
		
		for(Point lettre:points)
		{
			int counter_alignement = NOT_FOUND;
			
			if(alignement==HORIZONTAL)
				counter_alignement = VERTICAL;
			else if (alignement==VERTICAL)
				counter_alignement = HORIZONTAL;
			
			if(alignement==HORIZONTAL || alignement==VERTICAL)
			{
				String mot = Plateau.findWord(lettre, counter_alignement,plateau_joueur);
				if(mot!="" && mot.length()>1)
					words.add(mot);
			}
			
			/*Point lettre_haut = new Point(lettre.x-1, lettre.y);
			Point lettre_bas = new Point(lettre.x+1, lettre.y);
			Point lettre_gauche = new Point(lettre.x, lettre.y-1);
			Point lettre_droite = new Point(lettre.x, lettre.y+1);
			
			Point[] lettre_a_tester = new Point[4];
			
			lettre_a_tester[0] = lettre_haut;
			lettre_a_tester[1] = lettre_bas;
			lettre_a_tester[2] = lettre_gauche;
			lettre_a_tester[3] = lettre_droite;
			
			boolean bienPlace = false;
			
			int i = 0;
			while(!bienPlace && i<4)
			{
				Point _case = lettre_a_tester[i];
				
				if(_case.x>=0 && _case.x<taille_plateau && _case.y>=0 && _case.y<taille_plateau)
				{
					char valeur = plateau_joueur.plateau[_case.x][_case.y];
					
					if(valeur!=char_empty)
						bienPlace = true;
				}
				
				i++;
			}
			
			if(!bienPlace)*/
			
		}
		if(words.isEmpty()){
			throw new ExceptionPlateau("Aucun mot trouvé", "POS");

		}
		if(words.get(0).length()<=points.size())
			throw new ExceptionPlateau("Les lettres ne complètent pas un mot", "POS");
		
		System.out.println("WORD:"+words);
		return words;
	}


	public List<Point> getLettresJouees(Plateau plateau_joueur) throws ExceptionPlateau{
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
	public int getOrientation(List<Point> points){
		if(points.size() <= 1){
			return NOT_FOUND;
		}
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
			return VERTICAL;
		}
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
	public ServiceClient getMeilleur_joueur() {
		return meilleur_joueur;
	}





	public void reset() {
		score = 0;
		meilleur_joueur = null;
	}



	public String getVainqueur() {
		if(meilleur_joueur == null){
			return "//";
		}else
			return meilleur_joueur.getPlateau().getMot_courantToString() + "/" +meilleur_joueur.getPseudo()+"/";
	}


 
	public boolean hadWiner() {
		return meilleur_joueur != null;
	}
	
	public boolean isEmpty() {
		for(int i = 0; i < taille_plateau; i++)
			for(int j = 0; j < taille_plateau; j++)
				if(this.plateau[i][j] != char_empty)
					return false;
		return true;
	}
	public void setEmpty(boolean empty) {
		this.empty = empty;
	}

}

