package org.dpdns.timerverse.ninjabrain.client.config;

import java.nio.file.Path;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import net.fabricmc.loader.api.FabricLoader;

public final class NinjabrainConfig {

	private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
	private static final Path PATH = FabricLoader.getInstance().getConfigDir().resolve("ninjabrain-fabricated.json");

	private static NinjabrainConfigData data;

	public record NinjabrainConfigData(
		double stdDev,
		double altStdDev,
		double crosshairCorrection,
		int numberOfPredictions,
		boolean useAdvancedStatistics
	) {
		public static NinjabrainConfigData defaults() {
			return new NinjabrainConfigData(0.05, 0.10, 0.0, 5, false);
		}
	}

	private NinjabrainConfig() {
	}

	public static NinjabrainConfigData load() {
		if (data != null) return data;
		try {
			var reader = java.nio.file.Files.newBufferedReader(PATH);
			data = GSON.fromJson(reader, NinjabrainConfigData.class);
			if (data == null) data = NinjabrainConfigData.defaults();
		} catch (Exception e) {
			data = NinjabrainConfigData.defaults();
		}
		return data;
	}

	public static void save(NinjabrainConfigData newData) {
		data = newData;
		try {
			var writer = java.nio.file.Files.newBufferedWriter(PATH);
			GSON.toJson(data, writer);
			writer.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
