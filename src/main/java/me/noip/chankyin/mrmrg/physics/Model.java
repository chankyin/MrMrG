package me.noip.chankyin.mrmrg.physics;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import me.noip.chankyin.mrmrg.ui.render.Canvas2D;
import me.noip.chankyin.mrmrg.ui.render.Canvas3D;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
public abstract class Model{
	public abstract void render(Canvas2D renderer);

	public abstract void render(Canvas3D renderer);

	public abstract Model copy();
}
