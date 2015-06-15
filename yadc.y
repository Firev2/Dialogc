%{
        /* ------------------
        Alex Schwarz
        SID 0719732
        CIS*2750 A3
        y.tab.c: Generated from yadc.y
        --------------------*/
        #include <stdlib.h>
	#include <stdio.h>
        #include <string.h>
        #include "list.h"
        #include "hash.h"

        #define MAX_BUFFER_IN 4096

        char* GUI_title = NULL;

        list* temp_list = NULL;
        list* buttons_list = NULL;
        list* fields_list = NULL;
        hashtable* assignment_table = NULL;

        Boolean found_title = false; 
        Boolean found_buttons = false;
        Boolean found_fields = false;
        int current_line = 0;
%}

%union
{
    char *str_val;
}

%token COMMA
%token EQUALS
%token SEMICOLON
%token LEFT_BRACE
%token RIGHT_BRACE
%token QUOTATION

%token <str_val> GUI_TITLE
%token <str_val> GUI_BUTTONS
%token <str_val> GUI_FIELDS
%token <str_val> WORD

%type <str_val> expression
%type <str_val> str

%%

statement	: statement assignment 
                | assignment;
str		: GUI_TITLE
                | GUI_FIELDS
                | GUI_BUTTONS
                | WORD
                ;

expression	: str SEMICOLON                 { $$ = $1; }
                | SEMICOLON                     { }
                | LEFT_BRACE                    { return yyerror("Unexpected type"); }
                ;

list_expr	: LEFT_BRACE list_item SEMICOLON
                | str                           { return yyerror("Unexpected type"); }
                | SEMICOLON                     { return yyerror("Unexpected type"); }
                ;

list_item       : str COMMA list_item           {   if(temp_list == NULL)
                                                        temp_list = create_list(0);
                                                    
                                                    char word[MAX_BUFFER_IN];
                                                    char word_no_quotes[MAX_BUFFER_IN];
                                                    int first_quote = 0;
                                                    int i;
                                                    int j = 0;

                                                    strcpy(word,(char*)$1);

                                                    /* remove quotations from string. if no correct quotes in string, parse error! */
                                                    for(i = 0; i < strlen(word); i++) {
                                                        if(i == 0) {
                                                            if(word[0] != '"')
                                                                return yyerror("Invalid string found.");
                                                        }
                                                        else if(i == strlen(word)-1) {
                                                            if(word[i] != '"')
                                                                return yyerror("Invalid string found.");
                                                        }
                                                        else {
                                                            word_no_quotes[j] = word[i];
                                                            j++;
                                                        }
                                                    } /* check if string has correct quotations in it */
                                                      word_no_quotes[j] = '\0';

                                                    list_node* node = (list_node*)malloc(sizeof(list_node)*1);
                                                    node->data = (char*)malloc(sizeof(char)*strlen(word_no_quotes)+1);
                                                    strcpy((char*)node->data, word_no_quotes);
                                                    
                                                    add_list_back(temp_list,node);

						}
		| str RIGHT_BRACE		{   if(temp_list == NULL) {
                                                        temp_list = create_list(0);
                                                    }

                                                    char word[MAX_BUFFER_IN];
                                                    char word_no_quotes[MAX_BUFFER_IN];
                                                    int first_quote = 0;
                                                    int i;
                                                    int j = 0;

                                                    strcpy(word,(char*)$1);
                                                    /* remove quotations from string. if no correct quotes in string, parse error! */
                                                    for(i = 0; i < strlen(word); i++) {
                                                        if(i == 0) {
                                                            if(word[0] != '"')
                                                                return yyerror("Invalid string found.");
                                                        }
                                                        else if(i == strlen(word)-1) {
                                                            if(word[i] != '"')
                                                                return yyerror("Invalid string found.");
                                                        }
                                                        else {
                                                            word_no_quotes[j] = word[i];
                                                            j++;
                                                        }
                                                    } /* check if string has correct quotations in it */
                                                      word_no_quotes[j] = '\0';


                                                    list_node* node = (list_node*)malloc(sizeof(list_node)*1);
                                                    node->data = (char*)malloc(sizeof(char)*strlen(word_no_quotes)+1);
                                                    strcpy((char*)node->data, word_no_quotes);
                                                    add_list_back(temp_list,node);

						}
		| RIGHT_BRACE			{   if(temp_list == NULL)  
                                                        temp_list = create_list(0);

                                                }
                ;

