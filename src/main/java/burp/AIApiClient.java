package burp;

import burp.api.montoya.MontoyaApi;
import burp.api.montoya.http.message.HttpRequestResponse;
import burp.api.montoya.http.message.requests.HttpRequest;
import com.google.gson.Gson;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.nio.charset.StandardCharsets;

public class AIApiClient {
    private final MontoyaApi api;
    private final HttpClient client;
    private final Gson gson;
    private static final String POLLINATIONS_API_URL = "https://text.pollinations.ai/";

    public AIApiClient(MontoyaApi api) {
        this.api = api;
        this.client = HttpClient.newHttpClient();
        this.gson = new Gson();
    }

    public String analyzeRequest(HttpRequest requestResponse) {
        try {
            String requestDetails = String.format("""
                Method: %s
                URL: %s
                Headers: %s
                Body: %s
                """,
                requestResponse.method(),
                requestResponse.url(),
                requestResponse.headers(),
                new String(requestResponse.body().getBytes(), StandardCharsets.UTF_8));

            return sendToAPI(requestDetails);
        } catch (Exception e) {
            api.logging().logToError("Error analyzing request: " + e.getMessage());
            return "Error analyzing request: " + e.getMessage();
        }
    }

    public String analyzeRequest(HttpRequestResponse requestResponse) {
        try {
            String requestDetails = String.format("""
                Method: %s
                URL: %s
                Headers: %s
                Body: %s
                """,
                requestResponse.request().method(),
                requestResponse.request().url(),
                requestResponse.request().headers(),
                new String(requestResponse.request().body().getBytes(), StandardCharsets.UTF_8));

            return sendToAPI(requestDetails);
        } catch (Exception e) {
            api.logging().logToError("Error analyzing request: " + e.getMessage());
            return "Error analyzing request: " + e.getMessage();
        }
    }

    private String sendToAPI(String content) throws Exception {
        Map<String, Object> payload = new HashMap<>();
        ArrayList<Map<String, String>> messages = new ArrayList<>();
        
        messages.add(Map.of(
            "role", "system",
            "content", AISettings.customSystemPrompt
        ));
        
        messages.add(Map.of(
            "role", "user",
            "content", content
        ));

        payload.put("messages", messages);
        payload.put("model", AISettings.selectedModel);
        payload.put("jsonMode", true);

        var request = java.net.http.HttpRequest.newBuilder()
            .uri(URI.create(POLLINATIONS_API_URL))
            .header("Content-Type", "application/json")
            .POST(java.net.http.HttpRequest.BodyPublishers.ofString(gson.toJson(payload)))
            .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        return response.body();
    }

    public String analyzeRequest(String prompt) {
        try {
            return sendToAPI(prompt);
        } catch (Exception e) {
            api.logging().logToError("Error analyzing request: " + e.getMessage());
            return "Error analyzing request: " + e.getMessage();
        }
    }
}