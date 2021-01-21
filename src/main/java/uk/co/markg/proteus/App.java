package uk.co.markg.proteus;

import java.awt.Color;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import javax.security.auth.login.LoginException;
import disparse.discord.jda.Dispatcher;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.utils.cache.CacheFlag;

public class App {

  public static final String PREFIX = "proteus!";

  public static void main(String[] args) throws LoginException, InterruptedException {
    Dispatcher.Builder dispatcherBuilder = new Dispatcher.Builder(App.class).prefix(PREFIX)
        .pageLimit(10).withHelpBaseEmbed(() -> new EmbedBuilder().setColor(Color.decode("#eb7701")))
        .description("Proteus: A bot that talks like .. anyone?")
        .autogenerateReadmeWithNameAndPath("", "COMMANDS.md");

    var builder = Dispatcher.init(JDABuilder.create(System.getenv("PROTEUS_TOKEN"), getIntents()),
        dispatcherBuilder.build());
    builder.disableCache(getFlags());
    builder.build().awaitReady();
  }

  private static List<GatewayIntent> getIntents() {
    List<GatewayIntent> intents = new ArrayList<>();
    intents.add(GatewayIntent.GUILD_MESSAGES);
    intents.add(GatewayIntent.GUILD_MEMBERS);
    return intents;
  }

  private static EnumSet<CacheFlag> getFlags() {
    List<CacheFlag> flags = new ArrayList<>();
    flags.add(CacheFlag.ACTIVITY);
    flags.add(CacheFlag.VOICE_STATE);
    flags.add(CacheFlag.CLIENT_STATUS);
    return EnumSet.copyOf(flags);
  }
}
