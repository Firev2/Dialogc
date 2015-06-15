//---------------------
// Compiler.java
// By Alex Schwarz
// SID 0719732
// For: CIS*2750 A4
//---------------------
import java.io.*;
import java.util.*;
import javax.swing.*;

// Class Compiler
// Uses a ParameterInterface along with an input source to generate two .java files: an interface and a main java program.
public class Compiler {

    public Process executable_Process; //process of actual compiled dialog
    public Process compiler_Process; //process for compiler running
    public Thread runThread;

    public int compilation_return; //exit code of compiler process

    public ParameterInterface pm1;
    public ParameterInterface pm2;

    public String title;    //our required components
    public String[] fields;
    public String[] buttons;

    public String no_Extension; //filename without extension

    //InvokeExternalCompiler(File,String,File)
    //Invokes the lex+yacc compiler and attempts to create .java files out of a .config file
    //pre: configFile is a File structure pointing to a valid .config file, classname is the project name
    //post: returns no value, but throws IOException.
    //post: Attempts to compile the .config file using the yadc compiler
    public void InvokeExternalCompiler(File configFile, String classname, File cwd) throws IOException {

        int end = 0;

        if(classname.equals("") || classname.equals(" ")) {

            String name = configFile.getName().toString();

            if (name.indexOf(".") > 0) {
                classname = name.substring(0, name.lastIndexOf("."));
            }
          
        }

        String streamline = new String(); //stream used in executing java compiler
        String error = new String(); //our error message, if any

        String full_command = new String("./yadc " + configFile.toString() + " " + classname + " " + cwd);
        
        try {
            compiler_Process = Runtime.getRuntime().exec(full_command); //execute the compiler!

            BufferedReader error_reader = new BufferedReader(new InputStreamReader(compiler_Process.getErrorStream()));

            while ((streamline = error_reader.readLine()) != null)
                error += streamline + "\n";//print any errors

            compilation_return = compiler_Process.waitFor(); //get return status from compilation...

            if(compilation_return != 0) {
                throw new IOException("Error occured while compiling: " + full_command);
                
            }

        }
        catch(Exception e) {
             throw new IOException("Failed to execute command: " + e);
        }
    }

    //function write_illegalExceptionFile:
    //Writes the IllegalFieldValueException file
    //has no return value, but throws IOException.
    //pre:  classname is the project name 
    //post: generates a file called illegalExceptionFile.java
    public void write_illegalExceptionFile(File cwd, String classname) throws IOException {

        String javaFile_Name = cwd + "/" + classname + "/IllegalFieldValueException.java";
      
        //open file, write to it
        PrintWriter writer = new PrintWriter(new BufferedWriter(new FileWriter(javaFile_Name)));

        writer.println("public class IllegalFieldValueException extends Exception {\n\n");
        writer.println("    public IllegalFieldValueException(String errorMsg) {\n");
        writer.println("        super(errorMsg);\n");
        writer.println("    }\n");

        writer.println("}\n");
        writer.close();

    }

    //function String write_interface:
    //writes the "...FieldEdit.java" file required for a generated class.
    //returns the classname of the generated interface as a string
    //pre: classname is the project name, working_Directory is a valid directory on the file system
    //post: writes the <projectnameFieldEdit>.java file
    public String write_interface(String classname, File working_Directory) throws IOException {

        String newName = working_Directory + "/" + classname + "/" + classname + "FieldEdit.java";

        File file = new File(newName);

        PrintWriter writer = new PrintWriter(new BufferedWriter(new FileWriter(newName)));

        //Start of xxFieldEdit.java file
        writer.println("interface " + classname + "FieldEdit" + " {\n");

        for(String field : fields) {
             writer.println("     public String getDC" + field + "() throws IllegalFieldValueException;");
             writer.println("     public void setDC" + field + "(String " + field + ");");
             writer.println("    ");
         }

        writer.println("\n     public void appendToStatusArea(String toAppend);");

        writer.println("}");
        writer.close();

        return file.getName().toString();
    }

