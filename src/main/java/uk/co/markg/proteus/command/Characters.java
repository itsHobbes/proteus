package uk.co.markg.proteus.command;

import java.io.IOException;
import java.time.temporal.ChronoUnit;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import disparse.discord.jda.DiscordResponse;
import disparse.parser.dispatch.CooldownScope;
import disparse.parser.reflection.CommandHandler;
import disparse.parser.reflection.Cooldown;
import disparse.parser.reflection.MessageStrategy;
import net.dv8tion.jda.api.EmbedBuilder;
import uk.co.markg.proteus.data.CharacterCollection;
import java.util.ArrayList;
import java.util.List;

public class Characters {

  public List<CharacterCollection> characters;

  @Cooldown(amount = 10, unit = ChronoUnit.SECONDS, scope = CooldownScope.USER,
      messageStrategy = MessageStrategy.REACT)
  @CommandHandler(commandName = "show", description = "Show a list of all characters")
  public DiscordResponse show() {
    EmbedBuilder eb = new EmbedBuilder();
    eb.setTitle("Characters");
    characters = loadCollection();
    for (CharacterCollection c : characters) {
      eb.addField(c.getSource(), "```\n" + c.getCharactersAsString() + "```", true);
    }
    return DiscordResponse.of(eb);
  }

  public static List<CharacterCollection> loadCollection() {
    ObjectMapper mapper = new ObjectMapper();
    try {
      return mapper.readValue(
          ClassLoader.getSystemClassLoader().getResourceAsStream("characters.json"),
          new TypeReference<List<CharacterCollection>>() {
          });
    } catch (IOException e) {
      e.printStackTrace();
    }
    return new ArrayList<CharacterCollection>();
  }

}
