package me.noip.chankyin.mrmrg.object;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import me.noip.chankyin.mrmrg.math.Dimension;
import me.noip.chankyin.mrmrg.math.PositionVector;
import me.noip.chankyin.mrmrg.math.RelativeVector;
import me.noip.chankyin.mrmrg.math.VectorD;
import me.noip.chankyin.mrmrg.utils.Physics;

@AllArgsConstructor
public abstract class Substance implements Visible, Saveable{
	@Getter @NonNull private final Project project;
	@Getter private final int id;
	@Getter @Setter @PositionVector @Dimension(2) private VectorD position;
	@Getter @Setter @RelativeVector @Dimension(2) private VectorD velocity;
	@Getter @Setter private double mass;
	@Getter @Setter private double charge;
	@Getter @Setter private double collisionRadius; // TODO deprecate

	protected Substance(Substance copy){
		project = copy.project;
		id = copy.id;
		position = copy.position;
		velocity = copy.velocity;
		mass = copy.mass;
		charge = copy.charge;
		collisionRadius = copy.collisionRadius;
	}

	public VectorD getNetForces(){
		VectorD force = VectorD.ZERO_2;
		for(Substance substance : project.getDisplayedSubstances().values()){
			force = force.add(substance.gForceOn(this)).add(substance.eForceOn(this));
		}
		return force;
	}

	public VectorD gForceOn(Substance other){
		return Physics.gForce(this, other);
	}

	public VectorD eForceOn(Substance other){
		return Physics.eForce(this, other);
	}

	public Substance addVelocity(VectorD dv){
		velocity = velocity.add(dv);
		return this;
	}

	public Substance move(double virtualTimeElapsed){
		VectorD displacement = velocity.multiply(virtualTimeElapsed); // m = ms-1 * s
		//noinspection UnnecessaryLocalVariable
		VectorD newPos = position.add(displacement);

		// check collision: disabled
//		double distance = displacement.modulus();
//		double maxBase = distance + collisionRadius;
//		for(Substance other : project.getDisplayedSubstances().values()){
//			if(other == this){
//				continue;
//			}
//			double maxDistance = distance + collisionRadius + other.collisionRadius;
//			if(maxDistance * maxDistance < position.distanceSquared(other.position)){ // chance to collide
//				double radiusSum = collisionRadius + other.collisionRadius;
//				if(VectorD.pointLineDistanceSquared2D(position, newPos, other.position) < radiusSum * radiusSum){
		// what to do here?
//				}
//			}
//		}

		position = newPos;
		return this;
	}

	public boolean isStationary(){
		return false;
	}

	public abstract Substance copy();
}