    //function void write_java_file(String file_Name, File working_Directory, String interface_name, ParameterInterface pm, String classname)
    //Generates a .java file based on a .config file.
    //pre: pm is a pointer to a non-null ParameterInterface, classname is the project name, interface_name matches the project name + "FieldEdit"
    //post: <projectname>.java and <projectnameFieldEdit>.java are generated
    public void write_java_file(String file_Name, File working_Directory, String interface_name, ParameterInterface pm, String classname) throws IOException {

        String newName = working_Directory + "/" + classname + "/" + classname + ".java";

        PrintWriter writer = new PrintWriter(new BufferedWriter(new FileWriter(newName)));

        // Start of file, Write imports
        writer.println("import java.awt.*;");
        writer.println("import javax.swing.*;");
        writer.println();
        writer.println("public class " + classname + " extends JFrame implements " + interface_name + " {");
        writer.println("    ");
        writer.println("    // bottom textbox for status");
        writer.println("    JTextArea status_Area;");
        writer.println("    ");
        writer.println("    // Fields");

        for(String field : fields) {
            writer.println("    private JLabel " + field + "_label;");
            writer.println("    private JTextField " + field + "_field;");
        }

        writer.println("    ");
        writer.println("    // buttons");
        for(String button : buttons) {
            writer.println("    private JButton " + button + "_Button;");
        }

        writer.println("    ");
        writer.println("    // Class constructor");
        writer.println("    public " + classname + "() {\n");
        writer.println("        super(\"" + title +"\");\n");
        writer.println("        JPanel fields_Panel = new JPanel(new BorderLayout());");
        writer.println("        JPanel buttons_Panel = new JPanel();");
        writer.println("        JPanel upper_Panel = new JPanel(new BorderLayout());");
        writer.println("        JPanel status_Panel = new JPanel(new BorderLayout());\n");
        writer.println("        upper_Panel.add(fields_Panel, BorderLayout.NORTH);");
        writer.println("        upper_Panel.add(buttons_Panel, BorderLayout.CENTER);\n");
        writer.println("        getContentPane().add(upper_Panel, BorderLayout.NORTH);");
        writer.println("        getContentPane().add(status_Panel, BorderLayout.CENTER);\n");
        writer.println("        JPanel label_Panel = new JPanel(new GridLayout(" + fields.length + ", 1));");
        writer.println("        JPanel text_field_Panel = new JPanel(new GridLayout(" + fields.length + ", 1));\n");
        writer.println("        fields_Panel.add(label_Panel, BorderLayout.WEST);");
        writer.println("        fields_Panel.add(text_field_Panel, BorderLayout.CENTER);\n");

        for(String field : fields) {
            writer.println("        " + field + "_label = new JLabel(\"" + field + "\", JLabel.RIGHT);");
            writer.println("        " + field + "_field = new JTextField(10);");
            writer.println("        " + field + "_label.setLabelFor(" + field + "_field);");
            writer.println("        label_Panel.add(" + field + "_label);");
            writer.println("        text_field_Panel.add(" + field + "_field);");
            writer.println("        ");
         }

         for(String button : buttons) {
             writer.println("        " + button + "_Button = new JButton(\"" + button + "\");");
             writer.println("        " +  button + "_Button.addActionListener(new " + pm.J_getString(button) + "(this));");
             writer.println("        buttons_Panel.add(" + button + "_Button);");
             writer.println("        \n");
         }

         writer.println("        status_Panel.add(new JLabel(\"Status\", JLabel.CENTER), BorderLayout.NORTH);");
         writer.println("        status_Area = new JTextArea();");
         writer.println("        status_Area.setLineWrap(true);");
         writer.println("        status_Area.setEditable(false);\n");
         writer.println("        JScrollPane status_scroll = new JScrollPane(status_Area);");
         writer.println("        status_Panel.add(status_scroll, BorderLayout.CENTER);\n");
         writer.println("        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);");
         writer.println("        setSize(600, 400);");
         writer.println("        setVisible(true);\n");
         writer.println("    }");
         writer.println("    ");

         for(String field : fields) { //For A3: make sure values are correct inside fields
             
             writer.println("    public String getDC" + field + "() throws IllegalFieldValueException");
             writer.println("    {");

             String s = pm2.J_getString(field);
             
             if(s.equals("integer")) {

                 writer.println("        try {");
                 writer.println("            Integer.parseInt(" + field + "_field.getText());");   
                 writer.println("        }");
                 writer.println("        catch(NumberFormatException e) {");
                 writer.println("            throw new IllegalFieldValueException(" + field + "_field.getText());");
                 writer.println("        }");

             }
             else if(s.equals("float")) {

                 writer.println("        try {");
                 writer.println("            Float.parseFloat(" + field + "_field.getText());");
                 writer.println("        }");
                 writer.println("        catch(NumberFormatException e) {");
                 writer.println("            throw new IllegalFieldValueException(" + field + "_field.getText());");
                 writer.println("        }");
             } //no case for "string" required

             writer.println("        return " + field + "_field.getText();");
             writer.println("    }");
             writer.println("    ");
             writer.println("    public void setDC" + field + "(String " + field + ")");
             writer.println("    {");
             writer.println("        " + field + "_field.setText(" + field + ");");
             writer.println("    }");
             writer.println("    \n");
         }

         writer.println("    public void appendToStatusArea(String message)");
         writer.println("    {");
         writer.println("        status_Area.append(message + \"\\n\");");
         writer.println("    }");
         writer.println("    ");
         // main() : public static void main(String[] args)
         writer.println("    // main method.");
         writer.println("    public static void main(String[] args)");
         writer.println("    {");
         writer.println("        new " + classname + "();");
         writer.println("    }");
         writer.println("    \n");
         writer.println("}");
         writer.close();
    }

