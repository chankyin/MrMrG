package me.noip.chankyin.mrmrg.object.viewport;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.SneakyThrows;
import me.noip.chankyin.mrmrg.object.Saveable;
import me.noip.chankyin.mrmrg.utils.ProjectInputStream;
import me.noip.chankyin.mrmrg.utils.ProjectOutputStream;

import java.io.IOException;

@AllArgsConstructor
public class Dimensions2D implements Saveable{
	public final double x0;
	public final double x1;
	public final double y0;
	public final double y1;
	public final double width;
	public final double height;

	@Builder
	public Dimensions2D(double x0, double x1, double y0, double y1){
		this.x0 = x0;
		this.x1 = x1;
		this.y0 = y0;
		this.y1 = y1;

		width = x1 - x0;
		height = y1 - y0;
	}

	public static Dimensions2D widthHeight(double x0, double y0, double width, double height){
		return new Dimensions2D(x0, x0 + width, y0, y0 + height);
	}

	@SneakyThrows({IOException.class})
	public static Dimensions2D read(ProjectInputStream is){
		return new Dimensions2D(is.readDouble(), is.readDouble(), is.readDouble(), is.readDouble());
	}

	@Override
	@SneakyThrows({IOException.class})
	public void write(ProjectOutputStream os){
		os.writeDouble(x0);
		os.writeDouble(x1);
		os.writeDouble(y0);
		os.writeDouble(y1);
	}

	public Dimensions2D propChangeHorizontal(double newX0, double newX1){
		Dimensions2DBuilder builder = builder().x0(newX0).x1(newX1);
		double newHalf = (newX1 - newX0) / width * height / 2;
		double center = (y0 + y1) / 2;
		return builder.y0(center - newHalf).y1(center + newHalf).build();
	}


	public Dimensions2D propChangeVertical(double newY0, double newY1){
		Dimensions2DBuilder builder = builder().y0(newY0).y1(newY1);
		double newHalf = (newY1 - newY0) / height * width / 2;
		double center = (x0 + x1) / 2;
		return builder.x0(center - newHalf).x1(center + newHalf).build();
	}
}
