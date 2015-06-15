/* --------------
ParameterList.h
By Alex Schwarz
SID 0719732
CIS*2750 A4
---------------*/

#pragma once
#include "list.h"

typedef struct {

    int count;
    int current_pos;
    char** list; /* 2D array of strings */

} ParameterList;

char * PL_next(ParameterList *l);
int PL_length(ParameterList* l);
