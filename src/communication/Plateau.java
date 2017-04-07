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
	public static final int NOT_FOUND = 10;

	
	public final static int NOTIFY_ALL = 0;
	public final static int NOTIFY_ONLY_OLD = 1;
	public final static int NO_NOTIFY = 2;
	public static int taille_plateau = 15;
	protected char plateau[][];
	public final static char char_empty = '0';
	protected int score;
	protected List<String> my_word;
	protected List<Point> myPoint;
	
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
		this.score = p.getScore();
		this.my_word = p.my_word;
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
	
	public static String[] getCroisement(Point p,Plateau plateau)
	{
		String[] mots = new String[2];
		mots[0]=findWord(p,HORIZONTAL,plateau);
		mots[1]=findWord(p,VERTICAL,plateau);
		
		return mots;
	}
	
	public static String findWord(Point p,int alignement,Plateau plateau)
	{		
		switch (alignement) {
		case VERTICAL:
			return searchUpDown(p,plateau);
		case HORIZONTAL:
			return searchLeftRight(p,plateau);
		default:
			return null;
		}
	}
	
	public static String searchUpDown(Point p,Plateau plateau) {
		
		int start = p.x;
		
		while((start-1)%taille_plateau>=0 && plateau.plateau[start-1][p.y]!=char_empty)
			start--;
		
		String s ="";
		
		int end = start;
		
		while(end<taille_plateau && plateau.plateau[end][p.y]!=char_empty)
		{
			s+=plateau.plateau[end][p.y];
			end++;
		}
		
		return s;
	}
	
	public static String searchLeftRight(Point p,Plateau plateau) {
		
		int start = p.y;
		
		while(start-1>=0 && plateau.plateau[p.x][start-1]!=char_empty)
			start--;
		
		String s ="";
		
		int end = start;
		
		while(end<taille_plateau && plateau.plateau[p.x][end]!=char_empty)
		{
			s+=plateau.plateau[p.x][end];
			end++;
		}
		
		return s;
	}
	
	/*public String findMainWord(List<Point> points, int alignement) throws ExceptionPlateau{
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

	protected ArrayList<String> searchLeftRight(List<Point> points) {
		ArrayList<String> mots = new ArrayList<String>();
		for(Point pt: points){
			String tmp = searchLeftRight(pt);
			if(tmp.length() > 1){
				mots.add(tmp);
			}
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
	protected ArrayList<String> searchUpDown(List<Point> points) {
		ArrayList<String> mots = new ArrayList<String>();
		for(Point pt: points){
			String tmp = searchUpDown(pt);
			if(tmp.length() > 1){
				mots.add(tmp);
			}
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
	}*/
	   public static void main (String[] args){
		   System.out.println(new Plateau(15));
	}


	public List<String> getMy_word() {
		return my_word;
	}

	public void setMy_word(List<String> my_word) {
		this.my_word = my_word;
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
	
	public  int calculScore(List<String> liste, Alphabet liste_letter) {
		int score = 0;
		for(String str : liste){
			for(int i = 0; i < str.length(); i++){
				score += Alphabet.findScoreForLetter(str.charAt(i), liste_letter.getListe_all_letter());
			}
		}
		return score;
	}

	public String getMot_courantToString() {
		String liste ="";
		for(String str: my_word){
			liste+="-"+str;
		}
		return liste;
	}
}
