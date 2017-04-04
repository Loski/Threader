#ifndef GUI_H
#define GUI_H
#include <gtk/gtk.h>
	GtkWidget *init_window();
	GtkWidget * init_main_container();
	GtkWidget * createInputText(GtkWidget *buffer);
	void cb_quit (GtkWidget *p_widget);
	void createTexte(GtkWidget * p_window, GtkWidget * p_label, char const * texte);
	void askConnexion(GtkButton *button, GtkWidget * p_input);
	void connexion_windows();
	void proposerMot(GtkButton *button);
	 void lancementGUI();
	void editPlateau(GtkButton *button);
	void createGrille ();
	void selectLetter(GtkButton * button);
	void logger(char * message);
	
#endif
