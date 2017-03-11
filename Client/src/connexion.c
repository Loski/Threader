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
    printf("Je me connecte : %s\n", message);
    return sendMessage(joueur->socket, message);
}

void *thread_input(void* arg){
    printf("je rentre");
    if(arg != NULL){
        Session * session = (Session *) arg;
        while(session->p_client->socket!=0){
            char buffer[1024];
            int n = 0;
            if((n = recv(session->p_client->socket, buffer, 1024, 0)) < 0)
            {
                printf("error");
                exit(0);
            }
            buffer[n] = '\0';
            
            puts(buffer);
            handle_event(buffer, session);
        }   
    }
}

void *thread_output(void* arg){
    
}
void initThread(Session * session){
    printf("je suis là?");
   if(pthread_create(&(session->p_client->input), NULL, thread_input, session)){
       printf("bah alors ça sent pas bon");
   }
   //pthread_create(session->p_joueur->p_output, NULL, thread_output, (void *)(session));
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

int handle_event(char * message_recu, Session * session){
    
    char **pp_message = NULL, *protocole = NULL;
    int count = split(message_recu, '/', &pp_message);
    if(count < 0)
        return -1;
    protocole = pp_message[0];
    if(strcmp(protocole, TOUR ) == 0){
        printf("%s",protocole);
    }else if(strcmp(protocole, BIENVENUE) == 0){
        if(count < 4)
            return -1;
        char * placement = pp_message[1];
        char * tirage = pp_message[2];
        char * joueurs = pp_message[3];
        init_session(session, placement, tirage, joueurs);
    }
    else if(strcmp(protocole, DECONNEXION) == 0){
        
    }else if(strcmp(protocole, TROUVE) == 0){
        
    }else if(strcmp(protocole, RVALIDE) == 0){
        
    }else if(strcmp(protocole, RINVALIDE) == 0){
        
    }else if(strcmp(protocole, RATROUVE) == 0){
        
    }else if(strcmp(protocole, RFIN) == 0){
        
    }else if(strcmp(protocole, SVALIDE) == 0){
        
    }else if(strcmp(protocole, SINVALIDE) == 0){
        
    }else if(strcmp(protocole, SFIN) == 0){
        
    }else if(strcmp(protocole, BILAN) == 0){
        
    }else if(strcmp(protocole, VAINQUEUR) == 0){
        
    }else if(strcmp(protocole, SESSION) == 0){
        
    }else{
        printf("bad param for handler");
    }
    
}