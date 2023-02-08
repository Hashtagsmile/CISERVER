import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.ServletException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.stream.Collectors;

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
    // Takes a JSON string as an input and converts it to a JSON object.
    // Necessary properties/attributes are retrieved and stored in a hashmap
    public HashMap<String, String> handleJSONObject(JsonObject jsonObject) throws Exception {
        HashMap<String, String> hm = new HashMap<>();
        // owner repo SHA commitId?
        try{
            //Retrieves the path of the branch
            String branchName = jsonObject.get("ref").toString().substring(12).replaceAll("\"","");
            hm.put("BranchName", branchName);
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

    // Checks if the directory for the cloned repository exists, if it does it removes it
    // otherwise it clones the repository to the directory.
    public void cloneRepo(String cloneUrl, String branch) throws IOException, InterruptedException {
        String tempDir = " ./clonedRepo"; //This path can be changed
        System.out.println("Repo cloned to following directory: " + tempDir);
        System.out.println("The clone URL: " + cloneUrl);
        System.out.println("Cloning repository... ");
        String command = "git clone -b "+ branch+ " " + cloneUrl + tempDir;
        String newCommand = command.replaceAll("\"", "");
        Path cloneDir = Paths.get("clonedRepo");
        System.out.println("Path to cloneDir: " + cloneDir);
        System.out.println("File exists: " + Files.exists(cloneDir));
        if(Files.exists(cloneDir)){
            Process process = Runtime.getRuntime().exec("rm -r clonedRepo");
            process.waitFor();
        }
        Process process = Runtime.getRuntime().exec(newCommand);
        process.waitFor();
        if(process.exitValue() != 0){
            System.out.println("Something went wrong with cloning the repo!");
        }else {
            System.out.println("Successfully cloned repository!");
        }
    }

    // Executes maven commands for installing and compiling the cloned repository
    // Flags are used to check if the commands was successfull.
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
        boolean compileFlag = false;
        while ((line = reader.readLine()) != null) {
            if(line.contains("SUCCESS")){
                compileFlag = true;
            }
            outputFromCommand.append(line);
        }
        System.out.println("Maven compile: " + outputFromCommand);
        System.out.println("Compile status: " + compileFlag);

    }


    public void handle(String target,
                       Request baseRequest,
                       HttpServletRequest request,
                       HttpServletResponse response)
            throws IOException, ServletException {
        response.setContentType("text/html;charset=utf-8");
        response.setStatus(HttpServletResponse.SC_OK);
        baseRequest.setHandled(true);

        // here you do all the continuous integration tasks
        // for example
        // 1st clone your repository
        // 2nd compile the code

        String reqString = request.getReader().lines().collect(Collectors.joining(System.lineSeparator()));
        HashMap<String, String> extractedInfo;
        if(!reqString.isEmpty()) {
            try {
                JsonObject jsonObject = new Gson().fromJson(reqString, JsonObject.class);
                extractedInfo = handleJSONObject(jsonObject);
                String clone_url = extractedInfo.get("CloneUrl");
                cloneRepo(clone_url,extractedInfo.get("BranchName"));
                installAndCompileRepo();
            } catch (Exception e) {
                throw new RuntimeException("Error when calling handleJSON, error: " + e);
            }

            System.out.println("JSON parsed: " + extractedInfo);
        }

        response.getWriter().println("CI job done");
    }



    // used to start the CI server in command line
    public static void main(String[] args) throws Exception
    {
        Server server = new Server(8080);
        server.setHandler(new ContinuousIntegrationServer());
        server.start();
        server.join();
    }
}