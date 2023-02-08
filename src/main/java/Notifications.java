import javax.net.ssl.HttpsURLConnection;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URL;

public class Notifications {

    // Send an HTTP POST to the given URL with the given json data
    // used in the next function
    public void post(URL url, String postString) throws IOException {

        // from: https://stackoverflow.com/questions/28114663/java-httpurlconnection-setrequestmethod-doesnt-work
        // info from: https://www.javatpoint.com/java-url-openconnection-method
        HttpsURLConnection connection = (HttpsURLConnection) url.openConnection();
        connection.setRequestMethod("POST");
        //connection.setRequestProperty();
        //connection.setRequestProperty();
        connection.setDoOutput(true); // converts the request to POST, because we want to send/output a request body

        OutputStream outputStream = connection.getOutputStream();
        byte[] input = postString.getBytes("utf-8"); //StandardCharsets.UTF_8
        outputStream.write(input, 0, input.length);

        connection.getInputStream();
    }


    // Send an HTTP POST to GitHub API to update the status for a given commit
    public void post_status( String OwnerName, String RepoName, String SHA, String status, String description , String targetURL) throws IOException{

        // info from here: https://docs.github.com/en/rest/commits/statuses?apiVersion=2022-11-28#create-a-commit-status
        URL url = new URL("https://api.github.com/repos/" + OwnerName + "/" + RepoName + "/statuses/" + SHA);

        String postString = "{\n" +
                " \"state\": \"" + status + "\",\n" +
                (targetURL != null ? "  \"target_url\": \"" + targetURL + "\",\n" : "") +
                " \"description\": \"" + description + "\",\n" +
                "}";

        post(url, postString);
    }
}
