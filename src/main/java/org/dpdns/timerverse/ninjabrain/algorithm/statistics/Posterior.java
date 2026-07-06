package org.dpdns.timerverse.ninjabrain.algorithm.statistics;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

import org.dpdns.timerverse.ninjabrain.algorithm.divine.IDivineContext;
import org.dpdns.timerverse.ninjabrain.algorithm.endereye.EnderEyeThrow;
import org.dpdns.timerverse.ninjabrain.algorithm.mcversion.McVersion;
import org.dpdns.timerverse.ninjabrain.algorithm.stronghold.Chunk;
import org.dpdns.timerverse.ninjabrain.algorithm.stronghold.Ring;
import org.dpdns.timerverse.ninjabrain.algorithm.stronghold.StrongholdConstants;
import org.dpdns.timerverse.ninjabrain.algorithm.util.Coords;

public class Posterior {

	private final McVersion version;
	private final double stdDev;
	private final double altStdDev;

	final ArrayList<Chunk> chunks;

	public Posterior(List<EnderEyeThrow> eyeThrows, IDivineContext divineContext, double stdDev, double altStdDev, boolean useAdvStatistics, McVersion version) {
		this.version = version;
		this.stdDev = stdDev;
		this.altStdDev = altStdDev;
		double sigma0 = eyeThrows.get(0).getStdDev(stdDev, altStdDev);
		chunks = new ArrayList<Chunk>();
		double px = eyeThrows.get(0).x();
		double pz = eyeThrows.get(0).z();
		double maxDist = StrongholdConstants.getMaxDistance(px, pz) / 16.0;
		double maxDist2 = maxDist * maxDist;

		if (maxDist * sigma0 > 1000)
			return;

		IPrior prior = new RayApproximatedPrior(eyeThrows.get(0), Math.min(1.0, 30 * sigma0) / 180.0 * Math.PI, divineContext, version);
		for (Chunk c : prior.getChunks()) {
			Chunk clone = c.clone();
			double dx = clone.x - px / 16.0;
			double dz = clone.z - pz / 16.0;
			if (dx * dx + dz * dz > maxDist2) {
				clone.weight = 0;
			}
			chunks.add(clone);
		}
		for (EnderEyeThrow t : eyeThrows) {
			condition(t);
		}
		if (useAdvStatistics)
			closestStrongholdCondition(eyeThrows.get(0), 0.001);
	}

	public void condition(EnderEyeThrow t) {
		chunks.forEach((chunk) -> updateConditionalProbability(chunk, t));
		double weightSum = 0.0;
		for (Chunk chunk : chunks) {
			weightSum += chunk.weight;
		}
		final double totalWeight = weightSum;
		chunks.forEach((chunk) -> chunk.weight /= totalWeight);
	}

	public double getMinDistance(double tolerance, EnderEyeThrow position) {
		return getClosestPossibleChunk(tolerance, position).getOverworldDistance(version, position.x(), position.z());
	}

	public Chunk getClosestPossibleChunk(double tolerance, EnderEyeThrow position) {
		Chunk closest = null;
		double minDist = Double.POSITIVE_INFINITY;
		for (Chunk c : chunks) {
			if (c.weight > tolerance) {
				double dist = c.getOverworldDistance(version, position.x(), position.z());
				if (dist < minDist) {
					minDist = dist;
					closest = c;
				}
			}
		}
		return closest;
	}

	public Chunk getMostProbableChunk() {
		Optional<Chunk> prediction = getChunks().stream().max(Comparator.comparingDouble((chunk) -> chunk.weight));
		try {
			return prediction.get();
		} catch (Exception e) {
			return new Chunk(0, 0);
		}
	}

	private void updateConditionalProbability(Chunk chunk, EnderEyeThrow t) {
		double deltax = chunk.x * 16 + StrongholdConstants.getStrongholdChunkCoord(version) - t.x();
		double deltaz = chunk.z * 16 + StrongholdConstants.getStrongholdChunkCoord(version) - t.z();
		double gamma = -180 / Math.PI * Math.atan2(deltax, deltaz);
		double delta = Math.abs((gamma - t.horizontalAngle()) % 360.0);
		delta = Math.min(delta, 360.0 - delta);
		double s1 = t.getStdDev(stdDev, altStdDev);
		double v2 = getVarianceFromPositionImprecision(deltax * deltax + deltaz * deltaz, t.x(), t.z());
		double v = s1 * s1 + v2;
		chunk.weight *= Math.exp(-delta * delta / (2 * v));
	}

	private double getVarianceFromPositionImprecision(double distance2, double throwX, double throwZ) {
		if ((Math.abs(throwX - Math.floor(throwX) - 0.3) < 1e-6 || Math.abs(throwX - Math.floor(throwX) - 0.7) < 1e-6) &&
			(Math.abs(throwZ - Math.floor(throwZ) - 0.3) < 1e-6 || Math.abs(throwZ - Math.floor(throwZ) - 0.7) < 1e-6)) {
			return 0;
		}
		double maxLateralError = 0.005 * Math.sqrt(2) * 180 / Math.PI;
		return maxLateralError * maxLateralError / distance2 / 6;
	}

