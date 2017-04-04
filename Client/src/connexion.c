#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <stdbool.h>
#include <pthread.h>
#include "../include/socket.h"
#include "../include/transmission.h"
#include "../include/scrabble.h"
#include "../include/joueur.h"
#include "../include/connexion.h"

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

socket_t connexion_socket(const char* ip){
    
    SOCKADDR_IN serveur;
    socket_t my_socket = socket(AF_INET , SOCK_STREAM , 0);
    if (my_socket == INVALID_SOCKET)
    {
       perror("Error socket creation");
       return -1;
    }
    
    serveur.sin_addr.s_addr = inet_addr(ip);
    serveur.sin_family = AF_INET;
    serveur.sin_port = htons(PORT);
    
    if (connect(my_socket , (SOCKADDR*)&serveur , sizeof(serveur)) < 0)
    {
        perror("Error socket connexion");
        return -1;
    }
    
    return my_socket;
}

int connexion_joueur(JoueurClient * joueur){
    char message[255] = CONNEXION;
    strcat(message, joueur->p_joueur->username);
    strcat(message, "/\n");
    return sendMessage(joueur->socket, message);
}



int deconnexion_joueur(JoueurClient *joueur){
    char message[255] = "SORT/";
    strcat(message, joueur->p_joueur->username);
    if(sendMessage(joueur->socket, message)!=0)
        closesocket(joueur->socket);
    else
        return -1;
    return 1;
}
