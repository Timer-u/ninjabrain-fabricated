package org.dpdns.timerverse.ninjabrain.client.mixin;

import java.util.List;

import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.Hud;

import org.dpdns.timerverse.ninjabrain.client.config.NinjabrainConfig;
import org.dpdns.timerverse.ninjabrain.algorithm.data.MeasurementManager;
import org.dpdns.timerverse.ninjabrain.algorithm.stronghold.ChunkPrediction;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Hud.class)
public class InGameHudMixin {

	@Unique
	private static final int GREEN = 0xFF55FF55;
	@Unique
	private static final int GRAY = 0xFFAAAAAA;
	@Unique
	private static final int YELLOW = 0xFFFFFF55;
	@Unique
	private static final int BORDER = 0x44FFFFFF;

	@Inject(at = @At("TAIL"), method = "extractRenderState")
	private void onExtractRenderState(GuiGraphicsExtractor extractor, DeltaTracker deltaTracker, CallbackInfo ci) {
		Minecraft mc = Minecraft.getInstance();
		if (mc.options == null || mc.player == null) return;

		MeasurementManager mgr = MeasurementManager.getInstance();
		List<ChunkPrediction> predictions = mgr.getTopPredictions();
		if (predictions == null || predictions.isEmpty()) return;

		Font font = mc.font;
		if (font == null) return;

		var config = NinjabrainConfig.load();
		double scale = config.hudScale();
		int bgAlpha = config.hudBackgroundAlpha();
		String position = config.hudPosition();

		int margin = 4;
		int panelWidth = 180;
		int lineHeight = font.lineHeight + 2;

		int numThrows = mgr.getThrowCount();
		int predLines = Math.min(predictions.size(), 5);
		int totalLines = 2 + predLines + 1;
		int panelHeight = totalLines * lineHeight + 8;

		int screenWidth = mc.getWindow().getGuiScaledWidth();
		int screenHeight = mc.getWindow().getGuiScaledHeight();

		int panelScreenW = (int)(panelWidth * scale);
		int panelScreenH = (int)(panelHeight * scale);

		int originX, originY;
		switch (position) {
			case "top_right":
				originX = screenWidth - panelScreenW - margin;
				originY = margin;
				break;
			case "bottom_left":
				originX = margin;
				originY = screenHeight - panelScreenH - margin;
				break;
			case "bottom_right":
				originX = screenWidth - panelScreenW - margin;
				originY = screenHeight - panelScreenH - margin;
				break;
			default:
				originX = margin;
				originY = margin;
		}

		var pose = extractor.pose();
		pose.pushMatrix();
		pose.translate(originX, originY);
		pose.scale((float)scale, (float)scale);

		int bgColor = (bgAlpha << 24) | 0x000000;
		extractor.fill(-2, -2, panelWidth, panelHeight, bgColor);
		if (bgAlpha > 0) {
			extractor.outline(-2, -2, panelWidth, panelHeight, BORDER);
		}

		int cx = 0;
		int cy = 2;

		extractor.text(font, "Ninjabrain-Fabricated", cx, cy, GREEN);
		cy += lineHeight;
		extractor.text(font, "Throws: " + numThrows, cx, cy, GRAY);
		cy += lineHeight;

		for (int i = 0; i < predLines; i++) {
			ChunkPrediction p = predictions.get(i);
			if (p == null || !p.success) break;
			extractor.text(font, String.format("#%d: (%d, %d) %s  %dm",
				i + 1, p.eightEightX(), p.eightEightZ(),
				p.certaintyString, p.distance()), cx, cy, YELLOW);
			cy += lineHeight;
		}

		ChunkPrediction best = mgr.getBestPrediction();
		if (best != null && best.success) {
			extractor.text(font, "4,4: (" + best.fourFourX() + ", " + best.fourFourZ() + ")", cx, cy, GRAY);
		}

		pose.popMatrix();
	}

}
