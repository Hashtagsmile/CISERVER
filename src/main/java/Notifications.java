import javax.net.ssl.HttpsURLConnection;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.stream.MalformedJsonException;

public class Notifications {

    String GITHUB_TOKEN = "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";
    String BASE_URL = "https://api.github.com/repos/Hashtagsmile/CI_SERVER/statuses/";
    // Send an HTTP POST to the given URL with the given json data
    // used in the next function
    public void post(URL url, JsonObject js) throws IOException {

        // inspired from: https://stackoverflow.com/questions/28114663/java-httpurlconnection-setrequestmethod-doesnt-work

        HttpsURLConnection connection = (HttpsURLConnection)url.openConnection();
        // URLConnection connection = new URL(url).openConnection();

        connection.setRequestMethod("POST");
        connection.setRequestProperty("Content-Type", "application/json");
        connection.setRequestProperty("Accept", "application/vnd.github+json");
        connection.setDoOutput(true); // // Triggers POST: converts the request to POST, because we want to send/output a request body
        connection.setRequestProperty("Authorization", "Bearer " + GITHUB_TOKEN);


        try (OutputStream output = connection.getOutputStream()) {
            byte[] input = js.toString().getBytes(StandardCharsets.UTF_8); //StandardCharsets.UTF_8
            output.write(input, 0, input.length);
        }
        connection.getInputStream();
    }


    // Send an HTTP POST to GitHub API to update the status for a given commit
    public void post_status(String OwnerName, String RepoName, String SHA, String state, String description, String target_url, String context) throws IOException{

        //URL url = new URL("https://api.github.com/repos/" + OwnerName + "/" + RepoName + "/statuses/" + SHA);
        URL url = new URL(BASE_URL + SHA);
        System.out.println("Post URL: " + url);

        String postString = "{\"state\": \"" + state.toLowerCase() + "\"}";
        JsonObject jsonObject = new Gson().fromJson(postString, JsonObject.class);
        post(url, jsonObject);
    }
}
