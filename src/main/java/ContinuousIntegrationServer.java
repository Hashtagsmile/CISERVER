import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.ServletException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Array;
import java.util.HashMap;
import java.util.stream.Collectors;
import java.lang.Runtime;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.handler.AbstractHandler;
import com.google.gson.Gson;
import org.eclipse.jetty.util.ajax.JSON;
import org.omg.SendingContext.RunTime;

/**
 Skeleton of a ContinuousIntegrationServer which acts as webhook
 See the Jetty documentation for API documentation of those classes.
 */
public class ContinuousIntegrationServer extends AbstractHandler
{
    public HashMap<String, String> handleJSONObject(JsonObject jsonObject) throws Exception {
        HashMap<String, String> hm = new HashMap<>();
        // owner repo SHA commitId?
        try{
            //Retrieves the path of the branch
            hm.put("BranchName", String.valueOf(jsonObject.get("ref")));
            } catch (Exception e) {
            throw new Exception("Something wrong with ref, error: " + e);
        }
        try{
            //Retrieves the name of the repo and its owner
            JsonObject js = jsonObject.getAsJsonObject("repository");
            hm.put("Repo", String.valueOf(js.get("name")));
            hm.put("Owner", String.valueOf(js.getAsJsonObject("owner").get("name")));
        } catch (Exception e) {
            throw new Exception("Something wrong with Repo/owner name, error: " + e);
        }
        try{
            //Retrieves the SHA number i.e head commit Id
            hm.put("SHA", String.valueOf(jsonObject.getAsJsonObject("head_commit").get("id")));

        } catch (Exception e) {
            throw new Exception("Something wrong with SHA, error: " + e);
        }
        try{
            //Retrieves the clone url
            hm.put("CloneUrl", String.valueOf(jsonObject.getAsJsonObject("repository").get("clone_url")));
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

    public void cloneRepo(String url) throws IOException, InterruptedException {
        String tempDir = " ./clonedRepo"; //This path can be changed
        System.out.println("Temporary directory to clone to: " + tempDir);
        System.out.println("Cloning repository...");
        Process process = Runtime.getRuntime().exec("git clone " + url + tempDir);
        process.waitFor();
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


        String clone_url = "https://github.com/Hashtagsmile/CISERVER.git";
        try {
            cloneRepo(clone_url);
        } catch (InterruptedException e) {
            throw new RuntimeException("Could not clone repo: " + e);
        }

        //String dir = System.getProperty("user.dir");
        installAndCompileRepo();


        String reqString = request.getReader().lines().collect(Collectors.joining(System.lineSeparator()));
        HashMap<String, String> extractedInfo;
        if(!reqString.isEmpty()) {
            try {
                JsonObject jsonObject = new Gson().fromJson(reqString, JsonObject.class);
                extractedInfo = handleJSONObject(jsonObject);
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
