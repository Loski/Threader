package communication;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

public class VerificationFR implements VerificationMot{

	ArrayList<String> listemot;
	public VerificationFR() {
		listemot = new ArrayList<>();
		
		try{
			InputStream flux=new FileInputStream("ressource/scrabble.dico"); 
			InputStreamReader lecture=new InputStreamReader(flux);
			BufferedReader buff=new BufferedReader(lecture);
			String ligne;
			while ((ligne=buff.readLine())!=null){
				listemot.add(ligne);
			}
				buff.close(); 
			}catch (Exception e){
				System.out.println(e.toString());
			}
		Server.logger.info("Dictionnaire français");
	}
	@Override
	public boolean isRealWord(String s) {
		for(String mot: listemot){
			if(mot.equals(s))
				return true;
		}
		return false;
	}
	
	public static void main (String[] args){
		   System.out.println(new VerificationFR().isRealWord("NORMAL"));
	}

}
