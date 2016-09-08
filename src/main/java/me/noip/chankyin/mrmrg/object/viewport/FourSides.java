package me.noip.chankyin.mrmrg.object.viewport;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.experimental.FieldDefaults;
import me.noip.chankyin.mrmrg.object.Saveable;
import me.noip.chankyin.mrmrg.utils.ProjectInputStream;
import me.noip.chankyin.mrmrg.utils.ProjectOutputStream;

import java.io.IOException;

@FieldDefaults(makeFinal = true, level = AccessLevel.PUBLIC)
@RequiredArgsConstructor
public class FourSides implements Saveable{
	Property top;
	Property left;
	Property bottom;
	Property right;

	public static FourSides defaultInstance(){
		return new FourSides(
				new Property(Unit.RELATIVE, 0.05),
				new Property(Unit.RELATIVE, 0.05),
				new Property(Unit.RELATIVE, 0.05),
				new Property(Unit.RELATIVE, 0.05)
		);
	}

	public static FourSides read(ProjectInputStream is){
		return new FourSides(Property.read(is), Property.read(is), Property.read(is), Property.read(is));
	}

	@Override
	public void write(ProjectOutputStream os){
		top.write(os);
		left.write(os);
		bottom.write(os);
		right.write(os);
	}

	@FieldDefaults(makeFinal = true, level = AccessLevel.PUBLIC)
	@RequiredArgsConstructor
	public static class Property implements Saveable{
		Unit unit;
		double value;

		@SneakyThrows({IOException.class})
		public static Property read(ProjectInputStream is){
			Unit unit = Unit.values()[is.readByte()];
			double value = is.readDouble();
			return new Property(unit, value);
		}

		@Override
		@SneakyThrows({IOException.class})
		public void write(ProjectOutputStream os){
			os.writeByte(unit.ordinal());
			os.writeDouble(value);
		}
	}

	public enum Unit{
		ABSOLUTE,
		RELATIVE
	}
}
