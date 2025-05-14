package discord.qeid.chatgamesplus.util;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import net.md_5.bungee.api.ChatColor;

public class color {
    // Precompile the pattern for hex color codes
    private static final Pattern HEX_PATTERN = Pattern.compile("<##([A-Fa-f0-9]{6})>");

    public static String c(String message) {
        Matcher matcher = HEX_PATTERN.matcher(message);
        StringBuilder buffer = new StringBuilder();

        int lastEnd = 0;
        while (matcher.find()) {
            buffer.append(message, lastEnd, matcher.start());
            buffer.append(hex(matcher.group(1)));
            lastEnd = matcher.end();
        }
        buffer.append(message, lastEnd, message.length());

        return ChatColor.translateAlternateColorCodes('&', buffer.toString());
    }

    public static ArrayList<String> cc(String... texts) {
        ArrayList<String> a = new ArrayList<>();
        for (String text : texts)
            a.add(c(text));
        return a;
    }

    public static String noColor(String message) {
        return ChatColor.stripColor(c(message));
    }

    public static String hex(String hexCode) {
        try {
            return ChatColor.of("#" + hexCode).toString();
        } catch (Exception e) {
            return null;
        }
    }
}


