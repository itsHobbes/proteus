package uk.co.markg.proteus.data;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Character {

  private String name;
  private String alias;

  public Character(@JsonProperty("name") String name, @JsonProperty("alias") String alias) {
    this.name = name;
    this.alias = alias;
  }

  /**
   * @return the name
   */
  public String getName() {
    return name;
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
      return name;
    }
  }

  public boolean matches(String name) {
    if (alias == null) {
      return this.name.toLowerCase().equals(name.toLowerCase());
    }
    return this.name.toLowerCase().equals(name.toLowerCase())
        || this.alias.toLowerCase().equals(name.toLowerCase());
  }

}
