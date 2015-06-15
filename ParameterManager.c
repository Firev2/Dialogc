/*-----------------------
ParameterManager.c
By Alex Schwarz
SID 0719732
CIS*2750 A1
Performs the registering and storage of "Name = value" expressions
Includes helper functions such as parsing
------------------------*/

#include "ParameterManager.h"

/*
function: PM_create

pre: size is a positive integer (see note 1 below)
post: Returns a new parameter manager object initialized to be empty (i.e. managing no parameters) on success, NULL otherwise (memory allocation failure)
*/
ParameterManager * PM_create(int size) {

    if(size <= 0)
        return (ParameterManager*)NULL;

    ParameterManager* pm  = (ParameterManager*)malloc(sizeof(ParameterManager)*1);

    if(pm == NULL)
        return (ParameterManager*)NULL;

    pm->param_list = create_list();

    if(pm->param_list == NULL)
        return NULL;

    return pm;
}

/*
function: PM_destroy

pre: n/a
post: all memory associated with parameter manager p is freed; returns 1 on success, 0 otherwise
*/
int PM_destroy(ParameterManager *p) {

    if(p == NULL || p->param_list == NULL)
        return 0;

    /* todo: change to bool */
    destroy_list(p->param_list,(void*)destroy_node);
    free(p);
    return 1;
}

/*
function: parse_int
converts a string to an integer and fills the node with it's value.
pre: node is a pointer to a valid ParameterNode, value is a string representation of an integer.
post: node.int_val is filled with an integer.
*/
Boolean parse_int(ParameterNode* node, char* value) {

    if(value == (char*)NULL)
        return false;

    int i;
    int len = strlen(value);

    for(i = 0; i < len; i++) {
        if(value[i] == '.')
            return 0;
        else if(value[i] <= '0' && value[i] >= '9')
            return false;
        else if(value[i] == '-' && i == 0)
            continue;
    }

    node->pval.int_val = atoi(value);
    node->filled = true;
    return true;
}

/*
function: parse_real
converts a string to a float and fills the node with it's value.
pre: node is a pointer to a valid ParameterNode, value is a string representation of a float
post: post: node.real_val is filled with a float.
*/
Boolean parse_real(ParameterNode* node, char* value) {

    Boolean found_decimal_point = false;
    Boolean found_decimal_number = false;

    int i;
    int len = strlen(value);

    for(i = 0; i < len; i++) {
        if(value[i] == '.')
            found_decimal_point = true;
        else if((value[i] >= '0' && value[i] <= '9') && found_decimal_point == true)
            found_decimal_number = true;
    }

    if(found_decimal_point != true && found_decimal_number != true)
        return false;

    node->pval.real_val = atof(value);
    node->filled = true;
    return true;
}

/*
function: parse_boolean
converts a string to a boolean and fills the node with it's value.
pre: node is a pointer to a valid ParameterNode, value is a string representation of a boolean ("true" or "false")
post: post: node.bool_val is filled with a float.
*/
Boolean parse_boolean(ParameterNode* node, char* value) {

    if(value == (char*)NULL)
        return false;

    if(strcmp(value,"true") == 0) {
        node->pval.bool_val = true;
        return true;
    }
    else if(strcmp(value,"false") == 0) {
        node->pval.bool_val = false;
        return true;
    }

    return false;
}

/*
function: parse_string
converts a quoted string to an unquoted string and fills the node with it's value.
pre: node is a pointer to a valid ParameterNode, value is a non-null string surrounded by quotations (")
post: post: node.str_val is filled with a string.
*/
Boolean parse_string(ParameterNode* node, char* value) {

    if(value == (char*)NULL)
        return false;

    char temp[1025];
    int i = 0;
    int j = 0;
    int len = strlen(value);

    Boolean found_first_quotation = false;
    Boolean found_second_quotation = false;

    for(i = 0; i < len; i++) {
        if(value[i] == '"' && found_first_quotation == false)
            found_first_quotation = true;
        else if(value[i] == '"' && found_first_quotation == true)
            found_second_quotation = true;
        else if(value[i] != '"' && found_first_quotation == true) {
            temp[j] = value[i];
            j++;
        }
        else if(found_second_quotation == true)
            return false; /* bad input */
    }

    temp[j] = '\0';

    int trimmed_length = strlen(temp);

    node->pval.str_val = (char*)malloc(sizeof(char)*trimmed_length+1);
    strcpy(node->pval.str_val, temp);

    if(found_first_quotation != true && found_second_quotation != true)
        return false;

    return true;
}

