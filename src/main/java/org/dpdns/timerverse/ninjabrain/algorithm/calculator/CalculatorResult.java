package org.dpdns.timerverse.ninjabrain.algorithm.calculator;

import java.util.ArrayList;
import java.util.List;

import org.dpdns.timerverse.ninjabrain.algorithm.endereye.EnderEyeThrow;
import org.dpdns.timerverse.ninjabrain.algorithm.statistics.Posterior;
import org.dpdns.timerverse.ninjabrain.algorithm.stronghold.Chunk;
import org.dpdns.timerverse.ninjabrain.algorithm.stronghold.ChunkPrediction;
import org.dpdns.timerverse.ninjabrain.algorithm.mcversion.McVersion;

public class CalculatorResult {

	private final Posterior posterior;
	private final List<EnderEyeThrow> eyeThrows;
	private final int numberOfReturnedPredictions;
	private final McVersion version;

	public CalculatorResult(Posterior posterior, List<EnderEyeThrow> eyeThrows, int numberOfReturnedPredictions) {
		this.posterior = posterior;
		this.eyeThrows = eyeThrows;
		this.numberOfReturnedPredictions = numberOfReturnedPredictions;
		this.version = McVersion.V1_21;
	}

	public ChunkPrediction getBestPrediction() {
		Chunk best = posterior.getMostProbableChunk();
		double playerX = eyeThrows.get(eyeThrows.size() - 1).x();
		double playerZ = eyeThrows.get(eyeThrows.size() - 1).z();
		return new ChunkPrediction(best, playerX, playerZ, version);
	}

	public List<ChunkPrediction> getTopPredictions() {
		List<Chunk> sorted = posterior.getChunks();
		sorted.sort((c1, c2) -> -Double.compare(c1.weight, c2.weight));
		List<ChunkPrediction> predictions = new ArrayList<>();
		double playerX = eyeThrows.get(eyeThrows.size() - 1).x();
		double playerZ = eyeThrows.get(eyeThrows.size() - 1).z();
		int count = Math.min(numberOfReturnedPredictions, sorted.size());
		for (int i = 0; i < count; i++) {
			predictions.add(new ChunkPrediction(sorted.get(i), playerX, playerZ, version));
		}
		return predictions;
	}

	public Posterior getPosterior() {
		return posterior;
	}

}
