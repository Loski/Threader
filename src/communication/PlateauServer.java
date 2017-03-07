package communication;

public class PlateauServer extends Plateau {

	
	public PlateauServer(int taillePlateau) {
		super(taillePlateau);
	}
	
	public boolean placementValide(Plateau plateau_joueur){
		 for(int i = 0; i < taille_plateau; i++){
			 for(int j = 0; j < taille_plateau; j++){
				 if(this.plateau[i][j] != plateau_joueur.plateau[i][j]){
					 try{
						 if(this.plateau[i][j] != char_empty){
							 throw new ExceptionPlateau("un joueur tente de tricher Michel");
						 }
					 }catch (ExceptionPlateau e) {
						e.printStackTrace();
						return false;
					}
				 }
			 }
		 }
		 return true;
	}

}