/*
function: parse_list
converts a string to a sequence of strings and fills a list with the values.
pre: node is a pointer to a valid ParameterNode, value is a string representation of a ParameterList
post: node.list_val is filled with a pointer to a list containing a 2D array of strings
*/
Boolean parse_list(ParameterNode* node, char* value) {

    if(node == NULL || value == (char*)NULL)
        return false;

    Boolean found_first_bracket = false;
    Boolean found_second_bracket = false;

    Boolean found_first_quotation = false;
    Boolean found_second_quotation = false;

    int i;
    int j = 0;
    int item_count = 0;
    int count = strlen(value);
    int num_found = 0;

    char buf[1025];
    char temp[200][200]; /* todo: find a better solution for static-size matrix */


    /* state-based parsing ftw */
    for(i = 0; i < count; i++) {

        if(value[i] == '{' && found_first_bracket == false)
            found_first_bracket = true;
        else if(value[i] == '}' && found_first_bracket == true)
            found_second_bracket = true;
        else if(value[i] == ' ' || value[i] == '\t') {
            continue;
        }
        else if(found_first_quotation == false && found_first_bracket == true) { /* finding first quotation... */

            if(value[i] == '"')
                found_first_quotation = true;
            else
                return 0;
        }
        else if(found_first_quotation == true && found_second_quotation == false) { /* found first quotation, copy characters until second quote */

            if(value[i] == '"') {
                found_second_quotation = true;
                buf[j++] = '\0';
                j = 0;
                num_found++;

                /* add to 2D array, clear buf */
                strcpy(temp[item_count++],buf);
                
                int l;
                for(l = 0; l < 1025; l++)
                    buf[l] = 0x00; /* clear the buffer for next parameter to be found */
            }
            else {
                if(j < 1024) {
                    buf[j++] = value[i];
                }
                else
                    return 0;
            }
        }
        else if(value[i] == ',' && found_second_quotation == true) {
            found_first_quotation = false;
            found_second_quotation = false;
        }
        else {
            return 0;
        }
    }

    if(found_first_bracket == false || found_second_bracket == false)
        return false;

    ParameterList* p_list = (ParameterList*)malloc(sizeof(ParameterList)*1);
    p_list->current_pos = 0;

    if(p_list == NULL)
        return false;

    /* insert string sequence to list here */
    p_list->list = (char**)malloc(sizeof(char*)*item_count);

    for(i = 0; i < item_count; i++) {
        p_list->list[i] = (char*)malloc(sizeof(char)*strlen(temp[i])+1);
        strcpy(p_list->list[i],temp[i]);
    }

    p_list->count = item_count;
    node->pval.list_val = p_list;
    return true;
}

/*
function: store_value
converts a string to its appropriate type based on the string's format and node's type.
pre: node is a pointer to a valid ParameterNode, value is a non-null string
post: node is filled with a value
*/

Boolean store_value(ParameterNode* node, char* value) {

    if(value == (char*)NULL || node == (ParameterNode*)NULL)
        return false;

    switch(node->type) {

        case INT_TYPE: {
            if(parse_int(node,value) == false)
                return false;
        }break;

        case REAL_TYPE: {
            if(parse_real(node,value) == false)
                return false;
        }break;

        case BOOLEAN_TYPE: {
            if(parse_boolean(node,value) == false)
                return false;
        }break;

        case STRING_TYPE: {
            if(parse_string(node,value) == false)
                return false;
        }break;

        case LIST_TYPE: {
            if(parse_list(node,value) == false)
                return false;
        }break;
    };

    node->filled = true;
    return true;
}

/*
function: PM_parseFrom

pre: fp is a valid input stream ready for reading that contains the desired parameters
post: All required parameters, and those optional parameters present, are assigned values that are consumed from fp, respecting comment as a "start of comment to end of line" character if not nul ('\0'); returns non-zero on success, 0 otherwise (parse error,memory allocation failure)
*/

