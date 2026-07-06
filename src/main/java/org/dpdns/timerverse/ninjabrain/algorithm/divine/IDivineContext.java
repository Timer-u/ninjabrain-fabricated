package org.dpdns.timerverse.ninjabrain.algorithm.divine;

public interface IDivineContext {

	Fossil getFossil();
	boolean hasDivine();
	double relativeDensity();
	double getDensityAtAngleBeforeSnapping(double phi);

}
