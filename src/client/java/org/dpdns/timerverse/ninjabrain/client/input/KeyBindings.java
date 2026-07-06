package org.dpdns.timerverse.ninjabrain.client.input;

import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keymapping.v1.KeyMappingHelper;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;

import org.dpdns.timerverse.ninjabrain.NinjabrainFabricated;
import org.dpdns.timerverse.ninjabrain.algorithm.data.MeasurementManager;
import org.lwjgl.glfw.GLFW;

public class KeyBindings {

	private static final KeyMapping.Category CATEGORY = KeyMapping.Category.register(
		NinjabrainFabricated.id("ninjabrain"));

	public static KeyMapping resetKey;
	public static KeyMapping undoKey;
	public static KeyMapping lockKey;
	public static KeyMapping incrementKey;
	public static KeyMapping decrementKey;

	public static void register() {
		resetKey = KeyMappingHelper.registerKeyMapping(new KeyMapping(
			"key.ninjabrain.reset", GLFW.GLFW_KEY_R, CATEGORY));

		undoKey = KeyMappingHelper.registerKeyMapping(new KeyMapping(
			"key.ninjabrain.undo", GLFW.GLFW_KEY_Z, CATEGORY));

		lockKey = KeyMappingHelper.registerKeyMapping(new KeyMapping(
			"key.ninjabrain.lock", GLFW.GLFW_KEY_L, CATEGORY));

		incrementKey = KeyMappingHelper.registerKeyMapping(new KeyMapping(
			"key.ninjabrain.increment", GLFW.GLFW_KEY_UP, CATEGORY));

		decrementKey = KeyMappingHelper.registerKeyMapping(new KeyMapping(
			"key.ninjabrain.decrement", GLFW.GLFW_KEY_DOWN, CATEGORY));

		ClientTickEvents.END_CLIENT_TICK.register(client -> {
			MeasurementManager m = MeasurementManager.getInstance();

			while (resetKey.consumeClick()) {
				m.reset();
				if (client.player != null)
					client.player.sendSystemMessage(Component.literal("§7[Ninjabrain] Reset"));
			}

			while (undoKey.consumeClick()) {
				m.undoLastThrow();
				if (client.player != null)
					client.player.sendSystemMessage(Component.literal("§7[Ninjabrain] Undo"));
			}

			while (lockKey.consumeClick()) {
				m.toggleLocked();
				String status = m.isLocked() ? "§cLocked" : "§aUnlocked";
				if (client.player != null)
					client.player.sendSystemMessage(Component.literal("§7[Ninjabrain] " + status));
			}

			while (incrementKey.consumeClick()) {
				m.applyCorrectionToLastThrow(0.01, 1);
			}

			while (decrementKey.consumeClick()) {
				m.applyCorrectionToLastThrow(-0.01, -1);
			}
		});
	}

}
