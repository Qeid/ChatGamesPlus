package gg.minecrush.reactions;

import gg.minecrush.reactions.command.ReactionCommand;
import gg.minecrush.reactions.command.ReactionTabComplete;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Random;
import java.util.logging.Logger;

public class Reactions extends JavaPlugin {

    private ReactionManager reactionManager;
    private static final Logger LOGGER = Logger.getLogger(Reactions.class.getName());
    private int automaticReactionsInterval;

    @Override
    public void onEnable() {
        LOGGER.info("Reactions plugin is enabling...");
        try {
            this.saveDefaultConfig();
            reactionManager = new ReactionManager(this);
            this.automaticReactionsInterval = this.getConfig().getInt("automaticReactionsInterval", 3);
            this.getCommand("reaction").setExecutor(new ReactionCommand(reactionManager, this));
            this.getCommand("reaction").setTabCompleter(new ReactionTabComplete());
            this.getServer().getPluginManager().registerEvents(new ReactionListener(reactionManager), this);
            if (this.getConfig().getBoolean("automaticReactionsEnabled", true)) {
                scheduleAutomaticReactions();
            }
            reactionManager.reset_Reaction();
            LOGGER.info("Reactions plugin enabled successfully!");
        } catch (Exception e) {
            LOGGER.severe("Failed to enable Reactions plugin: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private static final String[] REACTION_TYPES = {"math", "scramble", "fastest"};

    private void scheduleAutomaticReactions() {

        new BukkitRunnable() {
            @Override
            public void run() {
                Random random = new Random();
                String type = REACTION_TYPES[random.nextInt(REACTION_TYPES.length)];
                reactionManager.startReaction(type);
            }
        }.runTaskTimer(this, 0, 20 * 60 * automaticReactionsInterval); // Convert minutes to ticks
    }

    @Override
    public void onDisable() {
        LOGGER.info("Reactions plugin is disabling...");
    }

    public Reactions() {
    }

    public void onReload() {
        this.reloadConfig();
        this.automaticReactionsInterval = this.getConfig().getInt("automaticReactionsInterval", 3); // Update the automaticReactionsInterval value
        this.reactionManager = new ReactionManager(this);
        this.getCommand("reaction").setExecutor(new ReactionCommand(reactionManager, this));
        this.getServer().getPluginManager().registerEvents(new ReactionListener(reactionManager), this);
    }
}