    //function: void Compile
    //Compiles a .config file to two .java files: an interface and a main class
    //pre: file points to a valid .config file
    //post: .java files are generated from a .config file
    public void Compile (File file, File working_Directory, String compile_Command, String compile_Options, String compile_Arguments, String classname) throws IOException {

         pm1 = new ParameterInterface();

         pm1.J_manage("title", 4, true);
         pm1.J_manage("fields", 5, true);
         pm1.J_manage("buttons", 5, true);
         //manage our required components

         int result1 = pm1.J_parseFrom(file.getPath(),true);
         //scan for said components...

         if(result1 == 0) {
             pm1.J_destroy();
             throw new IOException("Failed to parse file!");
         }

         title = pm1.J_getString("title");
         buttons = pm1.J_getList("buttons");
         fields = pm1.J_getList("fields");
         //...now retrieve the strings/lists of these components

         pm2  = new ParameterInterface();
         //2nd ParameterInterface for scanning the parameters found from title, fields, buttons

         for(String button: buttons) {
             pm2.J_manage(button, 4, true);
         }
         for(String field: fields) {
             pm2.J_manage(field, 4, true);
         } //manage our fields and buttons such that parsefrom will expect their names

         int result2 = pm2.J_parseFrom(file.getPath(),false); //second parse. false as third parameter indicates not the first time parsing the file.

         if(result2 == 0) {
             pm2.J_destroy();
             pm1.J_destroy();
             throw new IOException("Failed to parse file!");
         }

         for(String field : fields) { //check that type "strings" are valid types
             String value = pm2.J_getString(field);
                 if(value.equals("integer") == false && value.equals("string") == false && value.equals("float") == false) {
                     throw new IOException("Field '" + field + "' assigned invalid type '" + value + "'");
                 }
         }

         String file_Name = new String(file.toString());

         int end = 0;

         for(int i=0; i< file_Name.length(); i++) { //gets project name

             if(file_Name.charAt(i) == '.') {
                 end = i;
             }

         }

         String project_Name = new String(file_Name.substring(0,end) + ".java"); //get the full path including .java

         no_Extension = new String(file_Name.substring(0,end)); //get the full file path without .java

         //call func that writes out illegalexception file
         try {
             write_illegalExceptionFile(working_Directory,classname);
         }
         catch(IOException e) {
             throw new IOException("Failed to write IllegalException file.");
         }
  
         String interface_name;

         try {
             interface_name = write_interface(classname,working_Directory);
         } //create interface.java file (...FieldEdit.java) and grab its classname
         catch(IOException e) {
             pm2.J_destroy();
             throw new IOException("Failed to write interface file.");
         }

         file_Name = new String(interface_name);

         end = 0;

         for(int i=0; i< file_Name.length(); i++) //gets project name
             if(file_Name.charAt(i) == '.') 
                 end = i;

         interface_name = file_Name.substring(0,end);

         try { //now use interface name to write the main java class which implements the interface!
             write_java_file(project_Name,working_Directory,interface_name,pm2,classname);
         }
         catch(IOException e) {
             //todo: print some error and ret
             throw new IOException("Failed to write java file.");
         }
    }

    //boolean InvokeJavaCompiler : base point of compilation - calls other generation functions
    //pre: working_Directory is a valid directory on the file system, classname is the project name
    //post: .java files are generated from a .config file
    public boolean InvokeJavaCompiler(String compile_Command, String compile_Options, String compile_Arguments, String working_Directory, String classname) throws IOException {

        String full_command = new String(); //our full string that we will use in exec
        String streamline = new String(); //stream used in executing java compiler
        String error = new String(); //our error message, if any

        try {

            compile_Options = "-cp " + working_Directory + "/" + classname + " " + compile_Options;
            full_command = compile_Command + " " + compile_Options + " " + compile_Arguments;

            compiler_Process = Runtime.getRuntime().exec(full_command); //execute the compiler!

            BufferedReader error_reader = new BufferedReader(new InputStreamReader(compiler_Process.getErrorStream()));

            while ((streamline = error_reader.readLine()) != null)
                error += streamline + "\n";//print any errors

            compilation_return = compiler_Process.waitFor(); //get return status from compilation...

            if(compilation_return != 0) {
                throw new IOException("Failed to compile project!");
            }
        }
        catch (Exception e) {
            throw new IOException("Failed to compile project!");
        }

        return true;
    }

    //function void Run
    //intended to run the compiled output from a generated .java file (made from a .config file)
    //pre: classname is the current project name, working_Directory is a valid directory of the file system
    //post: compiled .config file is executed until killed by user
    public Process Run(String classname, File working_Directory, String runtime_Options, String java_runtime) throws IOException {

        int run_ret;
        String streamline = new String(); //stream used in executing java compiler
        String error = new String();

        //java_runtime -cp working_Directory classname runtime_Options = command

        final String command = new String(java_runtime + " " + runtime_Options + " " + "-cp " + working_Directory.toString() + "/" + classname + " " + classname);
        
        runThread = new Thread(new Runnable() {
            public void run() {
                try {    
                    executable_Process = Runtime.getRuntime().exec(command); //execute the compiler!
                }
                catch(IOException e) {
                    //throw new IOException("Failed to run compiled program!");
                }
            }
        });

        runThread.start(); 
        return executable_Process;
    }
};

