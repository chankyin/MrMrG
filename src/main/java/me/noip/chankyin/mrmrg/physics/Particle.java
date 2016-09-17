package me.noip.chankyin.mrmrg.physics;

import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import me.noip.chankyin.mrmrg.utils.io.SavedObject;

@SavedObject(1)
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class Particle extends MoveableSubstance{
	public Particle(Particle particle){
		super(particle);
	}

	@Override
	public Substance copy(){
		return new Particle(this);
	}
}
