package discord.qeid.chatgamesplus;

import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import discord.qeid.chatgamesplus.util.color;

import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

public class ReactionManager {

    private final JavaPlugin plugin;
    private String currentAnswer;
    private long reactionStartTime;
    private boolean reactionActive;


    public String center(String message) {
        int chatWidth = 53;

        int messageLength = ChatColor.stripColor(message).length();
        int spacesNeeded = (chatWidth - messageLength) / 2;

        StringBuilder centeredMessage = new StringBuilder();
        for (int i = 0; i < spacesNeeded; i++) {
            centeredMessage.append(" ");
        }
        centeredMessage.append(message);

        return centeredMessage.toString();
    }

    public ReactionManager(JavaPlugin plugin) {
        this.plugin = plugin;
        this.reactionActive = false;
    }

    public void reset_Reaction(){
        this.reactionActive = false;
    }

    public boolean isReactionActive(){
        return reactionActive;
    }

    public boolean isAnswer(String message) {
        return currentAnswer != null && currentAnswer.equalsIgnoreCase(message);
    }

    public void handleCorrectAnswer(Player player, String message) {
        long timeTaken = System.currentTimeMillis() - reactionStartTime;
        Bukkit.getScheduler().runTask(plugin, () -> {
            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), plugin.getConfig().getString("options.reward-command").replace("%player%", player.getName()));
        });
        String timeFormatted = String.format("%.2f", timeTaken / 1000.0);
        Bukkit.broadcastMessage("");
        Bukkit.broadcastMessage(center(getMessage("chat-game-title")));
        Bukkit.broadcastMessage("");
        Bukkit.broadcastMessage(center(getMessage("player-answer").replace("%player%", player.getName()).replace("%message%", message).replace("%time%", timeFormatted)));
        Bukkit.broadcastMessage(center(getMessage("reward-message").replace("%reward%", plugin.getConfig().getString("options.reward"))));
        Bukkit.broadcastMessage("");
        currentAnswer = null;
        reactionActive = false;
    }

    public void startReaction(String type) {
        if (reactionActive) {
            //
            return;
        }
        reactionActive = true;

        if ("math".equalsIgnoreCase(type)) {
            startMathReaction();
        } else if ("scramble".equalsIgnoreCase(type)) {
            startScrambleReaction();
        } else if ("fastest".equalsIgnoreCase(type)) {
            startFastestReaction();
        }

        reactionStartTime = System.currentTimeMillis();
        Bukkit.getScheduler().runTaskLater(plugin, this::checkreaction, 600L);     }

    private void checkreaction() {
        if (reactionActive != false) {
            this.endReaction();
        }
    }

    private String getMessage(String key) {
        return color.c(plugin.getConfig().getString("messages." + key));
    }

    private void startMathReaction() {
        Random random = new Random();
        int min = plugin.getConfig().getInt("options.mathMin");
        int max = plugin.getConfig().getInt("options.mathMax");
        int num1 = random.nextInt(max - min + 1) + min;
        int num2 = random.nextInt(max - min + 1) + min;
        currentAnswer = String.valueOf(num1 + num2);
        Bukkit.broadcastMessage("");
        Bukkit.broadcastMessage(center(getMessage("chat-game-title")));
        Bukkit.broadcastMessage("");
        Bukkit.broadcastMessage(center(getMessage("math-question").replace("%num1%", String.valueOf(num1)).replace("%num2%", String.valueOf(num2))));
        Bukkit.broadcastMessage("");
    }

    private void startScrambleReaction() {
        List<String> words = plugin.getConfig().getStringList("reaction.words");
        if (words.isEmpty()) {
            return;
        }
        String word = words.get(new Random().nextInt(words.size()));
        currentAnswer = word;
        Bukkit.broadcastMessage("");
        Bukkit.broadcastMessage(center(getMessage("chat-game-title")));
        Bukkit.broadcastMessage("");
        Bukkit.broadcastMessage(center(getMessage("scramble-question").replace("%word%", scrambleWord(word))));
        Bukkit.broadcastMessage("");
    }

    private void startFastestReaction() {
        List<String> words = plugin.getConfig().getStringList("reaction.words");
        if (words.isEmpty()) {
            return;
        }
        String word = words.get(new Random().nextInt(words.size()));
        currentAnswer = word;
        Bukkit.broadcastMessage("");
        Bukkit.broadcastMessage(center(getMessage("chat-game-title")));
        Bukkit.broadcastMessage("");
        Bukkit.broadcastMessage(center(getMessage("fastest-question").replace("%word%", word)));
        Bukkit.broadcastMessage("");
    }

    private String scrambleWord(String word) {
        List<Character> characters = word.chars().mapToObj(e -> (char) e).collect(Collectors.toList());
        Collections.shuffle(characters);
        StringBuilder scrambled = new StringBuilder();
        for (char ch : characters) {
            scrambled.append(ch);
        }
        return scrambled.toString();
    }

    public void endReaction() {
        if (reactionActive) {
            Bukkit.broadcastMessage("");
            Bukkit.broadcastMessage(center(getMessage("chat-game-title")));
            Bukkit.broadcastMessage("");
            Bukkit.broadcastMessage(center(getMessage("chat-game-no-answer")));
            Bukkit.broadcastMessage("");
            currentAnswer = null;
            reactionActive = false;
        }
    }

    public void saveConfig() {
        plugin.saveConfig();
    }
}