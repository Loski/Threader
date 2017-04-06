#include "../include/FIFO.h"
#include <stdio.h>
#include <stdlib.h>
#include <unistd.h>
#include <string.h>
#include <pthread.h>



char * get_message(File *file, Session * session)
{
    if (file == NULL)
    {
        exit(EXIT_FAILURE);
    }

    char * message = NULL;
    g_mutex_lock (&session->mutex);
    /* On vérifie s'il y a quelque chose à défiler */
    if (file->premier != NULL)
    {
        Element *elementDefile = file->premier;

        message = elementDefile->message;
        file->premier = elementDefile->suivant;
        free(elementDefile);
    }
    g_mutex_unlock (&session->mutex);
    return message;
}



void ajouter_message(File *file, char  * messageGiven, Session * session)
{

    Element *nouveau = malloc(sizeof(*nouveau));
    if (file == NULL || nouveau == NULL)
    {
        exit(EXIT_FAILURE);
    }
    char * message = NULL;
	message = malloc(sizeof(char) * (1+strlen(messageGiven)));
		if(message == NULL){
			puts("Pointeur non initialisé");
			exit(1);
	}
    strcpy(message ,messageGiven);
    nouveau->message = message;
    nouveau->suivant = NULL;
    g_mutex_lock (&session->mutex);
    if (file->premier != NULL) /* La file n'est pas vide */
    {
        Element *elementActuel = file->premier;
        while (elementActuel->suivant != NULL)
        {
            elementActuel = elementActuel->suivant;
        }
        elementActuel->suivant = nouveau;
    }
    else /* La file est vide, notre élément est le premier */
    {
        file->premier = nouveau;
    }
    g_mutex_unlock (&session->mutex);

}