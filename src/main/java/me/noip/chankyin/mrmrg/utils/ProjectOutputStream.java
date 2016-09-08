package me.noip.chankyin.mrmrg.utils;

import lombok.SneakyThrows;
import me.noip.chankyin.mrmrg.math.VectorD;
import me.noip.chankyin.mrmrg.object.Project;
import me.noip.chankyin.mrmrg.object.Saveable;

import java.io.Closeable;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Collection;

public class ProjectOutputStream extends DataOutputStream implements Closeable{
	public ProjectOutputStream(OutputStream out){
		super(out);
	}

	public void write(Project project){
		project.write(this);
	}

	@SneakyThrows({IOException.class})
	public void writeString(String string){
		byte[] bytes = string.getBytes();
		writeShort((short) bytes.length);
		write(bytes);
	}

	@SneakyThrows({IOException.class})
	public void writeVector(VectorD vector){
		writeByte(vector.getDimension());
		for(Double aDouble : vector.getCoords()){
			writeDouble(aDouble);
		}
	}

	@SneakyThrows({IOException.class})
	public <T extends Saveable> void writeArray(Collection<T> coll){
		writeShort(coll.size());
		for(T t : coll){
			t.write(this);
		}
	}
}
