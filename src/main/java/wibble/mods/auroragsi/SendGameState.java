package wibble.mods.auroragsi;

import com.google.gson.*;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClientBuilder;



/**
 * Task that will send a single JSONified request to the Aurora HTTP server.
 */
public class SendGameState implements Runnable{

    private GSINode rootNode = new GSINode();
    private Gson gson =  new Gson(); // Create a Gson that will be used to encode the object into a JSON string.

    public void run() {
        try {
            HttpClient httpClient = HttpClientBuilder.create().build();
            HttpPost request = new HttpPost("http://localhost:" + "9088");
            request.addHeader("Content-Type", "application/json");
            request.setEntity(new StringEntity(gson.toJson(rootNode.update()))); // Update and stringify the GameStateNode and use it to set the request's body.
            httpClient.execute(request); // Execute the request, but don't worry about the response from the server.
        } catch (Exception ignore) {
        }
    }
}