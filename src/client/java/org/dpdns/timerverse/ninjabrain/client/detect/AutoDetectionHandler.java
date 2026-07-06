package org.dpdns.timerverse.ninjabrain.client.detect;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;

import net.minecraft.world.entity.projectile.EyeOfEnder;

import org.dpdns.timerverse.ninjabrain.algorithm.data.MeasurementManager;

public final class AutoDetectionHandler {

	public static final AutoDetectionHandler INSTANCE = new AutoDetectionHandler();

	private final Queue<double[]> pendingThrows = new LinkedList<>();
	private final Map<Integer, TrackingEye> tracking = new HashMap<>();

	private AutoDetectionHandler() {
	}

	public void onThrow(double playerX, double playerZ) {
		pendingThrows.add(new double[] { playerX, playerZ });
	}

	public void onEyeLoaded(EyeOfEnder eye) {
		double[] capture = pendingThrows.poll();
		if (capture != null) {
			tracking.put(eye.getId(), new TrackingEye(eye, capture[0], capture[1]));
		}
	}

	public void tick() {
		if (tracking.isEmpty()) return;
		var it = tracking.entrySet().iterator();
		while (it.hasNext()) {
			var entry = it.next();
			TrackingEye te = entry.getValue();
			EyeOfEnder eye = te.eye;
			if (!eye.isAlive()) {
				it.remove();
				continue;
			}
			double vx = eye.getDeltaMovement().x;
			double vz = eye.getDeltaMovement().z;
			double speedSq = vx * vx + vz * vz;
			if (speedSq < 1.0e-8) continue;

			double angle = -Math.toDegrees(Math.atan2(vx, vz));
			MeasurementManager.getInstance().addMeasurement(
				te.playerX, te.playerZ,
				angle, eye.getXRot(), 0.0);
			it.remove();
		}
	}

	private record TrackingEye(EyeOfEnder eye, double playerX, double playerZ) {
	}

}
