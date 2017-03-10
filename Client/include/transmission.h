/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/* 
 * File:   transmission.h
 * Author: maxime
 *
 * Created on 8 mars 2017, 22:24
 */

#ifndef TRANSMISSION_H
#define TRANSMISSION_H

#include "socket.h"
#include <stdbool.h>

void analyse(char * message);
bool sendMessage(socket_t socket, char * message);
extern char *strtok_r(char *, const char *, char **);

//reception
#define TOUR "TOUR"
#define BIENVENUE "BIENVENUE"
#define DECONNEXION "DECONNEXION"
#define SESSION "SESSION"
#define VAINQUEUR "VAINQUEUR"
#define TROUVE "TROUVE"
#define RVALIDE "RVALIDE"
#define RINVALIDE "RINVALIDE"
#define RATROUVE "RATROUVE"
#define RFIN "RFIN"
#define SVALIDE "SVALIDE"
#define SINVALIDE "SINVALIDE"
#define SFIN "SFIN"
#define BILAN "BILAN"


//envoie
#define CONNEXION "CONNEXION/"
#define SORT "SORT/"



#define DELIMITOR "/"

#endif /* TRANSMISSION_H */

