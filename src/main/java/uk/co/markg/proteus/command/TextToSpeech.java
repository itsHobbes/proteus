package uk.co.markg.proteus.command;

import java.time.temporal.ChronoUnit;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import com.fasterxml.jackson.core.JsonProcessingException;
import disparse.discord.jda.DiscordRequest;
import disparse.parser.dispatch.CooldownScope;
import disparse.parser.reflection.CommandHandler;
import disparse.parser.reflection.Cooldown;
import disparse.parser.reflection.Flag;
import disparse.parser.reflection.MessageStrategy;
import disparse.parser.reflection.ParsedEntity;
import uk.co.markg.proteus.App;
import uk.co.markg.proteus.data.CharacterCollection;
import uk.co.markg.proteus.FifteenAIAPIRequest;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class TextToSpeech {
  private static final Logger logger = LogManager.getLogger(TextToSpeech.class);

  private static final String commandName = "speak";

  @ParsedEntity
  static class SpeakRequest {
    @Flag(shortName = 'c', longName = "c", description = "The name of the character to speak.",
        required = true)
    String character = "";
  }

  @Cooldown(amount = 10, unit = ChronoUnit.SECONDS, scope = CooldownScope.USER,
      messageStrategy = MessageStrategy.REACT)
  @CommandHandler(commandName = commandName,
      description = "Speaks your message with a random voice")
  public void speak(SpeakRequest args, DiscordRequest request) {
    var event = request.getEvent();
    String message = event.getMessage().getContentDisplay();
    if (message.length() > 300) {
      event.getChannel().sendMessage("Your input cannot be longer than 300 characters.");
    }

    String character = doesCharacterExist(args.character);
    if (character.isEmpty()) {
      event.getChannel().sendMessage(
          "Your chosen character does not exist! Use the show command to see a list of supported characters. Put quotes `\"` around characters with spaces in their names");
      return;
    }
    logger.info("Retreiving file");
    var apiRequest = new FifteenAIAPIRequest.Builder().withInput(getText(message))
        .withCharacter(character).build();
    event.getChannel().sendTyping().queue();
    try {
      var future = apiRequest.getAudio(event.getAuthor().getIdLong());
      future.thenAccept(response -> event.getChannel().sendFile(response.toFile()).submit().thenAccept(r -> response.toFile().delete()));
    } catch (JsonProcessingException e) {
      e.printStackTrace();
    }
  }

  private String getText(String message) {
    String[] split = message.split(" ");
    for (int i = 0; i < split.length; i++) {
      if (split[i].equals(App.PREFIX + commandName)) {
        split[i] = "";
      }
      if (split[i].equals("-c") || split[i].equals("--c")) {
        split[i] = "";
        int j = 1;
        if (split[i + j].startsWith("\"")) {
          while (!split[i + j].endsWith("\"")) {
            split[i + j] = "";
            j++;
          }
        }
        split[i + j] = "";
        break;
      }
    }
    return Stream.of(split).filter(s -> !s.isEmpty()).collect(Collectors.joining(" "));
  }

  private String doesCharacterExist(String character) {
    String found = "";
    for (CharacterCollection c : Characters.loadCollection()) {
      found = c.findCharacter(character);
      if (!found.isEmpty()) {
        break;
      }
    }
    return found;
  }

}