	public List<Chunk> getChunks() {
		return chunks;
	}

	private void closestStrongholdCondition(EnderEyeThrow t, double probabilityTheshold) {
		chunks.sort((c1, c2) -> -Double.compare(c1.weight, c2.weight));
		double totalClosestStrongholdProbability = 0;
		int samples = 0;
		for (int i = 0; i < chunks.size(); i++) {
			Chunk c = chunks.get(i);
			if (i < 100 || c.weight > probabilityTheshold) {
				double a = closestStrongholdCondition(c, t);
				totalClosestStrongholdProbability += a;
				samples++;
			} else {
				c.weight *= totalClosestStrongholdProbability / samples;
			}
		}
		double weightSum = 0.0;
		for (Chunk chunk : chunks) {
			weightSum += chunk.weight;
		}
		final double totalWeight = weightSum;
		chunks.forEach((chunk) -> chunk.weight /= totalWeight);
	}

	final int K = 7;

	private double closestStrongholdCondition(Chunk chunk, EnderEyeThrow t) {
		double closestStrongholdProbability = 1;
		double deltax = chunk.x + (StrongholdConstants.getStrongholdChunkCoord(version) - t.x()) / 16.0;
		double deltaz = chunk.z + (StrongholdConstants.getStrongholdChunkCoord(version) - t.z()) / 16.0;
		double r_p = Math.sqrt(t.x() * t.x() + t.z() * t.z()) / 16.0;
		double d_i = Math.sqrt(deltax * deltax + deltaz * deltaz);
		double phi_prime = Coords.getPhi(chunk.x, chunk.z);
		double phi_p = Coords.getPhi(t.x(), t.z());
		double maxDist = StrongholdConstants.getMaxDistance(t.x(), t.z()) / 16.0;
		double stronghold_r_min = r_p - maxDist;
		double stronghold_r_max = r_p + maxDist;
		Ring ring_chunk = Ring.get(Math.sqrt(chunk.x * chunk.x + chunk.z * chunk.z));
		if (ring_chunk == null) {
			return 0;
		}
		for (int i = 0; i < StrongholdConstants.numRings; i++) {
			Ring ring = Ring.get(i);
			if (stronghold_r_max < ring.innerRadius || stronghold_r_min > ring.outerRadius)
				continue;
			boolean sameRing = ring_chunk.ring == ring.ring;
			double ak = ring_chunk.innerRadius;
			double dphi = sameRing ? (2.0 / (2.0 * K + 1) * 15 * Math.sqrt(2) / ak) : (2.0 / (2.0 * K + 1) * Math.PI / ring.numStrongholds);
			for (int l = 0; l < ring.numStrongholds; l++) {
				if (sameRing && l == 0) {
					continue;
				}
				double integral = integral(ring, l, phi_prime, dphi, phi_p, r_p, d_i, sameRing);
				closestStrongholdProbability *= 1.0 - integral;
			}
		}
		chunk.weight *= closestStrongholdProbability;
		return closestStrongholdProbability;
	}

	private double integral(Ring ring, int l, double phi_prime, double dphi, double phi_p, double r_p, double d_i, boolean sameRingAsChunk) {
		double phi_prime_l_mu = phi_prime + (l * 2 * Math.PI / ring.numStrongholds);
		double pdfint = 0;
		double integral = 0;
		for (int k = -K; k <= K; k++) {
			double delta_phi = k * dphi;
			double pdf = 0;
			if (sameRingAsChunk) {
				pdf = Math.pow(1 + delta_phi * ring.innerRadius / (15 * Math.sqrt(2)), 4.5) * Math.pow(1 - delta_phi * ring.innerRadius / (15 * Math.sqrt(2)), 4.5);
			} else {
				pdf = 1;
			}
			pdfint += pdf * dphi;
			double phi_prime_l = phi_prime_l_mu + k * dphi;
			double gamma = phi_p - phi_prime_l;
			double sin_beta = r_p / d_i * Math.sin(gamma);
			if (sin_beta < 1.0 && sin_beta > -1.0) {
				double beta = Math.asin(sin_beta);
				double alpha0 = beta - gamma;
				double alpha1 = Math.PI - gamma - beta;
				double R0 = d_i * Math.sin(alpha0) / Math.sin(gamma);
				double R1 = d_i * Math.sin(alpha1) / Math.sin(gamma);
				if (R1 > ring.outerRadiusPostSnapping)
					R1 = ring.outerRadiusPostSnapping;
				if (R0 < ring.innerRadiusPostSnapping)
					R0 = ring.innerRadiusPostSnapping;
				if (R0 > ring.outerRadiusPostSnapping)
					R0 = ring.outerRadiusPostSnapping;
				if (R1 < ring.innerRadiusPostSnapping)
					R1 = ring.innerRadiusPostSnapping;
				integral += pdf * (ApproximatedDensity.cumulativePolar(R1) - ApproximatedDensity.cumulativePolar(R0)) * dphi / ring.numStrongholds;
			}
		}
		integral /= pdfint;
		if (integral > 1.0)
			integral = 1.0;
		return integral;
	}

}
