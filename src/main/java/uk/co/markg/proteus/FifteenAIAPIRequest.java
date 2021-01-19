package uk.co.markg.proteus;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpClient.Redirect;
import java.net.http.HttpClient.Version;
import java.net.http.HttpRequest.BodyPublishers;
import java.net.http.HttpResponse.BodyHandlers;
import java.nio.file.Path;
import java.time.Duration;
import java.util.concurrent.CompletableFuture;
import com.fasterxml.jackson.core.JsonProcessingException;

public class FifteenAIAPIRequest {

  private final Request request;

  public static class Builder {
    private String text;
    private String character;
    private String emotion = "Contextual";
    private boolean useDiagonal = true;

    public Builder() {
    }

    public Builder withInput(String text) {
      this.text = cleanText(text);
      return this;
    }

    public Builder withCharacter(String character) {
      this.character = character;
      return this;
    }

    public FifteenAIAPIRequest build() {
      var request =
          new FifteenAIAPIRequest(this.text, this.character, this.emotion, this.useDiagonal);
      return request;
    }

    private String cleanText(String text) {
      return text.replaceAll("[@#_:\"£$%^&*()_+=:;~\\/><]", "");
    }
  }

  private FifteenAIAPIRequest(String text, String character, String emotion, boolean useDiagonal) {
    this.request = new Request(text, character, character, useDiagonal);
  }

  public CompletableFuture<Path> getAudio(long userid) throws JsonProcessingException {
    HttpClient client = HttpClient.newBuilder().version(Version.HTTP_2)
        .followRedirects(Redirect.NORMAL).connectTimeout(Duration.ofSeconds(20)).build();

    HttpRequest req = HttpRequest.newBuilder().uri(URI.create("https://api.15.ai/app/getAudioFile"))
        .timeout(Duration.ofMinutes(2)).header("Content-Type", "application/json")
        .POST(BodyPublishers.ofString(request.toJsonString())).build();

    return client.sendAsync(req, BodyHandlers.ofFile(Path.of(userid + ".wav")))
        .exceptionally(e -> null).thenApply(HttpResponse::body);
  }
}
