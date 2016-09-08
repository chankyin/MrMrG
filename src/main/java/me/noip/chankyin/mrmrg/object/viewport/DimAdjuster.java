package me.noip.chankyin.mrmrg.object.viewport;

import lombok.NonNull;
import lombok.SneakyThrows;
import me.noip.chankyin.mrmrg.object.Project;
import me.noip.chankyin.mrmrg.object.Saveable;
import me.noip.chankyin.mrmrg.utils.ProjectInputStream;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public abstract class DimAdjuster implements Saveable{
	public final static Map<Byte, Class<? extends DimAdjuster>> TYPES = new HashMap<>();

	@SneakyThrows({InstantiationException.class, IllegalAccessException.class})
	public static void register(Class<? extends DimAdjuster> type){
		DimAdjuster dimAdjuster = type.newInstance();
		TYPES.put(dimAdjuster.getId(), type);
	}

	@SneakyThrows({IOException.class})
	public static DimAdjuster readAdjuster(Project project, ProjectInputStream is){
		byte b = is.readByte();
		return getType(project, is, b);
	}

	protected abstract BestFitParticlesDimAdjuster init(Project project, ProjectInputStream is);

	public abstract byte getId();

	public abstract Dimensions2D adjust(Dimensions2D old);

	@SneakyThrows({InstantiationException.class, IllegalAccessException.class})
	public static DimAdjuster getType(@NonNull Project project, @NonNull ProjectInputStream is, byte id){
		if(!TYPES.containsKey(id)){
			throw new IllegalArgumentException("Type not found");
		}
		return TYPES.get(id).newInstance().init(project, is);
	}
}
