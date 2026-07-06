package org.dpdns.timerverse.ninjabrain.algorithm.statistics;

public class DiscretizedDensity {

	private double[] density;
	private double min;
	private double max;

	public DiscretizedDensity(double min, double max) {
		this.min = min;
		this.max = max;
		reset(1);
		setUniform();
	}

	public void reset(int n) {
		density = new double[n];
	}

	public void setUniform() {
		for (int i = 0; i < density.length; i++) {
			density[i] = 1.0 / density.length;
		}
	}

	public void addDensity(double from, double to, double value) {
		int i0 = (int) ((from - min) / (max - min) * density.length);
		int i1 = (int) ((to - min) / (max - min) * density.length);
		i0 = Math.max(0, Math.min(density.length - 1, i0));
		i1 = Math.max(0, Math.min(density.length - 1, i1));
		for (int i = i0; i <= i1; i++) {
			density[i] += value;
		}
	}

	public void normalize() {
		double sum = 0;
		for (int i = 0; i < density.length; i++) {
			sum += density[i];
		}
		if (sum > 0) {
			for (int i = 0; i < density.length; i++) {
				density[i] /= sum;
			}
		}
	}

	public double getDensity(double phi) {
		int i = (int) ((phi - min) / (max - min) * density.length);
		if (i < 0) i = 0;
		if (i >= density.length) i = density.length - 1;
		return density[i];
	}

}
