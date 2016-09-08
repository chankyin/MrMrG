package me.noip.chankyin.mrmrg.utils;

import me.noip.chankyin.mrmrg.math.VectorD;
import me.noip.chankyin.mrmrg.object.Substance;

import static java.lang.Math.PI;

public class Physics{
	public final static double G = 6.67408e-11;
	public final static double EPSILON_0 = 8.854187817e-12;

	/**
	 * Returns the force exerted on <code>on</code> by <code>by</code>, which is a vector pointing from <code>on</code> towards <code>by</code>
	 *
	 * @param by
	 * @param on
	 * @return gravitational attraction force
	 */
	public static VectorD gForce(Substance by, Substance on){
		double magnitude = G * on.getMass() * by.getMass() / // GMm
				by.getPosition().distanceSquared(on.getPosition()); // rr
		// <MrMrG>!!!
		VectorD unit = by.getPosition().subtract(on.getPosition()).unit();
		return unit.multiply(magnitude);
	}

	/**
	 * Returns the <strong>repulsion</strong> force on <code>on</code> by <code>by</code>. It may point from <code>by</code> to <code>on</code>, or the opposite, depending on whether the charges have the same signum
	 *
	 * @param by
	 * @param on
	 * @return electric repulsion force
	 */
	public static VectorD eForce(Substance by, Substance on){
		double magnitude = by.getCharge() * on.getCharge() /
				(4 * PI * EPSILON_0 * by.getPosition().distanceSquared(on.getPosition()));
		VectorD unit = on.getPosition().subtract(by.getPosition()).unit();
		return unit.multiply(magnitude);
	}

	@Size(2)
	public static double[] perfectCollision(double m1, double m2, double u1, double u2){
		double[] out = new double[2];
		out[0] = 2 * m2 * u2 / (m1 + m2) + (m1 - m2) * u1 / (m1 + m2);
		out[1] = out[0] + u1 - u2;
		return out;
	}

	public static void perfectCollision(Substance s1, Substance s2){
		if(s1.isStationary()){
			s2.setVelocity(s2.getVelocity().multiply(-1)); // TODO implement angles
			return;
		}
		if(s2.isStationary()){
			s1.setVelocity(s1.getVelocity().multiply(-1)); // TODO implement angles
			return;
		}

		int dimension = s1.getVelocity().getDimension();
		double[][] result = new double[dimension][];
		for(int i = 0; i < dimension; i++){
			result[dimension] = perfectCollision(s1.getMass(), s2.getMass(),
					s1.getVelocity().getCoords(i), s2.getVelocity().getCoords(i));
		}
		double[] v1 = new double[dimension];
		double[] v2 = new double[dimension];
		for(int i = 0; i < dimension; i++){
			v1[i] = result[i][0];
			v2[i] = result[i][1];
		}
		s1.setVelocity(new VectorD(v1));
		s2.setVelocity(new VectorD(v2));
	}
}
