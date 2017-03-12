package communication;

public class ExceptionPlateau extends Exception{
	String code_erreur;
	public ExceptionPlateau(String string, String code_errors) {
		super();
		this.code_erreur = code_errors;
	}

	public ExceptionPlateau(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
		// TODO Auto-generated constructor stub
	}

	public ExceptionPlateau(String message, Throwable cause) {
		super(message, cause);
		// TODO Auto-generated constructor stub
	}

	public ExceptionPlateau(String message) {
		super(message);
		// TODO Auto-generated constructor stub
	}

	public ExceptionPlateau(Throwable cause) {
		super(cause);
		// TODO Auto-generated constructor stub
	}
	
}
