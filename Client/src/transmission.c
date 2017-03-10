/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

#include "../include/socket.h"
#include <stdbool.h>
#include <stdint.h>
#include <string.h>
bool sendMessage(socket_t socket, char * message){
    if( send(socket , message , strlen(message) , 0) < 0)
    {
        puts("Send failed");
        return false;
    }
    return false;
}