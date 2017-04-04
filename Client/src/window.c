#include <gtk/gtk.h>
#include <stdlib.h>
#include <string.h>
#include "../include/gui.h"
#include "../include/joueur.h"
#include "../include/connexion.h"
#include "../include/scrabble.h"
#include "../include/transmission.h"

JoueurClient client;
Session session;

GtkWidget * p_main_grid;

/* Fenêtre de connexion*/

GtkWidget * label_connexion;
GtkWidget * button_connexion;
GtkWidget * inputTextConnexion;
GtkWidget * errorField;
GtkWidget * p_window;


/* Fenêtre de jeu */

GtkWidget * grille;
GtkWidget* plateau[TAILLE_PLATEAU*TAILLE_PLATEAU];
GtkWidget * tirage;

GtkWidget * consoleArea;

char selectedLetter = '1';

void lancementGUI(){
	init_window();
	init_main_container();
	connexion_windows();
	gtk_widget_show_all (p_window);  /* Lancement de la boucle principale */
}

GtkWidget *init_window(){
	p_window = gtk_window_new(GTK_WINDOW_TOPLEVEL);
	gtk_window_set_title(GTK_WINDOW(p_window), "Scrabble");
	gtk_window_set_default_size(GTK_WINDOW(p_window), 1000, 1000);
	gtk_window_set_position(GTK_WINDOW(p_window), GTK_WIN_POS_CENTER);
	g_signal_connect (G_OBJECT (p_window), "destroy",  G_CALLBACK (cb_quit), NULL);
	return p_window;
}


GtkWidget * init_main_container(){
	p_main_grid = gtk_grid_new ();
  	gtk_container_add (GTK_CONTAINER (p_window), p_main_grid);
  	return p_main_grid;
}
void cb_quit (GtkWidget *p_widget)
{
  gtk_main_quit();
}

void createGrille (){
	grille = gtk_grid_new ();
	
	int k = 0;
	
	for(int i=0;i<TAILLE_PLATEAU;i++)
		for(int j=0;j<TAILLE_PLATEAU;j++)
		{	
			char c = session.plateau[k];
			char *ptr = NULL;
			ptr = malloc(2*sizeof(char));
			if(ptr == NULL){
				puts("Erreur pointeur GtkWidget");
			}
			ptr[0] = c;
			ptr[1] = '\0';
			
			/*GtkWidget * text = gtk_text_view_new();
	
			GtkTextBuffer * buffer = gtk_text_view_get_buffer(GTK_TEXT_VIEW(text));
				
			GtkTextIter iter ;
				
			gtk_text_buffer_get_iter_at_offset(buffer, &iter, -1);
				
			gtk_text_buffer_insert(buffer, &iter, "", -1);
				
			g_signal_connect(G_OBJECT(buffer), "insert-text", G_CALLBACK(textLimiter), text)	
				
			gtk_grid_attach(GTK_GRID(grille), text, i, j, 1, 1);*/
			
			GtkWidget * text; 
	
			if (c == '0')
			{
				text =  gtk_button_new_with_label ("_");
				g_signal_connect(G_OBJECT(text), "clicked", G_CALLBACK(editPlateau),NULL);
			}
			else
			{
				text =  gtk_button_new_with_label (ptr);
			}
	
			gtk_button_set_relief(GTK_BUTTON(text), GTK_RELIEF_NONE); 
	
			gtk_grid_attach(GTK_GRID(grille), text, j, i, 1, 1);
			
			plateau[k] = text;
			

			
			k++;
		}
		
		gtk_grid_attach (GTK_GRID (p_main_grid), grille, 0,0,1000,500);
		
		GtkWidget * text =  gtk_button_new_with_label ("Valider le mot");
		
		g_signal_connect(G_OBJECT(text), "clicked", G_CALLBACK(proposerMot),NULL);
		
		gtk_grid_attach (GTK_GRID (p_main_grid), text, 400,800,200,100);
		
		
		gtk_widget_show_all (p_window);  /* Lancement de la boucle principale */
}

void createTirageDisplay()
{
	tirage = gtk_grid_new (); // A changer en ligne
	
	for(int i=0;i<TAILLE_TIRAGE;i++)
	{
		char c = session.tirage[i];
		char *ptr = NULL;
		ptr = malloc(2*sizeof(char));
		if(ptr == NULL){
			puts("Erreur pointeur tirage letter");
		}
		ptr[0] = c;
		ptr[1] = '\0';
		
		GtkWidget * text =  gtk_button_new_with_label (ptr);
		
		g_signal_connect(G_OBJECT(text), "clicked", G_CALLBACK(selectLetter),NULL);
	
		gtk_button_set_relief(GTK_BUTTON(text), GTK_RELIEF_NONE); 
	
		gtk_grid_attach(GTK_GRID(tirage), text, i, 0, 1, 1);
		
	}
		
	gtk_grid_attach (GTK_GRID (p_main_grid), tirage, 0,600,1000,100);
	gtk_widget_show_all (p_window);
}

