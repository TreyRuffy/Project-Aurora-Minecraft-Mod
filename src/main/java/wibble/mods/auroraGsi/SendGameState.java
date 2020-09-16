package wibble.mods.auroraGsi;

import com.google.gson.*;
import net.minecraft.client.settings.KeyBinding;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClientBuilder;

import java.lang.reflect.Type;


/**
 * Task that will send a single JSONified request to the Aurora HTTP server.
 */
public class SendGameState implements Runnable {

    private GSINode rootNode = new GSINode();
    private Gson gson; // Create a Gson that will be used to encode the object into a JSON string.

    public SendGameState() {
        // Add a custom serializer to the JSON-builder so that only relevant KeyBinding fields are returned.
        GsonBuilder builder = new GsonBuilder();
        JsonSerializer<KeyBinding> serializer = (KeyBinding src, Type typeOfSrc, JsonSerializationContext context) -> {
            JsonObject jObject = new JsonObject();
            jObject.addProperty("keyCode", wibble.mods.auroraGsi.AuroraKeyBinding.ToAuroraKeyCode(src.getKey().getTranslationKey()));
            jObject.addProperty("modifier", src.getKeyModifier().name());
            jObject.addProperty("context", src.getKeyConflictContext().toString());
            return jObject;
        };
        builder.registerTypeAdapter(KeyBinding.class, serializer);

        gson = builder.create();
    }

    public void run() {
        try {
            HttpClient httpClient = HttpClientBuilder.create().build();
            HttpPost request = new HttpPost("http://localhost:" + 9088);
            request.addHeader("Content-Type", "application/json");
            request.setEntity(new StringEntity(gson.toJson(rootNode.update()))); // Update and stringify the GameStateNode and use it to set the request's body.
            httpClient.execute(request); // Execute the request, but don't worry about the response from the server.
        } catch (Exception ignore) { }
    }
}