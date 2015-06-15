/* --------------------
Dialogc.java
By Alex Schwarz
SID 0719732
CIS*2750 A4

Dialogc.java: GUI-driven program to compile .config files into valid java code.
Requires: ParameterInterface.java, Compiler.java, CompileModeDialog.java
---------------------*/

import java.io.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;
import javax.swing.*;
import javax.swing.text.*;
import javax.swing.border.*;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.SwingUtilities;
import javax.swing.filechooser.FileNameExtensionFilter;

public class Dialogc extends JFrame implements KeyListener {

    //-------------------- <"DYNAMIC" VARIABLES> ----------------------//

    public boolean using_external_compiler = true;
    public CompileModeDialog d = new CompileModeDialog(false);

    public boolean is_Modified = false;
    public boolean is_new_Document = true;

    public String file_Name = new String("untitled.config");
    public String project_Name = new String("untitled");
    public String footer_Text = new String("Current Project:");
    public String file_extension = new String(".config");
    public String working_Directory = new String(GetDefaultOption("wd"));
    public String classname = new String();


    public File full_path = new File("./");

    public Compiler c;
    public String compile_Options =  new String(GetDefaultOption("cargs"));
    public String compiler_path = new String(GetDefaultOption("javac"));
    public String java_runtime_path = new String(GetDefaultOption("jrepath"));
    public String runtime_Options = new String(GetDefaultOption("jreargs"));

    //----------------------</"DYNAMIC" VARIABLES>---------------------//

    //----------------------<GUI VARIABLES>----------------------------//


    //frame and panels
    public static JFrame frame = new JFrame("Dialogc");

    public static JToolBar toolBar_buttons = new JToolBar();
    public static JPanel middle_Panel = new JPanel();
    public JPanel bottom_Panel = new JPanel();

    public JLabel footer_Label = new JLabel(footer_Text);

    //Menu components
    public static JMenuBar menubar = new JMenuBar();

    public static JMenu menu_File = new JMenu("File");
    public static JMenu menu_Compile = new JMenu("Compile");
    public static JMenu menu_Config = new JMenu("Config");
    public static JMenu menu_Help = new JMenu("Help");

    public static JMenuItem menu_File_New = new JMenuItem("New", 0);
    public static JMenuItem menu_File_Open = new JMenuItem("Open", 0);
    public static JMenuItem menu_File_Save = new JMenuItem("Save", 0);
    public static JMenuItem menu_File_SaveAs = new JMenuItem("Save as", 0);
    public static JMenuItem menu_File_Quit = new JMenuItem("Quit", 0);

    public JMenuItem menu_Compile_Compile = new JMenuItem("Compile current file",0);
    public JMenuItem menu_Compile_CompileRun = new JMenuItem("Compile and run current file",0);

    public JMenuItem menu_Config_JavaCompiler = new JMenuItem("Java Compiler: " + compiler_path,0);
    public JMenuItem menu_Config_CompileOptions = new JMenuItem("Compile Options: " + compile_Options,0);
    public JMenuItem menu_Config_JavaRunTime = new JMenuItem("Java Run-time: " + java_runtime_path,0);
    public JMenuItem menu_Config_RunTime = new JMenuItem("Run-time Options: " + runtime_Options,0);
    public JMenuItem menu_Config_WorkingDir = new JMenuItem(new String("Working Directory: " + working_Directory),0);
    public JMenuItem menu_Config_CompileMode = new JMenuItem("Compile Mode",0);

    public static JMenuItem menu_Help_Help = new JMenuItem("Help",0);
    public static JMenuItem menu_Help_About = new JMenuItem("About",0);

    public static JFileChooser OpenFileDialog = new JFileChooser();

    //buttons
    public static JButton button_New = new JButton();
    public static JButton button_Open = new JButton();
    public static JButton button_Save = new JButton();
    public static JButton button_SaveAs = new JButton();
    public static JButton button_Stop = new JButton();
    public static JButton button_CompileRun = new JButton();
    public static JButton button_Compile = new JButton();

    //scrollpane area
    public JTextArea editPane_CurrentFile = new JTextArea(); //had to change this to textare because JEditorPane doesn't support horizontal scrolls. SMH
    public JScrollPane scroll;
    public TitledBorder editBorder;

