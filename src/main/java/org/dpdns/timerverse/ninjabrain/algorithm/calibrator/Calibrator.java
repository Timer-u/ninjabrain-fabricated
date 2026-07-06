package org.dpdns.timerverse.ninjabrain.algorithm.calibrator;

import java.util.List;

import org.dpdns.timerverse.ninjabrain.algorithm.endereye.EnderEyeThrow;
import org.dpdns.timerverse.ninjabrain.algorithm.mcversion.McVersion;
import org.dpdns.timerverse.ninjabrain.algorithm.stronghold.Chunk;

public class Calibrator {

	public static double calculateStdDev(McVersion version, List<EnderEyeThrow> throws_, Chunk result) {
		double[] errors = result.getAngleErrors(version, throws_);
		double sqSum = 0;
		for (double e : errors) {
			sqSum += e * e;
		}
		return Math.sqrt(sqSum / (errors.length - 0.5));
	}

}
