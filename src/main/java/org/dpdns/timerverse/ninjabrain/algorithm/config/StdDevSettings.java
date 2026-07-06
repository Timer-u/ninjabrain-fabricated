package org.dpdns.timerverse.ninjabrain.algorithm.config;

public class StdDevSettings {

	public double stdDev;
	public double altStdDev;

	public StdDevSettings() {
		this(0.05, 0.10);
	}

	public StdDevSettings(double stdDev, double altStdDev) {
		this.stdDev = stdDev;
		this.altStdDev = altStdDev;
	}

}
