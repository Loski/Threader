package communication;

public class Letter {
	private Character letter;
	private int nombre_total;;
	private int puisssance;
	
	
	public Letter(Character letter, int nombre_total, int puisssance) {
		super();
		this.letter = letter;
		this.nombre_total = nombre_total;
		this.puisssance = puisssance;
	}
	
	public Letter(String ligne[]) {
		this(ligne[0].toCharArray()[0], new Integer(ligne[1]), new Integer(ligne[1]));	
	}

	public Character getLetter() {
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
		return puisssance;
	}
	public void setPuisssance(int puisssance) {
		this.puisssance = puisssance;
	}
	
	public void decrementeLetter(){
		this.nombre_total--;
	}

}
