package org.dpdns.timerverse.ninjabrain.algorithm.endereye;

public class EnderEyeThrow {

	protected final double x, z, horizontalAngleWithoutCorrection, verticalAngle;
	protected final double correction;
	protected final int correctionIncrements;

	public EnderEyeThrow(double x, double z, double horizontalAngle, double verticalAngle, double correction, int correctionIncrements) {
		this.x = x;
		this.z = z;
		this.horizontalAngleWithoutCorrection = clampToPlusMinus180Degrees(horizontalAngle);
		this.verticalAngle = verticalAngle;
		this.correction = correction;
		this.correctionIncrements = correctionIncrements;
	}

	public final double verticalAngle() { return verticalAngle; }

	public double x() { return x; }
	public double z() { return z; }

	public double horizontalAngle() {
		return horizontalAngleWithoutCorrection + correction;
	}

	public double horizontalAngleWithoutCorrection() { return horizontalAngleWithoutCorrection; }
	public double correction() { return correction; }
	public int correctionIncrements() { return correctionIncrements; }

	@Override
	public String toString() {
		return "x=" + x + ", z=" + z + ", alpha=" + horizontalAngleWithoutCorrection;
	}

	private static double clampToPlusMinus180Degrees(double angleInDegrees) {
		angleInDegrees %= 360.0;
		if (angleInDegrees < -180.0) {
			angleInDegrees += 360.0;
		} else if (angleInDegrees > 180.0) {
			angleInDegrees -= 360.0;
		}
		return angleInDegrees;
	}

	public static double getCorrectedHorizontalAngle(double alpha, double crosshairCorrection) {
		alpha += crosshairCorrection;
		alpha -= 0.000824 * Math.sin((alpha + 45) * Math.PI / 180.0);
		return alpha;
	}

	public double getStdDev(double stdDevSetting, double altStdDevSetting) {
		return stdDevSetting;
	}

}
