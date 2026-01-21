package org.nyt.simpleVoiceRadio.Utils;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;

public class MiniMessageSerializer {

    private static final MiniMessage MINI = MiniMessage.miniMessage();
    private static final LegacyComponentSerializer LEGACY = LegacyComponentSerializer.legacyAmpersand();

    public static Component parse(String text) {
        Component miniComponent = MINI.deserialize(text);

        String serialized = MINI.serialize(miniComponent);
        Component legacyComponent = LEGACY.deserialize(serialized);

        return miniComponent.children().isEmpty() && serialized.contains("&") ? legacyComponent : miniComponent;
    }
}