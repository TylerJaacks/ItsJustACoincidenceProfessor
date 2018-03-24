package com.tylerj.coincidenceprofessor.app;

import javax.swing.*; //swing is built on top of awt
import java.util.ArrayList;

import org.fife.ui.rtextarea.*;
import org.fife.ui.rsyntaxtextarea.*;

public class appWindow extends JFrame {

    static ArrayList<CodeObj> codeObjs;
    static String[] fileNameScores;
    static int[] fileScores;

    public appWindow(){

        JPanel leftPanel = new JPanel();
        JPanel rightPanel = new JPanel();

        RSyntaxTextArea leftTextArea = new RSyntaxTextArea(50, 90);
        RSyntaxTextArea rightTextArea = new RSyntaxTextArea(50, 90);

        leftTextArea.setLineWrap(true);
        rightTextArea.setLineWrap(true);

        CodeObj one = codeObjs.get(0);
        CodeObj two = codeObjs.get(1);

        for(int i = 0; i < one.getLines().size(); i++){
            leftTextArea.append(one.getLines().get(i));
        }
        for(int i = 0; i < two.getLines().size(); i++){
            rightTextArea.append(two.getLines().get(i));
        }

        leftTextArea.setSyntaxEditingStyle(SyntaxConstants.SYNTAX_STYLE_JAVA);
        rightTextArea.setSyntaxEditingStyle(SyntaxConstants.SYNTAX_STYLE_JAVA);

        leftTextArea.setCodeFoldingEnabled(true);
        rightTextArea.setCodeFoldingEnabled(true);

        RTextScrollPane leftPane = new RTextScrollPane(leftTextArea);
        RTextScrollPane rightPane = new RTextScrollPane(rightTextArea);

        leftPanel.add(leftPane);
        rightPanel.add(rightPane);

       // setContentPane();

        setTitle("It's Just A Coincidence Professor!");
        setSize(1800, 1000);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); //Close program when X is clicked




        getContentPane().add(leftPanel, "West"); //idk what this does
        getContentPane().add(rightPanel, "East"); //idk what this does

    }


}