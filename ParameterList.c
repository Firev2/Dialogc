/*----------------------------------------------
ParameterList.c
By Alex Schwarz
SID 0719732
CIS*2750 A4
Acts as a linked list with a char* data element
-----------------------------------------------*/
#include "ParameterList.h"

/*
function: PL_next

pre: n/a
post: Returns the next item in parameter list l, NULL if there are no items remaining in the list (see note below)
*/
char * PL_next(ParameterList *l) {

    if(l == NULL)
        return (char*)NULL;

    int len = PL_length(l);

    if(l->current_pos > l->count) {
        return NULL;
    } /* reached end of list. currently no implementation exists to reset the list to it's head. */
 
    l->current_pos += 1;
    return l->list[l->current_pos - 1];
}

int PL_length(ParameterList *l) {

    if(l == NULL)
        return 0;

    return l->count;

}
