/* -----------------
hash.h
Alex Schwarz
SID 0719732
CIS*2750 A4
------------------*/
#pragma once
#include <stdlib.h>
#include <string.h>
#include <limits.h>

struct pair_list {
    char *key;
    char *value;
    struct pair_list *next;
};
 
typedef struct pair_list pair;
 
typedef struct {
    int size;
    pair **table;	
}hashtable;
 
hashtable* create_ht(int size );
int hash(hashtable *hashtable, char *key );
pair* make_newpair(char *key,char *value);
void insert_pair(hashtable *hashtable, char *key, char *value );
char* get_value(hashtable *hashtable, char *key );

