package uk.co.markg.proteus.command;

import java.io.File;
import java.io.IOException;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import disparse.discord.jda.DiscordRequest;
import disparse.parser.dispatch.CooldownScope;
import disparse.parser.reflection.CommandHandler;
import disparse.parser.reflection.Cooldown;
import disparse.parser.reflection.MessageStrategy;
import disparse.parser.reflection.Usage;

public class ModifyChannel {

  private Map<Long, Set<Long>> serverConfigs;

  public ModifyChannel() {
    serverConfigs = loadServerConfigs();
  }

  public static Map<Long, Set<Long>> loadServerConfigs() {
    ObjectMapper mapper = new ObjectMapper();
    try {
      return mapper.readValue(new File("servers.json"), new TypeReference<Map<Long, Set<Long>>>() {
      });
    } catch (IOException e) {
      e.printStackTrace();
    }
    return new HashMap<>();
  }

  private void saveConfig() {
    ObjectMapper mapper = new ObjectMapper();
    try {
      mapper.writeValue(new File("servers.json"), serverConfigs);
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  @Cooldown(amount = 10, unit = ChronoUnit.SECONDS, scope = CooldownScope.USER,
      messageStrategy = MessageStrategy.REACT)
  @CommandHandler(commandName = "add", description = "Add all mentioned channels")
  @Usage(usage = "#my_awesome_channel #general", description = "Will add all mentioned channels")
  public void addChannel(DiscordRequest request) {
    var event = request.getEvent();
    long serverid = event.getGuild().getIdLong();
    var serverConfig = serverConfigs.getOrDefault(serverid, new HashSet<Long>());
    for (var channel : event.getMessage().getMentionedChannels()) {
      long channelId = channel.getIdLong();
      if (!serverConfig.contains(channelId)) {
        serverConfig.add(channelId);
      }
    }
    serverConfigs.put(serverid, serverConfig);
    event.getChannel().sendMessage("All valid channels have been added!").queue();
    saveConfig();
  }



  @Cooldown(amount = 10, unit = ChronoUnit.SECONDS, scope = CooldownScope.USER,
      messageStrategy = MessageStrategy.REACT)
  @CommandHandler(commandName = "remove", description = "Remove all mentioned channels")
  @Usage(usage = "#my_awesome_channel #general", description = "Will remove all mentioned channels")
  public void removeChannel(DiscordRequest request) {
    var event = request.getEvent();
    long serverid = event.getGuild().getIdLong();
    var serverConfig = serverConfigs.getOrDefault(serverid, new HashSet<Long>());
    for (var channel : event.getMessage().getMentionedChannels()) {
      long channelId = channel.getIdLong();
      if (serverConfig.contains(channelId)) {
        serverConfig.remove(channelId);
      }
    }
    serverConfigs.put(serverid, serverConfig);
    event.getChannel().sendMessage("All valid channels have been removed!").queue();
    saveConfig();
  }

}
