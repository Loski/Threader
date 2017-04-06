#ifndef FIFO_H
#define FIFO_H
typedef struct File File;

#include "scrabble.h"
typedef struct FIFO FIFO;

struct FIFO{
	char * message;
	FIFO * next;
};

typedef struct Element Element;

struct Element

{

    char * message;
    Element *suivant;

};


typedef struct File File;

struct File

{

    Element *premier;

};
void ajouter_message(File * list, char * message, Session * session);
char * get_message(File * list, Session * session);
void clear(FIFO ** list);




#endif