int PM_parseFrom(ParameterManager *p, FILE *fp, char comment) {

    /* todo: find way to remove code bloating from bad parse (copying + deletion) */

    /* state-based */
    char ch;
    int acquired_index = 0;

    Boolean inside_comment = false; /* state of: inside a comment */
    Boolean inside_quotation = false;
    Boolean acquired_name = false; /* state of: found X where X = Y */

    char param_val[MAX_PARAM_LENGTH];
    char temp[MAX_PARAM_LENGTH];

    ParameterManager* cpy = PM_copy(p); /* make a copy of p incase parsing fails, we can reset p to its copy */

    while ( (ch = fgetc(fp)) != EOF) {

        if((ch == ' ' && inside_quotation == false)|| ch == '\t') /* ignore whitespace, isspace() wont work for this because of needing new lines */
                continue;
        else if(ch == '\n') /* newline found = no more comment status */
            inside_comment = false;
        else if(inside_comment == false) { /* we are not inside a comment, and have found a character other than the comment char */

            if(ch == comment) { /* checks for !#! == EOF */
                inside_comment = true;
                int pos = ftell(fp);
                char next = fgetc(fp);
                    if(next == '!') {
                        char _next = fgetc(fp);
                            if(_next == comment) {
                                if(all_required_filled(p) == false) {
                                    ParameterManager* p_copy = p;
                                    p = cpy;
                                    PM_destroy(p_copy);
                                    return 0;
                                }

                                return 1;
                            }
                    }
                    else {
                        rewind(fp);
                        fseek(fp,pos,0);
                    }


            }
            else if(ch == '=') {

                if(acquired_name == false && acquired_index > 0) { /* read in param name if we haven't already */
                    param_val[acquired_index] = '\0';
                    strcpy(temp,param_val);
                    acquired_name = true;
                    acquired_index = 0;
                }
                else {/* bad equals placement in file */
                     ParameterManager* p_copy = p;
                     p = cpy; 
                     PM_destroy(p_copy);
                     return 0;
                }
            }
            else if(ch == ';') {

                if(acquired_name == true && acquired_index > 0) {

                    param_val[acquired_index] = '\0';
                    acquired_name = false;
                    acquired_index = 0;
                   
                    /* if our managed list finds the input parameter name, we can store its value. */
                    if(list_contains(p->param_list, temp, (void*)compare_node)) {
                       
                        ParameterNode* node = get_node(p->param_list, temp,(void*)compare_node);

                        if(node->filled == false && node != (ParameterNode*)NULL) {
                            if(!store_value(node, param_val)) {
                                ParameterManager* p_copy = p;
                                p = cpy;
                                PM_destroy(p_copy);
                                return 0;
                            }
                        }
                    } 
                    else {
                        ParameterManager* p_copy = p;
                        p = cpy;
                        PM_destroy(p_copy);
                        return 0;
                    }
               }
                else {
                    ParameterManager* p_copy = p;
                    p = cpy;
                    PM_destroy(p_copy);
                    return 0;
                }
            }
            else if(ch == '"' && inside_quotation == false) {
                inside_quotation = true;
                if(acquired_index < MAX_PARAM_LENGTH - 1) { /* max len */

                    param_val[acquired_index] = ch;
                    acquired_index++;
                }
                else {
                     ParameterManager* p_copy = p;
                     p = cpy;
                     PM_destroy(p_copy);
                     return 0;
                }
            }
            else if(ch == '"' && inside_quotation == true) {
                inside_quotation = false;
                if(acquired_index < MAX_PARAM_LENGTH - 1) { /* max len */

                    param_val[acquired_index] = ch;
                    acquired_index++;
                }
                else {
                     ParameterManager* p_copy = p;
                     p = cpy;
                     PM_destroy(p_copy);
                     return 0;
                }
            }
            else { /* all other characters */
                if(acquired_index < MAX_PARAM_LENGTH - 1) { /* max len */

                    param_val[acquired_index] = ch;
                    acquired_index++;
                }
                else {
                     ParameterManager* p_copy = p;
                     p = cpy;
                     PM_destroy(p_copy);
                     return 0;
                }
            }
        }
    }

    if(acquired_name == true) {
        ParameterManager* p_copy = p;
        p = cpy;
        PM_destroy(p_copy);
        return 0; /* we shouldn't have an acquired name at this point since it's EOF */  
    }

    /* check to see if everything required has been assigned a value of the right type */
    if(all_required_filled(p) == false) {
       
        ParameterManager* p_copy = p;
        p = cpy;
        PM_destroy(p_copy);
        return 0;
    }

    return 1;
}

ParameterManager* PM_copy(ParameterManager* to_copy) {

    if(to_copy == NULL)
        return NULL;

    ParameterManager* copy = PM_create(1);

    list_node* list_copy = to_copy->param_list->head;

    while(list_copy != NULL) {
    
        ParameterNode* temp = (ParameterNode*)list_copy->data;
        char temp_name[1024];
        if(temp->pname != NULL) {
            strcpy(temp_name,temp->pname);
            int required = temp->required;
            param_t type = temp->type;
            PM_manage(copy,temp_name,type,required);

            list_copy = list_copy->next;
        }
    }

    return copy;
}

