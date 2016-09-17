package me.noip.chankyin.mrmrg.ui.render.viewport;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import me.noip.chankyin.mrmrg.geometry.Dimension;
import me.noip.chankyin.mrmrg.geometry.PositionVector;
import me.noip.chankyin.mrmrg.geometry.RelativeVector;
import me.noip.chankyin.mrmrg.geometry.VectorD;
import me.noip.chankyin.mrmrg.utils.io.SavedObject;
import me.noip.chankyin.mrmrg.utils.io.SavedProperty;

@SavedObject(1)
@NoArgsConstructor
public class Viewport3D implements Viewport{
	@SavedProperty(1) @Getter @Setter @PositionVector private @Dimension(3) VectorD cameraPos;
	@SavedProperty(1) @Getter @Setter @RelativeVector private @Dimension(3) VectorD cameraOrientation;
}
