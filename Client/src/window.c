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
GtkWidget * errorField = NULL;
GtkWidget * p_window;


/* Fenêtre de jeu */

GtkWidget * grille;
GtkWidget * plateau[TAILLE_PLATEAU*TAILLE_PLATEAU];
GtkWidget * tirage[TAILLE_TIRAGE];
GtkWidget * event_box_list[TAILLE_PLATEAU*TAILLE_PLATEAU];
GtkWidget * event_box_tirage[TAILLE_TIRAGE];
GtkWidget * tirageDisplay;
GtkWidget * scoreDisplay = NULL;
GdkPixbuf * images[27];

GtkWidget * consoleArea;
GtkWidget * tourDisplay;
GtkWidget * phaseDisplay;
GtkWidget * timer;

GtkWidget * MeilleurMot;

char plateau_local[TAILLE_PLATEAU*TAILLE_PLATEAU];
char tirage_local[TAILLE_TIRAGE];

int selectedLetter = -1;

char * ip;

void lancementGUI(char * ip_arg){
	ip = ip_arg;
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
  if(session.p_client != NULL){
  	deconnexion_joueur(session.p_client);
  }
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
		
		widget = gtk_image_new_from_file (src); 
		
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
			}
			else
			{
				//text =  gtk_button_new_with_label (ptr);
				
				int index = (char)toupper(c) - 'A';
				
				image = gtk_image_new_from_pixbuf (images[index]);
			}
	
			event_box = gtk_event_box_new ();
				
			event_box_list[k] = event_box;

			gtk_container_add (GTK_CONTAINER (event_box), image);

			g_signal_connect (G_OBJECT (event_box),"button_press_event",G_CALLBACK (editPlateau),image);
	
			gtk_grid_attach(GTK_GRID(grille), event_box, j, i, 1, 1);
			
			plateau[k] = image;
			
			k++;
		}
		
		gtk_widget_set_size_request(grille, 500, 500);
		
		gtk_grid_attach (GTK_GRID (p_main_grid), grille, 0,100,500,500);
		
		
		GtkWidget * text =  gtk_button_new_with_label ("Valider le mot");
		
		g_signal_connect(G_OBJECT(text), "clicked", G_CALLBACK(proposerMot),NULL);
		
		
		gtk_grid_attach (GTK_GRID (p_main_grid), text, 400,800,200,100);
		
		GtkWidget * reset =  gtk_button_new_with_label ("Reset");
		
		g_signal_connect(G_OBJECT(reset), "clicked", G_CALLBACK(reset_placement),NULL);
		
		
		gtk_grid_attach (GTK_GRID (p_main_grid), reset, 200,800,200,100);
		
		
		gtk_widget_show_all (p_window);  /* Lancement de la boucle principale */
}

void createScoreDisplay()
{
	if(scoreDisplay!=NULL)
		gtk_widget_destroy (scoreDisplay);
	
	GtkWidget * scoreGrid = gtk_grid_new ();
	
	scoreDisplay = gtk_scrolled_window_new(NULL, NULL);

    //This code sets the preferred size for the widget, so it does not ask for extra space
    gtk_widget_set_size_request(scoreGrid, 300, 500);
	
	for(int i=0;i<session.nombre_joueur;i++)
	{
				
		GtkWidget * name = gtk_label_new(NULL);
		
		gtk_widget_set_size_request(name, 100, 20);
		
		gtk_label_set_markup(GTK_LABEL(name),session.p_liste_joueur[i].username);
		
		gtk_grid_attach (GTK_GRID (scoreGrid), name, 0,i,1,1);
		
		GtkWidget * score = gtk_label_new(NULL);
		
		char value[5] = "";
		sprintf(value, "%d",session.p_liste_joueur[i].score);
		
		gtk_label_set_markup(GTK_LABEL(score),value);
		
		gtk_grid_attach (GTK_GRID (scoreGrid), score, 1,i,1,1);
	}
	
	 gtk_scrolled_window_set_policy(GTK_SCROLLED_WINDOW(scoreDisplay), GTK_POLICY_NEVER,
                               GTK_POLICY_ALWAYS);
	
	gtk_scrolled_window_add_with_viewport(GTK_SCROLLED_WINDOW(scoreDisplay), scoreGrid);
    
	
	gtk_grid_attach (GTK_GRID (p_main_grid), scoreDisplay, 600,150,400,500);
	
	gtk_widget_show_all (p_window);
}

