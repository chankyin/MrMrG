package me.noip.chankyin.mrmrg.physics.simulator;

import lombok.Builder;

import me.noip.chankyin.mrmrg.geometry.VectorD;
import me.noip.chankyin.mrmrg.physics.MoveableSubstance;
import me.noip.chankyin.mrmrg.physics.Substance;
import me.noip.chankyin.mrmrg.physics.SubstanceMap;
import me.noip.chankyin.mrmrg.ui.render.viewport.Viewport;

public class Simulator{
	private final int dimension;
	private final SubstanceMap substanceMap;
	private Viewport viewport;
	private double timeRatio;

	@Builder
	public Simulator(int dimension, SubstanceMap substanceMap, Viewport viewport, double timeRatio){
		this.dimension = dimension;
		this.substanceMap = substanceMap;
		this.viewport = viewport;
		this.timeRatio = timeRatio;
	}

	private double timeElapsed = 0d;

	public void tick(double dt){
		timeElapsed += dt;
		dt *= timeRatio;
		for(Substance substance : substanceMap){
			if(substance instanceof MoveableSubstance){
				VectorD velocity = ((MoveableSubstance) substance).netForce() // force
						.divide(substance.getMass()) // acceleration
						.multiply(dt); // velocity
				((MoveableSubstance) substance).setVelocity(velocity);
			}
		}
		for(Substance substance : substanceMap){
			if(substance instanceof MoveableSubstance){
				((MoveableSubstance) substance).move(dt);
			}
		}
	}
}
