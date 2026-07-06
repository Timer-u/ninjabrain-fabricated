package org.dpdns.timerverse.ninjabrain.client;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientEntityEvents;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.world.entity.projectile.EyeOfEnder;

import org.dpdns.timerverse.ninjabrain.client.detect.AutoDetectionHandler;
import org.dpdns.timerverse.ninjabrain.client.input.KeyBindings;

public class NinjabrainFabricatedClient implements ClientModInitializer {

	@Override
	public void onInitializeClient() {
		KeyBindings.register();

		ClientEntityEvents.ENTITY_LOAD.register((entity, world) -> {
			if (entity instanceof EyeOfEnder eye) {
				AutoDetectionHandler.INSTANCE.onEyeLoaded(eye);
			}
		});

		ClientTickEvents.END_CLIENT_TICK.register(client -> {
			AutoDetectionHandler.INSTANCE.tick();
		});
	}

}
