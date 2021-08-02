package fr.apogeecraft.listeners.jdaListener;

import fr.apogeecraft.Core;
import fr.apogeecraft.utils.Lag;
import net.dv8tion.jda.api.EmbedBuilder;
import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitRunnable;
import java.text.DecimalFormat;
import java.text.Format;
import java.util.*;

public class JDAExecutor {
    private Core plugin;
    public JDAExecutor(Core plugin){
        this.plugin = plugin;
        stats();
    }

    private void stats(){
        new BukkitRunnable() {
            @Override
            public void run() {
                plugin.getJda().getCategoryById("870529253635158016").getManager().setName(">>\uD835\uDC09\uD835\uDC28\uD835\uDC2E\uD835\uDC1E\uD835\uDC2E\uD835\uDC2B\uD835\uDC2C \uD835\uDC02\uD835\uDC28\uD835\uDC27\uD835\uDC27\uD835\uDC1E\uD835\uDC1C\uD835\uDC2D\uD835\uDC1E\uD835\uDC2C : "+ Bukkit.getServer().getOnlinePlayers().size()+" <<").queue();

            }
        }.runTaskTimerAsynchronously(plugin,0,12000);

        new BukkitRunnable() {
            @Override
            public void run() {
                EmbedBuilder ebe = new EmbedBuilder();
                Calendar cal = Calendar.getInstance();
                int hour = cal.getTime().getHours()+2;
                int minutes = cal.getTime().getMinutes();
                ebe.setTitle("Stats:");
                Format format = new DecimalFormat("#0.00");
                //eb.setTitle("Stats");
                ebe.setDescription("\nConnectés: ```yaml\n"+ Bukkit.getServer().getOnlinePlayers().size()
                        +"```\ntps: ```yaml\n"+ format.format(Lag.getTPS())+
                         "```");
                ebe.setFooter("Mis a jour à "+ hour+":"+minutes);
                Objects.requireNonNull(plugin.getJda().getTextChannelById("870663275241689088")).editMessageEmbedsById("870728228774490113", ebe.build()).queue();
            }
        }.runTaskTimerAsynchronously(plugin,0,200);
    }
}