/*
function: PM_manage

pre: pname does not duplicate the name of a parameter already managed
post: Parameter named pname of type ptype (see note 3 below) is registered with p as a parameter; if required is zero the parameter will be considered optional, otherwise it will be considered required; returns 1 on success, 0 otherwise (duplicate name, memory allocation failure)
*/
int PM_manage(ParameterManager *p, char *pname, param_t ptype, int required) {
    
    if(p == (ParameterManager*)NULL)
        return 0;
   
   /*if(list_contains(p->param_list,pname,(void*)compare_node)) {
       return 0;
    } */

    ParameterNode* pnode = create_node(pname,ptype,1);

    list_node* node = (list_node*)malloc(sizeof(list_node)*1); 
    node->data = (void*)pnode;

    add_list_back(p->param_list,(list_node*)node);
    return 1;
}

/*
function: PM_hasValue

pre: pname is currently managed by p
post: Returns 1 if pname has been assigned a value, 0 otherwise (no value, unknown parameter)
*/
int PM_hasValue(ParameterManager *p, char *pname) {

    if(p == NULL || pname == (char*)NULL)
        return 0;

    if(list_contains(p->param_list, pname, (void*)compare_node) == true) { /* see if name is regisered */
        return true;
    }
    return false;
}


/*
function: PM_getValue

pre: pname is currently managed by p and has been assigned a value
post: Returns the value (see note 4 below) assigned to pname; result is undefined if pname has not been assigned a value or is unknown
*/
union param_value PM_getValue(ParameterManager *p, char *pname) {

    union param_value val;

    if(p == NULL || pname == (char*)NULL)
        return val;

    if(PM_hasValue(p,pname) != 1) {
        return val;
    }

    ParameterNode* node = get_node(p->param_list, pname,(void*)compare_node);

    if(node->filled == true && node != (ParameterNode*)NULL) { /* only get a value if it's filled */
        val = node->pval;
    }

    return val;
}

/*
function: all_required_filled

pre: p is a pointer to a non-NULL ParameterManager object
post: returns true if all registered parameters contain a value
*/
Boolean all_required_filled(ParameterManager* p) {

    list_node* temp = p->param_list->head;
    
    int i;
    
    for(i = 0; i < p->param_list->num_elements; i++) {

        if(temp == NULL)
            return false;
        else {
            ParameterNode* node = temp->data;
            if(node->required == true) {
                if(node->filled == false)
                    return false;
            }
            else
                temp = temp->next;
        }

    }

    return true;
}

/*
function: create_node
Initializes a new ParameterNode* and fills it with data based on function parameters
pre: pname is a null-terminated string
post: node is a valid ParameterNode* and filled with correct data
*/
ParameterNode* create_node(char* pname, param_t type, int required) {

    ParameterNode* node = (ParameterNode*)malloc(sizeof(ParameterNode)*1);

    if(node == NULL || pname == NULL)
        return (ParameterNode*)NULL;

    node->pname = (char*)malloc(sizeof(char)*strlen(pname)+1);
    strcpy(node->pname,pname);

    node->type = type;
    node->required = required;
    node->filled = false;

    return node;
}

/*
function: destroy_node
frees the memory associated with a ParameterNode* structure
pre: to_destroy is a valid ParameterNode* (not NULL)
post: any memory associated with to_destroy is freed, then to_destroy is freed.
*/

void destroy_node(ParameterNode* to_destroy) {

     free(to_destroy->pname);

/*  if(to_destroy->type == STRING_TYPE) {

        if(to_destroy->filled == true)
            free(to_destroy->pval.str_val);
    }*/

    return;
}

/*
function: compare_node
Compares the "name" of a node to a string literal, used as a function pointer iterator
pre: to_compared is a non-null ParameterNode*, name is a null-terminated string
post:
*/
int compare_node(ParameterNode* to_compare, char* name) {

    if(to_compare == NULL)
        return 0;

    if(name == NULL)
        return 0;

    if(strcmp(to_compare->pname,name) == 0)
        return true;

    return false;
}

/*
function: compare_filled_node
Compare only nodes that have values to a string literal, used as a function pointer iterator
pre: to_compared is a non-null ParameterNode*, name is a null-terminated string
post:
*/
Boolean compare_filled_node(ParameterNode* to_compare, char* name) {

    if(to_compare == NULL)
        return false;

    if(strcmp(to_compare->pname,name) == 0 && to_compare->filled == true)
        return true;

    return false;
}
