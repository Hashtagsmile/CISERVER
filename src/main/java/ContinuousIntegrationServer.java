import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.ServletException;

import java.io.IOException;
import java.lang.reflect.Array;
import java.util.HashMap;
import java.util.stream.Collectors;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.handler.AbstractHandler;
import com.google.gson.Gson;
import org.eclipse.jetty.util.ajax.JSON;

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
