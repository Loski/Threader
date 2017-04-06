#include "../include/FIFO.h"
#include <stdio.h>
#include <stdlib.h>
#include <unistd.h>
#include <string.h>
#include <pthread.h>

void ajouter_message(FIFO ** list, char * message)
{	
	FIFO * nouveau = malloc(sizeof(FIFO));
	if(nouveau != NULL)
	{
		nouveau->next = NULL;
		nouveau->message = message;
		
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
	}
	else
	{
		printf("ERREUR MALLOC message");
	}
	
}

char * get_message(FIFO ** list)
{
	if(*list == NULL)
	{
		printf("C'est Vide gros");
		return NULL;
	}
	else
	{
		FIFO * next = (*list)->next;
		char * message = NULL;
		message = malloc(sizeof(char) * (1+strlen((*list)->message)));
		if(message == NULL){
			puts("Pointeur non initialisé");
			return NULL;
		}
		
		strcpy(message, (*list)->message);
		
		
		free(*list);
		*list = NULL;
		
		*list = next;
		
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

/*int main()
{
	FIFO ** list = (FIFO**)malloc(sizeof(FIFO*));
	ajouter_message(list,"SESSION/");
	ajouter_message(list,"TOUR/");
	printf("%s\n",get_message(list));
	return 0;
}*/
