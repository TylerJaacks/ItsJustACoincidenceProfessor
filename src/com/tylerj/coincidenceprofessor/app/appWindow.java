package com.tylerj.coincidenceprofessor.app;

import javax.swing.*; //swing is built on top of awt
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;

import org.fife.ui.rtextarea.*;
import org.fife.ui.rsyntaxtextarea.*;

public class appWindow extends JFrame {

    static RSyntaxTextArea similarityTextArea;
    static RSyntaxTextArea plagTextArea;
    static RSyntaxTextArea gitResultsTextArea;

    static RSyntaxTextArea leftTextArea;
    static RSyntaxTextArea rightTextArea;

    static JSlider slider;

    int targetIndex = -1;
    int thresholdValue = 50;

    JButton prevBut;
    JButton nextBut;
    JButton gitBut;
    JFileChooser fileChooser;

    private ScoreBot sb;
    boolean inGitView;


    public appWindow(ScoreBot sb){
        setLayout(new BorderLayout());
        inGitView = false;
        this.sb = sb;



        JPanel leftPanel = new JPanel();
        JPanel rightPanel = new JPanel();
        JPanel centerPanel = new JPanel();

        leftTextArea = new RSyntaxTextArea(30, 65);
        rightTextArea = new RSyntaxTextArea(30, 65);
        plagTextArea = new RSyntaxTextArea(4, 20);
        similarityTextArea = new RSyntaxTextArea(2, 20);
        gitResultsTextArea = new RSyntaxTextArea(4, 20);
        fileChooser = new JFileChooser(new File("C:\\Users\\jeffy\\IdeaProjects\\v3\\ItsJustACoincidenceProfessor\\TestCode"));
        fileChooser.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);

        similarityTextArea.setFont(similarityTextArea.getFont().deriveFont(24f));
        plagTextArea.setFont(plagTextArea.getFont().deriveFont(20f));
        gitResultsTextArea.setFont(gitResultsTextArea.getFont().deriveFont(20f));

        leftTextArea.setFont(leftTextArea.getFont().deriveFont(20f));
        rightTextArea.setFont(rightTextArea.getFont().deriveFont(20f));

        leftTextArea.setEditable(false);rightTextArea.setEditable(false);
        similarityTextArea.setEditable(false); plagTextArea.setEditable(false); gitResultsTextArea.setEditable(false);

        gitResultsTextArea.setText("Git Results:");

        leftTextArea.setLineWrap(true);
        rightTextArea.setLineWrap(true);

        targetIndex = 0;
        setUpSlider();
        setUpListeners();
        updateScore(); updateLikelyhood();

        CodeObj srcFile = sb.srcCodeObj;
        CodeObj targetFile = sb.targetCodeObjs.get(targetIndex);

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
        centerPanel.add(gitResultsTextArea);




       // setContentPane();

        setTitle("It's Just A Coincidence Professor!");
        setSize(1920, 1080);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); //Close program when X is clicked


        JPanel southPanel = new JPanel();
        southPanel.add(prevBut); southPanel.add(nextBut); southPanel.add(gitBut);
        southPanel.add(fileChooser);

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

    private void setUpListeners(){

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
                    CodeObj target = sb.targetCodeObjs.get(targetIndex);
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
                if(targetIndex == sb.targetCodeObjs.size() - 1){

                }
                else{
                    targetIndex++;
                    rightTextArea.setText(null);
                    CodeObj target = sb.targetCodeObjs.get(targetIndex);
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
                    inGitView = true;
                    int results = sb.processCodeFiles(sb.srcCodeObj.getFileName(), true);
                    targetIndex = 0;
                    updateGitResults(results);
                    if(results > 0){
                        CodeObj targetFile = sb.targetCodeObjs.get(targetIndex);
                        rightTextArea.setText(null);
                        for(int i = 0; i < targetFile.getLines().size(); i++){
                            rightTextArea.append(targetFile.getLines().get(i));
                        }
                        rightTextArea.setCaretPosition(0);
                        updateScore();
                        updateLikelyhood();
                    }
                    else{
                        rightTextArea.setText(null);
                    }

                } catch (FileNotFoundException e1) {
                    e1.printStackTrace();
                }
            }
        });

        fileChooser.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String action = e.getActionCommand();
                if(action.equals("ApproveSelection")){
                    File f = fileChooser.getSelectedFile();
                    try {
                        sb.processCodeFiles(f.getAbsolutePath() , inGitView);

                        leftTextArea.setText(null);
                        rightTextArea.setText(null);
                        for(int i = 0; i < sb.srcCodeObj.getLines().size(); i++){
                            leftTextArea.append(sb.srcCodeObj.getLines().get(i));
                        }
                        for(int i = 0; i < sb.targetCodeObjs.get(0).getLines().size(); i++){
                            rightTextArea.append(sb.targetCodeObjs.get(0).getLines().get(i));
                        }
                        leftTextArea.setCaretPosition(0);
                        rightTextArea.setCaretPosition(0);

                        updateScore(); updateLikelyhood();

                    } catch (FileNotFoundException e1) {
                        e1.printStackTrace();
                    }
                }
            }
        });
    }

    private void updateScore(){
        similarityTextArea.setText(null);
        similarityTextArea.append("Similarity Score\n");
        similarityTextArea.append("     "+sb.fileScores[targetIndex] + "%\n");
    }

    private void updateLikelyhood(){
        plagTextArea.setText(null);
        String howLikelyPlag = "ERROR";
        float sim = sb.fileScores[targetIndex];
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

    private void updateGitResults(int count){
        gitResultsTextArea.setText(null);
        gitResultsTextArea.append("Git Results\n");
        gitResultsTextArea.append("" + count);
    }


}