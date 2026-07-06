package org.dpdns.timerverse.ninjabrain.algorithm.boat;

import org.dpdns.timerverse.ninjabrain.algorithm.endereye.EnderEyeThrow;

public class BoatEnderEyeThrow extends EnderEyeThrow {

	public BoatEnderEyeThrow(double x, double z, double horizontalAngle, double verticalAngle) {
		super(x, z, horizontalAngle, verticalAngle, 0, 0);
	}

	public BoatEnderEyeThrow(double x, double z, double horizontalAngle, double verticalAngle, double correction, int correctionIncrements) {
		super(x, z, horizontalAngle, verticalAngle, correction, correctionIncrements);
	}

	public BoatEnderEyeThrow withCorrection(double correction, int correctionIncrements) {
		return new BoatEnderEyeThrow(x, z, horizontalAngleWithoutCorrection, verticalAngle, correction, correctionIncrements);
	}

}
