package wibble.mods.auroraGsi;

import net.minecraftforge.fml.common.Mod;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Mod(AuroraGSI.MODID)
public final class AuroraGSI {

    public static final String MODID = "auroragsi";
    /**
     * Start a timer that will send a request to the Aurora HTTP listening server containing the game data.
     */
    public AuroraGSI() {
        ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
        scheduler.scheduleAtFixedRate(new SendGameState(), 0, 100, TimeUnit.MILLISECONDS);
    }
}