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

#include <pthread.h>
#include "socket.h"

typedef struct Joueur Joueur;
struct Joueur
{
    char * username;
    int score;
};

typedef struct JoueurClient JoueurClient;
struct JoueurClient{
    pthread_t input;
    pthread_t  output;
    socket_t socket;
    Joueur * p_joueur;
};

void initClient(JoueurClient * joueur, char * name);
void initJoueur(Joueur * joueur, char * name);
#endif /* JOUEUR_H */

