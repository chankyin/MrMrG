package me.noip.chankyin.mrmrg.physics;

import lombok.experimental.UtilityClass;

import me.noip.chankyin.mrmrg.geometry.VectorD;

@UtilityClass
public class PhysicsUtils{
	public final static double G = 6.6742E-11;
	public final static double EPSILON_0 = 1d / (299792458d * 299792458d) / 1.2566370614359172953850573533118E-6;
	public final static double ε0 = EPSILON_0;

	public static VectorD gForce(Substance by, MoveableSubstance on){ // Newton's law of gravitation
		double magnitude = G * by.getMass() * on.getMass() /
				by.getCenterOfMass().distanceSquared(on.getCenterOfMass());
		VectorD unit = by.getCenterOfMass().subtract(on.getCenterOfMass()).unit();
		return unit.multiply(magnitude);
	}

	public static VectorD eForce(Substance by, MoveableSubstance on){ // Coulomb's law
		double magnitude = by.getCharge() * on.getCharge() /
				(4 * Math.PI * ε0 * by.getCenterOfMass().distanceSquared(on.getCenterOfMass()));
		VectorD unit = on.getCenterOfMass().subtract(by.getCenterOfMass()).unit(); // repulsion force
		return unit.multiply(magnitude);
	}
}
