package com.tylerj.coincidenceprofessor.app;

import java.lang.reflect.Array;
import java.util.ArrayList;

public class CodeObj {

    private String words;
    private String fileName;
    private boolean isSrcCodeFile;

    private ArrayList<String> lines;

    public CodeObj(String wordList, String fileName, ArrayList<String> lines){
        words = wordList;
        this.fileName = fileName;
        this.lines = lines;

        if(fileName.equals("note.txt")){
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
