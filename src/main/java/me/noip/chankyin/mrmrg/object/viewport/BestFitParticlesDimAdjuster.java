package me.noip.chankyin.mrmrg.object.viewport;

import lombok.SneakyThrows;
import me.noip.chankyin.mrmrg.object.Particle;
import me.noip.chankyin.mrmrg.object.Project;
import me.noip.chankyin.mrmrg.object.Substance;
import me.noip.chankyin.mrmrg.utils.ProjectInputStream;
import me.noip.chankyin.mrmrg.utils.ProjectOutputStream;

import java.io.IOException;

public class BestFitParticlesDimAdjuster extends DimAdjuster{
	private Project project;
	private FourSides padding;

	@Override
	public BestFitParticlesDimAdjuster init(Project project, ProjectInputStream is){
		this.project = project;
		padding = FourSides.read(is);
		return this;
	}

	public BestFitParticlesDimAdjuster init(Project project, FourSides padding){
		this.project = project;
		this.padding = padding;
		return this;
	}

	@Override
	@SneakyThrows({IOException.class})
	public void write(ProjectOutputStream os){
		os.writeByte(getId());
		padding.write(os);
	}

	@Override
	public byte getId(){
		return 0;
	}

	@Override
	public Dimensions2D adjust(Dimensions2D old){
		double xmin = Double.MAX_VALUE;
		double xmax = -Double.MAX_VALUE;
		double ymin = Double.MAX_VALUE;
		double ymax = -Double.MAX_VALUE;
		boolean empty = true;
		for(Substance substance : project.getDisplayedSubstances().values()){
			if(!(substance instanceof Particle)){
				continue;
			}
			empty = false;
			xmin = Math.min(xmin, substance.getPosition().getX());
			xmax = Math.max(xmax, substance.getPosition().getX());
			ymin = Math.min(ymin, substance.getPosition().getY());
			ymax = Math.max(ymax, substance.getPosition().getY());
		}
		if(empty){
			return old;
		}

		final double epsilon = 0.0000000001;
		if(xmax - xmin < epsilon){
			xmin -= 5;
			xmax += 5;
		}
		if(ymax - ymin < epsilon){
			ymin -= 5;
			ymax += 5;
		}

		double tmpWidth = xmax - xmin;
		double tmpHeight = ymax - ymin;

		switch(padding.left.unit){
			case RELATIVE:
				xmin -= tmpWidth * padding.left.value;
				break;
			case ABSOLUTE:
				xmin -= padding.left.value;
				break;
		}
		switch(padding.right.unit){
			case RELATIVE:
				xmax += tmpWidth * padding.right.value;
				break;
			case ABSOLUTE:
				xmax += padding.right.value;
				break;
		}
		switch(padding.top.unit){
			case RELATIVE:
				ymin -= tmpHeight * padding.top.value;
				break;
			case ABSOLUTE:
				ymin -= padding.top.value;
				break;
		}
		switch(padding.bottom.unit){
			case RELATIVE:
				ymax += tmpHeight * padding.bottom.value;
				break;
			case ABSOLUTE:
				ymax += padding.bottom.value;
				break;
		}

		double width = xmax - xmin;
		double yWidth = (ymax - ymin) / old.height * old.width;

		Dimensions2D.Dimensions2DBuilder builder;
		if(width > yWidth){
			builder = Dimensions2D.builder()
					.x0(xmin)
					.x1(xmax);
			double ycenter = (ymin + ymax) / 2;
			double halfHeight = width / old.width * old.height / 2;
			builder.y0(ycenter - halfHeight)
					.y1(ycenter + halfHeight);
		}else{
			builder = Dimensions2D.builder()
					.y0(ymin)
					.y1(ymax);
			double xcenter = (xmin + xmax) / 2;
			double halfWidth = yWidth / 2;
			builder.x0(xcenter - halfWidth)
					.x1(xcenter + halfWidth);
		}
		return builder.build();
	}
}
