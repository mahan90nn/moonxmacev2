package com.moonx.macev2;

import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import net.kyori.adventure.text.minimessage.tag.resolver.PlaceholderResolver;

import java.util.regex.Pattern;

public final class MiniMessageUtil {

    private static final Pattern PLACEHOLDER_PATTERN = Pattern.compile("\\{([a-zA-Z0-9_]+)\\}");

    private final MiniMessage miniMessage;
    private final boolean parse;

    public MiniMessageUtil(boolean parse) {
        this.miniMessage = MiniMessage.miniMessage();
        this.parse = parse;
    }

    public net.kyori.adventure.text.Component deserialize(String text, TagResolver... resolvers) {
        if (!parse) {
            // Strip MiniMessage tags and placeholders, return plain text
            String stripped = stripTags(stripPlaceholders(text));
            return net.kyori.adventure.text.Component.text(stripped);
        }
        return miniMessage.deserialize(text, resolvers);
    }

    public String serialize(net.kyori.adventure.text.Component component) {
        return miniMessage.serialize(component);
    }

    private String stripPlaceholders(String text) {
        return PLACEHOLDER_PATTERN.matcher(text).replaceAll("$1");
    }

    private String stripTags(String text) {
        // Remove common MiniMessage tags like <gold>, </gold>, <red>, etc.
        return text.replaceAll("<[^>]+>", "");
    }
}