void createTirageDisplay()
{
	tirageDisplay = gtk_grid_new (); // A changer en ligne
	
	for(int i=0;i<TAILLE_TIRAGE;i++)
	{
		char c = session.tirage[i];
		
		int index = 26;
		
		if((session.phase==REC || session.phase==SOU) && c!='_')
			index = (char)toupper(c) - 'A';
			
		tirage[i] = gtk_image_new_from_pixbuf (images[index]);

		GtkWidget * event_box = gtk_event_box_new ();
				
		event_box_tirage[i] = event_box;

		gtk_container_add (GTK_CONTAINER (event_box), tirage[i]);
		
		if(c!='_')
			g_signal_connect (G_OBJECT (event_box),"button_press_event",G_CALLBACK (selectLetter),tirage[i]);
	
		gtk_grid_attach(GTK_GRID(tirageDisplay), event_box, i, 0, 1, 1);
		
	}
	
	MeilleurMot = gtk_label_new(NULL);
	gtk_label_set_markup(GTK_LABEL(MeilleurMot), "Meilleur mot [ ]");
	
	gtk_grid_attach (GTK_GRID (p_main_grid), tirageDisplay, 0,600,1000,100);
	gtk_grid_attach (GTK_GRID (p_main_grid), MeilleurMot, 0,800,200,100);
	gtk_widget_show_all (p_window);
}

void setPhaseDisplay()
{
	if(session.phase==DEB)
		gtk_label_set_markup(GTK_LABEL(phaseDisplay), "Début de Session");
	else if(session.phase==REC)
		gtk_label_set_markup(GTK_LABEL(phaseDisplay), "Phase de Recherche");
	else if(session.phase==SOU)
		gtk_label_set_markup(GTK_LABEL(phaseDisplay), "Phase de Soumission");
	else if(session.phase==RES)
		gtk_label_set_markup(GTK_LABEL(phaseDisplay), "Résultat");
}

void refreshTimer()
{
	char * time_val = timeToString(session.temps);
	
	gtk_label_set_markup(GTK_LABEL(timer), time_val);
}

void createPhaseDisplay()
{
	GtkWidget * phaseGrid = gtk_grid_new ();
	
	tourDisplay = gtk_label_new(NULL);
	phaseDisplay = gtk_label_new(NULL);
	timer = gtk_label_new(NULL);
	
	char tour[5] = "";
	sprintf(tour, "%d", session.tour);
	char tourTexte[11] = "TOUR ";
	
	strcat(tourTexte,tour);
	
	gtk_label_set_markup(GTK_LABEL(tourDisplay), tourTexte);
	
	
	//refreshTimer();
	setPhaseDisplay();
	
	gtk_widget_set_size_request(tourDisplay, 100, 10);
	gtk_widget_set_size_request(phaseDisplay, 100, 10);
	gtk_widget_set_size_request(timer, 100, 10);
	
	gtk_grid_attach(GTK_GRID(phaseGrid),tourDisplay,0,0,1,1);
	gtk_grid_attach(GTK_GRID(phaseGrid),phaseDisplay,1,0,1,1);
	gtk_grid_attach(GTK_GRID(phaseGrid),timer,2,0,1,1);
	
	gtk_grid_attach(GTK_GRID(p_main_grid),phaseGrid,600,10,400,100);
	
	gtk_widget_show_all (p_window);
}

void createConsoleLog()
{
    consoleArea = gtk_text_view_new();
	GtkWidget *scwin = gtk_scrolled_window_new(NULL, NULL);
	//gtk_container_add(GTK_CONTAINER(scwin), consoleArea);

	gtk_text_view_set_editable (GTK_TEXT_VIEW(consoleArea),FALSE);
    
    gtk_scrolled_window_set_policy(GTK_SCROLLED_WINDOW(scwin), GTK_POLICY_AUTOMATIC,
                               GTK_POLICY_ALWAYS);
    
    gtk_scrolled_window_add_with_viewport(GTK_SCROLLED_WINDOW(scwin), consoleArea);                           
                            
    //This code sets the preferred size for the widget, so it does not ask for extra space
    gtk_widget_set_size_request(consoleArea, 1000, 200);
    gtk_widget_set_size_request(scwin, 500, 200);
    
    gtk_grid_attach (GTK_GRID (p_main_grid), scwin, 0,900,1000,50);
    
	GtkWidget * inputText = createInputText(p_main_grid);
	inputText = gtk_entry_new();
	gtk_grid_attach (GTK_GRID (p_main_grid), inputText, 150,950,500,50);
	GtkWidget * button_send =  gtk_button_new_with_label ("Envoyer");
	gtk_grid_attach(GTK_GRID(p_main_grid),button_send, 700,950,300,50);
	g_signal_connect(G_OBJECT(button_send), "clicked", G_CALLBACK(sendMessageEvent), inputText);
	
	gtk_widget_show_all (p_window);

}