    //listeners
    public ActionListener new_project_Listener;
    public ActionListener open_Listener;
    public ActionListener save_Listener;
    public ActionListener save_as_Listener;
    public ActionListener stop_Listener;
    public ActionListener compile_Listener;
    public ActionListener compile_run_Listener;
    public ActionListener quit_Listener;
    public ActionListener compile_options_listener;
    public ActionListener compiler_path_listener;
    public ActionListener java_runtime_listener;
    public ActionListener runtime_listener;
    public ActionListener compile_type_listener;

    //filechoosers
    final JFileChooser open_fc = new JFileChooser();
    final JFileChooser save_as_fc = new JFileChooser();


    //-----------------</GUI VARIABLES>-----------------------------//

    //update_GUI : modifies text on some labels to suit current document
    //pre: n/a
    //post: Various controls within the Dialogc frame receive updated text if applicable
    public void update_GUI() {

        if(is_new_Document == false) {

            file_Name = full_path.getName();

            int end = 0;

            for(int i=0; i< file_Name.length(); i++) { //gets project name, filename w/o extension

                if(file_Name.charAt(i) == '.') {
                    end = i;
                }

            }

            project_Name = file_Name.substring(0,end);

        }
        else {
            file_Name = "untitled.config";
        }

        if(this.is_Modified == true) {
            String footer = new String("Current Project: " + project_Name + " [Modified]");
            footer_Text = footer;
            footer_Label.setText(footer_Text);
        }
        else {
            String footer = new String("Current Project: " + project_Name);
            footer_Text = footer;
            footer_Label.setText(footer_Text);
        } //update footer

        menu_Config_WorkingDir.setText("Working Directory: " + working_Directory);
        editBorder = new TitledBorder(file_Name); //update title
        scroll.setBorder(editBorder);
        classname = project_Name;
    }

    //set_DocumentModified(boolean modified) : sets whether or not the current doc. has been modified
    //pre: n/a
    //post: sets a document's modified status to true or false
    public void set_DocumentModified(boolean modified) {

        this.is_Modified = modified;
        update_GUI();
    }


    // ------------------------ Arguments functions -------------------//

    // GetDefaultOption(String option) : String
    // pre: option is one of the options found in the dialogc installer (or dialogc.config file in home directory)
    // post: retrieves default values for menu options. If the dialogc.config file is missing, defaults will be assumed
    public String GetDefaultOption(String option) {

        try {
            BufferedReader br = new BufferedReader(new FileReader(System.getProperty("user.home") + "/config.dialogc"));
            String line;
            try {
                while ((line = br.readLine()) != null) {
                    String[] vals = line.split("=");
                    if(vals[0].equals(option)) {
                        if(vals.length != 2) {
                            if(option.equals("javac")) {
                                return "javac";
                            }
                            else if(option.equals("cargs")) {
                                return "";
                            }
                            else if(option.equals("jrepath")) {
                                return "java";
                            }
                            else if(option.equals("jreargs")) {
                                return "";
                            }
                            else if(option.equals("wd")) {
                                return "."; //defaults
                            }
                        }
                        else
                            return vals[1];
                    }
                }
            }
            catch(IOException e) {
                if(option.equals("javac")) {
                    return "javac";
                }
                else if(option.equals("cargs")) {
                    return "";
                }
                else if(option.equals("jrepath")) {
                    return "java";
                }
                else if(option.equals("jreargs")) {
                    return "";
                }
                else if(option.equals("wd")) {
                    return ".";
                }//defaults
            }
        }
        catch(FileNotFoundException fe) {
            
            if(option.equals("javac")) {
                return "javac";
            }
            else if(option.equals("cargs")) {
                return "";
            }
            else if(option.equals("jrepath")) {
                return "java";
            }
            else if(option.equals("jreargs")) {
                return "";
            }
            else if(option.equals("wd")) {
                return ".";
            } //defaults
        }
        return "";
    }

    // set_cwd : sets the working directory for the current program instance. called from selecting menu item
    // pre: n/a
    // post: sets the working directory to that chosen inside the JFileChooser
    public void set_cwd() {

        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setFileFilter(null);
        fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        int ret = fileChooser.showOpenDialog(this);

        if (ret == JFileChooser.APPROVE_OPTION) {

            File open_dir = fileChooser.getSelectedFile();

            String file_Name = new String(full_path.toString());

            int end = 0;

            for(int i=0; i< file_Name.length(); i++) { //gets project name

                if(file_Name.charAt(i) == '.') {
                    end = i;
                }

            }

            classname = new String(file_Name.substring(0,end));

            working_Directory = open_dir.toString() + "/" + classname + "/";

            menu_Config_WorkingDir.setText("Working Directory: " + working_Directory);

        }

    }

