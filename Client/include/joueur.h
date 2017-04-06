/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/* 
 * File:   joueur.h
 * Author: maxime
 *
 * Created on 8 mars 2017, 19:08
 */

#ifndef JOUEUR_H
#define JOUEUR_H

#include "socket.h"
#include <pthread.h>
#include <glib.h>


typedef struct JoueurClient JoueurClient;
typedef struct Joueur Joueur;
typedef struct Session Session;

struct Joueur
{
    char * username;
    int score;
};
struct JoueurClient{
    GThread * input;
    socket_t socket;
    Joueur * p_joueur;
};

#include "scrabble.h"

void initClient(JoueurClient * joueur, char * name);
void initJoueur(Joueur * joueur, char * name);
int supprimerJoueur(char * nom_joueur, Session * session);
int connexion_nouveau_joueur(Session *session, char * nom_joueur);
int verification_connexion(Session * session);
#endif /* JOUEUR_H */

