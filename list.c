/* ----------------------
list.c
By Alex Schwarz
SID 0719732
CIS*2750 A4
Generic linked-list code
------------------------*/

#include "list.h"

/*
function: create_list

pre: N/A
post: Returns a pointer to an empty list
*/
list* create_list() {

    list* newlist = (list*)malloc(sizeof(list)*1);

    if(newlist == (list*)NULL)
        return (list*)NULL; /* mem alloc error */

    newlist->num_elements = 0;
    newlist->head = (list_node*)NULL;
    return newlist;
}

/*
function: destroy_list

pre: to_destroy is a valid list, destroy_func is a pointer to a type-specific function which frees your structure's members
post: to_destroy is free'd and should not be used further without creating a new list.
*/
void destroy_list(list* to_destroy, void (*destroy_func)(void* node)) {

    if(to_destroy == NULL)
        return;

    int i;
    int count = to_destroy->num_elements;
    list_node* cpy = to_destroy->head;

    for(i = 0; i < count; i++) { /* iterate list */

/*         (*destroy_func)(cpy);*/ /* function ptr to type-specific mem-freeing func */     
         cpy = cpy->next;        
    }
}

/*
function: add_list_back

pre: list is a pointer to a valid list, to_add is not a NULL pointer.
post: to_add is added to list
*/ 
int add_list_back(list* list, list_node* to_add) {

    if(list == NULL)
        return 0;

    if(list->num_elements == 0) {

        list->head = (list_node*)to_add;
        list->head->next = (list_node*)NULL;
        list->num_elements += 1;
        return 1;
    }
    else {

        list_node* head_copy = list->head;

        while(list->head != (list_node*)NULL) {
            
            if(list->head->next == (list_node*)NULL) {
                list->head->next = to_add;
                to_add->next = (list_node*)NULL;
                list->num_elements += 1;
                list->head = head_copy;
                return 1;
            }
            list->head = list->head->next;
        }
    }
    return 0;
}

/*
function: print_list
Used for debugging purposes
pre: list is a pointer to a valid list, print_func is a function that outputs type-specific information
post: N/A
*/
void print_list(list* list, void (*print_func)(void* data)) {

    if(list == NULL)
        return;

    list_node* temp = list->head;

        while(temp != NULL) {
            if(temp == NULL) {
                return;
            }
            (*print_func)(temp);
            temp = temp->next;
        }
}

/*
function: list_contains

pre: list is a valid pointer to a list structure, name is not NULL, compare_func is a valid function ptr
post: returns true if the list contains a node with "name"
*/
int list_contains(list* list, char* name, Boolean (*compare_func)(void* node, char* name)) {

    if(list == NULL)
        return 0;

    list_node* temp = list->head;

    int len = list->num_elements;
    int i;

    for(i = 0; i < len; i++) {
        
        if(temp == NULL) {
            return 0;
        }
        
        Boolean result = (*compare_func)(temp->data,name);

        if(result == true) {
            return 1;
        }
        else
            temp = temp->next;
    }

    return 0;
}

/*
function: get_node

pre: list is a valid pointer to a list structure, name is not NULL, compare_func is a valid function ptr
post: returns a pointer to a list_node's data pointer.
*/
void* get_node(list* list, char* name, Boolean (*compare_func)(void* node, char* pname)) {

    if(list == NULL)
        return (void*)NULL;

    list_node* temp = list->head;

    while(temp != NULL) { /* iterate list... */
        if(temp == NULL) {
            return false;
        }
        if((*compare_func)(temp->data,name) == true) {
            return temp->data; /* return the void* data of a list */
        }
        else
            temp = temp->next;
    }

    return NULL;
}

/* has_duplicate: Checks if a list contains a duplicate string value as its "data" member. should only be used when data* is used for strings.
pre: list is a non-null list
post: return 0 if no duplicate string is found, returns 1 if a duplicate string is found
*/
int has_duplicate(list* l) {

    list_node* cpy = l->head;
    list_node* inner_cpy = l->head;

    int i;
    int j;

    for(i = 0; i < l->num_elements; i++) {

        for(j = 0; j < l->num_elements; j++) {
            
            if(strcmp(inner_cpy->data,cpy->data) == 0 && i != j) {
                return 1;
            }
            inner_cpy = inner_cpy->next;
        }
        inner_cpy = l->head;
        cpy = cpy->next;
    }

    return 0;
}