    //set_compiler_path : sets the path to the java compiler
    //pre: n/a
    //post: the path to the java compiler will be updated
    public void set_compiler_path() {

        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
        int ret = fileChooser.showOpenDialog(this);

        if (ret == JFileChooser.APPROVE_OPTION) {

             File open_dir = fileChooser.getSelectedFile();
             compiler_path = open_dir.toString();
             menu_Config_JavaCompiler.setText("Java Compiler: " + compiler_path);
        }
    }

    //set_java_runtime() : sets the java runtime path
    //pre: n/a
    //post: sets java runtime path for invoking JVM
    public void set_java_runtime() {

        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
        int ret = fileChooser.showOpenDialog(this);

        if (ret == JFileChooser.APPROVE_OPTION) {

             File open_dir = fileChooser.getSelectedFile();
             java_runtime_path = open_dir.toString();
             menu_Config_JavaRunTime.setText("Java Runtime: " + java_runtime_path);

        }

    }

    // set_runtime : sets runtime options for running compiled program
    //pre: n/a
    //post: sets runtime variables 
    public void set_runtime() {

        runtime_Options = JOptionPane.showInputDialog(null,"Run-time Options: "," ");
        if(runtime_Options != null)
            menu_Config_RunTime.setText("Run-time Options: " + runtime_Options);

    }

    // ---------------- Document functions -------------//

    // new_Project() : creates a new project, closing the currently opened one
    //pre: n/a
    //post: clears the edit textfield and starts a project called untitled.config
    public void new_Project() {

        if(this.is_Modified == true) {

            int dialog_Result = JOptionPane.showConfirmDialog (null, "Save changes made to current document?","Warning",JOptionPane.YES_NO_OPTION);

            if(dialog_Result == JOptionPane.YES_OPTION) {

                //check to see if file exists
                if(is_new_Document == false) {
                    save_Project();
                }
                else {
                    save_as_Project();
                }

            }
        }

        editPane_CurrentFile.setText("");
        full_path = new File("untitled.config");
        project_Name = "untitled";
        is_new_Document = true;
        classname = "untitled";
        update_GUI();
    }

    //open_Project : prompts user to select a .config file. if a file is currently open and modified, user will be prompted to save it
    //pre: n/a
    //post: the selected project will be opened and ready for editing
    public void open_Project() throws IOException {

       FileNameExtensionFilter filter = new FileNameExtensionFilter("Config Files", "config", "config");

       if(this.is_Modified == true) { //modified file = prompt to save changes

           int dialog_Result = JOptionPane.showConfirmDialog (null, "Save changes made to current document?","Warning",JOptionPane.YES_NO_OPTION);

            if(dialog_Result == JOptionPane.YES_OPTION) {

                //check to see if file exists
                if(is_new_Document == false) {
                    save_Project();
                }
                else {
                    save_as_Project();
                }

            }
        }

       open_fc.setFileFilter(filter);

       int ret = open_fc.showOpenDialog(this);

       if (ret == JFileChooser.APPROVE_OPTION) {

           full_path = open_fc.getSelectedFile();
           file_Name = full_path.getName();
           working_Directory = full_path.getParentFile().toString();

           String file_Name = new String(full_path.getName().toString());

           int end = 0;

           for(int i=0; i< file_Name.length(); i++) { //gets project name

               if(file_Name.charAt(i) == '.') {
                   end = i;
               }

           }

           classname = new String(file_Name.substring(0,end));

           String read_text = new String();
           Scanner reader = new Scanner(new BufferedReader(new FileReader(full_path)));

           while(reader.hasNextLine()) {
               read_text = read_text + reader.nextLine() + "\n";
           }

           reader.close();
           editPane_CurrentFile.setText(read_text);
           set_DocumentModified(false);
           is_new_Document = false;

           update_GUI();
        }
    }

    //save_Project : sets up writing of current editbox area to disk (.config file)
    //pre: project already exists, if not saveas will be done
    //post: .config file is updated with current text in the edit area
    public boolean save_Project() {

        if(is_Modified == false)
            return true;

        if(is_new_Document == true) {

            if(save_as_Project() == false) 
                return false;
            
            return true;
        }

         //get current filename and save to it
        try {
            save_File(full_path.toString());
        }
        catch(IOException e) {
            JOptionPane.showMessageDialog(this, "Failed to save current document to a file!" ,"Dialogc Error", JOptionPane.PLAIN_MESSAGE);
            return false;
        }

        return true;
    }

