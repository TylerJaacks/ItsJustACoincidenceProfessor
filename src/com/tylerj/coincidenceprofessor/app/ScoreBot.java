package com.tylerj.coincidenceprofessor.app;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Scanner;

public class ScoreBot {

        public ArrayList<CodeObj> codeObjs;
        public String[] fileNameScores;
        public int[] fileScores;
        private File[] codeFiles;
        private CodeObj srcCodeObj;



        public ScoreBot(){
            codeObjs = new ArrayList<CodeObj>();
        }

    /**
     * Gets string from files, runs algo to get similarity, inits similarity Data Structure
     * srcFile is file to check for plagarism
     */
    public void processCodeFiles(String srcFile) throws FileNotFoundException {
            createCodeObjects();
            calculateValues(srcFile);
        }

        private void createCodeObj(File f) throws FileNotFoundException {
            Scanner s = new Scanner(f);
            String words = "";
                while(s.hasNext()){
                    words = words + s.next();
                }

            s = new Scanner(f);
            ArrayList<String> lines = new ArrayList<String>();
                while(s.hasNextLine()){
                    lines.add(s.nextLine() + "\n");
                }
                CodeObj temp = new CodeObj(words, f.getName(), lines);
            codeObjs.add(temp);
            if(temp.getIsSrcCodeFile()){
                srcCodeObj = temp;
            }
        }

    /**
     * Goto TestCode Folder, For each file, grab it, get its src code, create Code Object
     */
    public void createCodeObjects() throws FileNotFoundException {
            File testCodeDir = new File("C:\\Users\\jeffy\\IdeaProjects\\ItsJustACoincidenceProfessor\\TestCode");
            if(testCodeDir.isDirectory()){
                File[] codeFiles;
                codeFiles = testCodeDir.listFiles();
                this.codeFiles = testCodeDir.listFiles();

                for(int i = 0; i < codeFiles.length; i++){
                    createCodeObj(codeFiles[i]);
                }
            }
            else{
                System.out.println("ERROR Bad File Directory");
                System.exit(1);
            }
        }

        private int getSimilarityScore(CodeObj cur){
            return 1;
        }

        private void calculateValues(String srcCodeFile){
            fileNameScores = new String[codeObjs.size() - 1];
            fileScores = new int[codeObjs.size() - 1];

            if(srcCodeObj == null){
                System.exit(2);
            }
            else{
                int index = 0;
                for(int i = 0; i < codeObjs.size(); i++){
                    CodeObj cur = codeObjs.get(i);
                    if(!cur.getIsSrcCodeFile()){
                        fileNameScores[index] = cur.getFileName();
                        fileScores[index] = getSimilarityScore(cur);
                        index++;
                    }
                }
            }
        }
}
