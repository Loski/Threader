package communication;

import java.util.Arrays;

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

	public Plateau(String string) throws ExceptionPlateau {
		if(string.length() == taille_plateau * taille_plateau){
			this.plateau = new char[taille_plateau][taille_plateau];
			for(int i = 0; i < taille_plateau; i++){
				for(int j = 0; j < taille_plateau; j++){
					plateau[i][j] = string.charAt(i * taille_plateau + j);
				}
			 }
		}else{
			throw new ExceptionPlateau("Nombre de lettres invalides", "POS");
		}
	}

	@Override
	public String toString() {
		String s ="";
		for(int i = 0; i < taille_plateau; i++){
			for(int j = 0; j < taille_plateau; j++){
				s += this.plateau[i][j];
			}
		}
		return s;
	}
	
	   public static void main (String[] args){
		   System.out.println(new Plateau(15));
	}
}
