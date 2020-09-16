package wibble.mods.auroragsi;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import net.fabricmc.api.ClientModInitializer;

public class AuroraGSI implements ClientModInitializer {
	@Override
	public void onInitializeClient() {		
		ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
		scheduler.scheduleAtFixedRate(new SendGameState(), 0, 100, TimeUnit.MILLISECONDS);
	}
}
