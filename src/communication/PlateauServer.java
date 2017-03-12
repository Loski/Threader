package communication;

public class PlateauServer extends Plateau {

	public PlateauServer(int taillePlateau) {
		super(taillePlateau);
	}
	
	public boolean placementValide(Plateau plateau_joueur) throws ExceptionPlateau{
		boolean somethingChange = false;
		 for(int i = 0; i < taille_plateau; i++){
			 for(int j = 0; j < taille_plateau; j++){
				 if(this.plateau[i][j] != plateau_joueur.plateau[i][j]){
					 if(this.plateau[i][j] != char_empty){
						 throw new ExceptionPlateau("un joueur tente de tricher Michel", "POS");
					 }else{
						 somethingChange = true;
					 }
				 }
			 }
		 }
		 if(!somethingChange){
			 throw new ExceptionPlateau("Le changement, c'est pas maintenant !", "POS");
		 }
		 return true;
	}

}
