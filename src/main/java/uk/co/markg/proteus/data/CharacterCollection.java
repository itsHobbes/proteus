package uk.co.markg.proteus.data;

import java.util.List;
import com.fasterxml.jackson.annotation.JsonProperty;

public class CharacterCollection {

  private String source;
  private String alias;
  private List<Character> characters;

  public CharacterCollection(@JsonProperty("source") String source,
      @JsonProperty("alias") String alias, @JsonProperty("characters") List<Character> characters) {
    this.source = source;
    this.alias = alias;
    this.characters = characters;
  }

  /**
   * @return the source
   */
  public String getSource() {
    return source;
  }

  /**
   * @return the alias
   */
  public String getAlias() {
    return alias;
  }

  public String getDisplayName() {
    if (alias != null) {
      return alias;
    } else {
      return source;
    }
  }

  /**
   * @return the characters
   */
  public List<Character> getCharacters() {
    return characters;
  }

  public String findCharacter(String character) {
    for (Character s : characters) {
      if (s.matches(character)) {
        return s.getName();
      }
    }
    return "";
  }

}
