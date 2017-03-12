#include <stdlib.h>
#include <stdio.h>
#include <string.h>
#include <pthread.h>

#include "../include/joueur.h"
#include "../include/scrabble.h"
#include "../include/transmission.h"

void initClient(JoueurClient * client,  char * name){
    client->input = 0;
    client->output = 0;
    client->socket = -1;
    client->p_joueur = malloc(sizeof(Joueur));
    initJoueur(client->p_joueur, name);
}

void initJoueur(Joueur * joueur,  char *name){
    joueur->score = 50;
    joueur->username = name;
}

int chercherJoueur(char * nom_joueur, Session * session){
    for(int i = 0; i < session->nombre_joueur; i++){
        if(strcmp(session->p_liste_joueur[i].username, nom_joueur) == 0){
            return i;
        }
    }
    return -1;
}

int supprimerJoueur(char * nom_joueur, Session * session){
	int joueur_a_supprimer = -1;
	if((joueur_a_supprimer = chercherJoueur(nom_joueur, session)) < 0){
        return -1;
    }
	session->nombre_joueur--;
    Joueur * newJoueurs = malloc(sizeof(Joueur) * session->nombre_joueur);
    int j = 0;
    for(int i = 0; i < (session->nombre_joueur+1); i++){
        if(i == joueur_a_supprimer){
            continue;
        }else{
            newJoueurs[j] = session->p_liste_joueur[i];
            j++;
        }
    }
    session->p_liste_joueur = newJoueurs;
    return 1;
}

int connexion_nouveau_joueur(Session *session, char * nom_joueur){
    Joueur * liste_joueur = realloc(session->p_liste_joueur, session->nombre_joueur + 1);
    if (liste_joueur != NULL)
        session->p_liste_joueur = liste_joueur;
    initJoueur(&session->p_liste_joueur[session->nombre_joueur], nom_joueur);
    session->nombre_joueur++;
}

void print_joueur(Joueur * joueur){
    puts(joueur->username);
    printf("%d", joueur->score);
}

void print_all_joueur(Session * session){
    for(int i =0; i < session->nombre_joueur; i++){
        print_joueur(&session->p_liste_joueur[i]);
    }
}