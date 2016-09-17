package me.noip.chankyin.mrmrg.physics;

import lombok.Getter;
import lombok.NoArgsConstructor;

import me.noip.chankyin.mrmrg.geometry.VectorD;
import me.noip.chankyin.mrmrg.ui.render.Canvas2D;
import me.noip.chankyin.mrmrg.ui.render.Canvas3D;
import me.noip.chankyin.mrmrg.utils.io.FillWithOwner;
import me.noip.chankyin.mrmrg.utils.io.SavedObject;
import me.noip.chankyin.mrmrg.utils.io.SavedProperty;

import static me.noip.chankyin.mrmrg.utils.Utils.colorRGB;

@SavedObject(1)
@NoArgsConstructor
public class SphereModel extends Model{
	@FillWithOwner @Getter private Substance substance;
	@SavedProperty(1) @Getter private double radius;
	@SavedProperty(1) @Getter private int color;

	public SphereModel(Substance owner, SphereModel other){
		substance = owner;
		radius = other.radius;
		color = other.color;
	}

	@Override
	public void render(Canvas2D renderer){
		renderer.fillSector(substance.getCenterOfMass(), new VectorD(radius, radius), colorRGB(color), 0, 360);
	}

	@Override
	public void render(Canvas3D renderer){

	}

	@Override
	public Model copy(){
		return null;
	}
}
