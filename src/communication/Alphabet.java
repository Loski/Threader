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
	
    private HashMap<Character, List<Integer>> alphabet;
    public static int taille_tirage;
	public Alphabet(int tailleTirage) {
		Alphabet.taille_tirage = tailleTirage;
		alphabet = new HashMap<>();
		try{
			InputStream flux=new FileInputStream("ressource/alphabet.txt"); 
			InputStreamReader lecture=new InputStreamReader(flux);
			BufferedReader buff=new BufferedReader(lecture);
			String ligne;
			while ((ligne=buff.readLine())!=null){
				
				String str[] = ligne.split(" ");
				ArrayList<Integer> tmp = new ArrayList<>();
				tmp.add(new Integer(str[1]));
				tmp.add(new Integer(str[2]));
				this.alphabet.put(new Character(str[0].toCharArray()[0]), tmp);
			}
				buff.close(); 
			}catch (Exception e){
				System.out.println(e.toString());
			}
		
	}
	
	
	public Character[] tirage(){
		Character[] tirage = new Character[Alphabet.taille_tirage];
		Random generator = new Random();
		for(int i = 0; i < Alphabet.taille_tirage; i++){
			tirage[i] = 'A';
		}
		return tirage;
		
	}
	
	
}
