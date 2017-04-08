/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/* 
 * File:   main.c
 * Author: maxime
 *
 * Created on 8 mars 2017, 19:07
 */

#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <netinet/tcp.h>
#include <gtk/gtk.h>
#include "../include/socket.h"
#include "../include/transmission.h"
#include "../include/connexion.h"
#include "../include/joueur.h"
#include "../include/scrabble.h"
#include "../include/gui.h"



int main(int argc, char** argv) {
      /* Initialisation de GTK+ */
  gtk_init (&argc, &argv);
  if(argc > 1)
	lancementGUI(argv[1]);
	else
	lancementGUI("0.0.0.0");
   
      /* enter the GTK main loop */
    gtk_main();
    return (EXIT_SUCCESS);
}