    //save_as_Project : prompts user to save current document to a specific file path.
    //pre: n/a
    //post: a new file called <project>.config will be created and saved to disk
    public boolean save_as_Project() {

        FileNameExtensionFilter filter = new FileNameExtensionFilter("Config Files", "config", "config");
        save_as_fc.setFileFilter(filter);

        int ret = save_as_fc.showSaveDialog(this);

        if (ret == JFileChooser.APPROVE_OPTION) {

            File save_file = save_as_fc.getSelectedFile();

            if(!save_file.toString().contains(".config")) {
                  save_file = new File(save_file.toString() + ".config");
            }

            full_path = save_file;

            working_Directory = full_path.getParentFile().toString();

            save_as_fc.setCurrentDirectory(save_file);

            try {
                save_File(save_file.toString());
            }
            catch(IOException e) {
              JOptionPane.showMessageDialog(this, "Failed to save current document to a file!" ,"Dialogc Error", JOptionPane.PLAIN_MESSAGE);
              return false;
            }

            set_DocumentModified(false);
            is_new_Document = false;
            update_GUI();
            return true;
        }

        //set wd

        return false;
    }

    //stop_Project() : shuts down a compiled dialog program. called by pressing the stop button
    //pre: n/a
    //post: any running executable compiled by dialogc or yadc will be closed
    public void stop_Project() { //stop running project: after being compiled and run

        c.executable_Process.destroy();
    }

    //boolean compile_Project() : attempts to compile the text found in the edit area. if project is modified it will be saved.
    //pre: edit area has text in it which corresponds to .config file format (see readme)
    //post: .java files generated which can be compiled into a dialog program
    public boolean compile_Project() {

        if(editPane_CurrentFile.getText().equals("")) {
            JOptionPane.showMessageDialog(this, "Failed to compile: Empty document.","Dialogc Error", JOptionPane.PLAIN_MESSAGE);
            return false;
        }

        //lets create a class which will take a string input - this will be what is compiled
        if(save_Project() == false) {
            return false;
        }

        c = new Compiler();

        String file_Name = new String(full_path.getName().toString());

        File dir = new File(full_path.getParentFile().toString() + "/" + classname);

        if(!dir.exists()) {
            dir.mkdir();
        }

        int end = 0;

        for(int i=0; i< file_Name.length(); i++) { //gets project name
            if(file_Name.charAt(i) == '.')
                end = i;
        }
 
        String file = new String(working_Directory + "/" + classname + "/" + file_Name.substring(0,end) + ".java");

        //get whether compiler is external or not
        using_external_compiler = d.is_external;

        if(using_external_compiler == false) {

            try {
                c.Compile(full_path, new File(working_Directory), compiler_path, compile_Options, file, classname);
                c.InvokeJavaCompiler(compiler_path, compile_Options, file, working_Directory.toString(),classname);
            }
            catch(IOException e) {
                JOptionPane.showMessageDialog(this, e,"Dialogc Error", JOptionPane.PLAIN_MESSAGE);
                return false;
            }
        }
        else { //invoke yadc program
     
            try {
                c.InvokeExternalCompiler(full_path, classname, new File(working_Directory));
                c.InvokeJavaCompiler(compiler_path, compile_Options, file, working_Directory.toString(),classname);
            }
            catch(IOException e) {
                JOptionPane.showMessageDialog(this, e,"Dialogc Error", JOptionPane.PLAIN_MESSAGE);
                return false;
            }
                
        }

        return true;
    }

    //compile_run_Project() : attempts to compile and then run the current project
    //pre: edit area has text in it which corresponds to .config file format (see readme)
    //post: project is compiled and executed to the backround in its own thread.
    public void compile_run_Project() {

        if(compile_Project() == true) {

            try {
                c.executable_Process = c.Run(classname,new File(working_Directory),runtime_Options, java_runtime_path);
            }
            catch (IOException e) {
                JOptionPane.showMessageDialog(this, "Failed to run project: " + e,"Dialogc Error", JOptionPane.PLAIN_MESSAGE);
            }
        }
        else {
            JOptionPane.showMessageDialog(this, "Failed to run project!","Dialogc Error", JOptionPane.PLAIN_MESSAGE);
        }
    }

