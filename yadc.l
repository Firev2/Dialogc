%{
   /*----------------
   Generated from yadc.l
   Alex Schwarz
   SID 0719732
   CIS2750 A4 
   ----------------*/
   #include "y.tab.h"
   extern int current_line;
%}

ws            [\t ]+
equals        =
sc            ;
left_brace    [{]
right_brace   [}]
comma         ,
comment       #(.*)
newline       \n
word          [^{},=;\n\t ]+

%%

{newline}     { current_line += 1; }
{ws}          { }
{equals}      { return EQUALS; }
{sc}          { return SEMICOLON; }
{left_brace}  { return LEFT_BRACE; }
{right_brace} { return RIGHT_BRACE; }
{comma}       { return COMMA; }
{comment}     { }
{word}        {
                  yylval.str_val = strdup(yytext);
                  
                  if(strcmp(yytext, "title") == 0) {
                      return GUI_TITLE;
                  }
                  else if(strcmp(yytext, "buttons") == 0) {
                      return GUI_BUTTONS;
                  }
                  else if(strcmp(yytext, "fields") == 0) {
                      return GUI_FIELDS;
                  }
                  /* if the word isn't title buttons or fields, it's some other word */
                  return WORD;
              }

%%
