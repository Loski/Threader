#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <stdbool.h>
#include <pthread.h>
#include "../include/socket.h"
#include "../include/transmission.h"
#include "../include/scrabble.h"
#include "../include/joueur.h"

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

bool connexion_joueur(JoueurClient * joueur){
    char message[255] = CONNEXION;
    strcat(message, joueur->p_joueur->username);
    printf("Je me connecte : %s\n", message);
    return sendMessage(joueur->socket, message);
}

void *thread_input(void* arg){
    printf("je rentre");
    if(arg != NULL){
        Session * session = (Session *) arg;
        printf("%d", session->p_client->socket);
        printf("%s", session->p_client->p_joueur->username);
        while(session->p_client->socket!=0){
            char buffer[1024];
            int n = 0;
            if((n = recv(session->p_client->socket, buffer, 1024, 0)) < 0)
            {
                printf("error");
                exit(0);
            }
            buffer[n] = '\0';
            printf("%s", buffer);
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
bool deconnexion_joueur(JoueurClient *joueur){
    char message[255] = "SORT/";
    strcat(message, joueur->p_joueur->username);
    if(sendMessage(joueur->socket, message)!=0)
        closesocket(joueur->socket);
    else
        exit(-1);
    return 1;
}

void handle_event(char * message_recu, Session * session){
    char *saveptr1, *token;
    token = strtok_r(message_recu, DELIMITOR, &saveptr1);
    if(strcmp(token, TOUR ) == 0){
        printf("%s",token);
    }else if(strcmp(token, BIENVENUE) == 0){
        printf("Bienvenue !!");
        char * placement = strtok_r(NULL, DELIMITOR, &saveptr1);
        char * tirage = strtok_r(NULL, DELIMITOR, &saveptr1);
        char * joueurs = strtok_r(NULL, DELIMITOR, &saveptr1);
        init_session(session, placement, tirage, joueurs);
    }
    else if(strcmp(token, DECONNEXION) == 0){
        
    }else if(strcmp(token, TROUVE) == 0){
        
    }else if(strcmp(token, RVALIDE) == 0){
        
    }else if(strcmp(token, RINVALIDE) == 0){
        
    }else if(strcmp(token, RATROUVE) == 0){
        
    }else if(strcmp(token, RFIN) == 0){
        
    }else if(strcmp(token, SVALIDE) == 0){
        
    }else if(strcmp(token, SINVALIDE) == 0){
        
    }else if(strcmp(token, SFIN) == 0){
        
    }else if(strcmp(token, BILAN) == 0){
        
    }else if(strcmp(token, VAINQUEUR) == 0){
        
    }else if(strcmp(token, SESSION) == 0){
        
    }else{
        printf("bad param");
    }
    
}