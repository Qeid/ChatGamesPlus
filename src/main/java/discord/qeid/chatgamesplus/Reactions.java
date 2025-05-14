package discord.qeid.chatgamesplus;

import discord.qeid.chatgamesplus.command.ReactionCommand;
import discord.qeid.chatgamesplus.command.ReactionTabComplete;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Random;
import java.util.logging.Logger;

public class Reactions extends JavaPlugin {

    private ReactionManager reactionManager;
    private static final Logger LOGGER = Logger.getLogger(Reactions.class.getName());
    private int automaticReactionsInterval;

    final String RESET = "\u001B[0m";
    final String YELLOW = "\u001B[33m";
    final String GREEN = "\u001B[32m";
    final String CYAN = "\u001B[36m";
    final String BOLD = "\u001B[1m";

    @Override
    public void onEnable() {
        System.out.println(YELLOW + "Attempting to load ChatGamesPlus (v" + getDescription().getVersion() + ")");
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
            String banner = GREEN + BOLD + "ChatGamesPlus (v" + getDescription().getVersion() + ") loaded successfully." + RESET;

            System.out.println(banner);
        } catch (Exception e) {
            LOGGER.severe("Failed to enable ChatGamesPlus plugin: " + e.getMessage());
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
        LOGGER.info(CYAN + "ChatGamesPlus is now disabling. Good bye!");
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
