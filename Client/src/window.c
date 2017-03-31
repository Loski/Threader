#include <gtk/gtk.h>
#include <string.h>
#include "../include/gui.h"
#include "../include/joueur.h"
#include "../include/scrabble.h"
#include "../include/transmission.h"

JoueurClient client;
Session session;

GtkWidget *init_window(){
	GtkWidget * p_window = gtk_window_new(GTK_WINDOW_TOPLEVEL);
	gtk_window_set_title(GTK_WINDOW(p_window), "Scrabble");
	gtk_window_set_default_size(GTK_WINDOW(p_window), 1000, 1000);
	gtk_window_set_position(GTK_WINDOW(p_window), GTK_WIN_POS_CENTER);
	g_signal_connect (G_OBJECT (p_window), "destroy",  G_CALLBACK (cb_quit), NULL);
	return p_window;
}


GtkWidget * init_main_container(GtkWidget * p_window){
	GtkWidget * p_main_box = gtk_grid_new ();
  	gtk_container_add (GTK_CONTAINER (p_window), p_main_box);
  	return p_main_box;
}
void cb_quit (GtkWidget *p_widget)
{
  gtk_main_quit();
}


GtkWidget * createGrid(){
	GtkWidget *  widget= NULL;
}

GtkWidget * createInputText(GtkWidget * p_container){
	GtkWidget* inputText = gtk_entry_new();
	gtk_grid_attach (GTK_GRID (p_container), inputText, 70,0,56,561);
	return inputText;
}

void createTexte(GtkWidget * p_grid, GtkWidget * p_label, char const * texte){
	gtk_label_set_markup(GTK_LABEL(p_label), texte);
	gtk_grid_attach(GTK_GRID(p_grid),p_label,50,10,10,10);
}


void connexion_windows(GtkWidget * p_grid){
  GtkWidget *p_label=gtk_label_new(NULL);
  GtkWidget * inputText = createInputText(p_grid);
  createTexte(p_grid, p_label, "<span>CONNEXION</span>");
  GtkWidget * button_connexion =  gtk_button_new_with_label ("Se connecter");
  gtk_grid_attach(GTK_GRID(p_grid),button_connexion, 60,10,10,10);
  g_signal_connect(G_OBJECT(button_connexion), "clicked", G_CALLBACK(askConnexion), inputText);
}

void askConnexion(GtkButton *button, GtkWidget * input){
	char *nom = (char*)gtk_entry_get_text(GTK_ENTRY(input));
	if(strlen(nom) < 1)
		return;
	else{
	    initClient(&client, nom);
	    bind_joueur_to_session(&client, &session);
	    connexion_joueur(&client);
	    initThread(&session);
	    //pthread_join(client.input, NULL);
	}
		
}