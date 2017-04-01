#include <gtk/gtk.h>
#include <string.h>
#include "../include/gui.h"
#include "../include/joueur.h"
#include "../include/connexion.h"
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

GtkWidget * createGrille (GtkWidget * p_container){
	GtkWidget *  table = gtk_table_new (TAILLE_PLATEAU, TAILLE_PLATEAU, TRUE);
	
	/*GtkWidget * text = gtk_text_view_new();
	
	GtkTextBuffer * buffer = gtk_text_view_get_buffer(GTK_TEXT_VIEW(text));
	
	GtkTextIter iter ;
	
	gtk_text_buffer_get_iter_at_offset(buffer, &iter, 0);
	
	gtk_text_buffer_insert(buffer, &iter, "A", -1);
	
	gtk_table_attach_defaults (GTK_TABLE (table), text, 0, 1, 0, 1);
	
	gtk_text_view_set_editable(GTK_TEXT_VIEW(text), FALSE);*/
	
	
	for(int i=0;i<TAILLE_PLATEAU;i++)
		for(int j=0;j<TAILLE_PLATEAU;j++)
		{
			GtkWidget * text =  gtk_button_new_with_label ("KEK");
	
			gtk_button_set_relief(GTK_BUTTON(text), GTK_RELIEF_NONE); 
	
			gtk_table_attach_defaults (GTK_TABLE (table), text, i, i+1, j, j+1);
	
			
		}
	
	gtk_grid_attach (GTK_GRID (p_container), table, 0,500,1000,500);

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
  
  createGrille (p_grid);
}

void askConnexion(GtkButton *button, GtkWidget * input){
	char *nom = (char*)gtk_entry_get_text(GTK_ENTRY(input));
	if(strlen(nom) < 1)
		return;
	else{
	    initClient(&client, nom);
	    bind_joueur_to_session(&client, &session);
	    connexion_joueur(&client);
	    if(verification_connexion(&session) < 0){
	    	//bad connexion
	    	puts("bad connexion");
	    }else{
	    	puts("ça marche.");
	    	initThread(&session);
	    }
	    //pthread_join(client.input, NULL);
	}	
}

void proposerMot(GtkButton *button, GtkWidget * input){
	char *proposition = (char*)gtk_entry_get_text(GTK_ENTRY(input));
	if(strlen(proposition) > 1)
	{
		//annoncer_placement(proposition,session.p_client);
	}
}
