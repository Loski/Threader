#include "../include/FIFO.h"
#include <stdio.h>
#include <stdlib.h>
#include <unistd.h>
#include <string.h>
#include <pthread.h>

static pthread_mutex_t mutex_stock = PTHREAD_MUTEX_INITIALIZER;

void ajouter_message(FIFO ** list, char * message)
{
	FIFO * nouveau = malloc(sizeof(FIFO));
	if(nouveau != NULL)
	{
		nouveau->next = NULL;
		nouveau->message = message;
		
		pthread_mutex_lock (& mutex_stock);
		if(*list == NULL)
		{
			*list = nouveau;
		}
		else
		{
			FIFO * last = * list;
				
			while (last->next != NULL)
			{
				last = last->next;
			}
			
			last->next=nouveau;
		}
		pthread_mutex_unlock (& mutex_stock);
	}
	else
	{
		printf("ERREUR MALLOC message");
	}
	

}

char * get_message(FIFO ** list)
{
	if(*list == NULL)
		return NULL;
	else
	{
		pthread_mutex_lock (& mutex_stock);
		FIFO * next = (*list)->next;
		char * message = NULL;
		message = malloc(sizeof(char) * (1+strlen((*list)->message)));
		if(message == NULL){
			puts("Pointeur non initialisé");
			exit(1);
		}
		strcpy(message, (*list)->message);
		
		
		free(*list);
		 *list = NULL;
		
		*list = next;
		pthread_mutex_unlock (& mutex_stock);
		
		return message;
	}
}

void clear(FIFO ** list)
{
	while (*list != NULL)
    {
		get_message(list);
    }
}
/*
int main()
{
	FIFO ** list = (FIFO**)malloc(sizeof(FIFO*));
	return 0;
}*/
