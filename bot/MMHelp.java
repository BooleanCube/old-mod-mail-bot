package KrisModMailBot;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import javax.annotation.Nonnull;

public class MMHelp extends ListenerAdapter {
    @Override
    public void onGuildMessageReceived(@Nonnull GuildMessageReceivedEvent event) {
        String msg = event.getMessage().getContentRaw();
        if(msg.equalsIgnoreCase("mm!help")) {
            if(event.getMember().hasPermission(Permission.BAN_MEMBERS)) {
                EmbedBuilder e = new EmbedBuilder()
                        .setTitle("Mod Mail Help")
                        .addField("Command:", "report\n\n\nban\n\n\n\nunban\n\n\n\nclose", true)
                        .addField("Usage:", "mm!report [user] [reason]\n\n\nmm!ban [User ID] [# of minutes] [reason]\n\n\nmm!unban [user ID]\n\n\n\nmm!close", true)
                        .addField("Function:", "Any user can report another user for their wrong doings!\n\nMods can ban users from using the report command and from using mod mail\n\nMods can unban users from using the report command and from using mod mail\n\nCloses an opened ticket", true);
                event.getChannel().sendMessage(e.build()).queue();
                return;
            }
            EmbedBuilder e = new EmbedBuilder()
                    .setTitle("Mod Mail Help")
                    .addField("Command:", "report", true)
                    .addField("Usage:", "mm!report [user] [reason]", true)
                    .addField("Function:", "Any user can report another user for their wrong doings!", true);
            event.getChannel().sendMessage(e.build()).queue();
        }
    }
}
