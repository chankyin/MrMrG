package me.noip.chankyin.mrmrg.object;

import lombok.Builder;
import lombok.Getter;
import lombok.SneakyThrows;
import me.noip.chankyin.mrmrg.math.VectorD;
import me.noip.chankyin.mrmrg.object.viewport.Dimensions2D;
import me.noip.chankyin.mrmrg.ui.disp.DisplayPanel;
import me.noip.chankyin.mrmrg.utils.ProjectInputStream;
import me.noip.chankyin.mrmrg.utils.ProjectOutputStream;

import java.awt.Color;
import java.awt.Graphics;
import java.io.IOException;

public class Particle extends Substance{
	@Getter private int rgb;

	public Particle(Particle copy){
		super(copy);
		rgb = copy.rgb;
	}

	@Builder
	public Particle(Project project, int id, VectorD position, VectorD velocity, double mass, double charge, double collisionRadius, int rgb){
		super(project, id, position, velocity, mass, charge, collisionRadius);
		this.rgb = rgb;
	}

	@SneakyThrows({IOException.class})
	public static Particle read(ProjectInputStream is, Project project){
		return new Particle(
				project,
				is.readInt(),       // id
				is.readVector(),    // position
				is.readVector(),    // velocity
				is.readDouble(),    // mass
				is.readDouble(),    // charge
				is.readDouble(),    // radius
				is.readInt()        // rgb
		);
	}

	@Override
	@SneakyThrows({IOException.class})
	public void write(ProjectOutputStream os){
		os.writeInt(getId());
		os.writeInt(rgb);
		os.writeVector(getPosition());
		os.writeVector(getVelocity());
		os.writeDouble(getMass());
		os.writeDouble(getCharge());
		os.writeDouble(getCollisionRadius());
	}

	@Override
	public void draw(DisplayPanel panel){
		Dimensions2D dim = panel.getProject().getViewportDimensions();
		if(getPosition().getX() < dim.x0 || getPosition().getX() > dim.x1 ||
				getPosition().getY() < dim.y0 || getPosition().getY() > dim.y1){
			return;
		}

		int centerX = panel.xProjectToDisplay(getPosition().getX());
		int centerY = panel.yProjectToDisplay(getPosition().getY());

		int horizRad = Math.max(panel.xProjectToDisplay(getCollisionRadius(), false), 5);
		int vertRad = Math.max(panel.yProjectToDisplay(getCollisionRadius(), false), 5);

		Graphics graphics = panel.getTemporalGraphics();
		graphics.setColor(new Color(rgb));
		graphics.fillArc(centerX - horizRad, centerY - vertRad, horizRad * 2, vertRad * 2, 0, 360);
	}

	@Override
	public Substance copy(){
		return new Particle(this);
	}

	@Override
	public void setPosition(VectorD position){
		super.setPosition(position);

		onDisplayPropertiesChanged();
	}

	@Override
	public void setCollisionRadius(double collisionRadius){
		super.setCollisionRadius(collisionRadius);

		onDisplayPropertiesChanged();
	}

	public void setRgb(int rgb){
		this.rgb = rgb;

		onDisplayPropertiesChanged();
	}

	private void onDisplayPropertiesChanged(){
		if(getProject().getCurrentScreen() != null){
			getProject().getCurrentScreen().getDisplayPanel().repaint();
		}
	}
}
