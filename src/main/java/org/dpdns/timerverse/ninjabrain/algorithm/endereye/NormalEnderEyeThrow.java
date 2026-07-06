package org.dpdns.timerverse.ninjabrain.algorithm.endereye;

public class NormalEnderEyeThrow extends EnderEyeThrow {

	public NormalEnderEyeThrow(double x, double z, double horizontalAngle, double verticalAngle, double crosshairCorrection) {
		super(x, z, getCorrectedHorizontalAngle(horizontalAngle, crosshairCorrection), verticalAngle, 0, 0);
	}

	public NormalEnderEyeThrow(double x, double z, double horizontalAngle, double verticalAngle, double correction, int correctionIncrements) {
		super(x, z, horizontalAngle, verticalAngle, correction, correctionIncrements);
	}

	public NormalEnderEyeThrow withCorrection(double correction, int correctionIncrements) {
		return new NormalEnderEyeThrow(x, z, horizontalAngleWithoutCorrection, verticalAngle, correction, correctionIncrements);
	}

}
