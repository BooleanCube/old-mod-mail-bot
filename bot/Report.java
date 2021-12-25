package KrisModMailBot;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import javax.annotation.Nonnull;
import java.util.Collection;
import java.util.HashMap;
import java.util.concurrent.TimeUnit;

import static KrisModMailBot.ModMail.MODCHANNEL_ID;
import static KrisModMailBot.ModMail.REPORTMODCHANNEL_ID;


public class Report extends ListenerAdapter {

    public static HashMap<Long, Long> alreadyReported = new HashMap<>();

    @Override
    public void onGuildMessageReceived(@Nonnull GuildMessageReceivedEvent event) {
        String content = event.getMessage().getContentRaw();
        if(content.startsWith("mm!report ") && content.split(" ").length >= 3 && event.getMessage().getMentionedMembers().size() == 1 && content.split(" ")[1].startsWith("<@")) {
            event.getMessage().delete().queue();
            if(ModMailBan.blackListMembers.contains(event.getAuthor().getIdLong())) {
                if(ModMailBan.timeouts.get(event.getAuthor().getIdLong()) < System.currentTimeMillis()) {
                    ModMailBan.blackListMembers.remove(event.getAuthor().getIdLong());
                    ModMailBan.timeouts.remove(event.getAuthor().getIdLong());
                    ModMailBan.reasons.remove(event.getAuthor().getIdLong());
                } else {
                    event.getChannel().sendMessage("You have been temporarily banned from using the Mod Mail to contact the staff! Reason: " + ModMailBan.reasons.get(event.getAuthor().getIdLong())).queue();
                    return;
                }
            }
            Member beingReported = event.getMessage().getMentionedMembers().get(0);
            if(beingReported.getIdLong() == event.getMember().getIdLong()) {
                event.getChannel().sendMessage("You can not report yourself to the mods!").queue(msg ->
                        msg.delete().queueAfter(5, TimeUnit.SECONDS)
                );
                return;
            }
            if(alreadyReported.containsKey(beingReported.getIdLong())) {
                if(System.currentTimeMillis()-alreadyReported.get(beingReported.getIdLong())<600*1000) {
                    event.getChannel().sendMessage("This user has already been reported within the last 10 minutes! Thank you for your assistance in keeping the community fun!").queue(msg -> {
                        msg.delete().queueAfter(5, TimeUnit.SECONDS);
                    });
                    return;
                }
            }
            Member reporting = event.getMember();
            event.getGuild().getTextChannelById(REPORTMODCHANNEL_ID).sendMessage(event.getJDA().getGuildChannelById(MODCHANNEL_ID).getGuild().getRolesByName(ModMail.roleName, true).get(0).getAsMention()).queue();
            event.getGuild().getTextChannelById(REPORTMODCHANNEL_ID).sendMessage(new EmbedBuilder()
                    .setAuthor(reporting.getUser().getName(), reporting.getUser().getAvatarUrl(), reporting.getUser().getEffectiveAvatarUrl())
                    .setTitle("[REPORT] " + beingReported.getUser().getName())
                    .addField("User Reporting:", reporting.getUser().getAsTag() + "\nID: " + reporting.getId(), true)
                    .addField("User being reported:", beingReported.getUser().getAsTag() + "\nID: " + beingReported.getId(), true)
                    .addField("Reason:", content.split(" ", 3)[2], true)
                    .addField("Channel", event.getChannel().getAsMention(), false)
                    .build()).queue();
            alreadyReported.put(beingReported.getIdLong(), System.currentTimeMillis());
            event.getChannel().sendMessage("Successfully reported to our mods!").queue(msg -> {
                msg.delete().queueAfter(5, TimeUnit.SECONDS);
            });
        } else if(content.startsWith("mm!report ") && content.split(" ").length >= 3) {
            try {
                long id = Long.parseLong(content.split(" ")[1]);
                event.getMessage().delete().queue();
                if(ModMailBan.blackListMembers.contains(event.getAuthor().getIdLong())) {
                    if(ModMailBan.timeouts.get(event.getAuthor().getIdLong()) < System.currentTimeMillis()) {
                        ModMailBan.blackListMembers.remove(event.getAuthor().getIdLong());
                        ModMailBan.timeouts.remove(event.getAuthor().getIdLong());
                        ModMailBan.reasons.remove(event.getAuthor().getIdLong());
                    } else {
                        event.getChannel().sendMessage("You have been temporarily banned from using the Mod Mail to contact the staff! Reason: " + ModMailBan.reasons.get(event.getAuthor().getIdLong())).queue();
                        return;
                    }
                }
                Member beingReported = event.getGuild().getMemberById(id);
                if(beingReported.getIdLong() == event.getMember().getIdLong()) {
                    event.getChannel().sendMessage("You can not report yourself to the mods!").queue(msg ->
                            msg.delete().queueAfter(5, TimeUnit.SECONDS)
                    );
                    return;
                }
                if(alreadyReported.containsKey(beingReported.getIdLong())) {
                    if(System.currentTimeMillis()-alreadyReported.get(beingReported.getIdLong())<600*1000) {
                        event.getChannel().sendMessage("This user has already been reported within the last 10 minutes! Thank you for your assistance in keeping the community fun!").queue(msg -> {
                            msg.delete().queueAfter(5, TimeUnit.SECONDS);
                        });
                        return;
                    }
                }
                Member reporting = event.getMember();
                event.getGuild().getTextChannelById(REPORTMODCHANNEL_ID).sendMessage(event.getJDA().getGuildChannelById(MODCHANNEL_ID).getGuild().getRolesByName(ModMail.roleName, true).get(0).getAsMention()).queue();
                event.getGuild().getTextChannelById(REPORTMODCHANNEL_ID).sendMessage(new EmbedBuilder()
                        .setAuthor(reporting.getUser().getName(), reporting.getUser().getAvatarUrl(), reporting.getUser().getEffectiveAvatarUrl())
                        .setTitle("[REPORT] " + beingReported.getUser().getName())
                        .addField("User Reporting:", reporting.getUser().getAsTag() + "\nID: " + reporting.getId(), true)
                        .addField("User being reported:", beingReported.getUser().getAsTag() + "\nID: " + beingReported.getId(), true)
                        .addField("Reason:", content.split(" ", 3)[2], true)
                        .addField("Channel", event.getChannel().getAsMention(), false)
                        .build()).queue();
                alreadyReported.put(beingReported.getIdLong(), System.currentTimeMillis());
                event.getChannel().sendMessage("Successfully reported to our mods!").queue(msg -> {
                    msg.delete().queueAfter(5, TimeUnit.SECONDS);
                });
            } catch(Exception e) {

            }
        } else if(content.startsWith("mm!report")) {
            event.getChannel().sendMessage("Wrong Command Usage! Be sure to ping the user you want to report!\nUsage: `mm!report [user] [reason]`").queue();
            return;
        }
    }
}
