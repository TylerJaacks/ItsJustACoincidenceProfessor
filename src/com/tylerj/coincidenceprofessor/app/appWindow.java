package com.tylerj.coincidenceprofessor.app;

import javax.swing.*; //swing is built on top of awt
import javax.swing.border.Border;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

import org.fife.ui.rtextarea.*;
import org.fife.ui.rsyntaxtextarea.*;

public class appWindow extends JFrame {

    static ArrayList<CodeObj> codeObjs;
    static CodeObj srcCodeObj;

    static String[] fileNameScores;
    static int[] fileScores;

    static RSyntaxTextArea centerText;

    int targetIndex = -1;

    public appWindow(ArrayList<CodeObj> codeObjs, String[] fns, int[] fs){

        setLayout(new BorderLayout());

        this.codeObjs = codeObjs; fileNameScores = fns; fileScores = fs;

        JPanel leftPanel = new JPanel();
        JPanel rightPanel = new JPanel();
        JPanel centerPanel = new JPanel();

        RSyntaxTextArea leftTextArea = new RSyntaxTextArea(50, 90);
        RSyntaxTextArea rightTextArea = new RSyntaxTextArea(50, 90);
        centerText = new RSyntaxTextArea(2, 10);


        leftTextArea.setLineWrap(true);
        rightTextArea.setLineWrap(true);

        for(int i = 0; i < codeObjs.size(); i++){
            if(codeObjs.get(i).getIsSrcCodeFile()){
                srcCodeObj = codeObjs.get(i);
                codeObjs.remove(srcCodeObj);
                break;
            }
        }
        targetIndex = 0;
        updateScore();

        CodeObj srcFile = srcCodeObj;
        CodeObj targetFile = codeObjs.get(targetIndex);

        for(int i = 0; i < srcFile.getLines().size(); i++){
            leftTextArea.append(srcFile.getLines().get(i));
        }
        for(int i = 0; i < targetFile.getLines().size(); i++){
            rightTextArea.append(targetFile.getLines().get(i));
        }


        //~~~~~~~~~~~
        leftTextArea.setSyntaxEditingStyle(SyntaxConstants.SYNTAX_STYLE_JAVA);
        rightTextArea.setSyntaxEditingStyle(SyntaxConstants.SYNTAX_STYLE_JAVA);
        leftTextArea.setCodeFoldingEnabled(true);
        rightTextArea.setCodeFoldingEnabled(true);
        leftTextArea.setCaretPosition(0);
        rightTextArea.setCaretPosition(0);
        // ~~~~~~~~~~~


        RTextScrollPane leftPane = new RTextScrollPane(leftTextArea);
        RTextScrollPane rightPane = new RTextScrollPane(rightTextArea);


        leftPanel.add(leftPane);
        rightPanel.add(rightPane);
        centerPanel.add(centerText);


        JButton prevBut = new JButton("Prev");
        JButton nextBut = new JButton("Next");

        prevBut.addActionListener(new ActionListener(){
            @Override
            public void actionPerformed(ActionEvent e) {
             if(targetIndex == 0){

             }
             else{
                 targetIndex--;
                 rightTextArea.setText(null);
                 CodeObj target = codeObjs.get(targetIndex);
                 for(int i = 0; i < target.getLines().size(); i++){
                     rightTextArea.append(target.getLines().get(i));
                 }
                 updateScore();
             }
            }
        });

        nextBut.addActionListener(new ActionListener(){
            @Override
            public void actionPerformed(ActionEvent e) {
                if(targetIndex == codeObjs.size() - 1){

                }
                else{
                    targetIndex++;
                    rightTextArea.setText(null);
                    CodeObj target = codeObjs.get(targetIndex);
                    for(int i = 0; i < target.getLines().size(); i++){
                        String line = target.getLines().get(i);
                        rightTextArea.append(line);
                    }
                    updateScore();
                }
            }
        });

       // setContentPane();

        setTitle("It's Just A Coincidence Professor!");
        setSize(1800, 1000);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); //Close program when X is clicked


        JPanel southPanel = new JPanel();
        southPanel.add(prevBut); southPanel.add(nextBut);

        getContentPane().add(centerPanel, BorderLayout.CENTER);
        getContentPane().add(leftPanel, BorderLayout.WEST); //idk what this does
        getContentPane().add(rightPanel, BorderLayout.EAST); //idk what this does
        getContentPane().add(southPanel, BorderLayout.PAGE_END);
    }

    private void updateScore(){
        centerText.setText(null);
        centerText.append("Score\n");
        centerText.append(fileScores[targetIndex] + "\n");
    }


}