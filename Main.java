import KrisModMailBot.MMHelp;
import KrisModMailBot.ModMail;
import KrisModMailBot.ModMailBan;
import KrisModMailBot.Report;
import groovyjarjarpicocli.CommandLine;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.utils.ChunkingFilter;
import net.dv8tion.jda.api.utils.MemberCachePolicy;
import org.discordbots.api.client.DiscordBotListAPI;
import org.jibble.pircbot.IrcException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.security.auth.login.LoginException;
import java.io.IOException;

public class Main {

    private Main() throws IOException, IrcException, InterruptedException {
        try {
            JDA j = JDABuilder.createDefault("NzE0NTg1NTk5ODM4OTEyNjQy.Xswz9A.fijQ4LvfY76xAdiFYAkDrhzKgtA").setChunkingFilter(ChunkingFilter.ALL)
                    .setMemberCachePolicy(MemberCachePolicy.ALL).enableIntents(GatewayIntent.GUILD_MEMBERS)
                    .setActivity(Activity.streaming("DM me to contact the staff | mm!help", "https://www.twitch.tv/kristoferyee"))
                    .addEventListeners(new ModMail())
                    .addEventListeners(new ModMailBan())
                    .addEventListeners(new MMHelp())
                    .addEventListeners(new Report())
                    .build();
        } catch (LoginException e) {

        }
    }

    public static void main(String[] args) throws IOException, IrcException, InterruptedException {
        new Main();
    }

}
