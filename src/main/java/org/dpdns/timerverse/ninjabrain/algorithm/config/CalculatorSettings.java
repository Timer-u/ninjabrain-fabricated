package org.dpdns.timerverse.ninjabrain.algorithm.config;

import org.dpdns.timerverse.ninjabrain.algorithm.mcversion.McVersion;

public class CalculatorSettings {

	public final int numberOfReturnedPredictions;
	public final boolean useAdvancedStatistics;
	public final McVersion mcVersion;

	public CalculatorSettings() {
		this(5, true, McVersion.V1_21);
	}

	public CalculatorSettings(int numberOfReturnedPredictions, boolean useAdvancedStatistics, McVersion mcVersion) {
		this.numberOfReturnedPredictions = numberOfReturnedPredictions;
		this.useAdvancedStatistics = useAdvancedStatistics;
		this.mcVersion = mcVersion;
	}

}
