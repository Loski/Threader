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
    private List<Letter> tirage_courant;
    private List<Letter> liste_all_letter;
    public static int taille_tirage;
	private Random generator;

	
	public Alphabet(int tailleTirage) {
		Alphabet.taille_tirage = tailleTirage;
		tirage_courant = new ArrayList<Letter>();
		liste_lettre_voyelle = new ArrayList<Letter>();
		liste_lettre_consonne = new ArrayList<Letter>();
		liste_all_letter = new ArrayList<Letter>();

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
				liste_all_letter.add(tmp);
			}
				buff.close(); 
			}catch (Exception e){
				System.out.println(e.toString());
			}
		
	}
	
	public List<Letter> getListe_all_letter() {
		return liste_all_letter;
	}

	public void setListe_all_letter(List<Letter> liste_all_letter) {
		this.liste_all_letter = liste_all_letter;
	}

	public boolean canTirage(){
		return tirage_courant.size() + liste_lettre_consonne.size() + liste_lettre_voyelle.size() >= taille_tirage;
	}
	private int needVoyelle(){
		int count = 0;
		for(Letter c : this.tirage_courant){
			if(c.isVoyelle()){
				count++;
			}
		}
		return count;
	}
	public List<Letter> newTirage(){
		if(tirage_courant.size() == 7){
			while(!tirage_courant.isEmpty()){
				tirage_courant.remove(tirage_courant.size()-1);
			}
		}
		return tirage();
	}
	public List<Letter> tirage(){
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
	
	
	public Letter selectionLetterConsonne(){
		int index = generator.nextInt(liste_lettre_consonne.size());
		Letter l = liste_lettre_consonne.get(index);
		Character c = new Character(l.getLetter());
		l.decrementeLetter();
		if(l.getNombre_total() <= 0)
			liste_lettre_consonne.remove(l);
		return new Letter(l);
	}
	
	public Letter selectionLetterVoyelle(){
		int index = generator.nextInt(liste_lettre_voyelle.size());
		Letter l = liste_lettre_voyelle.get(index);
		l.decrementeLetter();
		if(l.getNombre_total() <= 0)
			liste_lettre_voyelle.remove(l);
		return new Letter(l);
	}
	public static int  findScoreForLetter(char charAt, List<Letter> liste_all_letter) {
		for(Letter l: liste_all_letter ){
			if(l.getLetter() == charAt)
				return l.getPuissance();
		}
		return 0;
	}
}
