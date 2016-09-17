package me.noip.chankyin.mrmrg.physics;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import me.noip.chankyin.mrmrg.geometry.PositionVector;
import me.noip.chankyin.mrmrg.geometry.VectorD;
import me.noip.chankyin.mrmrg.utils.io.FillWithOwner;
import me.noip.chankyin.mrmrg.utils.io.SavedObject;
import me.noip.chankyin.mrmrg.utils.io.SavedProperty;

@SavedObject(1)
@NoArgsConstructor
@EqualsAndHashCode
public abstract class Substance{
	@FillWithOwner @Getter @Setter private SubstanceMap substanceMap;
	@SavedProperty(1) @Getter private int id;
	@SavedProperty(1) @Getter @Setter @PositionVector private VectorD centerOfMass;
	@SavedProperty(1) @Getter @Setter private Model model;
	@SavedProperty(1) @Getter private double mass;
	@SavedProperty(1) @Getter private double charge;

	protected Substance(Substance copy){
		substanceMap = copy.getSubstanceMap();
		id = copy.id;
		centerOfMass = new VectorD(copy.centerOfMass);
		model = copy.model.copy();
		mass = copy.mass;
		charge = copy.charge;
	}

	public abstract Substance copy();
}
