package org.dpdns.timerverse.ninjabrain.algorithm.stronghold;

import org.dpdns.timerverse.ninjabrain.algorithm.mcversion.McVersion;

public class ChunkPrediction {

	public final Chunk chunk;
	public final String certaintyString;
	public final boolean success;

	private final double playerX;
	private final double playerZ;
	private final McVersion version;

	public ChunkPrediction(Chunk chunk, double playerX, double playerZ, McVersion version) {
		this.chunk = chunk;
		this.playerX = playerX;
		this.playerZ = playerZ;
		this.version = version;
		this.certaintyString = formatCertainty(chunk.weight);
		this.success = chunk.weight > 0.0005 && !Double.isNaN(chunk.weight) && !Double.isInfinite(chunk.weight);
	}

	public int distance() {
		return chunk.getOverworldDistance(version, playerX, playerZ);
	}

	public int fourFourX() { return chunk.fourFourX(); }
	public int fourFourZ() { return chunk.fourFourZ(); }
	public int eightEightX() { return chunk.eightEightX(); }
	public int eightEightZ() { return chunk.eightEightZ(); }

	private static String formatCertainty(double weight) {
		return String.format("%.1f%%", weight * 100.0);
	}

}