void createConsoleLog()
{
    consoleArea = gtk_text_view_new();
    GtkWidget* scrollbar= gtk_vscrollbar_new(gtk_text_view_get_vadjustment(GTK_TEXT_VIEW(consoleArea)));
    GtkWidget* textEntry = gtk_entry_new();

    GtkWidget* console = gtk_table_new(3, 2, FALSE);

	gtk_text_view_set_editable (GTK_TEXT_VIEW(consoleArea),FALSE);

    gtk_table_attach_defaults(GTK_TABLE(console), consoleArea, 0, 1, 0, 1);
    gtk_table_attach_defaults(GTK_TABLE(console), scrollbar, 1, 2, 0, 1);
    //gtk_table_attach_defaults(GTK_TABLE(console), textEntry, 0, 2, 1, 2);
    //This code sets the preferred size for the widget, so it does not ask for extra space
    gtk_widget_set_size_request(consoleArea, 1000, 240);
    
    gtk_grid_attach (GTK_GRID (p_main_grid), console, 0,900,1000,1000);
    
    gtk_widget_show_all (p_window);

}

void selectLetter(GtkButton * button)
{
	char * label = (char *)gtk_button_get_label (button);
	
	printf("switch letter to : %c",*label);
	
	selectedLetter = *label;
}

void editPlateau(GtkButton *button)
{
	if(selectedLetter!='1' && selectedLetter!='_')
	{
		char *ptr = NULL;
		ptr = malloc(2*sizeof(char));
		if(ptr == NULL){
			puts("Erreur pointeur tirage edit plateau");
		}
		ptr[0] = selectedLetter;
		ptr[1] = '\0';
		
		gtk_button_set_label(GTK_BUTTON(button),ptr);
		
		int i;
		
		for(i=0;i<TAILLE_PLATEAU;i++)
		{
			if((GtkButton *)plateau[i]==button)
				break;
		}
		
		session.plateau[i]=selectedLetter;
		selectedLetter='_';
		
	}
}

GtkWidget * createInputText(GtkWidget * p_container){
	inputTextConnexion = gtk_entry_new();
	gtk_grid_attach (GTK_GRID (p_container), inputTextConnexion, 70,0,56,561);
	return inputTextConnexion;
}

void createTexte(GtkWidget * p_grid, GtkWidget * p_label, char const * texte){
	gtk_label_set_markup(GTK_LABEL(p_label), texte);
	gtk_grid_attach(GTK_GRID(p_grid),p_label,50,10,10,10);
}


void connexion_windows(){	
  label_connexion=gtk_label_new(NULL);
  GtkWidget * inputText = createInputText(p_main_grid);
  createTexte(p_main_grid, label_connexion, "<span>CONNEXION</span>");
  button_connexion =  gtk_button_new_with_label ("Se connecter");
  gtk_grid_attach(GTK_GRID(p_main_grid),button_connexion, 60,10,10,10);
  g_signal_connect(G_OBJECT(button_connexion), "clicked", G_CALLBACK(askConnexion), inputText);
  
}

/*void textLimiter(GtkTextBuffer * buffer,GtkWidget * text_view)
{
	GtkTextIter start, end;
	GtkTextBuffer *buffer = gtk_text_view_get_buffer (text_view);
	gchar *text;

	gtk_text_buffer_get_bounds (buffer, &start, &end);
	text = gtk_text_buffer_get_text (buffer, &start, &end, FALSE);

	if(strlen(text>1))
	{
		GtkTextIter range_start, range_end;
		gtk_text_buffer_get_start_iter(buffer, &range_start);
		range_end = range_start;
		gtk_text_iter_forward_chars(&range_end,1);
		gtk_text_buffer_delete(buffer, &range_start, &range_end);
	}

	g_free (text)
}*/

void logger(char * message)
{
	GtkTextBuffer * buffer = gtk_text_view_get_buffer(GTK_TEXT_VIEW(consoleArea));
			
	GtkTextIter iter ;
				
	gtk_text_buffer_get_end_iter (buffer,&iter);
				
	gtk_text_buffer_insert(buffer, &iter, message, -1);
	
	gtk_text_buffer_get_end_iter (buffer,&iter);
	
	gtk_text_buffer_insert(buffer, &iter, "\n", -1);
}

void refresh_GUI()
{
	
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
	    	errorField=gtk_label_new(NULL);
			gtk_label_set_markup(GTK_LABEL(errorField), "ERREUR DE CONNEXION");
			gtk_grid_attach(GTK_GRID(p_main_grid),errorField,0,150,1000,500);
			gtk_widget_show_all (p_window); 
	    	
	    }else{

	    	puts("ça marche.");
	    	initThread(&session);
	    	gtk_widget_destroy (input);
	    	gtk_widget_destroy (button_connexion);
	    	gtk_widget_destroy (label_connexion);
	    	//gtk_widget_destroy (errorField);	    	
	    	print_session(&session);
	    	createTirageDisplay();
	    	createGrille();
	    	createConsoleLog();
	    	
	    	char str[80];
	    	
	    	strcpy(str, "BIENVENUE, ");
			strcat(str, (client.p_joueur)->username);
	    	
	    	logger(str);
	    	
	    	g_timeout_add (1000,refresh_GUI,NULL);
	    	
	    }
	    //pthread_join(client.input, NULL);
	}	
}

void proposerMot(GtkButton *button){
	
	annoncer_placement(session.plateau,session.p_client);

}
