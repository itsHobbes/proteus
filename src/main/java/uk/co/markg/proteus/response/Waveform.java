package uk.co.markg.proteus.response;

import java.util.List;
import com.fasterxml.jackson.annotation.JsonProperty;

public class Waveform {
  @JsonProperty("type")
  private String type;

  @JsonProperty("data")
  private List<Integer> samples;

  /**
   * @return the samples
   */
  public List<Integer> getSamples() {
    return samples;
  }
}
