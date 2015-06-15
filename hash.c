/* ---------------
hash.c
Alex Schwarz
SID 0719732
CIS*2750 A4
hash table implementation using key-value pairs
------------------*/
#include "hash.h"

/* 
hashtable* create_ht(int size) : created a newly allocated hash table
pre: size is a non-zero positive integer
post: returns a pointer to a new hashtable containing size number of slots.
*/
hashtable* create_ht(int size) {
 
    hashtable* hashtable = NULL;
    int i;
 
    if(size < 1) 
        return NULL;
 
    if((hashtable = malloc(sizeof(hashtable))) == NULL) 
        return NULL;
    
    /* allocate the table slots by size */
    if((hashtable->table = malloc(sizeof(pair*) * size)) == NULL) 
        return NULL;
    
    for(i = 0; i < size; i++) 
        hashtable->table[i] = NULL;
    
    hashtable->size = size;
    return hashtable;	
}
 
/*
int hash(hashtable* hashtable, char* key) : uses the key parameter and size of a hashtable to hash a string to an integer value
pre: hashtable is a non-null hashtable, key is a non-null pointer to a null-terminated string
post: returns an integer value used for direct lookup in the table
*/
int hash(hashtable* hashtable, char *key) {
 
    unsigned long int hashval;
    int i = 0;
 
    while(hashval < ULONG_MAX && i < strlen(key)) {
        hashval = hashval << 8;
        hashval += key[i];
        i++;
    }
 
    return hashval % hashtable->size;
}
 
/*
make_newpair(char* key, char* value) : creates a pair* struct and fills it based on key and value
pre: key and value are non-null strings
post: returns a new keypair
*/
pair* make_newpair( char *key, char *value ) {

    pair* newpair;
 
    if((newpair = malloc(sizeof(pair))) == NULL)
        return NULL;
    
    if((newpair->key = strdup(key)) == NULL) 
        return NULL;
 
    if((newpair->value = strdup(value)) == NULL) 
        return NULL;
    
    newpair->next = NULL;
    return newpair;
}


/*
void insert_pair(hashtable* table, char*, char*) : inserts a new key-value pair into a hashtable
pre: hashtable is a non-null and non-full hashtable
post: a new key-value pair is created and inserted into the hashtable. has no return value.
*/
void insert_pair(hashtable *hashtable, char *key, char *value) {
	
    int index = 0;
    pair* newpair = NULL;
    pair* next = NULL;
    pair* last = NULL;
 
    index = hash(hashtable, key);
 
    next = hashtable->table[index];
 
    while(next != NULL && next->key != NULL && strcmp(key, next->key) > 0) {
        last = next;
        next = next->next;
    }
 
    /* replace existing string if already exists */
    if(next != NULL && next->key != NULL && strcmp(key, next->key) == 0) {
 
        free(next->value);
        next->value = strdup(value);
    } 
    else {

        newpair = make_newpair(key, value);

        /* traverse list @ specific index: chaining */ 
        if(next == hashtable->table[index]) {
            newpair->next = next;
            hashtable->table[index] = newpair;	/* start of list */
        } 
        else if (next == NULL) 
            last->next = newpair; /* end of list */
        else {
            newpair->next = next;
            last->next = newpair; /* middle area of list */
        }
    }
}

/*
char* get_value(hashtable*, char*) : retrieves the value in hashtable associated with the key parameter 
pre: hashtable is a non-null hashtable
post: returns a pointer to the null-terminated string assuming a key/value match is found. otherwise returns NULL if none found.
*/ 
char* get_value(hashtable *hashtable, char *key) {

    int index = 0;
    pair* pair;
 
    index = hash(hashtable, key);
 
    pair = hashtable->table[index];
    while(pair != NULL && pair->key != NULL && strcmp(key, pair->key) > 0)
        pair = pair->next; /* go to end of list at specific index */
    
    if(pair == NULL || pair->key == NULL || strcmp(key, pair->key) != 0)
        return NULL;
    else
        return pair->value;
}
