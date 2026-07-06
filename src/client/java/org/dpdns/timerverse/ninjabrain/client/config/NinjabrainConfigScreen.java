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
			.setTitle(Component.translatable("ninjabrain.config.title"));

		var entry = ConfigEntryBuilder.create();
		var cat = builder.getOrCreateCategory(Component.translatable("ninjabrain.config.category.general"));

		cat.addEntry(entry.startDoubleField(Component.translatable("ninjabrain.config.stdDev"), holder.data.stdDev())
			.setDefaultValue(0.05).setMin(0.001).setMax(1.0)
			.setTooltip(Component.translatable("ninjabrain.config.stdDev.tooltip"))
			.setSaveConsumer(v -> holder.data = holder.data.withStdDev(v))
			.build());

		cat.addEntry(entry.startDoubleField(Component.translatable("ninjabrain.config.altStdDev"), holder.data.altStdDev())
			.setDefaultValue(0.10).setMin(0.001).setMax(1.0)
			.setTooltip(Component.translatable("ninjabrain.config.altStdDev.tooltip"))
			.setSaveConsumer(v -> holder.data = holder.data.withAltStdDev(v))
			.build());

		cat.addEntry(entry.startDoubleField(Component.translatable("ninjabrain.config.crosshairCorrection"), holder.data.crosshairCorrection())
			.setDefaultValue(0.0).setMin(-10.0).setMax(10.0)
			.setTooltip(Component.translatable("ninjabrain.config.crosshairCorrection.tooltip"))
			.setSaveConsumer(v -> holder.data = holder.data.withCrosshairCorrection(v))
			.build());

		cat.addEntry(entry.startIntField(Component.translatable("ninjabrain.config.numberOfPredictions"), holder.data.numberOfPredictions())
			.setDefaultValue(5).setMin(1).setMax(20)
			.setTooltip(Component.translatable("ninjabrain.config.numberOfPredictions.tooltip"))
			.setSaveConsumer(v -> holder.data = holder.data.withNumberOfPredictions(v))
			.build());

		cat.addEntry(entry.startBooleanToggle(Component.translatable("ninjabrain.config.advancedStatistics"), holder.data.useAdvancedStatistics())
			.setDefaultValue(false)
			.setTooltip(Component.translatable("ninjabrain.config.advancedStatistics.tooltip"))
			.setSaveConsumer(v -> holder.data = holder.data.withUseAdvancedStatistics(v))
			.build());

		var hudCat = builder.getOrCreateCategory(Component.translatable("ninjabrain.config.category.hud"));

		hudCat.addEntry(entry.startIntSlider(Component.translatable("ninjabrain.config.hudBackgroundAlpha"), holder.data.hudBackgroundAlpha(), 0, 255)
			.setDefaultValue(136)
			.setTooltip(Component.translatable("ninjabrain.config.hudBackgroundAlpha.tooltip"))
			.setTextGetter(v -> Component.literal(String.valueOf(v)))
			.setSaveConsumer(v -> holder.data = holder.data.withHudBackgroundAlpha(v))
			.build());

		var positions = java.util.List.of("top_left", "top_right", "bottom_left", "bottom_right");
		hudCat.addEntry(entry.startStringDropdownMenu(
				Component.translatable("ninjabrain.config.hudPosition"),
				holder.data.hudPosition())
			.setSelections(positions)
			.setDefaultValue("top_left")
			.setTooltip(Component.translatable("ninjabrain.config.hudPosition.tooltip"))
			.setSaveConsumer(v -> holder.data = holder.data.withHudPosition(v))
			.build());

		hudCat.addEntry(entry.startDoubleField(Component.translatable("ninjabrain.config.hudScale"), holder.data.hudScale())
			.setDefaultValue(1.0).setMin(0.25).setMax(4.0)
			.setTooltip(Component.translatable("ninjabrain.config.hudScale.tooltip"))
			.setSaveConsumer(v -> holder.data = holder.data.withHudScale(v))
			.build());

		cat.addEntry(entry.startTextDescription(Component.translatable("ninjabrain.config.savedTo"))
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
