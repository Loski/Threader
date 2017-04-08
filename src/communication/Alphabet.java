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
	

    private List<Letter> tirage_courant;
    private List<Letter> liste_all_letter;
    public static int taille_tirage;
    public static ArrayList<String> liste_mot;
	private Random generator;

	
	public Alphabet(int tailleTirage) {
		Alphabet.taille_tirage = tailleTirage;
		tirage_courant = new ArrayList<Letter>();
		liste_all_letter = new ArrayList<Letter>();
		generator = new Random();
		try{
			InputStream flux=new FileInputStream("ressource/alphabet.txt"); 
			InputStreamReader lecture=new InputStreamReader(flux);
			BufferedReader buff=new BufferedReader(lecture);
			String ligne;
			while ((ligne=buff.readLine())!=null){
				ajouterLetter(ligne.split(" "));
			}
				buff.close(); 
			}catch (Exception e){
				System.out.println(e.toString());
			}
		
	}
	
	private void ajouterLetter(String[] split) {
		for(int i = 0; i < new Integer(split[1]);i++){
			this.liste_all_letter.add(new Letter(split[0], split[2]));
		}
	}

	public List<Letter> getListe_all_letter() {
		return liste_all_letter;
	}

	public void setListe_all_letter(List<Letter> liste_all_letter) {
		this.liste_all_letter = liste_all_letter;
	}

	public boolean canTirage(){
		return liste_all_letter.size() >= 1;
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
		while(tirage_courant.size() < taille_tirage && liste_all_letter.size() > 0){
			tirage_courant.add(selectionLetter());
		}
		return tirage_courant;
	}
	public void majLetter(){
		this.tirage_courant = newTirage();
	}

	
	public Letter selectionLetter(){
		int index = generator.nextInt(liste_all_letter.size());
		Letter l = liste_all_letter.remove(index);
		return l;
	}
	public static int  findScoreForLetter(char charAt, List<Letter> liste_all_letter) {
		for(Letter l: liste_all_letter ){
			if(l.getLetter() == charAt)
				return l.getPuissance();
		}
		return 0;
	}

	public void delete(char c) {
		for(Letter l: tirage_courant){
			if(l.getLetter() == c){
				tirage_courant.remove(l);
				break;
			}
		}
	}
}
