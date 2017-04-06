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


#define TAILLE_PLATEAU 15
#define TAILLE_TIRAGE 7

#define DEB 0
#define REC 1
#define SOU 2
#define RES 3



#include "joueur.h"
#include "transmission.h"
#include "FIFO.h"

typedef struct Session Session;
struct Session{
    int tour;
    char plateau[TAILLE_PLATEAU * TAILLE_PLATEAU];
    Joueur * p_liste_joueur;
    char tirage[TAILLE_TIRAGE];
    int nombre_joueur;
    JoueurClient * p_client;
    int phase;
    int temps;
    FIFO ** messages; 
    pthread_mutex_t lock;
    int val;
};

int chercher_joueur(char * nom_joueur, Session * session);
void bind_joueur_to_session(JoueurClient * joueur, Session * session);
int init_session(Session * session, char * placement, char * tirage, char * liste_joueur,char * phase, char * temps);
void initThread(Session * session);
void *thread_input(void* arg);
int handle_event(char * message_recu, Session * session);
void change_tirage(Session * session, char * tirage);
void change_plateau(Session * session, char * placement);
void refresh_game(Session * session, char * placement, char * tirage);
int handle_connexion(char * message_recu, Session * session);
int annoncer_placement(char * proposition,JoueurClient * client);
void switch_phase(Session* session,int phase);
void print_session(Session * session);
#endif /* SCRABBLE_H */
