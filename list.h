/* -------------
list.h
By Alex Schwarz
SID 0719732
CIS*2750 A4
-------------- */
#pragma once
#include <stdlib.h>
#include <stdio.h>
#include "Boolean.h"

typedef struct list_node {

    void* data;
    struct list_node* next;

} list_node;

typedef struct list {

    list_node* head;
    int num_elements;

} list;

list* create_list();
void destroy_list(list* to_destroy, void (*destroy_func)(void* node));
int add_list_back(list* list, list_node* to_add);
void print_list(list* list, void (*print_func)(void* data));
int list_contains(list* p, char* name, Boolean (*compare_func)(void* node, char* name));
void* get_node(list* list, char* name, Boolean (*compare_func)(void* node, char* name));
int has_duplicate(list* list);
