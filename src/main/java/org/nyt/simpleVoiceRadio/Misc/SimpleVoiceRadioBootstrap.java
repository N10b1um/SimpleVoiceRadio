package org.nyt.simpleVoiceRadio.Misc;

import io.papermc.paper.plugin.bootstrap.BootstrapContext;
import io.papermc.paper.plugin.bootstrap.PluginBootstrap;
import io.papermc.paper.registry.RegistryKey;
import io.papermc.paper.registry.TypedKey;
import io.papermc.paper.registry.event.RegistryEvents;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.text.Component;
import org.bukkit.JukeboxSong;
import org.bukkit.Sound;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

public class SimpleVoiceRadioBootstrap implements PluginBootstrap {

    public static final Map<Integer, TypedKey<JukeboxSong>> SIGNAL_TO_SONG = new HashMap<>();

    @Override
    public void bootstrap(@NotNull BootstrapContext context) {
        Key vanillaSoundKey = Key.key("minecraft", "intentionally_empty");
        TypedKey<Sound> typedSoundKey = TypedKey.create(RegistryKey.SOUND_EVENT, vanillaSoundKey);

        context.getLifecycleManager().registerEventHandler(
                RegistryEvents.JUKEBOX_SONG.compose().newHandler(event -> {
                    for (int signal = 1; signal <= 15; signal++) {
                        Key songKey = Key.key("simple_voice_radio", "radio_signal_" + signal);
                        TypedKey<JukeboxSong> typedKey = TypedKey.create(RegistryKey.JUKEBOX_SONG, songKey);

                        int finalSignal = signal;
                        event.registry().register(
                                typedKey,
                                builder -> builder
                                        .lengthInSeconds(1f)
                                        .soundEvent(typedSoundKey)
                                        .description(Component.text("Radio Signal Level " + finalSignal))
                                        .comparatorOutput(finalSignal)
                        );

                        SIGNAL_TO_SONG.put(signal, typedKey);
                    }
                })
        );
    }
}