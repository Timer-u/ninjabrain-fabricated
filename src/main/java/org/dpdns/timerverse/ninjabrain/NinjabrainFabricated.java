package org.dpdns.timerverse.ninjabrain;

import net.fabricmc.api.ModInitializer;
import net.minecraft.resources.Identifier;

import org.dpdns.timerverse.ninjabrain.algorithm.statistics.ApproximatedDensity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class NinjabrainFabricated implements ModInitializer {
	public static final String MOD_ID = "ninjabrain-fabricated";

	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	public static Identifier id(String path) {
		return Identifier.fromNamespaceAndPath(MOD_ID, path);
	}

	@Override
	public void onInitialize() {
		ApproximatedDensity.init();
		LOGGER.info("Ninjabrain-Fabricated initialized (stronghold calculator ready)");
	}

}
