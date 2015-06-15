/*
ParameterManager.h
By Alex Schwarz
SID 0719732
CIS*2750 A1
*/

#include "ParameterList.h"
#include <string.h>
#include <stdlib.h>
#define MAX_PARAM_LENGTH 1025

typedef enum {
    INT_TYPE,
    REAL_TYPE,
    BOOLEAN_TYPE,
    STRING_TYPE,
    LIST_TYPE
} param_t;

union param_value
{
    int           int_val;
    float         real_val;
    Boolean       bool_val;   /* see additional types section below */
    char          *str_val;
    ParameterList *list_val;  /* see additional types section below */
};

typedef struct {

    Boolean filled;
    Boolean required;
    param_t type;
    char* pname;
    union param_value pval;

} ParameterNode;

typedef struct {

    list* param_list;

} ParameterManager;

ParameterManager * PM_create(int size);
int PM_destroy(ParameterManager *p);
int PM_parseFrom(ParameterManager *p, FILE *fp, char comment);
int PM_hasValue(ParameterManager *p, char *pname);
int PM_manage(ParameterManager *p, char *pname, param_t ptype, int required);
union param_value PM_getValue(ParameterManager *p, char *pname);


ParameterManager* PM_copy(ParameterManager* to_copy);
Boolean all_required_filled(ParameterManager* p);

/* string parsing funcs */
Boolean parse_int(ParameterNode* node, char* value);
Boolean parse_real(ParameterNode* node, char* value);
Boolean parse_boolean(ParameterNode* node, char* value);
Boolean parse_string(ParameterNode* node, char* value);
Boolean parse_list(ParameterNode* node, char* value);


/* ParameterNode operations to use in void* list function pointers */
ParameterNode* create_node(char* pname, param_t type, int required);
void destroy_node(ParameterNode* to_destroy);
void print_node(ParameterNode* to_print);
int compare_node(ParameterNode* to_compare, char* name);
Boolean compare_filled_node(ParameterNode* to_compare, char* name);
ParameterNode* getnode(ParameterManager* pm, char* pname);
