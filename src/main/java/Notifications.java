import javax.net.ssl.HttpsURLConnection;
import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class Notifications {

    // Send an HTTP POST to the given URL with the given json data
    // used in the next function
    public void post(URL url, String postString) throws IOException {

        // inspired from: https://stackoverflow.com/questions/28114663/java-httpurlconnection-setrequestmethod-doesnt-work

        HttpURLConnection connection = (HttpURLConnection)url.openConnection();
        // URLConnection connection = new URL(url).openConnection();

        connection.setRequestMethod("POST");
        connection.setRequestProperty("Content-Type", "application/json");
        connection.setRequestProperty("Accept", "application/json");
        connection.setDoOutput(true); // // Triggers POST: converts the request to POST, because we want to send/output a request body

        try (OutputStream output = connection.getOutputStream()) {
            byte[] input = postString.getBytes("utf-8"); //StandardCharsets.UTF_8
            output.write(input, 0, input.length);
        }
        connection.getInputStream();
    }


    // Send an HTTP POST to GitHub API to update the status for a given commit
    public void post_status( String OwnerName, String RepoName, String SHA, String state, String description) throws IOException{

        URL url = new URL("https://api.github.com/repos/" + OwnerName + "/" + RepoName + "/statuses/" + SHA);

        String postString = "{\n" +
                "  \"state\": \"" + state + "\",\n" +
                "  \"description\": \"" + description + "\",\n" +
                "}";

        post(url, postString);
    }
}
