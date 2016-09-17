package me.noip.chankyin.mrmrg.ui.render.viewport;

import lombok.Getter;
import lombok.NoArgsConstructor;

import me.noip.chankyin.mrmrg.utils.io.SavedObject;
import me.noip.chankyin.mrmrg.utils.io.SavedProperty;
import me.noip.chankyin.mrmrg.utils.io.Unserialized;

@SavedObject(1)
@NoArgsConstructor
@Getter
public class Viewport2D implements Viewport, Unserialized{
	@SavedProperty(1) private double x0;
	@SavedProperty(1) private double x1;
	@SavedProperty(1) private double y0;
	@SavedProperty(1) private double y1;
	private double width;
	private double height;

	public Viewport2D(double x0, double x1, double y0, double y1){
		this.x0 = x0;
		this.x1 = x1;
		this.y0 = y0;
		this.y1 = y1;
		postUnserialize();
	}

	@Override
	public void postUnserialize(){
		width = x1 - x0;
		height = y1 - y0;
	}
}
