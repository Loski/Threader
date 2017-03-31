/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/* 
 * File:   connexion.h
 * Author: maxime
 *
 * Created on 8 mars 2017, 20:39
 */

#ifndef CONNEXION_H
#define CONNEXION_H

#include "joueur.h"
#include "scrabble.h"

int connexion_socket(const char * ip);
int connexion_joueur(JoueurClient * client);
int deconnexion_joueur(JoueurClient * client);
socket_t connexion_socket(const char* ip);

#endif /* CONNEXION_H */

