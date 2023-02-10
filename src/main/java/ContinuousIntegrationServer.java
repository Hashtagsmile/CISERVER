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

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.util.Properties;

/**
 Skeleton of a ContinuousIntegrationServer which acts as webhook
 See the Jetty documentation for API documentation of those classes.
 The skeleton found at: https://github.com/KTH-DD2480/smallest-java-ci
 The skeleton was expanded and extra functionality and functions was added.
 */
public class ContinuousIntegrationServer extends AbstractHandler
{
    /**
     * This method takes an JSON object and retrieves certain
     * properties and their values as key-value pairs in a
     * hashmap.
     * @param jsonObject A JSON object to be handled
     * @return hm The hashmap representation of the JSON object
     */
    // Takes a JSON string as an input and converts it to a JSON object.
    // Necessary properties/attributes are retrieved and stored in a hashmap
    public HashMap<String, String> handleJSONObject(JsonObject jsonObject) throws Exception {
        HashMap<String, String> hm = new HashMap<>();
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
            hm.put("Repo", js.get("name").toString().replaceAll("\"",""));
            hm.put("Owner", js.getAsJsonObject("owner").get("name").toString().replaceAll("\"",""));
        } catch (Exception e) {
            throw new Exception("Something wrong with Repo/owner name, error: " + e);
        }
        try{
            //Retrieves the SHA number i.e head commit Id
            hm.put("SHA", jsonObject.get("after").toString().replaceAll("\"",""));

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
            JsonObject commitsArray = jsonObject.getAsJsonObject("head_commit");
            hm.put("Message", String.valueOf(commitsArray.get("message")));
            hm.put("Head_Id", String.valueOf(commitsArray.get("id")));
            hm.put("Timestamp", String.valueOf(commitsArray.get("timestamp")));
            hm.put("Author", String.valueOf(commitsArray.getAsJsonObject("committer").get("name")));
        } catch (Exception e){
            throw new Exception("Something wrong with commits info, error: " + e);
        }

        return hm;
    }

    /**
     * This method clones a specific branch of a repository.
     * It stores the repository in a temporary directory,
     * if the directory isnt empty it deletes it and creates
     * a new one each run.
     * @param cloneUrl The url of the repository
     * @param branch A The branch name
     * @return void
     */
    public void cloneRepo(String cloneUrl, String branch) {
        String absPath = System.getProperty("user.dir");
        String tempDir = "/clonedRepo"; //This path can be changed
        System.out.println("Repo cloned to following directory: " + absPath + tempDir);
        System.out.println("The clone URL: " + cloneUrl);
        System.out.println("Cloning repository... ");
        String command = "git clone -b "+ branch+ " " + cloneUrl + " ." + tempDir;
        String newCommand = command.replaceAll("\"", "");
        Path cloneDir = Paths.get("clonedRepo");
        System.out.println("File exists: " + Files.exists(cloneDir));
        try{
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
        catch(Exception e){
            System.out.println("When cloning: IOException from exec, or InterruptedException from waitFor");
        }

    }

    /**
     * This method handles the HTTP request from the github
     * webbhook.
     * @param target The target string
     * @param baseRequest The unwrapped base request
     * @param request The HTTP request
     * @param response The HTTP response
     * @return void
     */
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
        HashMap<String, String> extractedInfo = new HashMap<String, String>();
        if(!reqString.isEmpty()) {
            try {
                JsonObject jsonObject = new Gson().fromJson(reqString, JsonObject.class);
                extractedInfo = handleJSONObject(jsonObject);
            } catch (Exception e) {
                throw new RuntimeException("Error when calling handleJSON, error: " + e);
            }

            System.out.println("JSON parsed: " + extractedInfo);
        }

        String absolutePath = System.getProperty("user.dir");
        String clone_url = extractedInfo.get("CloneUrl");
        cloneRepo(clone_url,extractedInfo.get("BranchName"));
        extractedInfo = Features.compileRepo(extractedInfo, absolutePath + "/clonedRepo");
        boolean compRes = false;
        if(extractedInfo.containsKey("Status")){
            compRes = extractedInfo.get("Status").equals("Success");
        }
        System.out.println("Compile status: " + extractedInfo.get("Status"));
        //do the tests
        if(compRes){
            boolean testRes = Features.testRepo(absolutePath + "/clonedRepo");
            if(!testRes){
                response.getWriter().println("Tests failed");
                extractedInfo.put("TestStatus", "Failed");
            }
            else{
                response.getWriter().println("CI job done");
                extractedInfo.put("TestStatus", "Success");
            }
        }
        else{
            response.getWriter().println("Compilation failed");
            extractedInfo.put("TestStatus", "Not run");
        }
        System.out.println("Tests status: " + extractedInfo.get("TestStatus"));

        Features.sendNotificationMail(extractedInfo);
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