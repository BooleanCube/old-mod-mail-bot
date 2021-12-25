package KrisModMailBot;

import bot.Tools;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import javax.annotation.Nonnull;
import java.util.HashMap;
import java.util.HashSet;

import static KrisModMailBot.ModMail.MODCHANNEL_ID;

public class ModMailBan extends ListenerAdapter {

    public static HashSet<Long> blackListMembers = new HashSet<>();
    public static HashMap<Long, Long> timeouts = new HashMap<>();
    public static HashMap<Long, String> reasons = new HashMap<>();

    @Override
    public void onGuildMessageReceived(@Nonnull GuildMessageReceivedEvent event) {
        String msg = event.getMessage().getContentRaw();
        msg.replaceAll("<@!", "").replaceAll("<@", "").replaceAll(">", "");
        if(event.getMember() == null) {
            return;
        }
        if(!event.getMember().hasPermission(Permission.BAN_MEMBERS)) {
            return;
        }
        if(msg.toLowerCase().startsWith("mm!ban")) {
            long userId = 0;
            int minutes = 0;
            if(msg.split(" ").length < 3) {
                event.getChannel().sendMessage("Wrong Command Usage!\nUsage: `mm!ban [User ID] [# of minutes] [reason]`").queue();
                return;
            }
            try {
                userId = Long.parseLong(msg.split(" ")[1]);
                minutes = Integer.parseInt(msg.split(" ")[2]);
                String reason = "";
                if(msg.split(" ").length > 3) {
                    reason = msg.split(" ", 4)[3];
                } else {
                    reason = "unspecified reason";
                }
                if(blackListMembers.contains(userId) && System.currentTimeMillis()-timeouts.get(userId)<minutes*60*1000) {
                    event.getChannel().sendMessage("This user has already been temporarily banned! Reason: " + ModMailBan.reasons.get(userId)).queue();
                    return;
                }
                Member m = null;
                try {
                    m = event.getGuild().getMemberById(userId);
                } catch(Exception e) {
                    event.getChannel().sendMessage("User with ID, **" + userId + "**, was not found in this server!").queue();
                    return;
                }
                blackListMembers.add(userId);
                timeouts.put(userId, System.currentTimeMillis()+(minutes*60*1000));
                reasons.put(userId, reason);
                event.getChannel().sendMessage("Successfully banned " + event.getGuild().getMemberById(userId).getUser().getName() + " from using mod mail for " + minutes + " minute(s).").queue();
                event.getGuild().getTextChannelById(MODCHANNEL_ID).sendMessage(new EmbedBuilder()
                        .setTitle("[BAN] " + m.getUser().getName())
                        .addField("Banned Member:", m.getAsMention(), true)
                        .addField("Reason:", reason, true)
                        .addField("Duration:", Tools.secondsToTime(minutes*60), true)
                        .build()
                ).queue();
            } catch(Exception e) {
                e.printStackTrace();
                event.getChannel().sendMessage("Wrong Command Usage!\nUsage: `mm!ban [User ID] [# of minutes] [reason]`").queue();
                return;
            }
        } else if(msg.toLowerCase().startsWith("mm!unban")) {
            long userId = 0;
            if(msg.split(" ").length != 2) {
                event.getChannel().sendMessage("Wrong Command Usage!\nUsage: `mm!unban [User ID]`").queue();
                return;
            }
            try {
                userId = Long.parseLong(msg.split(" ")[1]);
                Member m = null;
                try {
                    m = event.getGuild().getMemberById(userId);
                } catch(Exception e) {
                    event.getChannel().sendMessage("User with ID, **" + userId + "**, was not found in this server!").queue();
                    return;
                }
                if(!blackListMembers.contains(userId)) {
                    event.getChannel().sendMessage("This user has not been banned!").queue();
                    return;
                }
                blackListMembers.remove(userId);
                timeouts.remove(event.getAuthor().getIdLong());
                String reason = reasons.get(userId);
                reasons.remove(userId);
                event.getChannel().sendMessage("Successfully unbanned " + event.getGuild().getMemberById(userId).getUser().getName() + " from using mod mail!").queue();
                event.getGuild().getTextChannelById(MODCHANNEL_ID).sendMessage(new EmbedBuilder()
                        .setTitle("[UNBAN] " + m.getUser().getName())
                        .addField("Unbanned Member:", m.getAsMention(), true)
                        .addField("Reason for ban:", reason, true)
                        .build()
                ).queue();
                return;
            } catch(Exception e) {
                event.getChannel().sendMessage("Wrong Command Usage!\nUsage: `mm!unban [User ID]`").queue();
                return;
            }
        }
    }
}
