package communication;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;
import java.util.Set;

import javax.swing.text.Document;
import javax.xml.*;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

public class Alphabet {
	/**
	 * key => Char
	 * 1er value => nombre de key dispo pour ce Char
	 * 2nde value => Score de la lettre 
	 *  EX : A est disponible 9 fois pour un score de 1.
	 */
	
    private List<Letter> liste_lettre_voyelle;
    private List<Letter> liste_lettre_consonne;
    private List<Character> tirage_courant;
    public static int taille_tirage;
	private Random generator;

	
	public Alphabet(int tailleTirage) {
		Alphabet.taille_tirage = tailleTirage;
		tirage_courant = new ArrayList<Character>();
		liste_lettre_voyelle = new ArrayList<Letter>();
		liste_lettre_consonne = new ArrayList<Letter>();

		generator = new Random();
		try{
			InputStream flux=new FileInputStream("ressource/alphabet.txt"); 
			InputStreamReader lecture=new InputStreamReader(flux);
			BufferedReader buff=new BufferedReader(lecture);
			String ligne;
			while ((ligne=buff.readLine())!=null){
				Letter tmp = new Letter(ligne.split(" "));
				if(tmp.isVoyelle()){
					this.liste_lettre_voyelle.add(tmp);
				}else{
					liste_lettre_consonne.add(tmp);
				}
			}
				buff.close(); 
			}catch (Exception e){
				System.out.println(e.toString());
			}
		
	}
	
	public boolean canTirage(){
		return tirage_courant.size() + liste_lettre_consonne.size() + liste_lettre_voyelle.size() >= taille_tirage;
	}
	private int needVoyelle(){
		int count = 0;
		for(Character c : this.tirage_courant){
			if(Letter.isVoyelle(c)){
				count++;
			}
		}
		return count;
	}
	public List<Character> tirage(){
		int count = needVoyelle();
		while(tirage_courant.size() < taille_tirage){
			if(count < 4){
				tirage_courant.add(selectionLetterVoyelle());
				count++;
			}else{
				tirage_courant.add(selectionLetterConsonne());
			}
		}
		return tirage_courant;
	}
	
	
	public Character selectionLetterConsonne(){
		int index = generator.nextInt(liste_lettre_consonne.size());
		Letter l = liste_lettre_consonne.get(index);
		Character c = new Character(l.getLetter());
		l.decrementeLetter();
		if(l.getNombre_total() <= 0)
			liste_lettre_consonne.remove(l);
		return c;
	}
	
	public Character selectionLetterVoyelle(){
		int index = generator.nextInt(liste_lettre_voyelle.size());
		Letter l = liste_lettre_voyelle.get(index);
		Character c = new Character(l.getLetter());
		l.decrementeLetter();
		if(l.getNombre_total() <= 0)
			liste_lettre_voyelle.remove(l);
		return c;
	}
	
	public static boolean isRealWord(String s){
		   URL url;
		   HttpURLConnection urlConnection = null;
		   BufferedReader input;
		   String ligne;
		   String xml ="";
		try {
			url = new URL("http://www.wordgamedictionary.com/api/v1/references/scrabble/"+s+"?key=9.320519842586974e29");
		    urlConnection = (HttpURLConnection) url.openConnection();
		     input = new BufferedReader(new InputStreamReader(new BufferedInputStream(urlConnection.getInputStream())));
		     while ((ligne=input.readLine())!=null){
				xml+=ligne;
			}
		     System.out.println(xml);
		     
		}catch(MalformedURLException e){} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
		     urlConnection.disconnect();
		   }

		return parse(xml);	
	}
	public static boolean parse(String xml){
		return xml.contains("<scrabble>1</scrabble>");
	}
	   public static void main (String[] args){
		  System.out.println(isRealWord("Milou"));
	}
	
}
