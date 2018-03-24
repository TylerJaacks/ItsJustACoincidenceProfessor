package com.tylerj.coincidenceprofessor.app;

import com.tylerj.coincidenceprofessor.algorithm.Algorithm;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Scanner;

import static com.tylerj.coincidenceprofessor.algorithm.Algorithm.getPercentageSimilarity;

public class ScoreBot {

        public ArrayList<CodeObj> targetCodeObjs;
        public CodeObj srcCodeObj;

        public float[] fileScores;
        private File[] codeFiles;




        public ScoreBot(){
            targetCodeObjs = new ArrayList<CodeObj>();
        }

    /**
     * Gets string from files, runs algo to get similarity, inits similarity Data Structure
     * srcFile is file to check for plagarism
     */
    public void processCodeFiles(String srcFile, boolean accessGit) throws FileNotFoundException {
            createCodeObjects(accessGit);
            calculateValues();
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

            if(temp.getIsSrcCodeFile()){
                srcCodeObj = temp;
            }
            else{
                targetCodeObjs.add(temp);
            }
        }

        private void createCodeObj(String file){
            Scanner s = new Scanner(file);
            String words = "";
            while(s.hasNext()){
                words = words + s.next();
            }

            s = new Scanner(file);
            ArrayList<String> lines = new ArrayList<String>();
            String line = "";
            while(s.hasNext()){
                String cur = s.next();
                if(cur.equals("\n")){
                    lines.add(line); //add line to arraylist
                    line = ""; //reset line
                }
                else{
                    line += cur; //add on to line
                }
            }
            CodeObj temp = new CodeObj(words, file, lines);

            if(temp.getIsSrcCodeFile()){
                srcCodeObj = temp;
            }
            else{
                targetCodeObjs.add(temp);
            }
        }

    /**
     * Goto TestCode Folder, For each file, grab it, get its src code, create Code Object
     */
    public boolean createCodeObjects(boolean accessGit) throws FileNotFoundException {
        if (!accessGit) {
            File testCodeDir = new File("C:\\Users\\jeffy\\IdeaProjects\\v2\\ItsJustACoincidenceProfessor\\TestCode");
            if (testCodeDir.isDirectory()) {
                File[] codeFiles;
                codeFiles = testCodeDir.listFiles();
                this.codeFiles = testCodeDir.listFiles();

                for (int i = 0; i < codeFiles.length; i++) {
                    createCodeObj(codeFiles[i]);
                }
                return true;
            } else {
                System.out.println("ERROR Bad File Directory");
                System.exit(1);
            }
        }
        else{
            String[] arr = getGitFiles();
            if(arr.length == 0){
                return false;
            }
            else{
                    for(int i = 0; i < arr.length; i++){

                    }
                return true;
            }
        }
        return false;
    }

        private float getSimilarityScore(CodeObj cur){
        int distance = Algorithm.getLevenshteinDistance(srcCodeObj.getWords(), cur.getWords());
        return getPercentageSimilarity(distance, cur.getWords().length(), srcCodeObj.getWords().length());
        }

        private void calculateValues(){
            fileScores = new float[targetCodeObjs.size()];

            if(srcCodeObj == null){
                System.exit(2);
            }
            else{
                int index = 0;
                for(int i = 0; i < targetCodeObjs.size(); i++){
                    CodeObj cur = targetCodeObjs.get(i);
                    if(!cur.getIsSrcCodeFile()){
                        float score = getSimilarityScore(cur);
                        fileScores[index] = score;
                        index++;
                    }
                }
            }
        }

        private String[] getGitFiles(){
            String[] arr = new String[1];
            return arr;
        }

        public String getSrcExt(){
            String fileName = srcCodeObj.getFileName();
            String ext = "";
            for(int i = 0; i < fileName.length(); i++){
                if(fileName.charAt(i) == '.'){
                    ext = fileName.substring(i+1);
                }
            }
            return ext;
        }

}