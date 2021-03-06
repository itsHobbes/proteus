package uk.co.markg.proteus.command;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.time.temporal.ChronoUnit;
import java.util.HashSet;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import disparse.discord.jda.DiscordRequest;
import disparse.parser.dispatch.CooldownScope;
import disparse.parser.reflection.CommandHandler;
import disparse.parser.reflection.Cooldown;
import disparse.parser.reflection.Flag;
import disparse.parser.reflection.MessageStrategy;
import disparse.parser.reflection.ParsedEntity;
import disparse.parser.reflection.Usage;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import uk.co.markg.proteus.App;
import uk.co.markg.proteus.FifteenAIAPIRequest;
import uk.co.markg.proteus.data.CharacterCollection;
import uk.co.markg.proteus.response.Response;

public class TextToSpeech {
  private static final Logger logger = LogManager.getLogger(TextToSpeech.class);
  private static final String commandName = "speak";
  private static final ObjectMapper mapper = new ObjectMapper();

  private String message;
  private String character;

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
  @Usage(usage = "-c \"Twilight Sparkle\" Hey there everybody!",
      description = "Twiglight Sparkle will say \"Hey there everybody!\"")
  public void speak(SpeakRequest args, DiscordRequest request) {

    if (channelIsNotAdded(request)) {
      return;
    }

    var event = request.getEvent();
    message = event.getMessage().getContentDisplay();
    if (message.length() > 300) {
      event.getChannel().sendMessage("Your input cannot be longer than 300 characters.");
    }

    character = doesCharacterExist(args.character);
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
      var future = apiRequest.getAudio();
      future.thenAccept(response -> parseAndSendResponse(response, event));
    } catch (JsonProcessingException e) {
      e.printStackTrace();
    }
  }

  private void parseAndSendResponse(String response, MessageReceivedEvent event) {
    if (response.contains("Error")) {
      event.getChannel()
          .sendMessage(
              "It looks like 15.ai is currently unavailable. See <http://15.ai> for more details.")
          .queue();
      return;
    }
    var filename = event.getAuthor().getIdLong() + "-" + character + ".mp3";
    try (var fos = new FileOutputStream(filename)) {
      var res = mapper.readValue(response, Response.class);
      var samples = res.getSamples();
      for (Integer sample : samples) {
        fos.write(sample);
      }
      var file = new File(filename);
      event.getMessage().reply(file).mentionRepliedUser(true).append(getText(message)).submit()
          .thenAccept(r -> file.delete());
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  private boolean channelIsNotAdded(DiscordRequest request) {
    var configs = ModifyChannel.loadServerConfigs();
    var channels =
        configs.getOrDefault(request.getEvent().getGuild().getIdLong(), new HashSet<Long>());
    if (channels.contains(request.getEvent().getChannel().getIdLong())) {
      return false;
    }
    logger.info("Channel not added");
    return true;
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
    for (CharacterCollection c : ShowCharacters.loadCollection()) {
      found = c.findCharacter(character);
      if (!found.isEmpty()) {
        break;
      }
    }
    return found;
  }

}
