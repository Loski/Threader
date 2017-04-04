/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

#include "../include/scrabble.h"
#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <pthread.h>
#include <unistd.h>

int init_session(Session * session, char * placement, char * tirage, char * liste_joueur, char * phase, char * temps){    
    refresh_game(session, placement, tirage);
    char **score = NULL;
    int count = split(liste_joueur, '*', &score);
    char *ptr;
    if(count < 0){
        puts("count négatif");
        return -1;
    }
    session->tour = strtol(score[0], &ptr, 10);
    session->p_liste_joueur = malloc(sizeof(Joueur) * (count-1)/ 2 );
    if(session->p_liste_joueur==NULL){
        puts("Pointeur NULL");
        return -1;
    }
    int j = 0;
    for(int i = 1; i < count -1; i+=2){
        session->p_liste_joueur[j].username = score[i];
        session->p_liste_joueur[j].score = strtol(score[i+1], &ptr, 10);
        j++;
    }
    session->nombre_joueur = j;
    printf("%s\n", phase);
    if(strcmp(phase,"DEB") == 0)
    {
		session->phase = DEB;
	}
    else if(strcmp(phase,"REC") == 0)
    {		
		session->phase = REC;
	}
    else if(strcmp(phase,"SOU") == 0)
    {
		session->phase = SOU;
	}
    else if(strcmp(phase,"RES") == 0)
    {
		session->phase = RES;
	}else{
        return -1;
    }
    
    session->messages = (FIFO**)malloc(sizeof(FIFO*));
    *(session->messages) = NULL;
    
    session->temps = atoi(temps);
    print_all_joueur(session);
    return 1;
}

void refresh_game(Session * session, char * placement, char * tirage){
    change_plateau(session, placement);
    change_tirage(session, tirage);
}
void change_plateau(Session * session, char * placement){
    for(int i = 0; i < TAILLE_PLATEAU * TAILLE_PLATEAU; i++){
        session->plateau[i] = *(placement + i);
    }
}
void change_tirage(Session * session, char * tirage){
    for(int i = 0; i < TAILLE_TIRAGE; i++){
        session->tirage[i] = tirage[i];
    }
}
void bind_joueur_to_session( JoueurClient * joueur, Session * session){
    session->p_client = joueur;
}

void initThread(Session * session){
    puts("je suis là?");
   if(pthread_create(&(session->p_client->input), NULL, thread_input, session)){
       puts("bah alors ça sent pas bon");
   }
   //pthread_create(session->p_joueur->p_output, NULL, thread_output, (void *)(session));
}

void *thread_input(void* arg){
    if(arg != NULL){
        Session * session = (Session *) arg;
        while(session->p_client->socket!=0){
            char buffer[1024];
            int n = 0;
            if((n = recv(session->p_client->socket, buffer, 1024, 0)) < 0)
            {
                puts("error. Socket dead?");
                //exit(0);
            }
            buffer[n] = '\0';
            handle_event(buffer, session);
        }   
    }
}
int handle_connexion(char * message_recu, Session * session){
    char **pp_message = NULL, *protocole = NULL;
    int count = split(message_recu, '/', &pp_message);
    if(count < 0)
        return -1;
    protocole = pp_message[0];
    if(strcmp(protocole, BIENVENUE) == 0){
        if(count < 6)
            return -1;
        puts("handle_connexion");
        return init_session(session, pp_message[1], pp_message[2], pp_message[3],pp_message[4],pp_message[5]);
    }else if(strcmp(protocole, REFUS) == 0)
        return -1;
    return -1;
}

void switch_phase(Session* session,int phase)
{
	
	/* Tester enchainement ?*/
	session->phase=phase;
}

int annoncer_placement(char * proposition,JoueurClient * client)
{
	puts("Annonce d'un placement :\n");

	char message[255] = TROUVE;
    strcat(message, proposition);
    strcat(message, "/\n");
    printf("%s\n", message);
    return sendMessage(client->socket, message);
}

int handle_event(char * message_recu, Session * session){
    
    char **pp_message = NULL, *protocole = NULL;
    int count = split(message_recu, '/', &pp_message);
    if(count < 0)
        return -1;
    protocole = pp_message[0];    
    ajouter_message(session->messages,message_recu);
    if(strcmp(protocole, TOUR ) == 0){
		switch_phase(session,REC);
        refresh_game(session, pp_message[1], pp_message[2]);
    }else if(strcmp(protocole, DECONNEXION) == 0){
        supprimerJoueur(pp_message[1], session);
    }else if(strcmp(protocole, CONNECTE) == 0){
        connexion_nouveau_joueur(session, pp_message[1]);
    }else if(strcmp(protocole, RVALIDE) == 0){
        
    }else if(strcmp(protocole, RINVALIDE) == 0){
        
    }else if(strcmp(protocole, RATROUVE) == 0){
        switch_phase(session,SOU);
    }else if(strcmp(protocole, RFIN) == 0){
		switch_phase(session,RES);
    }else if(strcmp(protocole, SVALIDE) == 0){
        
    }else if(strcmp(protocole, SINVALIDE) == 0){
        
    }else if(strcmp(protocole, SFIN) == 0){
        switch_phase(session,RES);
        switch_phase(session,RES);
    }else if(strcmp(protocole, BILAN) == 0){
        
    }else if(strcmp(protocole, VAINQUEUR) == 0){
        
    }else if(strcmp(protocole, SESSION) == 0){
        switch_phase(session,DEB);
    }else{
        puts("bad param for handler");
    }
    return 1;   
}


void print_session(Session * session)
{
    printf("NB Joueurs : %d\n",session->nombre_joueur);
    printf("JOUEUR : %s\n",((session->p_client)->p_joueur)->username);
    printf("TOUR : %d\n",session->tour);
    printf("PHASE : %d\n",session->phase);
    printf("PLATEAU : %s\n",session->plateau);
    printf("TIRAGE : %s\n",session->tirage);
    
}
