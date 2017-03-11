/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/* 
 * File:   scrabble.h
 * Author: maxime
 *
 * Created on 10 mars 2017, 00:34
 */

#ifndef SCRABBLE_H
#define SCRABBLE_H

#include "joueur.h"

#define TAILLE_PLATEAU 15
#define TAILLE_TIRAGE 7

typedef struct Session Session;
struct Session{
    int tour;
    char plateau[TAILLE_PLATEAU * TAILLE_PLATEAU];
    Joueur * p_liste_joueur;
    char tirage[TAILLE_TIRAGE];
    int nombre_joueur;
    JoueurClient * p_client;
};
 



int chercher_joueur(char * nom_joueur, Session * session);
void bind_joueur_to_session(JoueurClient * joueur, Session * session);
int init_session(Session * session, char * placement, char * tirage, char * liste_joueur);
void initThread(Session * session);



#endif /* SCRABBLE_H */

