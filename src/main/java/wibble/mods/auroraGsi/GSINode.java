package wibble.mods.auroraGsi;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraft.client.gui.screen.ChatScreen;
import net.minecraft.client.gui.screen.ControlsScreen;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.potion.Effect;
import net.minecraft.potion.Effects;
import net.minecraft.util.registry.DynamicRegistries;

/** Container for the Provider data and Player data. */
public class GSINode {
    private ProviderNode provider = new ProviderNode();
    private GameNode game = new GameNode();
    private WorldNode world = new WorldNode();
    private PlayerNode player = new PlayerNode();

    public GSINode update() {
        game.update();
        world.update();
        player.update();
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
        private static final HashMap<String, Effect> TARGET_POTIONS;
        static {
            TARGET_POTIONS = new HashMap<>();
            TARGET_POTIONS.put("moveSpeed", Effects.SPEED);
            TARGET_POTIONS.put("moveSlowdown", Effects.SLOWNESS);
            TARGET_POTIONS.put("haste", Effects.HASTE);
            TARGET_POTIONS.put("miningFatigue", Effects.MINING_FATIGUE);
            TARGET_POTIONS.put("strength", Effects.STRENGTH);
            //TARGET_POTIONS.put("instantHealth", INSTANT_HEALTH);
            //TARGET_POTIONS.put("instantDamage", INSTANT_DAMAGE);
            TARGET_POTIONS.put("jumpBoost", Effects.JUMP_BOOST);
            TARGET_POTIONS.put("confusion", Effects.NAUSEA);
            TARGET_POTIONS.put("regeneration", Effects.REGENERATION);
            TARGET_POTIONS.put("resistance", Effects.RESISTANCE);
            TARGET_POTIONS.put("fireResistance", Effects.FIRE_RESISTANCE);
            TARGET_POTIONS.put("waterBreathing", Effects.WATER_BREATHING);
            TARGET_POTIONS.put("invisibility", Effects.INVISIBILITY);
            TARGET_POTIONS.put("blindness", Effects.BLINDNESS);
            TARGET_POTIONS.put("nightVision", Effects.NIGHT_VISION);
            TARGET_POTIONS.put("hunger", Effects.HUNGER);
            TARGET_POTIONS.put("weakness", Effects.WEAKNESS);
            TARGET_POTIONS.put("poison", Effects.POISON);
            TARGET_POTIONS.put("wither", Effects.WITHER);
            //TARGET_POTIONS.put("healthBoost", Effects.HEALTH_BOOST);
            TARGET_POTIONS.put("absorption", Effects.ABSORPTION);
            //TARGET_POTIONS.put("saturation", Effects.SATURATION);
            TARGET_POTIONS.put("glowing", Effects.GLOWING);
            TARGET_POTIONS.put("levitation", Effects.LEVITATION);
            TARGET_POTIONS.put("luck", Effects.LUCK);
            TARGET_POTIONS.put("badLuck", Effects.UNLUCK);
            TARGET_POTIONS.put("slowFalling", Effects.SLOW_FALLING);
            TARGET_POTIONS.put("conduitPower", Effects.CONDUIT_POWER);
            TARGET_POTIONS.put("dolphinsGrace", Effects.DOLPHINS_GRACE);
            TARGET_POTIONS.put("badOmen", Effects.BAD_OMEN);
            TARGET_POTIONS.put("villageHero", Effects.HERO_OF_THE_VILLAGE);
        }

        private void update() {
            try {
                playerEffects.clear(); // clear before attempting to get the player else there may be values on the
                                       // mainmenu

                // Attempt to get a player, and store their health and stuff
                ClientPlayerEntity player = Minecraft.getInstance().player;
                assert player != null;
                health = player.getHealth();
                maxHealth = player.getMaxHealth();
                absorption = player.getAbsorptionAmount();
                isDead = !player.isAlive();
                armor = player.getTotalArmorValue();
                experienceLevel = player.experienceLevel;
                experience = player.experience;
                foodLevel = player.getFoodStats().getFoodLevel();
                saturationLevel = player.getFoodStats().getSaturationLevel();
                isSneaking = player.isSneaking();
                isRidingHorse = player.isRidingHorse();
                isBurning = player.isBurning();
                isInWater = player.isInWater();

                // Populate the player's effect map
                for (Map.Entry<String, Effect> potion : TARGET_POTIONS.entrySet())
                    playerEffects.put(
                                potion.getKey(),
                                player.getActivePotionEffect(potion.getValue()) != null);

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
                ClientWorld world = Minecraft.getInstance().world;
                assert world != null;
                worldTime = world.getDayTime();
                isDayTime = world.isDaytime();
                rainStrength = world.rainingStrength;
                isRaining = world.isRaining();
                dimensionID = world.func_241828_r().func_230520_a_().getId(world.getDimensionType());
            } catch (Exception ignore) {
            }
        }
    }

    /**
     * Contains lists of any used keys and any that are conflicting.
     */
    private static class GameNode {
        private KeyBinding[] keys;
        private boolean controlsGuiOpen;
        private boolean chatGuiOpen;

        private void update() {
            Minecraft mc = Minecraft.getInstance();
            controlsGuiOpen = mc.currentScreen instanceof ControlsScreen;
            chatGuiOpen = mc.currentScreen instanceof ChatScreen;
            keys = null;
            if(controlsGuiOpen)
                keys = mc.gameSettings.keyBindings;
        }
    }
}