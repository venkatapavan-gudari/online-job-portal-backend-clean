import java.net.*;
import java.net.http.*;
import java.net.http.HttpRequest.BodyPublishers;
import java.net.http.HttpResponse.BodyHandlers;

public class TestGroq {
    public static void main(String[] args) throws Exception {
        String key = System.getenv("GROQ_API_KEY");
        String body = "{\"model\":\"llama-3.1-8b-instant\",\"messages\":[{\"role\":\"user\",\"content\":\"test\"}]}";

        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("https://api.groq.com/openai/v1/chat/completions"))
                .header("Authorization", "Bearer " + key)
                .header("Content-Type", "application/json")
                .POST(BodyPublishers.ofString(body))
                .build();

        HttpResponse<String> response = client.send(request, BodyHandlers.ofString());
        System.out.println("HTTP status: " + response.statusCode());
        System.out.println("HTTP body: " + response.body());
    }
}
