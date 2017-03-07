package protocole;

public enum Protocole {
	
	// connexion
	CONNEXION{
		public final String toString(){
			return "CONNEXION";
		}
	},
	BIENVENUE{
		public String toString(){
			return "BIENVENUE";
		}
	},
	CONNECTE{
		public String toString(){
			return "CONNECTE";
		}
	},
	REFUS{
		public String toString(){
			return "REFUS";
		}
	},
	
	// deconnexion
	SORT,
	DECONNEXION,
	
	// debut partie
	SESSION,
	VAINQUEUR,
	
	// Phase de recherche
	TOUR,
	TROUVE,
	RVALIDE,
	RINVALIDE,
	RATROUVE,
	RFIN,
	
	// Phase de soumission
	SVALIDE,
	SINVALIDE,
	SFIN,
	
	// Phase de résultat
	BILAN,
	
	// Chat
	ENVOIE,
	PENVOIE,
	RECEPTION,
	PRECEPTION,
	
	//Best word
	MEILLEUR
	;
}