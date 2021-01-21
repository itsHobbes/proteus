package uk.co.markg.proteus.command;

import java.io.IOException;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import disparse.discord.jda.DiscordRequest;
import disparse.discord.jda.DiscordResponse;
import disparse.parser.dispatch.CooldownScope;
import disparse.parser.reflection.CommandHandler;
import disparse.parser.reflection.Cooldown;
import disparse.parser.reflection.Flag;
import disparse.parser.reflection.MessageStrategy;
import disparse.parser.reflection.ParsedEntity;
import net.dv8tion.jda.api.EmbedBuilder;
import uk.co.markg.proteus.data.Character;
import uk.co.markg.proteus.data.CharacterCollection;
import java.awt.Color;

public class Characters {

  public List<CharacterCollection> characters;

  @Cooldown(amount = 10, unit = ChronoUnit.SECONDS, scope = CooldownScope.USER,
      messageStrategy = MessageStrategy.REACT)
  @CommandHandler(commandName = "list", description = "Show a list of all characters")
  public DiscordResponse listShows() {
    return DiscordResponse.of(buildMediaListEmbed());
  }

  private EmbedBuilder buildMediaListEmbed() {
    EmbedBuilder eb = new EmbedBuilder();
    eb.setTitle("Shows");
    characters = loadCollection();
    StringBuilder sb = new StringBuilder("```\n");
    for (CharacterCollection c : characters) {
      sb.append(c.getDisplayName()).append("\n");
    }
    sb.append("```");
    eb.addField("", sb.toString(), true);
    eb.setColor(Color.decode("#eb7701"));
    return eb;
  }

  @ParsedEntity
  static class CharacterRequest {
    @Flag(shortName = 's', longName = "show",
        description = "The name of the media to display available characters", required = true)
    String show = "";
  }

  @Cooldown(amount = 10, unit = ChronoUnit.SECONDS, scope = CooldownScope.USER,
      messageStrategy = MessageStrategy.REACT)
  @CommandHandler(commandName = "characters", description = "Show a list of all characters")
  public DiscordResponse listCharacters(CharacterRequest args, DiscordRequest request) {
    if (args.show.isEmpty()) {
      request.getEvent().getChannel()
          .sendMessage(
              "Show cannot be empty! Use the list command to view a list of supported media")
          .queue();
    }
    return DiscordResponse.of(buildCharacterListEmbed(args));
  }

  private EmbedBuilder buildCharacterListEmbed(CharacterRequest args) {
    EmbedBuilder eb = new EmbedBuilder();
    eb.setTitle(args.show);
    characters = loadCollection();
    for (CharacterCollection c : characters) {
      if (c.getDisplayName().equals(args.show)) {
        int i = 0;
        StringBuilder sb = new StringBuilder("```\n");
        for (Character character : c.getCharacters()) {
          i++;
          sb.append(character.getDisplayName()).append("\n");
          if (i % 10 == 0) {
            sb.append("```");
            eb.addField("", sb.toString(), true);
            sb = new StringBuilder("```\n");
          }
        }
        sb.append("```");
        eb.addField("", sb.toString(), true);
        eb.setColor(Color.decode("#eb7701"));
      }
    }
    return eb;
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
