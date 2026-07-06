package org.dpdns.timerverse.ninjabrain.algorithm.divine;

public class DivineResult {

	public final Fossil fossil;
	public final double optimalAngle;

	public DivineResult(Fossil fossil) {
		this.fossil = fossil;
		this.optimalAngle = calculateOptimalAngle(fossil);
	}

	private static double calculateOptimalAngle(Fossil f) {
		int angleIndex = -4 + f.x;
		if (angleIndex < 0) angleIndex += 16;
		return 2.0 * Math.PI * ((angleIndex + 0.5) / 16.0);
	}

}
