package org.dpdns.timerverse.ninjabrain.client.mixin;

import java.util.List;

import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.Hud;

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
	private static final int BACKGROUND = 0x88000000;
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

		int x = 4;
		int y = 4;
		int lineHeight = font.lineHeight + 2;
		int panelWidth = 180;

		int numThrows = mgr.getThrowCount();
		int predLines = Math.min(predictions.size(), 5);
		int totalLines = 2 + predLines + 1;
		int panelHeight = totalLines * lineHeight + 8;

		extractor.fill(x - 2, y - 2, x + panelWidth, y + panelHeight, BACKGROUND);
		extractor.outline(x - 2, y - 2, panelWidth, panelHeight, BORDER);

		int tx = x;
		int ty = y + 2;

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
