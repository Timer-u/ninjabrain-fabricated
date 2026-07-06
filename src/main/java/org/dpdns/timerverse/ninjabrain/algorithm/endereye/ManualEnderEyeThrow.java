package org.dpdns.timerverse.ninjabrain.algorithm.endereye;

public class ManualEnderEyeThrow extends EnderEyeThrow {

	public ManualEnderEyeThrow(double x, double z, double horizontalAngle, double verticalAngle, double crosshairCorrection) {
		super(x, z, getCorrectedHorizontalAngle(horizontalAngle, crosshairCorrection), verticalAngle, 0, 0);
	}

	public ManualEnderEyeThrow(double x, double z, double horizontalAngle, double verticalAngle, double correction, int correctionIncrements) {
		super(x, z, horizontalAngle, verticalAngle, correction, correctionIncrements);
	}

	public ManualEnderEyeThrow withCorrection(double correction, int correctionIncrements) {
		return new ManualEnderEyeThrow(x, z, horizontalAngleWithoutCorrection, verticalAngle, correction, correctionIncrements);
	}

}
