package com.tylerj.coincidenceprofessor.app;

import javax.swing.*; //swing is built on top of awt
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
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
    static float[] fileScores;

    static RSyntaxTextArea similarityTextArea;
    static RSyntaxTextArea plagTextArea;

    static JSlider slider;

    int targetIndex = -1;
    int thresholdValue = 50;


    public appWindow(ArrayList<CodeObj> codeObjs, String[] fns, float[] fs){

        setLayout(new BorderLayout());

        this.codeObjs = codeObjs; fileNameScores = fns; fileScores = fs;

        slider = new JSlider();
        slider.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                int value = slider.getValue();
                updateLikelyhood();
                thresholdValue = value;
            }
        });


        JPanel leftPanel = new JPanel();
        JPanel rightPanel = new JPanel();
        JPanel centerPanel = new JPanel();

        RSyntaxTextArea leftTextArea = new RSyntaxTextArea(30, 65);
        RSyntaxTextArea rightTextArea = new RSyntaxTextArea(30, 65);
        plagTextArea = new RSyntaxTextArea(4, 10);
        similarityTextArea = new RSyntaxTextArea(2, 10);


        similarityTextArea.setFont(similarityTextArea.getFont().deriveFont(24f));
        plagTextArea.setFont(plagTextArea.getFont().deriveFont(16f));

        leftTextArea.setFont(leftTextArea.getFont().deriveFont(20f));
        rightTextArea.setFont(rightTextArea.getFont().deriveFont(20f));

        leftTextArea.setEditable(false);rightTextArea.setEditable(false);
        similarityTextArea.setEditable(false); plagTextArea.setEditable(false);

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
        updateScore(); updateLikelyhood();

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
        centerPanel.add(similarityTextArea);
        centerPanel.add(plagTextArea);
        centerPanel.add(slider);


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
                 rightTextArea.setCaretPosition(0);
                 updateScore(); updateLikelyhood();
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
                    rightTextArea.setCaretPosition(0);
                    updateScore();
                    updateLikelyhood();
                }
            }
        });

       // setContentPane();

        setTitle("It's Just A Coincidence Professor!");
        setSize(1920, 1080);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); //Close program when X is clicked


        JPanel southPanel = new JPanel();
        southPanel.add(prevBut); southPanel.add(nextBut);

        getContentPane().add(centerPanel, BorderLayout.CENTER);
        getContentPane().add(leftPanel, BorderLayout.WEST);
        getContentPane().add(rightPanel, BorderLayout.EAST);
        getContentPane().add(southPanel, BorderLayout.PAGE_END);
    }

    private void updateScore(){
        similarityTextArea.setText(null);
        similarityTextArea.append("Similarity Score\n");
        similarityTextArea.append("     "+fileScores[targetIndex] + "%\n");
    }

    private void updateLikelyhood(){
        plagTextArea.setText(null);
        String howLikelyPlag = "ERROR";
        float sim = fileScores[targetIndex];
        if(sim < thresholdValue){
            howLikelyPlag = "Unlikely PLag";
            plagTextArea.setForeground(Color.GREEN);
        }
        else{
            howLikelyPlag = "Likely PLag";
            plagTextArea.setForeground(Color.RED);
        }

        plagTextArea.append(howLikelyPlag + "\n");
        plagTextArea.append("THRESHOLD VALUE: " + thresholdValue);
    }


}