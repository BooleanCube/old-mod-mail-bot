package KrisModMailBot;

import bot.Tools;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.ChannelType;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.events.message.priv.PrivateMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import javax.annotation.Nonnull;
import java.awt.*;
import java.util.HashMap;
import java.util.HashSet;
import java.util.concurrent.TimeUnit;

public class ModMail extends ListenerAdapter {

    public static HashMap<Long, long[]> autoModCheck = new HashMap<>();
    public static HashSet<String> openedTickets = new HashSet<>();
    public static long MODCHANNEL_ID = 739977228045254777l;
    public static long REPORTMODCHANNEL_ID = 739977197757923359l;
    public static String roleName = "Moderators";

    @Override
    public void onPrivateMessageReceived(@Nonnull PrivateMessageReceivedEvent event) {
        if (event.getAuthor().isBot()) {
            return;
        }
        if (ModMailBan.blackListMembers.contains(event.getAuthor().getIdLong())) {
            if (ModMailBan.timeouts.get(event.getAuthor().getIdLong()) < System.currentTimeMillis()) {
                ModMailBan.blackListMembers.remove(event.getAuthor().getIdLong());
                ModMailBan.timeouts.remove(event.getAuthor().getIdLong());
                ModMailBan.reasons.remove(event.getAuthor().getIdLong());
            } else {
                event.getChannel().sendMessage("You have been temporarily banned from using the Mod Mail to contact the staff! Reason: " + ModMailBan.reasons.get(event.getAuthor().getIdLong())).queue();
                return;
            }
        }
        if (autoModCheck.containsKey(event.getAuthor().getIdLong())) {
            long numberOfMessages = autoModCheck.get(event.getAuthor().getIdLong())[0];
            long firstMessageTime = autoModCheck.get(event.getAuthor().getIdLong())[1];
            if (numberOfMessages == 10 && System.currentTimeMillis() - firstMessageTime <= 15000) {
                if (ModMailBan.blackListMembers.contains(event.getAuthor().getIdLong())) {
                    return;
                }
                ModMailBan.blackListMembers.add(event.getAuthor().getIdLong());
                ModMailBan.timeouts.put(event.getAuthor().getIdLong(), System.currentTimeMillis() + (60 * 60 * 1000));
                ModMailBan.reasons.put(event.getAuthor().getIdLong(), "Spamming");
                autoModCheck.remove(event.getAuthor().getIdLong());
                event.getChannel().sendMessage("You have been temporarily banned from using mod mail to contact the staff!").queue();
                if(!event.getJDA().getGuildChannelById(MODCHANNEL_ID).getGuild().getTextChannelsByName(event.getAuthor().getAsTag().replaceAll("#", "").replaceAll(" ", "-"), true).isEmpty()) {
                    event.getJDA().getGuildChannelById(MODCHANNEL_ID).getGuild().getTextChannelsByName(event.getAuthor().getAsTag().replaceAll("#", "").replaceAll(" ", "-"), true).get(0).sendMessage("Closing this ticket because Member received a ban for 1 hour! In case you want to unban, Member ID: **" + event.getAuthor().getIdLong() + "**").queue();
                    event.getJDA().getGuildChannelById(MODCHANNEL_ID).getGuild().getTextChannelsByName(event.getAuthor().getAsTag().replaceAll("#", "").replaceAll(" ", "-"), true).get(0).delete().queueAfter(10, TimeUnit.SECONDS);
                    openedTickets.remove(event.getAuthor().getId());
                    event.getJDA().getGuildChannelById(MODCHANNEL_ID).getGuild().getTextChannelById(MODCHANNEL_ID).sendMessage(new EmbedBuilder()
                            .setTitle("[BAN] " + event.getAuthor().getName())
                            .addField("Banned Member:", event.getAuthor().getAsMention(), true)
                            .addField("Reason:", "Spamming", true)
                            .addField("Duration:", "1 hour", true)
                            .build()
                    ).queue();
                }
                return;
            } else if (System.currentTimeMillis() - firstMessageTime > 15000) {
                autoModCheck.remove(event.getAuthor().getIdLong());
                autoModCheck.put(event.getAuthor().getIdLong(), new long[]{1, System.currentTimeMillis()});
            } else {
                autoModCheck.remove(event.getAuthor().getIdLong());
                autoModCheck.put(event.getAuthor().getIdLong(), new long[]{numberOfMessages + 1, firstMessageTime});
            }
        } else {
            autoModCheck.remove(event.getAuthor().getIdLong());
            autoModCheck.put(event.getAuthor().getIdLong(), new long[]{1, System.currentTimeMillis()});
        }
        if (openedTickets.contains(event.getAuthor().getId())) {
            EmbedBuilder e = new EmbedBuilder()
                    .setAuthor(event.getAuthor().getName(), event.getAuthor().getAvatarUrl(), event.getAuthor().getEffectiveAvatarUrl())
                    .setColor(Color.GREEN)
                    .setFooter("Use mm!close to close this ticket!")
                    .setDescription(event.getMessage().getContentRaw());
            event.getJDA().getGuildChannelById(MODCHANNEL_ID).getGuild().getTextChannelsByName(event.getAuthor().getAsTag().replaceAll("#", "").replaceAll(" ", "-"), true).get(0).sendMessage(e.build()).queue();
            event.getChannel().sendMessage("Successfully contacted our staff!").queue(msg ->
                    msg.delete().queueAfter(5, TimeUnit.SECONDS)
            );
        } else {
            EmbedBuilder e = new EmbedBuilder()
                    .setAuthor(event.getAuthor().getName(), event.getAuthor().getAvatarUrl(), event.getAuthor().getEffectiveAvatarUrl())
                    .setColor(Color.GREEN)
                    .setDescription(event.getMessage().getContentRaw());
            event.getJDA().getGuildChannelById(MODCHANNEL_ID).getGuild().getCategoriesByName("ModMail", true).get(0).createTextChannel(event.getAuthor().getAsTag()).setTopic(event.getAuthor().getId()).complete().sendMessage(new EmbedBuilder()
                    .setTitle("A ticket has been opened!")
                    .setDescription(event.getAuthor().getAsMention() + " has opened a ticket!")
                    .setFooter("Use mm!close to close this ticket!")
                    .setColor(Color.GREEN).build()).queue();
            event.getJDA().getGuildChannelById(MODCHANNEL_ID).getGuild().getTextChannelsByName(event.getAuthor().getAsTag().replaceAll("#", "").replaceAll(" ", "-"), true).get(0).sendMessage(e.build()).queue();
            EmbedBuilder newTicket = new EmbedBuilder()
                    .setTitle("A ticket has been opened!")
                    .setFooter("Use mm!close to close the ticket!")
                    .setDescription(event.getAuthor().getAsMention() + " has opened a ticket! Go to this channel to help them out -> " + event.getJDA().getGuildChannelById(MODCHANNEL_ID).getGuild().getTextChannelsByName(event.getAuthor().getAsTag().replaceAll("#", "").replaceAll(" ", "-"), true).get(0).getAsMention())
                    .setColor(Color.GREEN);
            event.getChannel().sendMessage(new EmbedBuilder()
                    .setTitle("A ticket has been opened!")
                    .setDescription("You have opened a ticket! The staff will get back to you shortly!")
                    .setColor(Color.GREEN).build()).queue();
            openedTickets.add(event.getAuthor().getId());
            event.getJDA().getGuildChannelById(MODCHANNEL_ID).getGuild().getTextChannelById(MODCHANNEL_ID).sendMessage(event.getJDA().getGuildChannelById(MODCHANNEL_ID).getGuild().getRolesByName(roleName, true).get(0).getAsMention()).queue();
            event.getJDA().getGuildChannelById(MODCHANNEL_ID).getGuild().getTextChannelById(MODCHANNEL_ID).sendMessage(newTicket.build()).queue();
            event.getChannel().sendMessage("Successfully contacted our staff!").queue(msg ->
                    msg.delete().queueAfter(5, TimeUnit.SECONDS)
            );
        }
    }

