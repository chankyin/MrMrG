package me.noip.chankyin.mrmrg.physics;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import me.noip.chankyin.mrmrg.geometry.RelativeVector;
import me.noip.chankyin.mrmrg.geometry.VectorD;
import me.noip.chankyin.mrmrg.utils.io.SavedObject;
import me.noip.chankyin.mrmrg.utils.io.SavedProperty;

@SavedObject(1)
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public abstract class MoveableSubstance extends Substance{
	@SavedProperty(1) @Getter @Setter @RelativeVector private VectorD velocity;

	protected MoveableSubstance(MoveableSubstance copy){
		super(copy);
		velocity = copy.velocity;
	}

	/**
	 * Net force on this object
	 *
	 * @return
	 */
	public VectorD netForce(){
		VectorD v = new VectorD(new double[getCenterOfMass().dimension()]);
		for(Substance substance : getSubstanceMap()){
			if(substance != this){
				v = v.add(PhysicsUtils.gForce(substance, this))
						.add(PhysicsUtils.eForce(substance, this));
			}
		}

		return v;
	}

	public void move(double dt){
		setCenterOfMass(getCenterOfMass().add(velocity.multiply(dt)));
	}
}
