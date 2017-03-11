/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

#include "../include/scrabble.h"
#include <stdio.h>
#include <stdlib.h>
#include <string.h>

int chercherJoueur(char * nom_joueur, Session * session){
    for(int i = 0; i < session->nombre_joueur; i++){
        if(strcmp(session->p_liste_joueur[i].username, nom_joueur) == 0){
            return i;
        }
    }
    return -1;
}

int init_session(Session * session, char * placement, char * tirage, char * liste_joueur){
    puts("im here baby");
    for(int i = 0; i < TAILLE_PLATEAU * TAILLE_PLATEAU; i++){
        session->plateau[i] = *(placement + i);
    }
    
    for(int i = 0; i < TAILLE_TIRAGE; i++){
        session->tirage[i] = tirage[i];
    }
    
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
    puts(session->plateau);
    puts(session->tirage);
    return 1;
}


void bind_joueur_to_session( JoueurClient * joueur, Session * session){
    session->p_client = joueur;
}