assignment      : GUI_TITLE EQUALS expression   {   if(GUI_title != NULL)
                                                        return yyerror("Multiple titles found.");

                                                    found_title = true;
                                                    GUI_title = $3;
                                                    
                                                }
                | GUI_FIELDS EQUALS list_expr   {   if(fields_list == NULL) {
                                                        found_fields = true;
                                                        fields_list = temp_list;

                                                        /* check if duplicate names are in the list */
                                                        if(has_duplicate(fields_list) == 1)
                                                            return yyerror("Duplicate variable name found in fields.");

                                                        temp_list = NULL;
                                                    }
                                                    else 
                                                        return yyerror("Multiple field entries found.");
                                                    
				                } 
                | GUI_BUTTONS EQUALS list_expr	{   if(buttons_list == NULL) {
                                                        found_buttons = true;
                                                        buttons_list = temp_list;
                                                        temp_list = NULL;

                                                        /* check if duplicate names are in the list */
                                                        if(has_duplicate(buttons_list) == 1)
                                                            return yyerror("Duplicate variable name found in buttons.");
                                                    }
                                                    else
                                                        return yyerror("Multiple button entries found.");
                                                }                                                 
                | WORD EQUALS expression	{ 
                                                    if(found_title == false || found_buttons == false || found_fields == false) {

                                                        return yyerror("Assignment found before declaration of title, buttons, or fields.");
                                                    }
                                                    char word[MAX_BUFFER_IN];
                                                    char word_no_quotes[MAX_BUFFER_IN];
                                                    int first_quote = 0;
                                                    int i;
                                                    int j = 0;

                                                    strcpy(word,(char*)$3);
                                                    /* remove quotations from string. if no correct quotes in string, parse error! */
                                                    for(i = 0; i < strlen(word); i++) {
                                                        if(i == 0) {
                                                            if(word[0] != '"')
                                                                return yyerror("Invalid string found.");
                                                        }
                                                        else if(i == strlen(word)-1) {
                                                            if(word[i] != '"')
                                                                return yyerror("Invalid string found.");                         
                                                        }
                                                        else {                                                        
                                                            word_no_quotes[j] = word[i];
                                                            j++;
                                                        } 
                                                    } /* check if string has correct quotations in it */
                                                      word_no_quotes[j] = '\0';

                                                      /* add key-value pair to table */  
                                                      insert_pair(assignment_table,$1,word_no_quotes);
                                                }
		;

%%


extern FILE *yyin;

int yyerror(char *s) {
    fprintf(stderr,"Compilation error: %s (found at line %d)\n",s,current_line+1);
    exit(-1);
}

