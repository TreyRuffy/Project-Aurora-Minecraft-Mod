package wibble.mods.auroragsi;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.options.ControlsOptionsScreen;
import net.minecraft.client.gui.screen.ChatScreen;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.options.KeyBinding;
import net.minecraft.client.render.SkyProperties;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.effect.StatusEffect;

/** Container for the Provider data and Player data. */
public class GSINode {
    private ProviderNode provider = new ProviderNode();
    private WorldNode world = new WorldNode();
    private PlayerNode player = new PlayerNode();
    private GameNode game = new GameNode();

    public GSINode update() {
        world.update();
        player.update();
        game.update();
        return this;
    }

    /**
     * Contains data required for Aurora to be able to parse the JSON data.
     */
    private static class ProviderNode {
        private String name = "minecraft";
        private int appid = -1;
    }

    /**
     * Contains the data extracted from the game about the player.
     */
    private static class PlayerNode {
        private boolean inGame;
        private float health;
        private float maxHealth;
        private float absorption;
        private boolean isDead;
        private int armor;
        private int experienceLevel;
        private float experience;
        private int foodLevel;
        private float saturationLevel;
        private boolean isSneaking;
        private boolean isRidingHorse;
        private boolean isBurning;
        private boolean isInWater;
        private HashMap<String, Boolean> playerEffects = new HashMap<>();

        // Potion effects that will be added to the playerEffects map.
        private static final HashMap<String, StatusEffect> TARGET_EFFECTS;
        static {
            TARGET_EFFECTS = new HashMap<>();
            TARGET_EFFECTS.put("moveSpeed", StatusEffect.byRawId(1));
            TARGET_EFFECTS.put("moveSlowdown", StatusEffect.byRawId(2));
            TARGET_EFFECTS.put("haste", StatusEffect.byRawId(3));
            TARGET_EFFECTS.put("miningFatigue", StatusEffect.byRawId(4));
            TARGET_EFFECTS.put("strength", StatusEffect.byRawId(5));
            //TARGET_EFFECTS.put("instantHealth", StatusEffect.byRawId(6));
            //TARGET_EFFECTS.put("instantDamage", StatusEffect.byRawId(7));
            TARGET_EFFECTS.put("jumpBoost", StatusEffect.byRawId(8));
            TARGET_EFFECTS.put("confusion", StatusEffect.byRawId(9));
            TARGET_EFFECTS.put("regeneration", StatusEffect.byRawId(10));
            TARGET_EFFECTS.put("resistance", StatusEffect.byRawId(11));
            TARGET_EFFECTS.put("fireResistance", StatusEffect.byRawId(12));
            TARGET_EFFECTS.put("waterBreathing", StatusEffect.byRawId(13));
            TARGET_EFFECTS.put("invisibility", StatusEffect.byRawId(14));
            TARGET_EFFECTS.put("blindness", StatusEffect.byRawId(15));
            TARGET_EFFECTS.put("nightVision", StatusEffect.byRawId(16));
            TARGET_EFFECTS.put("hunger", StatusEffect.byRawId(17));
            TARGET_EFFECTS.put("weakness", StatusEffect.byRawId(18));
            TARGET_EFFECTS.put("poison", StatusEffect.byRawId(19));
            TARGET_EFFECTS.put("wither", StatusEffect.byRawId(20));
            //TARGET_EFFECTS.put("healthBoost", StatusEffect.byRawId(21));
            TARGET_EFFECTS.put("absorption", StatusEffect.byRawId(22));
            //TARGET_EFFECTS.put("saturation", StatusEffect.byRawId(23));
            TARGET_EFFECTS.put("glowing", StatusEffect.byRawId(24));
            TARGET_EFFECTS.put("levitation", StatusEffect.byRawId(25));
            TARGET_EFFECTS.put("luck", StatusEffect.byRawId(26));
            TARGET_EFFECTS.put("badLuck", StatusEffect.byRawId(27));
            TARGET_EFFECTS.put("slowFalling", StatusEffect.byRawId(28));
            TARGET_EFFECTS.put("conduitPower", StatusEffect.byRawId(29));
            TARGET_EFFECTS.put("dolphinsGrace", StatusEffect.byRawId(30));
            TARGET_EFFECTS.put("bad_omen", StatusEffect.byRawId(31));
            TARGET_EFFECTS.put("villageHero", StatusEffect.byRawId(32));
        }

        private void update() {
            try {
                playerEffects.clear(); // clear before attempting to get the player else there may be values on the
                                       // mainmenu

                // Attempt to get a player, and store their health and stuff
                ClientPlayerEntity player = MinecraftClient.getInstance().player;
                assert player != null;
                health = player.getHealth();
                maxHealth = player.getMaxHealth();
                absorption = player.getAbsorptionAmount();
                isDead = !player.isLiving();
                armor = player.getArmor();
                experienceLevel = player.experienceLevel;
                experience = player.experienceProgress;
                foodLevel = player.getHungerManager().getFoodLevel();
                saturationLevel = player.getHungerManager().getSaturationLevel();
                isSneaking = player.isSneaking();
                isRidingHorse = player.hasVehicle();
                isBurning = player.isOnFire();
                isInWater = player.isSubmergedInWater();

                // Populate the player's effect map
                for (Map.Entry<String, StatusEffect> effect : TARGET_EFFECTS.entrySet())
                    playerEffects.put(effect.getKey(), player.getStatusEffect(effect.getValue()) != null);

                inGame = true;

            } catch (Exception ex) {
                // If this failed (I.E. could not get a player, the user is probably not in a
                // game)
                inGame = false;
            }
        }
    }

    /**
     * Contains the data extracted from the game about the current world.
     */
    private static class WorldNode {
        private long worldTime;
        private boolean isDayTime;
        private boolean isRaining;
        private float rainStrength;
        private int dimensionID;

        private void update() {
            try {
                ClientWorld world = MinecraftClient.getInstance().world;
                assert world != null;
                worldTime = world.getTimeOfDay();
                isDayTime = world.isDay();
                rainStrength = world.getRainGradient(1);
                isRaining = world.isRaining();
                world.isRaining();

                dimensionID = world.getRegistryManager().getDimensionTypes().getRawId(world.getDimension());
            } catch (Exception ignore) {
            }
        }
    }

    /**
     * Contains lists of any used keys and any that are conflicting.
     */
    private static class GameNode {
        private boolean controlsGuiOpen;
        private boolean chatGuiOpen;
        private AuroraKeyBinding[] keys;

        private void update() {
            try {
                MinecraftClient client = MinecraftClient.getInstance();
                controlsGuiOpen = client.currentScreen instanceof ControlsOptionsScreen;
                chatGuiOpen = client.currentScreen instanceof ChatScreen;
                keys = null;
                if (controlsGuiOpen) {
                    KeyBinding[] temp = client.options.keysAll;
                    List<AuroraKeyBinding> tempList = new ArrayList<>();
                    for (KeyBinding key : temp) {
                        if (!key.getTranslationKey().contains("unknown") && key.getTranslationKey().contains("keyboard")) {
                            String context = key.getCategory().equals("key.categories.inventory") ? "GUI" : "UNIVERSAL";
                            tempList.add(new AuroraKeyBinding(AuroraKeyBinding.ToAuroraKeyCode(key.getTranslationKey()), null,
                                    context));
                        }
                    }
                    keys = new AuroraKeyBinding[tempList.size()];
                    keys = tempList.toArray(keys);
                }
            } catch (Exception ignore) {
            }
        }
    }
}