package org.dpdns.timerverse.ninjabrain.algorithm.statistics;

import java.util.ArrayList;

import org.dpdns.timerverse.ninjabrain.algorithm.divine.IDivineContext;
import org.dpdns.timerverse.ninjabrain.algorithm.endereye.EnderEyeThrow;
import org.dpdns.timerverse.ninjabrain.algorithm.mcversion.McVersion;
import org.dpdns.timerverse.ninjabrain.algorithm.stronghold.Chunk;
import org.dpdns.timerverse.ninjabrain.algorithm.stronghold.Ring;
import org.dpdns.timerverse.ninjabrain.algorithm.stronghold.StrongholdConstants;
import org.dpdns.timerverse.ninjabrain.algorithm.util.Coords;

public class RayApproximatedPrior implements IPrior {

	ArrayList<Chunk> chunks;
	final IDivineContext divineContext;

	public RayApproximatedPrior(EnderEyeThrow r, IDivineContext divineContext, McVersion version) {
		this(r, 1.0 / 180.0 * Math.PI, divineContext, version);
	}

	public RayApproximatedPrior(EnderEyeThrow r, double tolerance, IDivineContext divineContext, McVersion version) {
		this.divineContext = divineContext;
		construct(r, tolerance, version);
	}

	private void construct(EnderEyeThrow r, double tolerance, McVersion version) {
		double range = 5000.0 / 16;
		chunks = new ArrayList<Chunk>();
		double phi = r.horizontalAngle() / 180.0 * Math.PI;
		double dx = -Math.sin(phi);
		double dz = Math.cos(phi);
		double ux = -Math.sin(phi - tolerance);
		double uz = Math.cos(phi - tolerance);
		double vx = -Math.sin(phi + tolerance);
		double vz = Math.cos(phi + tolerance);
		boolean majorX = Math.cos(phi) * Math.cos(phi) < 0.5;
		boolean majorPositive = majorX ? -Math.sin(phi) > 0 : Math.cos(phi) > 0;
		double origin_major = ((majorX ? r.x() : r.z()) - StrongholdConstants.getStrongholdChunkCoord(version)) / 16.0;
		double origin_minor = ((majorX ? r.z() : r.x()) - StrongholdConstants.getStrongholdChunkCoord(version)) / 16.0;
		double iter_start_major = getIterStartMajor(origin_major, origin_minor, ux, uz, vx, vz, majorX, majorPositive);
		double uk = majorX ? uz / ux : ux / uz;
		double vk = majorX ? vz / vx : vx / vz;
		boolean rightPositive = majorPositive ? vk - uk > 0 : uk - vk > 0;
		int i = (int) (majorPositive ? Math.ceil(iter_start_major) : Math.floor(iter_start_major));
		while ((majorX ? (i - iter_start_major) / dx : (i - iter_start_major) / dz) < range) {
			double minor_u = origin_minor + uk * (i - origin_major);
			double minor_v = origin_minor + vk * (i - origin_major);
			int j = (int) (rightPositive ? Math.ceil(minor_u) : Math.floor(minor_u));
			if (j < -StrongholdConstants.maxChunk)
				j = -StrongholdConstants.maxChunk;
			if (j > StrongholdConstants.maxChunk)
				j = StrongholdConstants.maxChunk;
			while (rightPositive ? j < minor_v : j > minor_v && j <= StrongholdConstants.maxChunk && j >= -StrongholdConstants.maxChunk) {
				Chunk chunk = majorX ? new Chunk(i, j) : new Chunk(j, i);

				int n = 2;
				double weight = 0;
				if (n == 1) {
					weight = strongholdDensity(chunk.x, chunk.z);
				} else {
					for (int k = 0; k < n; k++) {
						double x = chunk.x - 0.5 + k / (n - 1.0);
						for (int l = 0; l < n; l++) {
							double z = chunk.z - 0.5 + l / (n - 1.0);
							weight += strongholdDensity(x, z);
						}
					}
				}
				weight /= (double) n * n;

				chunk.weight = weight;
				chunks.add(chunk);
				j += rightPositive ? 1 : -1;
			}
			i += majorPositive ? 1 : -1;
		}
	}