/*
int generate_actionListener(char* button_name) : generates an actionListener class based on button_name (added for A4)
pre: button_name is either "ADD", "DELETE", "UPDATE", or "QUERY", project_name must be a valid project name being compiled
post: generates a .java file based on button_name
*/
int generate_actionListener(char* button_name, char* file_path, char* project_name) {

    FILE* listener_file = fopen(file_path,"w");
    if(listener_file == (FILE*)NULL) {
        printf("Compilation error: Unable to generate %sListener.java.\n",button_name);
        return 0;
    }

    fprintf(listener_file,"import java.sql.*;\n");
    fprintf(listener_file,"import java.awt.*;\n");
    fprintf(listener_file,"import java.awt.event.*;\n");
    fprintf(listener_file,"import javax.swing.*;\n");

    fprintf(listener_file,"public class %sListener implements ActionListener {\n\n",button_name);
    fprintf(listener_file,"    %sFieldEdit i_FieldEdit;\n",project_name);
    fprintf(listener_file,"    public %sListener(%sFieldEdit i_fe) { \n",button_name,project_name);
    fprintf(listener_file,"        this.i_FieldEdit = i_fe;\n");
    fprintf(listener_file,"    }\n");

    fprintf(listener_file,"    public void actionPerformed(ActionEvent e) {\n\n");
    
    /* do jdbc stuff here */
    if(strcmp(button_name,"ADD") == 0) {
        fprintf(listener_file,"        i_FieldEdit.appendToStatusArea(\"Database INSERT would of taken place here.\\n\");\n");
        fprintf(listener_file,"    }\n");
    }
    else if(strcmp(button_name,"DELETE") == 0) {
        fprintf(listener_file,"        i_FieldEdit.appendToStatusArea(\"Database DELETE would of taken place here.\\n\");\n");
        fprintf(listener_file,"    }\n");
    }
    else if(strcmp(button_name,"UPDATE") == 0) {
        fprintf(listener_file,"        i_FieldEdit.appendToStatusArea(\"Database UPDATE would of taken place here.\\n\");\n");
        fprintf(listener_file,"    }\n");
    }
    else if(strcmp(button_name,"QUERY") == 0) {
        fprintf(listener_file,"        i_FieldEdit.appendToStatusArea(\"Database SELECT query would of taken place here.\\n\");\n");
        fprintf(listener_file,"    }\n");
    }

    fprintf(listener_file,"}\n");    
    fclose(listener_file);

    return 1;
}

