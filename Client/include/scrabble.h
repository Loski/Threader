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

typedef struct FIFO FIFO;

#include "joueur.h"
#include "transmission.h"
#include "FIFO.h"
#include <gtk/gtk.h>

typedef struct Session Session;

// Structure de données d'une session de jeu
struct Session{
    int tour;
    char plateau[TAILLE_PLATEAU * TAILLE_PLATEAU];
    // Liste des joueurs connectée
    Joueur * p_liste_joueur;
    int nombre_joueur;

    //Pointeur vers le client joueur.
    JoueurClient * p_client;


    //tirage en cours
    char tirage[TAILLE_TIRAGE];
    int phase;
    int temps;
    Joueur meilleur_joueur;


    // Fifo de messages
    File * messages;
    char langue[3];
    //Mutex général pour bloquer un élément de session ou la session en elle même
    GMutex mutex;
};


int chercher_joueur(char * nom_joueur, Session * session);
void bind_joueur_to_session(JoueurClient * joueur, Session * session);
int init_session(Session * session, char * placement, char * tirage, char * liste_joueur,char * phase, char * temps);
void initThread(Session * session);
gpointer thread_input(gpointer arg);
int handle_event(char * message_recu, Session * session);
void change_tirage(Session * session, char * tirage);
void change_plateau(Session * session, char * placement);
void refresh_game(Session * session, char * placement, char * tirage);
int handle_connexion(char * message_recu, Session * session);
int annoncer_placement(char * proposition,JoueurClient * client);
void switch_phase(Session* session,int phase);
void print_session(Session * session);
Joueur getMeilleurJoueur(Session * session);
#endif /* SCRABBLE_H */
