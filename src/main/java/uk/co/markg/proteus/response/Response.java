package uk.co.markg.proteus.response;

import java.util.List;
import com.fasterxml.jackson.annotation.JsonProperty;

public class Response {

  @JsonProperty("batch")
  private List<Double> batch;

  @JsonProperty("dict_exists")
  private List<List<String>> dict_exists;

  @JsonProperty("scores")
  private List<Double> scores;

  @JsonProperty("text_parsed")
  private List<String> text_parsed;

  @JsonProperty("tokenized")
  private List<String> tokenized;

  @JsonProperty("torchmoji")
  private List<String> torchmoji;

  @JsonProperty("waveforms")
  private List<Waveform> waveforms;

  public List<Integer> getSamples() {
    return waveforms.get(0).getSamples();
  }
  
}
