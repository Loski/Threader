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

void analyse(char * message);
int sendMessage(socket_t socket, char * message);
int split (const char *str, char c, char ***arr);


//reception
#define TOUR "TOUR"
#define BIENVENUE "BIENVENUE"
#define CONNECTE "CONNECTE"
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
#define REFUS "REFUS"


//envoie
#define CONNEXION "CONNEXION/"
#define SORT "SORT/"



#define DELIMITOR "/"

#endif /* TRANSMISSION_H */

