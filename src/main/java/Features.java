import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class Features {

    public static boolean testRepo(String repo) throws IOException {
        boolean testFlag = false;
        //Test
        Process testProcess = Runtime.getRuntime().exec("mvn test " + repo);
        try {
            testProcess.waitFor();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        String line = "";
        BufferedReader reader = new BufferedReader(new InputStreamReader(testProcess.getInputStream()));
        while ((line = reader.readLine()) != null) {
            if(line.contains("BUILD SUCCESS")){
                testFlag = true;
                break;
            }
            if(line.contains("BUILD FAILURE")){
                break;
            }
        }
        System.out.println("Test status: " + testFlag);
        return testFlag;
    }
}
