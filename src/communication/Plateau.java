package communication;

public class Plateau {
	
	public static int taille_plateau = 15;
	protected char plateau[][];
	public final static char char_empty = '0';
		
	public Plateau(int taille){
		Plateau.taille_plateau = taille;
		 this.plateau = new char[taille_plateau][taille_plateau];
		 for(int i = 0; i < taille_plateau; i++){
			 for(int j = 0; j < taille_plateau; j++){
				 plateau[i][j] = char_empty;
			 }
		 }
	}
}
