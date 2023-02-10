import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.File;

public class Features {

    public static boolean testRepo(String repo) throws IOException {
        //System.out.println("Repo: " + repo);
        //System.out.println("File(repo): " + new File(repo));
        boolean testFlag = false;
        //Test
        Process testProcess = Runtime.getRuntime().exec("mvn test", null, new File(repo));
        try {
            testProcess.waitFor();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        String line = "";
        BufferedReader reader = new BufferedReader(new InputStreamReader(testProcess.getInputStream()));
        //System.out.println("Standard output[tests]:");
        while ((line = reader.readLine()) != null) {
            //System.out.println(line);
            if(line.contains("BUILD SUCCESS")){
                testFlag = true;
                break;
            }
            if(line.contains("BUILD FAILURE")){
                break;
            }
        }
        System.out.println("Test status: " + testFlag + ". REPO: " + repo);
        return testFlag;
    }
}
