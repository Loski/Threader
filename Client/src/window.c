#include <gtk/gtk.h>
#include <stdlib.h>
#include <string.h>
#include <stdbool.h>
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
GtkWidget * plateau[TAILLE_PLATEAU*TAILLE_PLATEAU];
GtkWidget * event_box_list[TAILLE_PLATEAU*TAILLE_PLATEAU];
GtkWidget * event_box_tirage[TAILLE_TIRAGE];
GtkWidget * tirage;
GtkWidget * scoreDisplay;
//GtkWidget * players[][2];
GdkPixbuf * images[27];

GtkWidget * consoleArea;

char selectedLetter = '1';

void lancementGUI(){
	init_window();
	init_main_container();
	loadImage();
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

void resize_image(GtkWidget *widget)
{
	GdkPixbuf * pixbuf = gtk_image_get_pixbuf(GTK_IMAGE(widget));
	if (pixbuf == NULL)
	{
		g_printerr("Failed to resize image\n");
	}
	
	pixbuf = gdk_pixbuf_scale_simple(pixbuf,40, 40, GDK_INTERP_BILINEAR);
	
	gtk_image_set_from_pixbuf(GTK_IMAGE(widget), pixbuf);
}

void loadImage()
{
	char c = 'A';
	
	for(int i=0;i<27;i++)
	{
		GtkWidget *widget = NULL;
		
		char lettre[2] = "";
		lettre[0] = c;
		lettre[1] = '\0';
		char src[10] = "img/";
		
		if(i==26)
			strcat(src,"_");
		else
		{
			strcat(src,lettre);
		}
		
		strcat(src, ".png");
		
		widget = gtk_image_new_from_file ("img/K.png"); 
		
		resize_image(widget);
		
		images[i] = gtk_image_get_pixbuf(GTK_IMAGE(widget));
		
		c = (char) (c+1);
	}
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
			
			GtkWidget * event_box;
			GtkWidget * image;
	
			if (c == '0')
			{
				/*text =  gtk_button_new_with_label ("_");
				g_signal_connect(G_OBJECT(text), "clicked", G_CALLBACK(editPlateau),NULL);
				gtk_button_set_relief(GTK_BUTTON(text), GTK_RELIEF_NONE); */

				image = gtk_image_new_from_pixbuf (images[26]);

				event_box = gtk_event_box_new ();
				
				event_box_list[k] = event_box;

				gtk_container_add (GTK_CONTAINER (event_box), image);

				g_signal_connect (G_OBJECT (event_box),"button_press_event",G_CALLBACK (editPlateau),image);
			}
			else
			{
				//text =  gtk_button_new_with_label (ptr);
				
				int index = (char)toupper(c) - 'A';
				
				image = gtk_image_new_from_pixbuf (images[26]);
			}
	
			gtk_grid_attach(GTK_GRID(grille), event_box, j, i, 1, 1);
			
			plateau[k] = image;
			
			k++;
		}
		
		gtk_widget_set_size_request(grille, 500, 500);
		
		gtk_grid_attach (GTK_GRID (p_main_grid), grille, 0,0,1,1);
		
		
		GtkWidget * text =  gtk_button_new_with_label ("Valider le mot");
		
		g_signal_connect(G_OBJECT(text), "clicked", G_CALLBACK(proposerMot),NULL);
		
		
		gtk_grid_attach (GTK_GRID (p_main_grid), text, 400,800,200,100);
		
		
		gtk_widget_show_all (p_window);  /* Lancement de la boucle principale */
}

void createScoreDisplay()
{
	scoreDisplay = tirage = gtk_grid_new ();
	
	for(int i=0;i<session.nombre_joueur;i++)
	{
		GtkWidget * name = gtk_label_new(NULL);
		
		printf("USERNAME :%s",(session.p_liste_joueur)->username);
		
		gtk_label_set_markup(GTK_LABEL(name),(session.p_liste_joueur)->username);
		gtk_grid_attach (GTK_GRID (p_main_grid), name, 600,0,400,200);
	}
	
	gtk_widget_show_all (p_window);
}

void createTirageDisplay()
{
	tirage = gtk_grid_new (); // A changer en ligne
	
	for(int i=0;i<TAILLE_TIRAGE;i++)
	{
		char c = session.tirage[i];
		
		int index = (char)toupper(c) - 'A';
		
		GtkWidget * image = gtk_image_new_from_pixbuf (images[index]);

		GtkWidget * event_box = gtk_event_box_new ();
				
		event_box_tirage[i] = event_box;

		gtk_container_add (GTK_CONTAINER (event_box), image);
		
		g_signal_connect (G_OBJECT (event_box),"button_press_event",G_CALLBACK (selectLetter),image);
	
		gtk_grid_attach(GTK_GRID(tirage), event_box, i, 0, 1, 1);
		
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

void selectLetter(GtkWidget* event_box,GdkEventButton *event,gpointer data)
{
	int i;
	
	for(i=0;i<TAILLE_PLATEAU*TAILLE_PLATEAU;i++)
	{
		if(event_box_tirage[i]==event_box)
			break;
	}
	
	selectedLetter = session.tirage[i];
}

void editPlateau(GtkWidget* event_box,GdkEventButton *event,gpointer data)
{
	if(selectedLetter!='1' && selectedLetter!='_')
	{
		char lettre[1] = "";
		lettre[0] = (char)toupper(selectedLetter);
		char src[6] = "";
		strcat(src,lettre);
		strcat(src, ".png\0");	
		
		//gtk_button_set_label(GTK_BUTTON(button),ptr);
		
		int i;
		
		for(i=0;i<TAILLE_PLATEAU*TAILLE_PLATEAU;i++)
		{
			if(event_box_list[i]==event_box)
				break;
		}
		
		gtk_image_set_from_pixbuf (GTK_IMAGE(plateau[i]),gtk_image_get_pixbuf(GTK_IMAGE(plateau[i])));
		
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

gboolean refresh_GUI(gpointer user_data)
{	
	char * message = get_message(session.messages);
	
	if(message!=NULL)
	{
		logger(message);
		for(int i=0;i<TAILLE_PLATEAU*TAILLE_PLATEAU;i++)
		{	
			char c = session.plateau[i];
			if (c == '0')
			{
				//gtk_button_set_label(GTK_BUTTON(plateau[i]),"_");
			}
			else
			{
				char * ptr = malloc(2*sizeof(char));
				
				ptr[0] = c;
				ptr[1] = '\0';
				
				//gtk_button_set_label(GTK_BUTTON(plateau[i]),ptr);
			}
		}
	}
	return true;
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
	    	createTirageDisplay();
	    	createGrille();
	    	createConsoleLog();
	    	createScoreDisplay();
	    	
	    	print_session(&session);
	    	
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
