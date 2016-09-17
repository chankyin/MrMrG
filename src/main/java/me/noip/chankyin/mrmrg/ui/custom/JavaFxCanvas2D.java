package me.noip.chankyin.mrmrg.ui.custom;

import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.PixelWriter;
import javafx.scene.paint.Color;
import javafx.scene.shape.ArcType;

import me.noip.chankyin.mrmrg.geometry.*;
import me.noip.chankyin.mrmrg.ui.render.Canvas2D;
import me.noip.chankyin.mrmrg.ui.render.viewport.Viewport2D;

public class JavaFxCanvas2D extends Canvas implements Canvas2D{
	private final Viewport2D viewport;
	private final VectorD size;
	private final GraphicsContext graphics;
	private final PixelWriter pixelWriter;

	public JavaFxCanvas2D(Viewport2D viewport, @Dimension(2) VectorD size){
		this.viewport = viewport;
		this.size = size;
		graphics = getGraphicsContext2D();
		pixelWriter = graphics.getPixelWriter();
	}

	@Override
	public void drawPixel(@Dimension(2) @PositionVector VectorD vector, Color color){
		pixelWriter.setColor((int) x_(vector.getX()), (int) y_(vector.getY()), color);
	}

	@Override
	public void drawLine(@Dimension(2) @PositionVector VectorD start, @Dimension(2) @PositionVector VectorD end, Color color){
		graphics.setStroke(color);
		graphics.strokeLine(start.getX(), start.getY(), end.getX(), end.getY());
	}

	@Override
	public void drawArc(@Dimension(2) @PositionVector VectorD center, @Dimension(2) @RelativeVector VectorD radii, Color color, @Degrees double from, @Degrees double to){
		graphics.setStroke(color);
		graphics.strokeArc(center.getX(), center.getY(), radii.getX() * 2, radii.getY() * 2, from, to, ArcType.ROUND);
	}

	@Override
	public void fillRectangle(@Dimension(2) @PositionVector VectorD from, @Dimension(2) @PositionVector VectorD to, Color color){
		graphics.setFill(color);
		VectorD dim = to.subtract(from);
		graphics.fillRect(from.getX(), from.getY(), dim.getX(), dim.getY());
	}

	@Override
	public void fillSector(@Dimension(2) @PositionVector VectorD center, @Dimension(2) @RelativeVector VectorD radii, Color color, @Degrees double from, @Degrees double to){
		graphics.setFill(color);
		graphics.fillArc(center.getX(), center.getY(), radii.getX() * 2, radii.getY() * 2, from, to, ArcType.ROUND);
	}

	/**
	 * Convert project coordinate to display coordinate
	 *
	 * @param x
	 * @param relative whether the passed value is relative, not positional
	 * @return
	 */
	private double x_(double x, boolean relative){
		return (x - (relative ? 0 : viewport.getX0())) / viewport.getWidth() * size.getX();
	}

	/**
	 * Convert project coordinate to display coordinate
	 *
	 * @param y
	 * @param relative whether the passed value is relative, not positional
	 * @return
	 */
	private double y_(double y, boolean relative){
		return (y - (relative ? 0 : viewport.getY0())) / viewport.getHeight() * size.getY();
	}

	/**
	 * Convert display coordinate to project coordinate
	 *
	 * @param x
	 * @param relative whether the passed value is relative, not positional
	 * @return
	 */
	private double _x(double x, boolean relative){
		return x / size.getX() * viewport.getWidth() + (relative ? 0 : viewport.getX0());
	}

	/**
	 * Convert display coordinate to project coordinate
	 *
	 * @param y
	 * @param relative whether the passed value is relative, not positional
	 * @return
	 */
	private double _y(double y, boolean relative){
		return y / size.getY() * viewport.getHeight() + (relative ? 0 : viewport.getY0());
	}

	/**
	 * Convert project coordinate to display coordinate
	 *
	 * @param x
	 * @return
	 */
	private double x_(double x){
		return x_(x, false);
	}

	/**
	 * Convert project coordinate to display coordinate
	 *
	 * @param y
	 * @return
	 */
	private double y_(double y){
		return y_(y, false);
	}

	/**
	 * Convert display coordinate to project coordinate
	 *
	 * @param x
	 * @return
	 */
	private double _x(double x){
		return _x(x, false);
	}

	/**
	 * Convert display coordinate to project coordinate
	 *
	 * @param y
	 * @return
	 */
	private double _y(double y){
		return _y(y, false);
	}
}
