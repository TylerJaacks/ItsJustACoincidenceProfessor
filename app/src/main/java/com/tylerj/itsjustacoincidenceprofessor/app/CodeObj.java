package com.tylerj.itsjustacoincidenceprofessor.app;

import java.lang.reflect.Array;
import java.util.ArrayList;

public class CodeObj {

    private String words;
    private String fileName;
    private boolean isSrcCodeFile;
    private static String srcFileName;

    private ArrayList<String> lines;

    public CodeObj(String wordList, String fileName, ArrayList<String> lines, String srcFileName){
        this.srcFileName = srcFileName;
        words = wordList;
        this.fileName = fileName;
        this.lines = lines;

        if(fileName.equals(srcFileName)){
            isSrcCodeFile = true;
        }
        else{
            isSrcCodeFile = false;
        }
    }

    public boolean getIsSrcCodeFile(){
        return isSrcCodeFile;
    }

    public String getFileName(){
        return fileName;
    }

    public String getWords(){
        return words;
    }

    public ArrayList<String> getLines(){
        return lines;
    }


}
