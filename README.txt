--------------+
Alex Schwarz  
SID 0719732   
CIS*2750 A4   
README.txt    
--------------+

Any specific questions or requests for previous version README's can be sent to aschwarz@mail.uoguelph.ca!

-------------------------------------
|       Table of Contents           |
-------------------------------------
I.                           Overview
II.                      Installation 
III.         ParameterManager Library
VI.                  Dialogc Compiler
V.           lex+yacc (yadc) Compiler
+-----------------------------------+

-------------------------------+
I. Overview: Dialogc as a whole
-------------------------------+

Dialogc is a system which takes a .config file as input and generates .java files as output. Dialogc has two separate compilers: yadc and the Dialogc compiler. The yadc compiler is selected by default. Each compiler along with the Dialogc IDE is explained in detail below.

------------------------+
II. - Dialogc Compiler 
------------------------+
Dialogc is a GUI-based program intended to produce working .java files from a .config file
input source. It acts as a compiler of .config files. Upon successful compilation of a .config
file, two .java files are produced. They take the form of projectname.java and projectnameFieldEdit.java.

Unlike the yadc compiler (as seen below in section III), Dialogc does not have any reserved button names. Besides this, the same .config compilation rules apply for Dialogc as yadc.

-------------------------------+
III. - lex+yacc (yadc) Compiler 
-------------------------------+
Dialogc's ability to generate .java files from .config files can now be achieved from the external program yadc. yadc was built using lex and yacc and is intended to produce the same java code as the Dialogc compiler does.

yadc introduces some new restrictions on .config files: see section II

1) the first statements found in the .config file should be the initialization of title, fields, and buttons (see appendix A for further detail).
   -> If other statements besides comments are found before title, buttons, and fields initialization, a parse error will occur at compilation.

2) Duplicate variable names are not allowed
   -> If there are two buttons or fields with the same name, a parse error will occur at compilation.


yadc can be invoked in the follower manner:

./yadc <config_file_path> <class/project_name> <working_directory>

Upon compilation if a folder named <working_directory>/<classname> does not exist, it will be created, and if compilation is successful three .java files will be created in such directory.

The three files generated are:

1) IllegalFieldValueException.java

This file is the same regardless of the .config file. Works as an exception class for parsing fields in the compiled dialog.

2) <project/class_name>FieldEdit.java

This file acts as an interface and declares methods related to the buttons and fields found in the .config file. These methods will then be implemented by the third .java file, seen below.

3) <project/class_name>.java

This file is the generated dialog class. It implements the methods found in the above java file's interface, and does all the GUI-related work of the compiled dialog.

After generation, the above three .java files can be compiled together to create a <project/class_name>.class file.

Example of a .config file and the file names it generates:

example3.config:

-- start of file --

title = "A3Example"
buttons = { "Queue", "Order", "DropTable", "ADD" };
fields = { "Student_id", "First_name", "Last_name" };

#!#

Student_id = "integer";
First_name = "string";
Last_name = "string";

Queue = "ExampleListener";
Order = "ExampleListener";
DropTable = "ExampleListener";

-- end of file --

Generates:
1) example3.java 
2) example3FieldEdit.java 
3) IllegalFieldValueException.java

Reserved button names (applies only to yadc compiler)
-----------------------------------------------------

The button names ADD, DELETE, UPDATE, and QUERY are used as reserved button names. If a user declares one of these names in their button's list, an actionListener will be automatically generated for the respective buttons. This is meant such that database integration could be done inside the generated actionListener. If a user declares other buttons, they will have to provide their own actionListeners.

----------------+
IV. Dialogc IDE 
----------------+

The Dialogc IDE supports a variety of features which allows one to work with .config files. A large edit area is provided for the creation and editing of .config files, along with several buttons and menu options.

Buttons:
--------

Menus:
------


Shortcuts:
----------

Shortcuts are available for all menu options.

-----------------+
V. Installation 
-----------------+

To install Dialogc, the following are required on your computer:

1. make
2. lex/yacc
3. java
4. python (for install script)

Dialogc must be built and then installed. Any errors found during either the build or install process will be reported to the user before exiting the script. 

To initiate the build process, type "python install_dialogc.py --build" at the command line and follow any instructions given during the process.

If the build was successful, type "python install_dialogc.py --install" at the command line and follow any instructions given during the process. Installing will prompt the user to enter a directory to install to. This is considered the "base" directory. From the base directory, two more directories are made: bin and lib. 

If installation was successful, directory "bin" will contain all Dialogc executables (yadc and any .class files needed), and directory "lib" will contain library files which can be used in your own C or java applications.

During installation, the installer will create a file within your unix home directory called "config.dialogc". This file contains default options used in the Dialogc IDE and should not be deleted after installation. If this file was deleted somehow, the Dialogc IDE will assume default values for each option.

Default options:
---------------
Java compiler: javac
Java Runetime Arguments: none
Working Directory: current directory of Dialogc (or .)
Database: none (any relevant actionListeners should set this value for database integration)
Compiler args: none
Java path: java


-----------+
VI. Misc. 
-----------+

Fixed limitations from previous version
---------------------------------------
- Cleaned up identation inconsistencies and added function headers
- Added menu shortcuts for all menus and menu options
- Made it so an empty document doesn't crash on compilation
- Added horizontal scrollbar to edit area

yadc .config file rules for compilation
----------------------------------------

If the user compiles and wishes to run a .config file using either Dialogc or yadc, it is their responsibility to ensure the correct actionListener.java files are present in the folder where .java files are emitted.

For the above example3.config file to successfully compile and run, the action listener ExampleListener.java must exist and be previously compiled using javac or some other java compiler.

Empty lists in config files are valid, such that:

buttons = { };
fields = { };

will compile with no issues.

When using the lex + yacc compiler, the titles, buttons, and fields declarations must appear first in the .config file. This restraint is not applicable to the Dialogc compiler.

Whitespace within a title declaration should not be used with yadc.

Known Limitations
-------------------

- PM_destroy still not working properly: freeing memory causes a JVM crash and I can't yet find what's specifically causing it, even with attempts at debugging.
- external compiler (yadc) will not compile a file if it has spaces in it's title name. ex) title = "ex ample"; will not compile using the yadc compiler. It will compile using the dialogc compiler however.


