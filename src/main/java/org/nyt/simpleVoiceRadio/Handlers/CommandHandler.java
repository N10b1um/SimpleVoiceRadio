package org.nyt.simpleVoiceRadio.Handlers;

import io.papermc.paper.command.brigadier.BasicCommand;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.nyt.simpleVoiceRadio.Misc.Item;
import org.nyt.simpleVoiceRadio.SimpleVoiceRadio;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class CommandHandler implements BasicCommand {
    private final SimpleVoiceRadio plugin;
    private final Item item;

    public CommandHandler(SimpleVoiceRadio plugin, Item item) {
        this.plugin = plugin;
        this.item = item;
    }

    @Override
    public void execute(@NotNull CommandSourceStack stack, @NotNull String[] args) {
        CommandSender sender = stack.getSender();

        if (args.length == 0) {
            sender.sendMessage(Component.text("Usage: /simple_voice_radio <reload|give>", NamedTextColor.RED));
            return;
        }

        String arg = args[0];

        if (arg.equalsIgnoreCase("reload")) {
            if (!sender.hasPermission("simple_voice_radio.reload")) {
                sender.sendMessage(Component.text("You don't have permission to use this command!", NamedTextColor.RED));
                return;
            }

            try {
                plugin.reloadConfig();
                item.reloadCraft();
                sender.sendMessage(Component.text("Config has been reloaded!", TextColor.fromHexString("#f5cb4e")));
            } catch (Exception e) {
                sender.sendMessage(Component.text("Failed to reload config!", NamedTextColor.RED));
                SimpleVoiceRadio.LOGGER.error("Failed to reload config", e);
            }

        } else if (arg.equalsIgnoreCase("give")) {
            if (!(sender instanceof Player player)) {
                sender.sendMessage(Component.text("Only players can use this command!", NamedTextColor.RED));
                return;
            }

            if (!player.hasPermission("simple_voice_radio.give")) {
                sender.sendMessage(Component.text("You don't have permission to use this command!", NamedTextColor.RED));
                return;
            }

            ItemStack radioItem = this.item.getItem();
            player.getInventory().addItem(radioItem);
            player.sendMessage(Component.text("Radio has been given!", TextColor.fromHexString("#f5cb4e")));
        } else {
            sender.sendMessage(Component.text("Usage: /simple_voice_radio <reload|give>", NamedTextColor.RED));
        }
    }

    @Override
    public @NotNull Collection<String> suggest(@NotNull CommandSourceStack stack, @NotNull String[] args) {
        if (args.length <= 1) {
            List<String> suggestions = new ArrayList<>();
            if (stack.getSender().hasPermission("simple_voice_radio.reload")) {
                suggestions.add("reload");
            }
            if (stack.getSender().hasPermission("simple_voice_radio.give")) {
                suggestions.add("give");
            }
            return suggestions;
        }
        return List.of();
    }
}