void sendMessageEvent(GtkButton *button, GtkWidget * input)
{
	char * mess = (char*)gtk_entry_get_text(GTK_ENTRY(input));
		
	if(strlen(mess) < 1)
		return;
	else{
		
		if(mess[0]=='/' && mess[1]=='w')
		{
			int i=2;
			int compteurPseudo = 0;
			int compteurMessage = 0;
			int first_espace = 0;
			int second_espace = 0;
			char pseudo[1024];
			char mess_age[1024];
			bool pseudoBool = true;
			while(mess[i] == ' '){
				i++;
			}
			while (mess[i]!='\0')
			{
				if(mess[i] == ' '){
					while(mess[i] == ' '){
						i++;
					}
					if(pseudoBool){
						pseudo[compteurPseudo] = '\0';
						pseudoBool = false;
						break;
					}
				}
				else if(pseudoBool){
					pseudo[compteurPseudo] = mess[i];
					compteurPseudo++;
				}else{
					mess_age[compteurMessage] = mess[i];
					compteurMessage++;
				}
				i++;
										
			}

			while (mess[i]!='\0')
			{
				mess_age[compteurMessage] = mess[i];
				compteurMessage++;
				i++;
			}
			mess_age[compteurMessage] = '\0';
			
			printf("\npseudo =%s", pseudo);
			printf("\nmessage =%s", mess_age);


			char message[6000] = PENVOIE;
			strcat(message, pseudo);
			strcat(message, "/");
			strcat(message, mess_age);
			strcat(message, "/\n");
			
			if(!sendMessage(client.socket, message))
				logger("Message non envoyé",1);
			else
			{
				logger("Message privé envoyé à ",0);
				logger(pseudo,1);
			}
		}
		else
		{
			char message[255] = ENVOIE;
			strcat(message, mess);
			strcat(message, "/\n");
			/*logger("(MOI)>",0);
			logger(mess,1);*/
			
			if(!sendMessage(client.socket, message))
				logger("Message non envoyé",1);
		}

	}
	
	gtk_entry_set_text(GTK_ENTRY(input),"");
}

void selectLetter(GtkWidget* event_box,GdkEventButton *event,gpointer data)
{
	if(session.phase!=REC && session.phase!=SOU)
	{
		printf("PHASE : %d\n",session.phase);
		return;
	}
		
	int i;
	
	for(int x=0;x<TAILLE_TIRAGE;x++)
	{
		if(event_box_tirage[x]==event_box)
			i=x;
		else
		{
			if(tirage_local[x]=='0')
			{
				int index = (char)toupper(session.tirage[x]) - 'A';
				gtk_image_set_from_pixbuf (GTK_IMAGE(tirage[x]),images[index]);
			}
			
			tirage_local[x]=session.tirage[x];
		}
	}
	
	if(tirage_local[i]!='0' && tirage_local[i]!='_')
	{
		gtk_image_set_from_pixbuf (GTK_IMAGE(tirage[i]),images[26]);
		selectedLetter = i;
		tirage_local[i]='0';
	}
	else if(tirage_local[i]=='0')
	{
		selectedLetter = -1;
		int index = (char)toupper(session.tirage[i]) - 'A';
		tirage_local[i]=session.tirage[i];
		gtk_image_set_from_pixbuf (GTK_IMAGE(tirage[i]),images[index]);
	}
	
	gtk_widget_show_all (p_window);
}