    @Override
    public void onGuildMessageReceived(@Nonnull GuildMessageReceivedEvent event) {
        String rawMessage = event.getMessage().getContentRaw();
        if (event.getAuthor().isBot()) {
            return;
        }
        if (rawMessage.equalsIgnoreCase("mm!close") && openedTickets.contains(event.getChannel().getTopic())) {
            event.getChannel().sendMessage("Closing this ticket...").queue();
            Member m = event.getGuild().getMemberById(event.getChannel().getTopic());
            event.getChannel().delete().queueAfter(2, TimeUnit.SECONDS);
            openedTickets.remove(event.getChannel().getTopic());
            m.getUser().openPrivateChannel().queue(channel ->
                    channel.sendMessage(new EmbedBuilder()
                            .setTitle("Ticket Closed!")
                            .setDescription("This ticket has been closed by " + event.getAuthor().getAsMention() + "!")
                            .setColor(Color.RED)
                            .build()).queue()
            );
            event.getJDA().getGuildChannelById(MODCHANNEL_ID).getGuild().getTextChannelById(MODCHANNEL_ID).sendMessage(new EmbedBuilder()
                    .setTitle("Ticket Closed!")
                    .setDescription(m.getUser().getAsMention() + "'s ticket has been closed by " + event.getAuthor().getAsMention() + "!")
                    .setColor(Color.RED)
                    .build()
            ).queue();
            return;
        } else if (!openedTickets.contains(event.getChannel().getTopic()) && rawMessage.equalsIgnoreCase("mm!close")) {
            event.getChannel().sendMessage("This command can not be used in this channel!").queue();
            return;
        }
        if (openedTickets.contains(event.getChannel().getTopic())) {
            Member m = event.getGuild().getMemberById(event.getChannel().getTopic());
            EmbedBuilder e = new EmbedBuilder()
                    .setAuthor(event.getAuthor().getName(), event.getAuthor().getAvatarUrl(), event.getAuthor().getEffectiveAvatarUrl())
                    .setColor(Color.GREEN)
                    .setDescription(rawMessage);
            m.getUser().openPrivateChannel().queue((channel) -> {
                channel.sendMessage(e.build()).queue();
            });
            event.getChannel().sendMessage("Successfully contacted the user " + m.getUser().getAsMention()).queue(msg ->
                    msg.delete().queueAfter(5, TimeUnit.SECONDS)
            );
        }
    }
    public static boolean isNumeric(String str) {
        try {
            Long.parseLong(str);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }
}
