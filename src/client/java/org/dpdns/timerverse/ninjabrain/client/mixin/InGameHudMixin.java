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

		int baseX = (int)(4 * scale);
		int baseY = (int)(4 * scale);
		int panelWidth = (int)(180 * scale);
		int lineHeight = (int)((font.lineHeight + 2) * scale);

		int numThrows = mgr.getThrowCount();
		int predLines = Math.min(predictions.size(), 5);
		int totalLines = 2 + predLines + 1;
		int panelHeight = totalLines * lineHeight + (int)(8 * scale);

		int screenWidth = mc.getWindow().getGuiScaledWidth();
		int screenHeight = mc.getWindow().getGuiScaledHeight();

		int x, y;
		switch (position) {
			case "top_right":
				x = screenWidth - panelWidth - baseX;
				y = baseY;
				break;
			case "bottom_left":
				x = baseX;
				y = screenHeight - panelHeight - baseY;
				break;
			case "bottom_right":
				x = screenWidth - panelWidth - baseX;
				y = screenHeight - panelHeight - baseY;
				break;
			default:
				x = baseX;
				y = baseY;
		}

		int bgColor = (bgAlpha << 24) | 0x000000;
		extractor.fill(x - (int)(2 * scale), y - (int)(2 * scale), x + panelWidth, y + panelHeight, bgColor);
		extractor.outline(x - (int)(2 * scale), y - (int)(2 * scale), panelWidth, panelHeight, BORDER);

		int tx = x;
		int ty = y + (int)(2 * scale);

		extractor.text(font, "Ninjabrain-Fabricated", tx, ty, GREEN);
		ty += lineHeight;
		extractor.text(font, "Throws: " + numThrows, tx, ty, GRAY);
		ty += lineHeight;

		for (int i = 0; i < predLines; i++) {
			ChunkPrediction p = predictions.get(i);
			if (p == null || !p.success) break;
			extractor.text(font, String.format("#%d: (%d, %d) %s  %dm",
				i + 1, p.eightEightX(), p.eightEightZ(),
				p.certaintyString, p.distance()), tx, ty, YELLOW);
			ty += lineHeight;
		}

		ChunkPrediction best = mgr.getBestPrediction();
		if (best != null && best.success) {
			extractor.text(font, "4,4: (" + best.fourFourX() + ", " + best.fourFourZ() + ")", tx, ty, GRAY);
		}
	}

}
