/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

#include "../include/scrabble.h"
#include <string.h>

int chercherJoueur(char * nom_joueur, Session * session){
    for(int i = 0; i < session->nombre_joueur; i++){
        if(strcmp(session->p_liste_joueur[i].username, nom_joueur) == 0){
            return i;
        }
    }
    return -1;
}

void init_session(Session * session, char * placement, char * tirage, char * liste_joueur){
    
    for(int i = 0; i < TAILLE_PLATEAU * TAILLE_PLATEAU; i++){
        session->plateau[i] = *(placement + i);
    }
    
    for(int i = 0; i < TAILLE_TIRAGE; i++){
        session->tirage[i] = tirage[i];
    }
}


void bind_joueur_to_session( JoueurClient * joueur, Session * session){
    session->p_client = joueur;
}
