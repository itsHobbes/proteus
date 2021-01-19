package uk.co.markg.proteus;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class Request {

    @JsonProperty("text")
    private final String text;
    @JsonProperty("character")
    private final String character;
    @JsonProperty("emotion")
    private final String emotion;
    @JsonProperty("use_diagonal")
    private final boolean useDiagonal;

    public Request(String text, String character, String emotion, boolean useDiagonal) {
        this.text = text;
        this.character = character;
        this.emotion = emotion;
        this.useDiagonal = useDiagonal;
    }

    public String toJsonString() throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper.writeValueAsString(this);
    }

}