/*
int main(int argc, char** argv) : Entry point for yadc program.
pre: argv[1] is the .config filename, argv[2] is the classname, argv[3] is the working directory. argc is 4.
post: generates .java files based on a .config file (parses the .config file and converts its contents to .java)
*/
int main(int argc, char** argv)
{
    
    FILE* config_file;
    FILE* written_file;

    /* our argv copies */
    char config_filename[MAX_BUFFER_IN];
    char cwd[MAX_BUFFER_IN];
    char classname[MAX_BUFFER_IN];
    char filename[MAX_BUFFER_IN];

    /* array of field and button names */
    char fields[255][MAX_BUFFER_IN];
    char buttons[255][MAX_BUFFER_IN];

    if(argc != 4) {

        printf("Usage: ./yadc <configfilePath> <class/project name> <working directory>\n");
        return -1; /* we'll use -1 as error for return in main, then 0 as compilation success */
    }
   
    assignment_table = create_ht(1024);

    strcpy(classname,argv[2]);
    strcpy(config_filename, argv[1]);
    strcpy(cwd,argv[3]);

    config_file = fopen(config_filename, "r");
    if(config_file == (FILE*)NULL) {
        printf("Could not open the config file: %s\n",config_filename);
        return -1;
    }

    /* set our yyin input stream to our config file for parsing */
    yyin = config_file;

    while(feof(yyin) == 0) {
        yyparse();
    }
 
    /* after parsing, check if the title buttons or fields are null. */
    if(GUI_title == NULL) {
        printf("Config file error: No title found in config file.\n");
        return -1;
    }

    if(fields_list == NULL) {
        printf("Config file error: No fields found in config file.\n");
        return -1;
    }

    if(buttons_list == NULL) {
        printf("Config file error: No buttons found in config file.\n");
        return -1;
    }

    list* buttons_list_copy = buttons_list;

    int i;
 
    /* check to make sure everything is initialized */
    for(i = 0; i < buttons_list->num_elements; i++) {

        char* key_value = NULL;
        key_value = get_value(assignment_table,buttons_list_copy->head->data);

        strcpy(buttons[i],buttons_list_copy->head->data); /* for later use */

        if(strcmp(buttons[i],"ADD") == 0 || strcmp(buttons[i],"DELETE") == 0 || strcmp(buttons[i],"UPDATE") == 0 || strcmp(buttons[i],"QUERY") == 0) {
            if(key_value != NULL) {
                printf("Compilation error: Listener was set for a reserved button.\n");
                return -1;
            } 
        }
        else if(key_value == NULL) {
            printf("Compilation error: button %s not initialized in config file.\n", buttons_list_copy->head->data);
            return -1;
        }

        buttons_list_copy->head = buttons_list_copy->head->next;
    }

    list_node* head = fields_list->head;
    list_node* fields_list_copy = fields_list->head;

    for(i = 0; i < fields_list->num_elements; i++) { /* find any uninit keypairs or improper types */
        
        char* key_value = NULL;

        key_value = get_value(assignment_table,fields_list_copy->data);

        strcpy(fields[i],fields_list_copy->data); /* for later use */

        if(key_value == NULL) {
            printf("Compilation error: field %s not initialized in config file.\n", fields[i]);
            return -1;   
        }
        else if(strcmp("string",key_value) != 0 && strcmp("integer",key_value) != 0 && strcmp("float",key_value) != 0) {
            printf("Compilation error: Value other than string, integer, or float was found. %s\n", fields_list_copy->data);
            return -1;
        }
        
        fields_list_copy = fields_list_copy->next;
    }

    fields_list->head = head;
    
    /* by this point parsing should be error-free, we can write files and compile */    
    strcpy(filename,cwd);
    strcat(filename,"/");
    strcat(filename,classname);
    strcat(filename,"/IllegalFieldValueException.java");

    written_file = fopen(filename,"w");

    if(written_file == (FILE*)NULL) { /* Dialogc should ensure this directory exists, but put this here just incase */
        printf("Could not write IllegalFieldValueException.java file.\n");
        return -1;
    }

    /* ...begin writing files. First, illegalexception file */
    fprintf(written_file, "public class IllegalFieldValueException extends Exception {\n\n");
    fprintf(written_file, "    public IllegalFieldValueException(String errorMsg) {\n");
    fprintf(written_file, "        super(errorMsg);\n");
    fprintf(written_file, "    }\n");
    fprintf(written_file, "}\n");
    fclose(written_file);

    /* write ..FieldEdit.java file */
    strcpy(filename,cwd);
    strcat(filename,"/");
    strcat(filename,classname);
    strcat(filename,"/");
    strcat(filename,classname);
    strcat(filename,"FieldEdit.java");

    written_file = fopen(filename,"w");

    if(written_file == (FILE*)NULL) { /* Dialogc should ensure this directory exists, but put this here just incase */
        printf("Could not write <projectName>FieldEdit.java file.\n");
        return -1;
    }
    
    fprintf(written_file, "interface ");
    fprintf(written_file, classname);
    fprintf(written_file, "FieldEdit {\n");

    
    for(i = 0; i < fields_list->num_elements; i++) {
        
        fprintf(written_file,"    public String getDC");
        fprintf(written_file,fields[i]);
        fprintf(written_file,"() throws IllegalFieldValueException;\n");
        fprintf(written_file,"    public void setDC");
        fprintf(written_file,fields[i]);
        fprintf(written_file,"(String ");
        fprintf(written_file,fields[i]);
        fprintf(written_file,");\n");
    }

    fprintf(written_file,"    public void appendToStatusArea(String toAppend);\n");
    fprintf(written_file,"}");
    fclose(written_file);

    /* now write our projectname.java file */
    strcpy(filename,cwd);
    strcat(filename,"/");
    strcat(filename,classname);
    strcat(filename,"/");
    strcat(filename,classname);
    strcat(filename,".java");

    written_file = fopen(filename,"w");

    if(written_file == (FILE*)NULL) { /* Dialogc should ensure this directory exists, but put this here just incase */
        printf("Could not write <projectName>.java file.\n");
        return -1;
    }

    fprintf(written_file,"import javax.swing.*;\n");
    fprintf(written_file,"import java.awt.*;\n\n");
    
    fprintf(written_file,"public class %s extends JFrame implements %sFieldEdit {\n\n",classname,classname);
    fprintf(written_file,"    private JTextArea status_Area;\n");

    fprintf(written_file,"\n    //fields\n");

    /* for a4 */
    fprintf(written_file,"    private JLabel ID_label;\n");
    fprintf(written_file,"    private JTextField ID_field;\n");

    for(i = 0; i < fields_list->num_elements; i++) {
        fprintf(written_file,"    private JLabel %s_label;\n",fields[i]);
        fprintf(written_file,"    private JTextField %s_field;\n",fields[i]);
    }

    fprintf(written_file,"\n    //buttons\n");
    for(i = 0; i < buttons_list->num_elements; i++) {
        fprintf(written_file,"    private JButton %s_Button;\n",buttons[i]);
    }

    fprintf(written_file,"\n    //constructor\n");
    fprintf(written_file,"    public %s() {\n",classname);
    fprintf(written_file,"        super(%s);\n",GUI_title); 
    fprintf(written_file,"        JPanel fields_Panel = new JPanel(new BorderLayout());\n");
    fprintf(written_file,"        JPanel buttons_Panel = new JPanel();\n");
    fprintf(written_file,"        JPanel upper_Panel = new JPanel(new BorderLayout());\n");
    fprintf(written_file,"        JPanel status_Panel = new JPanel(new BorderLayout());\n");
    fprintf(written_file,"        upper_Panel.add(fields_Panel, BorderLayout.NORTH);\n");
    fprintf(written_file,"        upper_Panel.add(buttons_Panel, BorderLayout.CENTER);\n");
    fprintf(written_file,"        getContentPane().add(upper_Panel, BorderLayout.NORTH);\n");
    fprintf(written_file,"        getContentPane().add(status_Panel, BorderLayout.CENTER);\n");
    fprintf(written_file,"        JPanel label_Panel = new JPanel(new GridLayout(%d, 1));\n",fields_list->num_elements+1);
    fprintf(written_file,"        JPanel text_field_Panel = new JPanel(new GridLayout(%d, 1));\n",fields_list->num_elements+1);
    fprintf(written_file,"        fields_Panel.add(label_Panel, BorderLayout.WEST);\n");
    fprintf(written_file,"        fields_Panel.add(label_Panel, BorderLayout.WEST);\n");
    fprintf(written_file,"        fields_Panel.add(text_field_Panel, BorderLayout.CENTER);\n");

    /* for a4: ID field */
    fprintf(written_file,"        ID_label = new JLabel(\"ID\", JLabel.RIGHT);\n");
    fprintf(written_file,"        ID_field = new JTextField(100);\n");
    fprintf(written_file,"        ID_label.setLabelFor(ID_field);\n");
    fprintf(written_file,"        label_Panel.add(ID_label);\n");
    fprintf(written_file,"        text_field_Panel.add(ID_field);\n");

    for(i = 0; i < fields_list->num_elements; i++) {

        fprintf(written_file,"        %s_label = new JLabel(\"%s\", JLabel.RIGHT);\n",fields[i],fields[i]);
        fprintf(written_file,"        %s_field = new JTextField(100);\n",fields[i]);
        fprintf(written_file,"        %s_label.setLabelFor(%s_field);\n",fields[i],fields[i]);
        fprintf(written_file,"        label_Panel.add(%s_label);\n",fields[i]);
        fprintf(written_file,"        text_field_Panel.add(%s_field);\n",fields[i]);
    }       

    fprintf(written_file,"\n");

    for(i = 0; i < buttons_list->num_elements; i++) {

        char* key_value = NULL;
        key_value = get_value(assignment_table,buttons[i]);
        
        /* for a4: autoassign actionlistener if found ADD,DELETE,UPDATE,QUERY */
        if(strcmp(buttons[i],"ADD") == 0 || strcmp(buttons[i],"DELETE") == 0 || strcmp(buttons[i],"UPDATE") == 0 || strcmp(buttons[i],"QUERY") == 0) {

            char listener_name[256];
            strcpy(listener_name,buttons[i]);
            strcat(listener_name,"Listener.java");

            strcpy(filename,cwd);
            strcat(filename,"/");
            strcat(filename,classname);
            strcat(filename,"/");
            strcat(filename,listener_name);

            int listener_result = generate_actionListener(buttons[i],filename,classname);

            if(listener_result == 0) {
                printf("Compilation error: Unable to generate %sListener.java.\n",buttons[i]);
                return -1;
            }

            fprintf(written_file,"        %s_Button = new JButton(\"%s\");\n",buttons[i],buttons[i]);
            fprintf(written_file,"        %s_Button.addActionListener(new %sListener(this));\n",buttons[i],buttons[i]);
            fprintf(written_file,"        buttons_Panel.add(%s_Button);\n",buttons[i]);
        }
        else {
            fprintf(written_file,"        %s_Button = new JButton(\"%s\");\n",buttons[i],buttons[i]);
            fprintf(written_file,"        %s_Button.addActionListener(new %s(this));\n",buttons[i],key_value);
            fprintf(written_file,"        buttons_Panel.add(%s_Button);\n",buttons[i]);
        }
    }

    fprintf(written_file,"\n");
    fprintf(written_file,"        status_Panel.add(new JLabel(\"Status\",JLabel.CENTER),BorderLayout.NORTH);\n");
    fprintf(written_file,"        status_Area = new JTextArea();\n");
    fprintf(written_file,"        status_Area.setLineWrap(true);\n");
    fprintf(written_file,"        status_Area.setEditable(false);\n");
    fprintf(written_file,"        JScrollPane status_scroll = new JScrollPane(status_Area);\n");
    fprintf(written_file,"        status_Panel.add(status_scroll, BorderLayout.CENTER);\n\n");
    fprintf(written_file,"        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);\n"); 
    fprintf(written_file,"        setSize(600, 400);\n");
    fprintf(written_file,"        setVisible(true);\n");
    fprintf(written_file,"    }\n");

    fprintf(written_file,"    public void appendToStatusArea(String message) {\n");
    fprintf(written_file,"        status_Area.append(message);\n");
    fprintf(written_file,"    }\n");

    /* added for a4: ID field */
    fprintf(written_file,"    public String getDCID() throws IllegalFieldValueException {\n");
    fprintf(written_file,"        try {\n");
    fprintf(written_file,"            Integer.parseInt(ID_field.getText());\n");
    fprintf(written_file,"        }\n");
    fprintf(written_file,"        catch(NumberFormatException e) {\n");
    fprintf(written_file,"            throw new IllegalFieldValueException(ID_field.getText());\n");
    fprintf(written_file,"        }\n");
    fprintf(written_file,"        return ID_field.getText();\n");
    fprintf(written_file,"    }\n");

    fprintf(written_file,"    public void setDCID(String ID) {\n");
    fprintf(written_file,"        ID_field.setText(ID);\n");
    fprintf(written_file,"    }\n");

    for(i = 0; i < fields_list->num_elements; i++) {

        char* key_value = NULL;

        key_value = get_value(assignment_table, fields[i]);

        fprintf(written_file,"    public String getDC%s() throws IllegalFieldValueException {\n",fields[i]);
    
        /* validate input. must be integer string or float */        
        if(strcmp(key_value,"integer") == 0) {

            fprintf(written_file,"        try {\n");
            fprintf(written_file,"            Integer.parseInt(%s_field.getText());\n", fields[i]);
            fprintf(written_file,"        }\n");
            fprintf(written_file,"        catch(NumberFormatException e) {\n");
            fprintf(written_file,"            throw new IllegalFieldValueException(%s_field.getText());\n", fields[i]);
            fprintf(written_file,"        }\n");
        }
        else if(strcmp(key_value,"float") == 0) {

            fprintf(written_file,"        try {\n");
            fprintf(written_file,"            Float.parseFloat(%s_field.getText());\n", fields[i]);
            fprintf(written_file,"        }\n");
            fprintf(written_file,"        catch(NumberFormatException e) {\n");
            fprintf(written_file,"            throw new IllegalFieldValueException(%s_field.getText());\n", fields[i]);
            fprintf(written_file,"        }\n");
        }

        fprintf(written_file,"        return %s_field.getText();\n",fields[i]);
        fprintf(written_file,"    }\n\n");

        fprintf(written_file,"    public void setDC%s(String %s) {\n", fields[i],fields[i]);
        fprintf(written_file,"        %s_field.setText(%s);\n",fields[i],fields[i]);
        fprintf(written_file,"    }\n");
    }

    fprintf(written_file,"\n");
    fprintf(written_file,"    public static void main(String[] args) {\n");
    fprintf(written_file,"        new %s();\n",classname);
    fprintf(written_file,"    }\n");
    fprintf(written_file,"}");

    fclose(written_file);
    /* we're done writing all the required files. close the last one and return 0 to indicate success. */

    return 0;
}

