#ifndef FIFO_H
#define FIFO_H

typedef struct FIFO FIFO;

struct FIFO{
	char * message;
	FIFO * next;
};


void ajouter_message(FIFO ** list, char * message);
char * get_message(FIFO ** list);
void clear(FIFO ** list);


#endif