	protected double strongholdDensity(double cx, double cz) {
		double relativeWeight = 1.0;
		double d2 = cx * cx + cz * cz;
		double chunkR = Math.sqrt(d2);
		if (chunkR <= Ring.get(0).outerRadiusPostSnapping && divineContext.hasDivine()) {
			int m = StrongholdConstants.snappingRadius;
			double w = 0;
			for (int i = -m; i <= m; i++) {
				for (int j = -m; j <= m; j++) {
					w += divineContext.getDensityAtAngleBeforeSnapping(Coords.getPhi(cx + i, cz + j));
				}
			}
			w /= (2 * m + 1) * (2 * m + 1);
			relativeWeight *= w * 2.0 * Math.PI;
		}
		return relativeWeight * ApproximatedDensity.density(cx, cz);
	}

	private double getIterStartMajor(double o_major, double o_minor, double ux, double uz, double vx, double vz, boolean majorX, boolean majorPositive) {
		if (o_major * o_major + o_minor * o_minor <= StrongholdConstants.maxChunk * StrongholdConstants.maxChunk)
			return o_major;
		double ox = majorX ? o_major : o_minor;
		double oz = majorX ? o_minor : o_major;
		double u_orth_mag = orthogonalComponent(-ox, -oz, ux, uz);
		double v_orth_mag = orthogonalComponent(-ox, -oz, vx, vz);
		if (u_orth_mag > 0 && v_orth_mag < 0) {
			double o_mag = Math.sqrt(ox * ox + oz * oz);
			double ix = ox / o_mag * StrongholdConstants.maxChunk;
			double iz = oz / o_mag * StrongholdConstants.maxChunk;
			double m1 = o_major + projectAndGetMajorComponent(ix - ox, iz - oz, ux, uz, majorX);
			double m2 = o_major + projectAndGetMajorComponent(ix - ox, iz - oz, vx, vz, majorX);
			return majorPositive ^ m1 > m2 ? m1 : m2;
		}
		double i_u_major = findCircleIntersection(ox, oz, ux, uz, StrongholdConstants.maxChunk, majorX);
		double i_v_major = findCircleIntersection(ox, oz, vx, vz, StrongholdConstants.maxChunk, majorX);
		if (i_u_major != 0 || i_v_major != 0) {
			if (i_u_major != 0 && i_v_major != 0) {
				return majorPositive ^ i_u_major > i_v_major ? i_u_major : i_v_major;
			}
			if (i_u_major != 0)
				return i_u_major;
			return i_v_major;
		}
		return o_major;
	}

	private double orthogonalComponent(double ax, double az, double ux, double uz) {
		double u_par_mag = ux * ax + uz * az;
		double u_par_x = ux * u_par_mag;
		double u_par_z = uz * u_par_mag;
		double u_orth_x = u_par_x - ax;
		double u_orth_z = u_par_z - az;
		return uz * u_orth_x - ux * u_orth_z;
	}

	private double projectAndGetMajorComponent(double ax, double az, double ux, double uz, boolean majorX) {
		double proj_mag = ax * ux + az * uz;
		return majorX ? (ux * proj_mag) : (uz * proj_mag);
	}

	private double findCircleIntersection(double ox, double oz, double ux, double uz, double r, boolean majorX) {
		double o_dot_u = ox * ux + oz * uz;
		double a = o_dot_u * o_dot_u + r * r - ox * ox - oz * oz;
		if (a < 0)
			return 0;
		double b = -o_dot_u - Math.sqrt(a);
		return majorX ? ox + b * ux : oz + b * uz;
	}

	@Override
	public Iterable<Chunk> getChunks() {
		return chunks;
	}

}
