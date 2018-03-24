package com.tylerj.coincidenceprofessor.app;

import javax.swing.*; //swing is built on top of awt
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.FileNotFoundException;
import java.util.ArrayList;

import org.fife.ui.rtextarea.*;
import org.fife.ui.rsyntaxtextarea.*;

public class appWindow extends JFrame {

    static ArrayList<CodeObj> targetCodeObjs;
    static CodeObj srcCodeObj;

    static float[] fileScores;

    static RSyntaxTextArea similarityTextArea;
    static RSyntaxTextArea plagTextArea;

    static RSyntaxTextArea leftTextArea;
    static RSyntaxTextArea rightTextArea;

    static JSlider slider;

    int targetIndex = -1;
    int thresholdValue = 50;

    JButton prevBut;
    JButton nextBut;
    JButton gitBut;

    private ScoreBot sb;


    public appWindow(ScoreBot sb){
        setLayout(new BorderLayout());

        this.sb = sb;
        targetCodeObjs = sb.targetCodeObjs;
        fileScores = sb.fileScores;
        srcCodeObj = sb.srcCodeObj;



        JPanel leftPanel = new JPanel();
        JPanel rightPanel = new JPanel();
        JPanel centerPanel = new JPanel();

        leftTextArea = new RSyntaxTextArea(30, 65);
        rightTextArea = new RSyntaxTextArea(30, 65);
        plagTextArea = new RSyntaxTextArea(4, 20);
        similarityTextArea = new RSyntaxTextArea(2, 20);


        similarityTextArea.setFont(similarityTextArea.getFont().deriveFont(24f));
        plagTextArea.setFont(plagTextArea.getFont().deriveFont(20f));

        leftTextArea.setFont(leftTextArea.getFont().deriveFont(20f));
        rightTextArea.setFont(rightTextArea.getFont().deriveFont(20f));

        leftTextArea.setEditable(false);rightTextArea.setEditable(false);
        similarityTextArea.setEditable(false); plagTextArea.setEditable(false);

        leftTextArea.setLineWrap(true);
        rightTextArea.setLineWrap(true);

        targetIndex = 0;
        setUpSlider();
        setUpButtonListeners();
        updateScore(); updateLikelyhood();

        CodeObj srcFile = srcCodeObj;
        CodeObj targetFile = targetCodeObjs.get(targetIndex);

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




       // setContentPane();

        setTitle("It's Just A Coincidence Professor!");
        setSize(1920, 1080);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); //Close program when X is clicked


        JPanel southPanel = new JPanel();
        southPanel.add(prevBut); southPanel.add(nextBut); southPanel.add(gitBut);

        getContentPane().add(centerPanel, BorderLayout.CENTER);
        getContentPane().add(leftPanel, BorderLayout.WEST);
        getContentPane().add(rightPanel, BorderLayout.EAST);
        getContentPane().add(southPanel, BorderLayout.PAGE_END);

        setVisible(true);
    }

    private void setUpSlider(){
        slider = new JSlider();
        slider.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                int value = slider.getValue();
                updateLikelyhood();
                thresholdValue = value;
            }
        });
    }

    private void setUpButtonListeners(){

        prevBut = new JButton("Prev");
        nextBut = new JButton("Next");
        gitBut = new JButton("Search GIT");

        prevBut.setPreferredSize(new Dimension(150, 100));
        nextBut.setPreferredSize(new Dimension(150, 100));
        gitBut.setPreferredSize(new Dimension(150, 100));

        prevBut.setFont(new Font("Arial", Font.PLAIN, 18));
        nextBut.setFont(new Font("Arial", Font.PLAIN, 18));
        gitBut.setFont(new Font("Arial", Font.PLAIN, 18));

        prevBut.setBackground(Color.orange);
        nextBut.setBackground(Color.orange);
        gitBut.setBackground(Color.orange);




        prevBut.addActionListener(new ActionListener(){
            @Override
            public void actionPerformed(ActionEvent e) {
                if(targetIndex == 0){

                }
                else{
                    targetIndex--;
                    rightTextArea.setText(null);
                    CodeObj target = targetCodeObjs.get(targetIndex);
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
                if(targetIndex == targetCodeObjs.size() - 1){

                }
                else{
                    targetIndex++;
                    rightTextArea.setText(null);
                    CodeObj target = targetCodeObjs.get(targetIndex);
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

        gitBut.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    sb.processCodeFiles(srcCodeObj.getFileName(), true);
                    targetIndex = 0;
                    CodeObj targetFile = targetCodeObjs.get(targetIndex);
                    rightTextArea.setText(null);
                    for(int i = 0; i < targetFile.getLines().size(); i++){
                        rightTextArea.append(targetFile.getLines().get(i));
                    }
                    rightTextArea.setCaretPosition(0);
                    updateScore();
                    updateLikelyhood();
                } catch (FileNotFoundException e1) {
                    e1.printStackTrace();
                }
            }
        });


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
            howLikelyPlag = "CLEAN";
            plagTextArea.setForeground(Color.black);
        }
        else{
            howLikelyPlag = "PLAGARISM!!!!!";
            plagTextArea.setForeground(Color.RED);
        }

        plagTextArea.append(howLikelyPlag + "\n");
        plagTextArea.append("THRESHOLD VALUE: " + thresholdValue);
    }


}