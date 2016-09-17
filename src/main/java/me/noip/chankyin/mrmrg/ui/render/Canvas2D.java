package me.noip.chankyin.mrmrg.ui.render;

import javafx.scene.paint.Color;

import me.noip.chankyin.mrmrg.geometry.*;

public interface Canvas2D{
	public abstract void drawPixel(@Dimension(2) @PositionVector VectorD vector, Color color);

	public abstract void drawLine(@Dimension(2) @PositionVector VectorD start, @Dimension(2) @PositionVector VectorD end, Color color);

	public abstract void drawArc(@Dimension(2) @PositionVector VectorD center, @Dimension(2) @RelativeVector VectorD radii, Color color, @Degrees double from, @Degrees double to);

	public abstract void fillRectangle(@Dimension(2) @PositionVector VectorD from, @Dimension(2) @PositionVector VectorD to, Color color);

	public abstract void fillSector(@Dimension(2) @PositionVector VectorD center, @Dimension(2) @RelativeVector VectorD radii, Color color, @Degrees double from, @Degrees double to);
}
