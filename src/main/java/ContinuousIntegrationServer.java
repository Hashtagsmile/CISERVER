import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.ServletException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.stream.Collectors;
import java.lang.Runtime;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.handler.AbstractHandler;
import com.google.gson.Gson;

/**
 Skeleton of a ContinuousIntegrationServer which acts as webhook
 See the Jetty documentation for API documentation of those classes.
 */
public class ContinuousIntegrationServer extends AbstractHandler
{
    public HashMap<String, String> handleJSONObject(JsonObject jsonObject) throws Exception {
        HashMap<String, String> hm = new HashMap<>();
        try{
            //Retrieves the path of the branch
            hm.put("BranchName", String.valueOf(jsonObject.get("ref")));
            } catch (Exception e) {
            throw new Exception("Something wrong with ref, error: " + e);
        }
        try{
            //Retrieves the clone url
            hm.put("CloneUrl", jsonObject.getAsJsonObject("repository").get("clone_url").toString());
        } catch (Exception e){
            throw new Exception("Something wrong with github url, error: " + e);
        }
        try {
            //Retrieve commits information
            JsonArray commitsArray = jsonObject.getAsJsonArray("commits");
            hm.put("Message", String.valueOf(commitsArray.get(0).getAsJsonObject().get("message")));
            hm.put("Timestamp", String.valueOf(commitsArray.get(0).getAsJsonObject().get("timestamp")));
            hm.put("Author", String.valueOf(commitsArray.get(0).getAsJsonObject().get("author").getAsJsonObject().get("name")));
            hm.put("Modified", String.valueOf(commitsArray.get(0).getAsJsonObject().get("modified")));
        } catch (Exception e){
            throw new Exception("Something wrong with commits info, error: " + e);
        }

        return hm;
    }

    public void cloneRepo(String cloneUrl) throws IOException, InterruptedException {
        String tempDir = " ./clonedRepo"; //This path can be changed
        System.out.println("Temporary directory to clone to: " + tempDir);
        System.out.println("Cloning repository...: " + cloneUrl);
        String command = "git clone " + cloneUrl + tempDir;
        String newCommand = command.replaceAll("\"", "");
        System.out.println(newCommand);
        Process process = Runtime.getRuntime().exec(newCommand);
        process.waitFor();
        System.out.println(process.exitValue());
        System.out.println("Successfully cloned repository!");
    }

    public void installAndCompileRepo() throws IOException {
        //Install
        Process process = Runtime.getRuntime().exec("mvn install -f " + "./clonedRepo");
        try {
            process.waitFor();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        System.out.println("Maven install: " + process.exitValue());

        //Compile
        Process compileProcess = Runtime.getRuntime().exec("mvn compile -f " + "./clonedRepo");
        try {
            compileProcess.waitFor();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        BufferedReader reader = new BufferedReader(new InputStreamReader(compileProcess.getInputStream()));
        StringBuilder outputFromCommand = new StringBuilder();
        String line = "";
        while ((line = reader.readLine()) != null) {
            outputFromCommand.append(line);
        }
        System.out.println("Maven compile: " + outputFromCommand);
    }


    public void handle(String target,
                       Request baseRequest,
                       HttpServletRequest request,
                       HttpServletResponse response)
            throws IOException, ServletException {
        response.setContentType("text/html;charset=utf-8");
        response.setStatus(HttpServletResponse.SC_OK);
        baseRequest.setHandled(true);


        String reqString = request.getReader().lines().collect(Collectors.joining(System.lineSeparator()));
        HashMap<String, String> extractedInfo;
        if(!reqString.isEmpty()) {
            try {
                JsonObject jsonObject = new Gson().fromJson(reqString, JsonObject.class);
                extractedInfo = handleJSONObject(jsonObject);
                String clone_url = extractedInfo.get("CloneUrl");
                try {
                    cloneRepo(clone_url);
                } catch (InterruptedException e) {
                    throw new RuntimeException("Could not clone repo: " + e);
                }

                installAndCompileRepo();

            } catch (Exception e) {
                throw new RuntimeException("Error when calling handleJSON, error: " + e);
            }
            System.out.println("JSON parsed: " + extractedInfo);
        }

        response.getWriter().println("CI job done");
    }


    //TODO: Notifications


    // used to start the CI server in command line
    public static void main(String[] args) throws Exception
    {
        Server server = new Server(8080);
        server.setHandler(new ContinuousIntegrationServer());
        server.start();
        server.join();
    }
}
