/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/* 
 * File:   main.c
 * Author: maxime
 *
 * Created on 8 mars 2017, 19:07
 */

#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <pthread.h>
#include <netinet/tcp.h>
#include "../include/socket.h"
#include "../include/transmission.h"
#include "../include/connexion.h"
#include "../include/joueur.h"
#include "../include/scrabble.h"

void initClient(JoueurClient * client,  char * name){
    client->input = 0;
    client->output = 0;
    client->socket = -1;
    client->p_joueur = malloc(sizeof(Joueur));
    initJoueur(client->p_joueur, name);
}

void initJoueur(Joueur * joueur,  char *name){
    joueur->score = 50;
    joueur->username = malloc(sizeof(char) * (1 + strlen(name)));
    strcpy(joueur->username, name);
}

/*
void setName(JoueurClient * client, char * name){
    client->joueur->username = name;
}*/

int main(int argc, char** argv) {
    JoueurClient client;
    Joueur michel;
    Session session;
    initClient(&client, "Loski");
    client.socket = connexion_socket("0.0.0.0");
    if(client.socket < 0)
        exit(EXIT_FAILURE);
    bind_joueur_to_session(&client, &session);
    connexion_joueur(&client);
    initThread(&session);
    pthread_join(client.input, NULL);
    return (EXIT_SUCCESS);
}


