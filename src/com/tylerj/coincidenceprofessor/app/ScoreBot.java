package com.tylerj.coincidenceprofessor.app;

import com.tylerj.coincidenceprofessor.algorithm.Algorithm;
import com.tylerj.coincidenceprofessor.codesearch.CodeSearch;
import com.tylerj.coincidenceprofessor.codesearch.Languages;
import com.tylerj.coincidenceprofessor.codesearch.Locations;
import com.tylerj.coincidenceprofessor.codesearch.Utils;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Scanner;

import static com.tylerj.coincidenceprofessor.algorithm.Algorithm.getPercentageSimilarity;

public class ScoreBot {

        public ArrayList<CodeObj> targetCodeObjs;
        public CodeObj srcCodeObj;

        public float[] fileScores;
        private File[] codeFiles; //get ride of

        public Languages language;
        public Locations locations;
        public String searchString;

        public String srcFilePath;
        public String srcFileName;

        public ScoreBot(){
            targetCodeObjs = new ArrayList<CodeObj>();
        }

    /**
     *
     * @param srcFile ABSOLUTE PATH OF SRC FILE
     * @param accessGit
     * @return
     * @throws FileNotFoundException
     */
    public int processCodeFiles(String srcFile, boolean accessGit) throws FileNotFoundException {
        resetValues();
        String buildSearchString = "";
        Scanner s = new Scanner(new File(srcFile));
        while(s.hasNextLine()){
            buildSearchString = buildSearchString + s.nextLine();
        }
        searchString = buildSearchString;
        srcFilePath = srcFile;

        String temp = ""; boolean periodFound = false;
        for(int i = srcFilePath.length() - 1; i > 0; i--){
            char c = srcFilePath.charAt(i);
            if(c != '\\'){
                temp = c + temp;
            }
            else if(c == '.' && !periodFound){
                periodFound = true;
                temp = c + temp;
            }
            else{
                break;
            }
        }

        srcFileName = temp;
        CodeObj.srcFileName = srcFileName;



            boolean success;
            success = createCodeObjects(accessGit);

            if(success){
                    language = Utils.getLocation(getSrcExt().toUpperCase());
                locations = Locations.GITHUB;

                calculateValues();
                return targetCodeObjs.size();
            }
            else{
                targetCodeObjs = null;
                srcCodeObj = null;
                fileScores = null;
                return 0;
            }

        }

        private void resetValues(){
            targetCodeObjs = new ArrayList<CodeObj>();
            srcCodeObj = null;
            fileScores = null;
            codeFiles = null; //get ride of
            language = null;
            locations = null;
            searchString = null;
            srcFilePath = null;
            srcFileName = null;
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
                CodeObj temp = new CodeObj(words, f.getName(), lines, srcFileName);

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
//            while(s.hasNext()){
//                String cur = s.next();
//                if(cur.equals("\n")){
//                    lines.add(line); //add line to arraylist
//                    line = ""; //reset line
//                }
//                else{
//                    line += cur; //add on to line
//                }
//            }

            while(s.hasNextLine()){
                String cur = s.nextLine();
                lines.add(cur);
            }

            CodeObj temp = new CodeObj(words, file, lines, srcFilePath);

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
            File testCodeDir = new File("C:\\Users\\jeffy\\IdeaProjects\\v3\\ItsJustACoincidenceProfessor\\TestCode");
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

            if(arr.length == 0){ //No results
                return false;
            }
            else{
                    for(int i = 0; i < arr.length; i++){
                        createCodeObj(arr[i]);
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

        private String[] getGitFiles() {


            ArrayList<String> arr = CodeSearch.getSimilarSourceCode(searchString, Utils.languageEnumToId(language), Utils.locationEnumToId(locations));
            String[] finalArr = new String[arr.size()];

            finalArr = arr.toArray(finalArr);

            return finalArr;
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