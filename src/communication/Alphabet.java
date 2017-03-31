package communication;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;
import java.util.Set;

public class Alphabet {
	/**
	 * key => Char
	 * 1er value => nombre de key dispo pour ce Char
	 * 2nde value => Score de la lettre 
	 *  EX : A est disponible 9 fois pour un score de 1.
	 */
	
    private List<Letter> liste_lettre;
    private List<Character> tirage_courant;
    public static int taille_tirage;
	private Random generator;

	
	public Alphabet(int tailleTirage) {
		Alphabet.taille_tirage = tailleTirage;
		tirage_courant = new ArrayList<Character>();
		liste_lettre = new ArrayList<Letter>();
		generator = new Random();
		try{
			InputStream flux=new FileInputStream("ressource/alphabet.txt"); 
			InputStreamReader lecture=new InputStreamReader(flux);
			BufferedReader buff=new BufferedReader(lecture);
			String ligne;
			while ((ligne=buff.readLine())!=null){
				this.liste_lettre.add(new Letter(ligne.split(" ")));
			}
				buff.close(); 
			}catch (Exception e){
				System.out.println(e.toString());
			}
		
	}
	
	public boolean canTirage(){
		return tirage_courant.size() + liste_lettre.size() >= taille_tirage;
	}
	
	public List<Character> tirage(){
		while(tirage_courant.size() < taille_tirage){
			tirage_courant.add(selectionLetter());
		}
		return tirage_courant;
	}
	
	
	public Character selectionLetter(){
		int index = generator.nextInt(liste_lettre.size());
		Letter l = liste_lettre.get(index);
		Character c = new Character(l.getLetter());
		l.decrementeLetter();
		if(l.getNombre_total() <= 0)
			liste_lettre.remove(l);
		return c;
	}
	
}
