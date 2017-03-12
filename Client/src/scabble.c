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

int init_session(Session * session, char * placement, char * tirage, char * liste_joueur){    
    refresh_game(session, placement, tirage);
    char **score = NULL;
    int count = split(liste_joueur, '*', &score);
    char *ptr;
    if(count < 0)
        return -1;
    session->tour = strtol(score[0], &ptr, 10);
    session->p_liste_joueur = malloc(sizeof(Joueur) * (count-1)/ 2 );
    int j = 0;
    for(int i = 1; i < count -1; i+=2){
        session->p_liste_joueur[j].username = score[i];
        session->p_liste_joueur[j].score = strtol(score[i+1], &ptr, 10);
        j++;
    }
    session->nombre_joueur = ++j;
    return 1;
}

int refresh_game(Session * session, char * placement, char * tirage){
    change_plateau(session, placement);
    change_tirage(session, tirage);
}
int change_plateau(Session * session, char * placement){
    for(int i = 0; i < TAILLE_PLATEAU * TAILLE_PLATEAU; i++){
        session->plateau[i] = *(placement + i);
    }
}
int change_tirage(Session * session, char * tirage){
    for(int i = 0; i < TAILLE_TIRAGE; i++){
        session->tirage[i] = tirage[i];
    }
}
void bind_joueur_to_session( JoueurClient * joueur, Session * session){
    session->p_client = joueur;
}


void initThread(Session * session){
    printf("je suis là?");
   if(pthread_create(&(session->p_client->input), NULL, thread_input, session)){
       printf("bah alors ça sent pas bon");
   }
   //pthread_create(session->p_joueur->p_output, NULL, thread_output, (void *)(session));
}

void *thread_input(void* arg){
    printf("je rentre");
    if(arg != NULL){
        Session * session = (Session *) arg;
        while(session->p_client->socket!=0){
            char buffer[1024];
            int n = 0;
            if((n = recv(session->p_client->socket, buffer, 1024, 0)) < 0)
            {
                printf("error");
                exit(0);
            }
            buffer[n] = '\0';
            handle_event(buffer, session);
            puts(buffer);
        }   
    }
}

int handle_event(char * message_recu, Session * session){
    
    char **pp_message = NULL, *protocole = NULL;
    int count = split(message_recu, '/', &pp_message);
    if(count < 0)
        return -1;
    protocole = pp_message[0];
    if(strcmp(protocole, TOUR ) == 0){
        refresh_game(session, pp_message[1], pp_message[2]);
    }else if(strcmp(protocole, BIENVENUE) == 0){
        if(count < 4)
            return -1;
        init_session(session, pp_message[1], pp_message[2], pp_message[3]);
    }
    else if(strcmp(protocole, DECONNEXION) == 0){
        supprimerJoueur(pp_message[1], session);
    }else if(strcmp(protocole, CONNECTE) == 0){
        connexion_nouveau_joueur(session, pp_message[1]);
    }else if(strcmp(protocole, RVALIDE) == 0){
        
    }else if(strcmp(protocole, RINVALIDE) == 0){
        
    }else if(strcmp(protocole, RATROUVE) == 0){
        
    }else if(strcmp(protocole, RFIN) == 0){
        
    }else if(strcmp(protocole, SVALIDE) == 0){
        
    }else if(strcmp(protocole, SINVALIDE) == 0){
        
    }else if(strcmp(protocole, SFIN) == 0){
        
    }else if(strcmp(protocole, BILAN) == 0){
        
    }else if(strcmp(protocole, VAINQUEUR) == 0){
        
    }else if(strcmp(protocole, SESSION) == 0){
        
    }else{
        printf("bad param for handler");
    }   
}