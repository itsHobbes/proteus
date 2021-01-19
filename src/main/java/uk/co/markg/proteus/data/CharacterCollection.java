package uk.co.markg.proteus.data;

import java.util.ArrayList;
import java.util.List;

public class CharacterCollection {

  private String source;
  private List<Character> characters;

  public CharacterCollection(String source, List<Character> characters) {
    this.source = source;
    this.characters = characters;
  }

  public CharacterCollection() {
    this.source = "";
    this.characters = new ArrayList<>();
  }

  /**
   * @return the source
   */
  public String getSource() {
    return source;
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

  /**
   * @return the characters
   */
  public String getCharactersAsString() {
    StringBuilder sb = new StringBuilder();
    for (Character character : characters) {
      if (character.getAlias() == null || character.getAlias().isEmpty()) {
        sb.append(character.getName()).append("\n");
      } else {
        sb.append(character.getAlias()).append("\n");
      }
    }
    return sb.toString();
  }

}
