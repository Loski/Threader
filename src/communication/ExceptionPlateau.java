package communication;

public class ExceptionPlateau extends Exception{
	/**
	 * 
	 */
	private static final long serialVersionUID = -1036815448619192977L;
	String code_erreur;
	String message;
	public ExceptionPlateau(String message, String code_errors) {
		super(message);
		this.code_erreur = code_errors;
		this.message = message;
	}
	public String getCode_erreur() {
		return code_erreur;
	}
	public void setCode_erreur(String code_erreur) {
		this.code_erreur = code_erreur;
	}
	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}

	
	
}
