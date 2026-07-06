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
		boolean useAdvancedStatistics,
		int hudBackgroundAlpha,
		String hudPosition,
		double hudScale
	) {
		public static NinjabrainConfigData defaults() {
			return new NinjabrainConfigData(0.05, 0.10, 0.0, 5, false, 136, "top_left", 1.0);
		}

		public NinjabrainConfigData withStdDev(double v) { return new NinjabrainConfigData(v, altStdDev, crosshairCorrection, numberOfPredictions, useAdvancedStatistics, hudBackgroundAlpha, hudPosition, hudScale); }
		public NinjabrainConfigData withAltStdDev(double v) { return new NinjabrainConfigData(stdDev, v, crosshairCorrection, numberOfPredictions, useAdvancedStatistics, hudBackgroundAlpha, hudPosition, hudScale); }
		public NinjabrainConfigData withCrosshairCorrection(double v) { return new NinjabrainConfigData(stdDev, altStdDev, v, numberOfPredictions, useAdvancedStatistics, hudBackgroundAlpha, hudPosition, hudScale); }
		public NinjabrainConfigData withNumberOfPredictions(int v) { return new NinjabrainConfigData(stdDev, altStdDev, crosshairCorrection, v, useAdvancedStatistics, hudBackgroundAlpha, hudPosition, hudScale); }
		public NinjabrainConfigData withUseAdvancedStatistics(boolean v) { return new NinjabrainConfigData(stdDev, altStdDev, crosshairCorrection, numberOfPredictions, v, hudBackgroundAlpha, hudPosition, hudScale); }
		public NinjabrainConfigData withHudBackgroundAlpha(int v) { return new NinjabrainConfigData(stdDev, altStdDev, crosshairCorrection, numberOfPredictions, useAdvancedStatistics, v, hudPosition, hudScale); }
		public NinjabrainConfigData withHudPosition(String v) { return new NinjabrainConfigData(stdDev, altStdDev, crosshairCorrection, numberOfPredictions, useAdvancedStatistics, hudBackgroundAlpha, v, hudScale); }
		public NinjabrainConfigData withHudScale(double v) { return new NinjabrainConfigData(stdDev, altStdDev, crosshairCorrection, numberOfPredictions, useAdvancedStatistics, hudBackgroundAlpha, hudPosition, v); }
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
