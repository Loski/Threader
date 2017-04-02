#include <stdlib.h>
#include <stdio.h>
#include <string.h>
#include <pthread.h>

#include "../include/joueur.h"
#include "../include/scrabble.h"
#include "../include/connexion.h"
#include "../include/transmission.h"

void initClient(JoueurClient * client,  char * name){
    client->input = 0;
    client->output = 0;
    client->socket = -1;
    client->p_joueur = malloc(sizeof(Joueur));
    initJoueur(client->p_joueur, name);
    client->socket = connexion_socket("0.0.0.0");
    /*if(client->socket < 0)
        exit(EXIT_FAILURE);*/
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

int verification_connexion(Session * session){
    if(session->p_client->socket!=0){
        char buffer[2048];
        int n = 0;
        if((n = recv(session->p_client->socket, buffer, 2048, 0)) < 0)
        {
            puts("error. Socket dead?");
            return -1;
        }
        buffer[n] = '\0';
        return handle_connexion(buffer, session);
    }else{
        puts("error. Socket null/dead?");
    }
    return -1;
}