    // info_help : displays a help messagebox containing text found in the README.txt file
    // pre: README.txt exists in the same directory as Dialogc.class
    // post: n/a
    public void info_help() { //todo: put readme.txt contents into here

        String input = new String();
        Scanner in_scanner;

        try {
            in_scanner = new Scanner(new BufferedReader(new FileReader("README.txt")));
        }
        catch(Exception e) {
            JOptionPane.showMessageDialog(this, "Cannot find README.txt file." ,"Error", JOptionPane.PLAIN_MESSAGE);
            return;
        }

        while(in_scanner.hasNextLine())
            input = input + in_scanner.nextLine() + "\n";

        in_scanner.close();

        JTextArea textArea = new JTextArea(input);
        JScrollPane scrollPane = new JScrollPane(textArea);

        JOptionPane.showMessageDialog(this, scrollPane,"Help", JOptionPane.PLAIN_MESSAGE);
    }

    //info_about : displays a simple messagebox with some information about the program's author
    //pre: n/a
    //post: n/a
    public void info_about() {
        JOptionPane.showMessageDialog(this, "Dialogc: By Alex Schwarz\nSID: 0719732\nFor: CIS*2750 Assignment 4" ,"About", JOptionPane.PLAIN_MESSAGE);
    }

    //quit_Program : exits dialogc.
    //pre: n/a
    //post: n/a
    public void quit_Program() {

        //check for modified status
        if(is_Modified == true) {

            int dialog_Result = JOptionPane.showConfirmDialog (null, "Save changes made to current document?","Warning",JOptionPane.YES_NO_OPTION);

            if(dialog_Result == JOptionPane.YES_OPTION) { //if modified, prompt to save before quiting.

                //check to see if file exists
                if(is_new_Document == false) {
                    save_Project();
                }
                else {
                    save_as_Project();
                }

            }

        }

        System.exit(0);
    }

    // save_File(String filename) : writes editpane to .config file
    // pre: filename is the filename of a <projectname>.config file
    // post: text inside editpane is written to file
    public void save_File(String filename) throws IOException {

        if(filename.toString().contains(".config")) {

            PrintWriter writer = new PrintWriter(new BufferedWriter(new FileWriter(full_path)));
            writer.print(editPane_CurrentFile.getText());
            writer.close();
            set_DocumentModified(false);
        }
        else {
            PrintWriter writer = new PrintWriter(new BufferedWriter(new FileWriter(full_path+".config")));
            writer.print(editPane_CurrentFile.getText());
            writer.close();
            set_DocumentModified(false);
        }

        update_GUI();
    }

    //AddButtons : adds buttons to the GUI along with their icons and actionlisteners.
    //pre: n/a
    //post: Buttons are added to the dialogc frame
    private void AddButtons() {

        button_New.setIcon(new ImageIcon("icons/new.jpg"));
        button_Open.setIcon(new ImageIcon("icons/open.jpg"));
        button_Save.setIcon(new ImageIcon("icons/save.jpg"));
        button_SaveAs.setIcon(new ImageIcon("icons/saveas.jpg"));
        button_Stop.setIcon(new ImageIcon("icons/stop.jpg"));
        button_Compile.setIcon(new ImageIcon("icons/compile.jpg"));;
        button_CompileRun.setIcon(new ImageIcon("icons/compile_run.jpg"));

        button_New.setToolTipText("New Project");
        button_Open.setToolTipText("Open Project");
        button_Save.setToolTipText("Save Project");
        button_SaveAs.setToolTipText("Save as Project");
        button_Compile.setToolTipText("Compile Project");
        button_CompileRun.setToolTipText("Compile and Run Project");


        new_project_Listener = new ActionListener() {
            public void actionPerformed(ActionEvent event) {
                new_Project();
            }
        };

        button_New.addActionListener(new_project_Listener);

        open_Listener = new ActionListener() {
            public void actionPerformed(ActionEvent event) {
                try {
                    open_Project();
                }
                catch(IOException e) {

                }
            }
        };

        button_Open.addActionListener(open_Listener);

        save_Listener = new ActionListener() {
            public void actionPerformed(ActionEvent event) {

                    save_Project();
            }
        };

       button_Save.addActionListener(save_Listener);

       save_as_Listener = new ActionListener() {
            public void actionPerformed(ActionEvent event) {
                save_as_Project();
            }
        };

        button_SaveAs.addActionListener(save_as_Listener);

        stop_Listener = new ActionListener() {
            public void actionPerformed(ActionEvent event) {
                stop_Project();
            }
        };

        button_Stop.addActionListener(stop_Listener);


        compile_Listener = new ActionListener() {
            public void actionPerformed(ActionEvent event) {
                compile_Project();
            }
        };

        button_Compile.addActionListener(compile_Listener);

        compile_run_Listener = new ActionListener() {
            public void actionPerformed(ActionEvent event) {
                compile_run_Project();
            }
        };

        button_CompileRun.addActionListener(compile_run_Listener);

        toolBar_buttons.setPreferredSize(new Dimension(500,50));
        toolBar_buttons.add(button_New);
        toolBar_buttons.add(button_Open);
        toolBar_buttons.add(button_Save);
        toolBar_buttons.add(button_SaveAs);
        toolBar_buttons.add(button_Stop);
        toolBar_buttons.add(button_CompileRun);
        toolBar_buttons.add(button_Compile);

        frame.add(toolBar_buttons,BorderLayout.NORTH);
    }

