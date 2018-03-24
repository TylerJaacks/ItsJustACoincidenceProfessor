import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Scanner;

public class ScoreBot {

        ArrayList<CodeObj> codeObjs;
        File[] codeFiles;

        public ScoreBot(){
            codeObjs = new ArrayList<CodeObj>();
        }

    /**
     * Gets string from files, runs algo to get similarity, inits similarity Data Structure
     * srcFile is file to check for plagarism
     */
    public void processCodeFiles(String srcFile) throws FileNotFoundException {
            createCodeObjects();

        }

        private void createCodeObj(File f) throws FileNotFoundException {
            Scanner s = new Scanner(f);
            String words = "";
                while(s.hasNext()){
                    words = words + s.next();
                }
            codeObjs.add(new CodeObj(words, f.getName()));
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
}
