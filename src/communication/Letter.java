package communication;

public class Letter {
	private char letter;
	private int puissance;
	
	public Letter(Character letter, int puisssance) {
		super();
		this.letter = letter;
		this.puissance = puisssance;
	}
	
	public Letter(String ligne[]) {
		this(ligne[0].toCharArray()[0], new Integer(ligne[1]));	
	}
	
	public Letter(String str, String puissance){
		this(str.charAt(0), new Integer(puissance));
	}

	public Letter(Letter l) {
		this.letter = new Character(l.letter);
		this.puissance =  l.puissance;
	}

	public char getLetter() {
		return letter;
	}
	public void setLetter(Character letter) {
		this.letter = letter;
	}
	public int getPuissance() {
		return puissance;
	}
	public void setPuissance(int puisssance) {
		this.puissance = puisssance;
	}


}