    //AddToolbar : adds the toolbar and it's menu options
    //pre: n/a
    //post: the toolbar is added to dialogc frame
    public void AddToolbar() {

        menu_File_New.setAccelerator(KeyStroke.getKeyStroke("ctrl N"));
        menu_File_Open.setAccelerator(KeyStroke.getKeyStroke("ctrl O"));
        menu_File_Save.setAccelerator(KeyStroke.getKeyStroke("ctrl S"));
        menu_File_SaveAs.setAccelerator(KeyStroke.getKeyStroke("ctrl M")); 
        menu_File_Quit.setAccelerator(KeyStroke.getKeyStroke("ctrl Q"));
     
        menu_Compile_Compile.setAccelerator(KeyStroke.getKeyStroke("ctrl E")); //not cntrl C because that's for copy pasta
        menu_Compile_CompileRun.setAccelerator(KeyStroke.getKeyStroke("ctrl R"));

        menu_File.setMnemonic('Q');
        menu_Compile.setMnemonic('W');
        menu_Config.setMnemonic('E');
        menu_Help.setMnemonic('R');

        menu_Config_JavaCompiler.setAccelerator(KeyStroke.getKeyStroke("F1"));
        menu_Config_CompileOptions.setAccelerator(KeyStroke.getKeyStroke("F2"));
        menu_Config_JavaRunTime.setAccelerator(KeyStroke.getKeyStroke("F3"));
        menu_Config_RunTime.setAccelerator(KeyStroke.getKeyStroke("F4"));
        menu_Config_WorkingDir.setAccelerator(KeyStroke.getKeyStroke("F5"));
        menu_Config_CompileMode.setAccelerator(KeyStroke.getKeyStroke("F6"));

        menu_Help_Help.setAccelerator(KeyStroke.getKeyStroke("F11"));
        menu_Help_About.setAccelerator(KeyStroke.getKeyStroke("F12"));
 
        //add submenus to menu options
        menu_File.add(menu_File_New);
        menu_File.add(menu_File_Open);
        menu_File.add(menu_File_Save);
        menu_File.add(menu_File_SaveAs);
        menu_File.add(menu_File_Quit);

        menu_Compile_Compile.addActionListener(compile_Listener);
        menu_Compile_CompileRun.addActionListener(compile_run_Listener);


        menu_Compile.add(menu_Compile_Compile);
        menu_Compile.add(menu_Compile_CompileRun);

        menu_Config.add(menu_Config_JavaCompiler);
        menu_Config.add(menu_Config_CompileOptions);
        menu_Config.add(menu_Config_JavaRunTime);
        menu_Config.add(menu_Config_RunTime);
        menu_Config.add(menu_Config_WorkingDir);
        menu_Config.add(menu_Config_CompileMode);

        menu_Help.add(menu_Help_Help);
        menu_Help.add(menu_Help_About);

        //add the four menus to the menubar
        menubar.add(menu_File);
        menubar.add(menu_Compile);
        menubar.add(menu_Config);
        menubar.add(menu_Help);

        menu_File_New.addActionListener(new_project_Listener);
        menu_File_Open.addActionListener(open_Listener);
        menu_File_Save.addActionListener(save_Listener);
        menu_File_SaveAs.addActionListener(save_as_Listener);

        quit_Listener = new ActionListener() {
            public void actionPerformed(ActionEvent event) {
                quit_Program(); //exit the program, save if modified
            }
        };

        menu_Help_About.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent event) {
                info_about(); //launch "about" box
            }
        });

        menu_Help_Help.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent event) {
                info_help(); //launch "help" box
            }
        });

        menu_File_Quit.addActionListener(quit_Listener);

        menu_Config_WorkingDir.addActionListener(new ActionListener() {
                                        public void actionPerformed(ActionEvent event)
                                        {
                                              set_cwd(); //set current working dir
                                        }
                                }
                        );

        compile_options_listener = new ActionListener() {
            public void actionPerformed(ActionEvent event) {
                compile_Options = JOptionPane.showInputDialog(null,"Compiler Arguments: ",compile_Options);
                if(compile_Options != null)
                    menu_Config_CompileOptions.setText("Compiler Arguments: " + compile_Options);

            }
        };

        menu_Config_CompileOptions.addActionListener(compile_options_listener);

        compiler_path_listener = new ActionListener() {
            public void actionPerformed(ActionEvent event) {
                set_compiler_path();
            }
        };

        menu_Config_JavaCompiler.addActionListener(compiler_path_listener);


        java_runtime_listener = new ActionListener() {
            public void actionPerformed(ActionEvent event) {
                set_java_runtime();
            }
        };

        menu_Config_JavaRunTime.addActionListener(java_runtime_listener);

        runtime_listener = new ActionListener() {
            public void actionPerformed(ActionEvent event) {
                set_runtime();
            }
        };

        menu_Config_RunTime.addActionListener(runtime_listener);

        compile_type_listener = new ActionListener() {

            public void actionPerformed(ActionEvent event) {
                d = new CompileModeDialog(true);      
            }
        };

        menu_Config_CompileMode.addActionListener(compile_type_listener);
        
        frame.setJMenuBar(menubar);
    }

    //createAndShowGUI : sets up the Dialogc frame for usage
    //pre: n/a
    //post: Dialogc window is created and shown to the user
    private void createAndShowGUI() { //should be called from constructor

        frame.setLayout(new BorderLayout());
        frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        frame.addWindowListener(new WindowAdapter() {

            public void windowClosing(WindowEvent w) {
                quit_Program();
            }

        });

        OpenFileDialog .setDialogTitle("Select a file to open:");

        //add buttons
        AddButtons();

        //add the toolbar /w buttons
        AddToolbar();

        editPane_CurrentFile.setLineWrap(false);
        editPane_CurrentFile.setEditable(true);
        editPane_CurrentFile.addKeyListener(this);
        //configure the editpane

        scroll = new JScrollPane(editPane_CurrentFile);
        editBorder = new TitledBorder(file_Name);
        scroll.setBorder(editBorder);

        bottom_Panel.add(footer_Label); //Current project: example [modified]

        frame.add(scroll,BorderLayout.CENTER);
        frame.add(bottom_Panel,BorderLayout.SOUTH);

        //Display the window.
        frame.setSize(400,500);
        frame.setVisible(true);

        new_Project();
    }

    public void keyPressed(KeyEvent e) {} //implement this one because compiler wants us to

    // keyTyped(KeyEvent e) : actionListener for the editpane dialog since Dialogc implements keylistener to catch keystrokes
    // pre: n/a
    // post: The document has been modified
    public void keyTyped(KeyEvent e) {

        //we can easily check to see if someone modifies the document by implementing the keylistener and checking for keydowns

        //...but control key down = some action besides typing
        if(!e.isControlDown() && !e.isAltDown()) {
            set_DocumentModified(true);
        }
    }

    //keyReleased(KeyEvent e) : actionListener for the editpane dialog since Dialogc implements keylistener to catch keystroke releases
    //pre: n/a
    //post: document has been modified
    public void keyReleased(KeyEvent e) {
         //...but control key down = some action besides typing
        if(!e.isControlDown() ) {
            set_DocumentModified(true);
        }
    }

    // Dialogc() : Class constructor
    // pre: n/a
    // post: Creates the dialogc frame and displays it to the user
    public Dialogc() {

        super();
        createAndShowGUI();

    }

    // void main(String[] args) : program entry. make one Dialogc instance and go from there.
    // pre: n/a
    // post: n/a
    public static void main(String[] args) {

        Dialogc dialog = new Dialogc();
    }
}

