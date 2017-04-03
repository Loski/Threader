package communication;

public class Letter {
	private char letter;
	private int nombre_total;;
	private int puissance;
	
	
	public Letter(Character letter, int nombre_total, int puisssance) {
		super();
		this.letter = letter;
		this.nombre_total = nombre_total;
		this.puissance = puisssance;
	}
	
	public Letter(String ligne[]) {
		this(ligne[0].toCharArray()[0], new Integer(ligne[1]), new Integer(ligne[1]));	
	}

	public char getLetter() {
		return letter;
	}
	public void setLetter(Character letter) {
		this.letter = letter;
	}
	public int getNombre_total() {
		return nombre_total;
	}
	public void setNombre_total(int nombre_total) {
		this.nombre_total = nombre_total;
	}
	public int getPuisssance() {
		return puissance;
	}
	public void setPuisssance(int puisssance) {
		this.puissance = puisssance;
	}
	
	public void decrementeLetter(){
		this.nombre_total--;
	}

	public boolean isVoyelle() {
		if(letter == 'A' || letter=='E' || letter =='I' || letter == 'Y' || letter == 'O' ||letter == 'U')
			return true;
		return false;
	}

	public static boolean isVoyelle(char letter) {
		if(letter == 'A' || letter=='E' || letter =='I' || letter == 'Y' || letter == 'O' ||letter == 'U')
			return true;
		return false;
	}

}
