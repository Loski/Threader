package communication;

import java.awt.Point;
import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Plateau {
	
	public static final int HORIZONTAL = 0;
	public static final int VERTICAL = 1;
	public static final int NO_DIRECTION = 2;
	public static int taille_plateau = 15;
	protected char plateau[][];
	public final static char char_empty = '0';
	private int score;
	
	public Plateau(int taille){
		Plateau.taille_plateau = taille;
		 this.plateau = new char[taille_plateau][taille_plateau];
		 for(int i = 0; i < taille_plateau; i++){
			 for(int j = 0; j < taille_plateau; j++){
				 plateau[i][j] = char_empty;
			 }
		 }
		 setScore(0);
	}

	public Plateau(String string){
		if(string.length() == taille_plateau * taille_plateau){
			this.plateau = new char[taille_plateau][taille_plateau];
			for(int i = 0; i < taille_plateau; i++){
				for(int j = 0; j < taille_plateau; j++){
					plateau[i][j] = string.charAt(i * taille_plateau + j);
				}
			 }
			 setScore(0);
		}else{
			try {
				throw new ExceptionPlateau("Nombre de lettres invalides", "POS");
			} catch (ExceptionPlateau e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	public Plateau(Plateau p){
		this(p.toString());
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
	public String findMainWord(List<Point> points, int alignement) throws ExceptionPlateau{
		if(points.isEmpty())
			 throw new ExceptionPlateau("Aucune lettre !", "POS");
		switch (alignement) {
			case VERTICAL:
				return searchUpDown(points.get(0));
			case HORIZONTAL:
				return searchLeftRight(points.get(0));
			default:
				break;
			}
		return null;
	}
	
	public List<String> chercherAutreMots(List<Point> points, int alignement){
		switch (alignement) {
		case VERTICAL:
				return searchLeftRight(points);
		case HORIZONTAL:
				return searchUpDown(points);
		default:
			break;
		}
		return null;
	}

	private ArrayList<String> searchLeftRight(List<Point> points) {
		ArrayList<String> mots = new ArrayList<String>();
		for(Point pt: points){
			mots.add(searchLeftRight(pt));
		}
		return mots;
	}
	private String searchLeftRight(Point pt){
		String str ="";
		int x = (int) pt.getX() -1;
		while(x >= 0 && plateau[(int) pt.getY()][x] != char_empty){
			str+= plateau[(int) pt.getY()][x];
			x--;
		}
		if(str.length() > 0){
			str = new StringBuilder(str).reverse().toString();
		}
		x = (int) pt.getX();
		while(x < taille_plateau && plateau[(int) pt.getY()][x] != char_empty){
			str+= plateau[(int) pt.getY()][x];
			x++;
		}
		return str;
	}
	private ArrayList<String> searchUpDown(List<Point> points) {
		ArrayList<String> mots = new ArrayList<String>();
		for(Point pt: points){
			mots.add(searchUpDown(pt));
		}
		return mots;
	}
	
	
	private String searchUpDown(Point pt) {
			String str ="";
			int y = (int) pt.getY() -1;
			while(y >= 0 && plateau[y][(int) pt.getX()] != char_empty){
				str+=plateau[y][(int) pt.getX()];
				y--;
			}
			if(str.length() > 0){
				str = new StringBuilder(str).reverse().toString();
			}
			y = (int) pt.getY();
			while(y < taille_plateau && plateau[y][(int) pt.getX()] != char_empty){
				str+=  plateau[y][(int) pt.getX()];
				y++;
			}
			return str;
	}
	   public static void main (String[] args){
		   System.out.println(new Plateau(15));
	}

	public int getScore() {
		return score;
	}

	public void setScore(int score) {
		this.score = score;
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
	
	public static int calculScore(String mot, Alphabet tirage) {
		int score = 0;
		for(int i = 0; i < mot.length(); i++){
			score += findScoreForLetter(mot.charAt(i));
		}
	}

	private static int findScoreForLetter(char charAt) {
		// TODO Auto-generated method stub
		return 0;
	}
}
