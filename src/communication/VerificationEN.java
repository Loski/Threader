package communication;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class VerificationEN implements VerificationMot{
	public VerificationEN(){
		Server.logger.info("Dictionnaire anglais");
	}
	  public  boolean isRealWord(String s){ 
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
		   System.out.println(new VerificationEN().isRealWord("normal"));
	}
}
