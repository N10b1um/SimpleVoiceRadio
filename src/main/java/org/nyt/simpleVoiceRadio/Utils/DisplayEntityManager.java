package org.nyt.simpleVoiceRadio.Utils;

import com.destroystokyo.paper.profile.PlayerProfile;
import com.destroystokyo.paper.profile.ProfileProperty;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Display;
import org.bukkit.entity.ItemDisplay;
import org.bukkit.entity.TextDisplay;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.util.Transformation;
import org.joml.Quaternionf;
import org.joml.Vector3f;
import org.nyt.simpleVoiceRadio.SimpleVoiceRadio;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class DisplayEntityManager {
    private final SimpleVoiceRadio plugin;

    public DisplayEntityManager(SimpleVoiceRadio plugin) {
        this.plugin = plugin;
    }

    public List<ItemDisplay> createItemDisplays(Location loc) {
        ArrayList<ItemDisplay> list = new ArrayList<>();
        for (int i = 1; i <= 8; i++) {
            ItemDisplay display = loc.getWorld().spawn(loc, ItemDisplay.class);
            ItemStack item = ItemStack.of(Material.PLAYER_HEAD);
            String skull_skin = plugin.getConfig().getString("radio-block.texture_parts." + i + ".skull_skin");
            getSkullByValue(skull_skin, item);

            display.setItemStack(item);
            display.setViewRange(512);
            display.setBrightness(new Display.Brightness(15,15));
            Vector3f translation = parseVector(plugin.getConfig().getString("radio-block.texture_parts." + i + ".translation"), new Vector3f(0, 0, 0));
            Vector3f scale = parseVector(plugin.getConfig().getString("radio-block.texture_parts." + i + ".scale"), new Vector3f(1.003f, 1.003f, 1.003f));
            updateTransformation(display, translation, null, scale, null);
            list.add(display);
        }
        return list;
    }

    public void setStateSkin(ItemDisplay display, String state) {
        String displaySkullSkin;
        if (state.equalsIgnoreCase("input")) {
            displaySkullSkin = plugin.getConfig().getString("radio-block.texture_parts.1.input_state");
        }
        else if (state.equalsIgnoreCase("broadcasting")) {
            displaySkullSkin = plugin.getConfig().getString("radio-block.texture_parts.3.broadcasting_state");
        }
        else {
            displaySkullSkin = plugin.getConfig().getString("radio-block.texture_parts.1.skull_skin");
        }
        ItemStack displayItem = display.getItemStack();
        getSkullByValue(displaySkullSkin, displayItem);
        display.setItemStack(displayItem);
    }


    public TextDisplay createTextDisplay(Location loc, int frequency) {
        TextDisplay display = loc.getWorld().spawn(loc, TextDisplay.class);
        display.text(Component.text(String.valueOf(frequency), NamedTextColor.DARK_RED));
        display.setBackgroundColor(Color.fromARGB(0,0,0,0));
        display.setViewRange(512);
        display.setBrightness(new Display.Brightness(15,15));
        Vector3f scale = new Vector3f(1.5f,1.435f,0);
        Vector3f translation = new Vector3f(-0.501f,-0.01f,-0.0185f);

        Quaternionf leftRot = new Quaternionf().rotateY((float) Math.toRadians(270));
        updateTransformation(display, translation, leftRot, scale, null);

        return display;
    }

    public void getSkullByValue(String base64, ItemStack item) {
        if (base64 == null || base64.isEmpty()) {
            SimpleVoiceRadio.LOGGER.warn("Skull skin is null or empty");
            return;
        }

        SkullMeta meta = (SkullMeta) item.getItemMeta();
        if (meta != null) {
            try {
                UUID uuid = new UUID(base64.hashCode(), base64.hashCode());
                PlayerProfile profile = Bukkit.createProfile(uuid);

                ProfileProperty property = new ProfileProperty("textures", base64);
                profile.getProperties().clear();
                profile.getProperties().add(property);

                meta.setPlayerProfile(profile);
                item.setItemMeta(meta);
            }
            catch (Exception e) {
                SimpleVoiceRadio.LOGGER.error("Failed to parse skull skin: " + e.getMessage());
            }
        }
    }

    public void updateTransformation(Display display, Vector3f translation, Quaternionf leftRotation, Vector3f scale, Quaternionf rightRotation) {
        if (display == null) { return; }

        Transformation oldTrans = display.getTransformation();
        Transformation newTrans = new Transformation(
                translation != null ? translation : oldTrans.getTranslation(),
                leftRotation != null ? leftRotation : oldTrans.getLeftRotation(),
                scale != null ? scale : oldTrans.getScale(),
                rightRotation != null ? rightRotation : oldTrans.getRightRotation()
        );
        display.setTransformation(newTrans);
    }

    private Vector3f parseVector(String s, Vector3f defaultVec) {
        if (s == null || s.isEmpty()) return defaultVec;
        try {
            String[] parts = s.split(",");
            if (parts.length != 3) return defaultVec;
            float x = Float.parseFloat(parts[0]);
            float y = Float.parseFloat(parts[1]);
            float z = Float.parseFloat(parts[2]);
            return new Vector3f(x, y, z);
        } catch (Exception e) {
            return defaultVec;
        }
    }

}


