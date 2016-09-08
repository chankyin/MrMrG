package me.noip.chankyin.mrmrg.utils;

import lombok.SneakyThrows;
import me.noip.chankyin.mrmrg.math.VectorD;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

public class ProjectInputStream extends DataInputStream{
	public ProjectInputStream(InputStream in){
		super(in);
	}

	@SneakyThrows({IOException.class})
	public String readString(){
		short length = readShort();
		byte[] bytes = new byte[length];
		read(bytes);
		return new String(bytes);
	}

	@SneakyThrows({IOException.class})
	public VectorD readVector(){
		double[] array = new double[readByte()];
		for(int i = 0; i < array.length; i++){
			array[i] = readDouble();
		}
		return new VectorD(array);
	}

	@SneakyThrows({IOException.class})
	public <T> List<T> readArray(Function<ProjectInputStream, T> constructor){
		short count = readShort();
		List<T> out = new ArrayList<>(count);
		for(int i = 0; i < count; i++){
			out.add(constructor.apply(this));
		}
		return out;
	}
}
