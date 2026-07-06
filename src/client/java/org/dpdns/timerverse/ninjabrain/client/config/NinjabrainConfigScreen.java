package org.dpdns.timerverse.ninjabrain.client.config;

import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

import me.shedaniel.clothconfig2.api.ConfigBuilder;
import me.shedaniel.clothconfig2.api.ConfigEntryBuilder;

import org.dpdns.timerverse.ninjabrain.algorithm.data.MeasurementManager;

public final class NinjabrainConfigScreen {

	public static Screen create(Screen parent) {
		var holder = new MutableConfig(NinjabrainConfig.load());

		var builder = ConfigBuilder.create()
			.setParentScreen(parent)
			.setTitle(Component.literal("Ninjabrain-Fabricated Config"));

		var entry = ConfigEntryBuilder.create();
		var cat = builder.getOrCreateCategory(Component.literal("General"));

		cat.addEntry(entry.startDoubleField(Component.literal("Standard Deviation"), holder.data.stdDev())
			.setDefaultValue(0.05).setMin(0.001).setMax(1.0)
			.setSaveConsumer(v -> holder.data = new NinjabrainConfig.NinjabrainConfigData(
				v, holder.data.altStdDev(), holder.data.crosshairCorrection(),
				holder.data.numberOfPredictions(), holder.data.useAdvancedStatistics()))
			.build());

		cat.addEntry(entry.startDoubleField(Component.literal("Alt Standard Deviation"), holder.data.altStdDev())
			.setDefaultValue(0.10).setMin(0.001).setMax(1.0)
			.setSaveConsumer(v -> holder.data = new NinjabrainConfig.NinjabrainConfigData(
				holder.data.stdDev(), v, holder.data.crosshairCorrection(),
				holder.data.numberOfPredictions(), holder.data.useAdvancedStatistics()))
			.build());

		cat.addEntry(entry.startDoubleField(Component.literal("Crosshair Correction"), holder.data.crosshairCorrection())
			.setDefaultValue(0.0).setMin(-10.0).setMax(10.0)
			.setSaveConsumer(v -> holder.data = new NinjabrainConfig.NinjabrainConfigData(
				holder.data.stdDev(), holder.data.altStdDev(), v,
				holder.data.numberOfPredictions(), holder.data.useAdvancedStatistics()))
			.build());

		cat.addEntry(entry.startIntField(Component.literal("Number of Predictions"), holder.data.numberOfPredictions())
			.setDefaultValue(5).setMin(1).setMax(20)
			.setSaveConsumer(v -> holder.data = new NinjabrainConfig.NinjabrainConfigData(
				holder.data.stdDev(), holder.data.altStdDev(), holder.data.crosshairCorrection(),
				v, holder.data.useAdvancedStatistics()))
			.build());

		cat.addEntry(entry.startBooleanToggle(Component.literal("Advanced Statistics"), holder.data.useAdvancedStatistics())
			.setDefaultValue(false)
			.setSaveConsumer(v -> holder.data = new NinjabrainConfig.NinjabrainConfigData(
				holder.data.stdDev(), holder.data.altStdDev(), holder.data.crosshairCorrection(),
				holder.data.numberOfPredictions(), v))
			.build());

		cat.addEntry(entry.startTextDescription(Component.literal("Saved to config/ninjabrain-fabricated.json"))
			.build());

		builder.setSavingRunnable(() -> {
			NinjabrainConfig.save(holder.data);
			MeasurementManager.getInstance().recalculate();
		});

		return builder.build();
	}

	private static final class MutableConfig {
		NinjabrainConfig.NinjabrainConfigData data;
		MutableConfig(NinjabrainConfig.NinjabrainConfigData data) { this.data = data; }
	}

}