void editPlateau(GtkWidget* event_box,GdkEventButton *event,gpointer data)
{
	if(session.phase!=REC && session.phase!=SOU)
		return;
	
	if(selectedLetter!=-1)
	{		
		int index = (char)toupper(session.tirage[selectedLetter]) - 'A';
		
		//gtk_button_set_label(GTK_BUTTON(button),ptr);
		
		int i;
		
		for(i=0;i<TAILLE_PLATEAU*TAILLE_PLATEAU;i++)
		{
			if(event_box_list[i]==event_box)
				break;
		}
		
		if(plateau_local[i]!='0')
			return;
			
		
		gtk_image_set_from_pixbuf (GTK_IMAGE(plateau[i]),images[index]);
		
		tirage_local[selectedLetter]='_';
		plateau_local[i]=session.tirage[selectedLetter];
		selectedLetter=-1;
			
		gtk_widget_show_all (p_window);
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

void logger(char * message,int newline)
{
	GtkTextBuffer * buffer = gtk_text_view_get_buffer(GTK_TEXT_VIEW(consoleArea));
			
	GtkTextIter iter ;
				
	gtk_text_buffer_get_end_iter (buffer,&iter);
				
	gtk_text_buffer_insert(buffer, &iter, message, -1);
	
	for(int i=0;i<newline;i++)
	{		
		gtk_text_buffer_get_end_iter (buffer,&iter);
	
		gtk_text_buffer_insert(buffer, &iter, "\n", -1);
		
	}

}

void refresh_grille()
{				
	int i;
		
	for(i=0;i<TAILLE_PLATEAU*TAILLE_PLATEAU;i++)
	{			
		int index = -1;
		
		if(session.plateau[i]=='0')
			index = 26;
		else
			index = (char)toupper(session.plateau[i]) - 'A';
				
		gtk_image_set_from_pixbuf (GTK_IMAGE(plateau[i]),images[index]);
	}
			
	gtk_widget_show_all (p_window);
}

void refresh_tirage()
{
	int i;
		
	for(i=0;i<TAILLE_TIRAGE;i++)
	{			
		int index = 26;
		
		if(session.tirage[i] !='_')
			index = (char)session.tirage[i] - 'A';
				
		gtk_image_set_from_pixbuf (GTK_IMAGE(tirage[i]),images[index]);
	}
			
	gtk_widget_show_all (p_window);
}

char * timeToString(int chrono)
{	
	int minutes = chrono/60;
	
	int seconde = chrono - minutes*60;
	
	char minutesStr[5] = "";
	if(minutes<10)
		sprintf(minutesStr, "0%d", minutes);
	else
		sprintf(minutesStr, "%d", minutes);
	
	char secondeStr[3] = "";
	if(seconde<10)
		sprintf(secondeStr, "0%d", seconde);
	else
		sprintf(secondeStr, "%d", seconde);
	
	char time_val[10];
	strcpy(time_val,minutesStr);
	strcat(time_val,":");
	strcat(time_val,secondeStr);
	
	return UTF8(time_val);
}

void refresh_tour()
{
	char tour[5] = "";
	sprintf(tour, "%d", session.tour);
	char tourTexte[11] = "TOUR ";
	
	strcat(tourTexte,tour);
	
	gtk_label_set_markup(GTK_LABEL(tourDisplay), tourTexte);
	
	gtk_widget_show_all (p_window);
}

void reset_placement()
{
	saveToLocal();
	refresh_tirage();
	refresh_grille();
}

void hideTirage()
{
	for(int i=0;i<TAILLE_TIRAGE;i++)
	{
		int index = 26;
		
		gtk_image_set_from_pixbuf (GTK_IMAGE(tirage[i]),images[index]);
		
		tirage_local[i]='_';
	}
}

void timer_decrement()
{
	session.temps--;
	
	refreshTimer();
}

void refreshIAmTheBest(bool imthebest){
	
	if(imthebest)
		gtk_label_set_markup(GTK_LABEL(MeilleurMot), "Meilleur mot [X]");
	else
		gtk_label_set_markup(GTK_LABEL(MeilleurMot), "Meilleur mot [ ]");
}

gboolean refresh_GUI(gpointer user_data)
{	
	char * message = get_message(session.messages, &session);
	if(message!=NULL)
	printf("Je retire :%s\n,",message);
	
	setPhaseDisplay();
	
	if(session.temps>0)
		timer_decrement();
	
	if(message!=NULL)
	{
		char **pp_message = NULL, *protocole = NULL;
		int count = split(message, '/', &pp_message);
		 
		if(count < 0)
			return false;
			
		protocole = pp_message[0];
		
		if(strcmp(protocole, TOUR ) == 0){
			logger("-------------------Début d'un nouveau tour-------------------",2);
			refreshIAmTheBest(false);
			refresh_tirage();
			refresh_grille();
			refresh_tour();
			saveToLocal();
			logger("-------------------La phase de recherche débute-------------------",2);
		}else if(strcmp(protocole, MEILLEUR) == 0){ 
          if(strcmp(pp_message[1], "1") == 0){ 
            refreshIAmTheBest(true); 
          }else{ 
            refreshIAmTheBest(false); 
          } 
      	} 
		else if(strcmp(protocole, DECONNEXION) == 0)
		{
			logger("Déconnexion de :",0);
			logger(pp_message[1],1);
			createScoreDisplay();
		}
		else if(strcmp(protocole, CONNECTE) == 0){
			logger("Connexion de :",0);
			logger(pp_message[1],1);
			createScoreDisplay();
		}
		else if(strcmp(protocole, SESSION) == 0){
			logger("-------------------Début d'une nouvelle session-------------------",2);
		}
		else if(strcmp(protocole, RVALIDE) == 0 || strcmp(protocole, SVALIDE) == 0){
			logger("Proposition valide",1);
		}
		else if(strcmp(protocole, RINVALIDE) == 0 || strcmp(protocole, SINVALIDE) == 0){
			
			reset_placement();
			
			logger("Proposition invalide : ",0);
			if(count>1)
				logger(pp_message[1],1);
		}
		else if(strcmp(protocole, RATROUVE) == 0){
			logger("Le joueur [",0);
			if(count>1)
				logger(pp_message[1],0);
			else
				logger("???",0);
			
			logger("] a trouvé un mot",1);
		}
		else if(strcmp(protocole, RFIN) == 0)
		{
			logger("-------------------La phase de recherche est terminée-------------------",2);
			reset_placement();
			logger("-------------------La phase de soumission débute-------------------",2);
		}
		else if(strcmp(protocole, SFIN) == 0)
		{
			hideTirage();
			logger("-------------------La phase de soumission est terminée-------------------",2);
			reset_placement();
			
		}
		else if(strcmp(protocole, RECEPTION) == 0)
		{
			if(count>1)
			{
				logger(">",0);
				logger(pp_message[1],1);
			}
		}
		else if(strcmp(protocole, PRECEPTION) == 0)
		{
			if(count>1)
			{
				if(count>2)
				{
					logger(pp_message[2],0);
					logger(" : ",0);
				}
				else
					logger("??? :",0);
					
				logger(pp_message[1],1);
			}
		}
		else if(strcmp(protocole, BILAN) == 0)
		{
			if(count>2)
			{
				if(strcmp(pp_message[2], "") != 0)
				{
					logger(pp_message[2],0);
					logger(" a gagné",0);
					
					if(strcmp(pp_message[1], "") != 0)
					{
						logger(" avec le mot ",0);
						logger(pp_message[1],1);
					}
				}
				else
					logger("Personne n'a trouvé de mot",2);
			}
			createScoreDisplay();
		}
		else if(strcmp(protocole, VAINQUEUR) == 0)
		{			
			if(session.meilleur_joueur.username!=NULL && session.meilleur_joueur.score>-1)
			{
				logger("-------------------Fin de la Session-------------------",2);
				logger("Le grand gagnant est ",0);
				logger(session.meilleur_joueur.username,0);
				logger(" avec ",0);
				logger(session.meilleur_joueur.score,0);
				logger(" points",2);
			}
		}
		else
		{
			logger("Le serveur utilise un protocole non reconnu par le client : ",0);
			logger(protocole,1);
		}
	}
	
	return true;
}

void saveToLocal()
{
	for(int i=0;i<TAILLE_PLATEAU*TAILLE_PLATEAU;i++)
		plateau_local[i]=session.plateau[i];
		
	for(int i=0;i<TAILLE_TIRAGE;i++)
		tirage_local[i]=session.tirage[i];
}

void askConnexion(GtkButton *button, GtkWidget * input){
	char *nom = (char*)gtk_entry_get_text(GTK_ENTRY(input));
	
	if(strlen(nom) < 1)
		return;
	else{
	    initClient(&client, nom,ip);
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
	    	
	    	saveToLocal();
	    	
	    	initThread(&session);
	    	gtk_widget_destroy (input);
	    	gtk_widget_destroy (button_connexion);
	    	gtk_widget_destroy (label_connexion);
	    	if(errorField!=NULL)
				gtk_widget_destroy (errorField);	    	
	    	createTirageDisplay();
	    	createGrille();
	    	createConsoleLog();
	    	createScoreDisplay();
	    	createPhaseDisplay();
	    	
	    	print_session(&session);
	    	
	    	logger("BIENVENUE, ",0);
			logger((client.p_joueur)->username,0);
			logger("\t\t",0);
	    	
	    	logger("La session est en ",0);	    	
	    	
	    	if(strcmp(session.langue, "EN" ) == 0)
				logger("Anglais",2);
			else
				logger("Français",2);
			
	    	
	    	g_timeout_add (1000,refresh_GUI,NULL);
	    	
	    }
	    //pthread_join(client.input, NULL);
	}	
}

void proposerMot(GtkButton *button){
	
	if(session.phase==REC || session.phase==SOU)
	{
		annoncer_placement(plateau_local,session.p_client);
		reset_placement();
	}

}
