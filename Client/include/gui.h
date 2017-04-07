#ifndef GUI_H
#define GUI_H

#define UTF8(string) g_locale_to_utf8(string, -1, NULL, NULL, NULL)

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
	void editPlateau(GtkWidget* event_box,GdkEventButton *event,gpointer data);
	void createGrille ();
	void selectLetter(GtkWidget* event_box,GdkEventButton *event,gpointer data);
	void logger(char * message, int newline);
	void createScoreDisplay();
	void loadImage();
	void resize_image(GtkWidget *widget);
	void saveToLocal();
	void reset_placement();
	void sendMessageEvent(GtkButton *button, GtkWidget * input);
	char * timeToString(int chrono);
#endif
