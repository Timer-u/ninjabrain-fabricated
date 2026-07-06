package org.dpdns.timerverse.ninjabrain.algorithm.data;

import java.util.ArrayList;
import java.util.List;

import org.dpdns.timerverse.ninjabrain.algorithm.calculator.Calculator;
import org.dpdns.timerverse.ninjabrain.algorithm.calculator.CalculatorResult;
import org.dpdns.timerverse.ninjabrain.algorithm.config.CalculatorSettings;
import org.dpdns.timerverse.ninjabrain.algorithm.config.StdDevSettings;
import org.dpdns.timerverse.ninjabrain.algorithm.divine.DivineContext;
import org.dpdns.timerverse.ninjabrain.algorithm.divine.Fossil;
import org.dpdns.timerverse.ninjabrain.algorithm.endereye.EnderEyeThrow;
import org.dpdns.timerverse.ninjabrain.algorithm.endereye.NormalEnderEyeThrow;
import org.dpdns.timerverse.ninjabrain.algorithm.stronghold.ChunkPrediction;

public class MeasurementManager {

	private static MeasurementManager instance;

	private final List<EnderEyeThrow> throws$ = new ArrayList<>();
	private final Calculator calculator;
	private final DivineContext divineContext;
	private CalculatorSettings settings;
	private StdDevSettings stdDevSettings;

	private CalculatorResult currentResult;
	private boolean locked = false;

	public static MeasurementManager getInstance() {
		if (instance == null) {
			instance = new MeasurementManager();
		}
		return instance;
	}

	private MeasurementManager() {
		this.settings = new CalculatorSettings();
		this.stdDevSettings = new StdDevSettings();
		this.calculator = new Calculator(settings, stdDevSettings);
		this.divineContext = new DivineContext();
	}

	public void addMeasurement(double x, double z, double horizontalAngle, double verticalAngle, double crosshairCorrection) {
		if (locked) return;
		NormalEnderEyeThrow throw_ = new NormalEnderEyeThrow(x, z, horizontalAngle, verticalAngle, crosshairCorrection);
		throws$.add(throw_);
		recalculate();
	}

	public void setFossil(int fossilX, int fossilZ) {
		divineContext.setFossil(new Fossil(fossilX, fossilZ));
		recalculate();
	}

	public void clearFossil() {
		divineContext.setFossil(null);
		recalculate();
	}

	public void recalculate() {
		if (throws$.isEmpty()) {
			currentResult = null;
			return;
		}
		currentResult = calculator.triangulate(new ArrayList<>(throws$), divineContext);
	}

	public void undoLastThrow() {
		if (!throws$.isEmpty() && !locked) {
			throws$.remove(throws$.size() - 1);
			recalculate();
		}
	}

	public void reset() {
		if (locked) return;
		throws$.clear();
		divineContext.setFossil(null);
		currentResult = null;
	}

	public void toggleLocked() {
		locked = !locked;
	}

	public boolean isLocked() { return locked; }

	public CalculatorResult getCurrentResult() { return currentResult; }

	public List<EnderEyeThrow> getThrows() { return new ArrayList<>(throws$); }

	public DivineContext getDivineContext() { return divineContext; }

	public CalculatorSettings getSettings() { return settings; }

	public StdDevSettings getStdDevSettings() { return stdDevSettings; }

	public int getThrowCount() { return throws$.size(); }

	public ChunkPrediction getBestPrediction() {
		if (currentResult == null) return null;
		return currentResult.getBestPrediction();
	}

	public List<ChunkPrediction> getTopPredictions() {
		if (currentResult == null) return new ArrayList<>();
		return currentResult.getTopPredictions();
	}

	public void applyCorrectionToLastThrow(double correction, int increments) {
		if (throws$.isEmpty() || locked) return;
		int last = throws$.size() - 1;
		EnderEyeThrow t = throws$.get(last);
		if (t instanceof NormalEnderEyeThrow) {
			throws$.set(last, ((NormalEnderEyeThrow) t).withCorrection(correction, increments));
			recalculate();
		}
	}

}
