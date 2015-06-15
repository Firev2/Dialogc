// CompileModeDialog.java
// Alex Schwarz
// SID 0719732
// CIS*2750 A3
// Simple JFrame with radio buttons on it - specifies the compiler

import java.io.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;
import javax.swing.*;

//class acts as a simple GUI with two radio buttons. Decides whether or not the external compile is to be used.

public class CompileModeDialog extends JFrame {

    public static JFrame frame = new JFrame("Compile Mode");
    public JPanel controlPanel = new JPanel();

    public final JRadioButton normal_Option = new JRadioButton("Dialogc Compiler");
    public final JRadioButton external_Option = new JRadioButton("External Compiler (lex+yacc)",true);
    public final ButtonGroup group = new ButtonGroup();

    public boolean is_external = true;

    //class constructor
    //pre: n/a
    //post: sets up the dialog window and displays it if isVisible is true
    public CompileModeDialog(boolean isVisible) {

        frame.setLayout(new BorderLayout());
        controlPanel.setLayout(new FlowLayout());

        normal_Option.addItemListener(new ItemListener() {
            public void itemStateChanged(ItemEvent e) {
                if(e.getStateChange() == 1) {
                    is_external = false;
                }
            }           
        });

        external_Option.addItemListener(new ItemListener() {
            public void itemStateChanged(ItemEvent e) {
                if(e.getStateChange() == 1) {
                    is_external = true;
                }
            }           
        });

        group.add(normal_Option);
        group.add(external_Option);

        controlPanel.add(normal_Option);
        controlPanel.add(external_Option);


        frame.addWindowListener(new WindowAdapter() {

            public void windowClosing(WindowEvent w) {

                //get radio button selection

                frame.setVisible(false);
            }

        });

        frame.add(controlPanel);
        
        frame.setSize(400,75);
        frame.setVisible(isVisible);
    }

}
