package me.noip.chankyin.mrmrg.utils.io;

import java.io.FilterOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.nio.ByteOrder;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import lombok.NonNull;
import lombok.SneakyThrows;

import org.apache.commons.lang3.ArrayUtils;

import me.noip.chankyin.mrmrg.utils.Utils;

public class SavedObjectOutputStream extends FilterOutputStream{
	private final ByteOrder byteOrder;
	private final Map<String, Short> writtenVersions = new HashMap<>();

	public SavedObjectOutputStream(@NonNull OutputStream out){
		this(out, ByteOrder.BIG_ENDIAN);
	}

	public SavedObjectOutputStream(@NonNull OutputStream out, ByteOrder byteOrder){
		super(out);
		this.byteOrder = byteOrder;
	}

	public void writeByte(byte b){
		writeBytes(b);
	}

	public void writeShort(short s){
		writeBytes((byte) (s >>> 8), (byte) s);
	}

	public void writeInt(int i){
		writeBytes((byte) (i >>> 24), (byte) (i >>> 16), (byte) (i >>> 8), (byte) i);
	}

	public void writeLong(long l){
		writeBytes((byte) (l >>> 56), (byte) (l >>> 48), (byte) (l >>> 40), (byte) (l >>> 32),
				(byte) (l >>> 24), (byte) (l >>> 16), (byte) (l >>> 8), (byte) l);
	}

	public void writeIntVarSize(long num, int size){
		byte[] bytes = new byte[size];
		for(int i = 0; i < size; i++){
			bytes[size - i - 1] = (byte) (num >>> (i * 8));
		}
		writeBytes(bytes);
	}

	public void writeBoolean(boolean bool){
		writeByte((byte) (bool ? 1 : 0));
	}

	public void writeFloat(float f){
		writeInt(Float.floatToIntBits(f));
	}

	public void writeDouble(double d){
		writeLong(Double.doubleToLongBits(d));
	}

	public void writeChar(char c){
		writeShort((short) c);
	}

	public void writeString(String s){
		writeString(s, 3);
	}

	@SneakyThrows(IOException.class)
	public void writeString(String s, int size){
		byte[] bytes = s.getBytes();
		writeIntVarSize(bytes.length, size);
		write(bytes); // do not make it byte-order dependent!
	}

	private boolean writeVersion(String className, short version){
		if(!writtenVersions.containsKey(className)){
			writtenVersions.put(className, version);
			writeBoolean(true);
			writeString(className);
			writeShort(version);
			return true;
		}else{
			writeBoolean(false);
			return false;
		}
	}

	public void writeSavedObject(Object object){
		if(object == null){
			writeBoolean(false);
			return;
		}
		if(object.getClass().getDeclaredAnnotation(SavedObject.class) == null){
			throw new IllegalArgumentException("Cannot write non-@SavedObject");
		}
		writeHierarchyVersions(object.getClass());
		if(object instanceof Serialized){
			((Serialized) object).preSerialize();
		}
		Utils.getAllFields(object.getClass(), s -> s.getDeclaredAnnotation(SavedObject.class) != null)
				.stream().filter(field -> {
			SavedProperty annotation = field.getAnnotation(SavedProperty.class);
			return annotation != null && annotation.removed() == SavedProperty.VERSION_NIL;
		}).forEachOrdered(field -> writeField(field, object));
	}

	/**
	 * <p>Writes hierarchy <code>@SavedObject D extends @SavedObject C extends B extends A</code> in this pattern:</p>
	 * <p>{@code true}, {@code D}, {@code true}, {@code C}, {@code false}</p>
	 *
	 * @param clazz bottom class in hierarchy to write
	 */
	private void writeHierarchyVersions(Class<?> clazz){
		do{
			SavedObject annotation = clazz.getDeclaredAnnotation(SavedObject.class);
			if(annotation == null){
				break;
			}
			if(!writeVersion(clazz.getName(), annotation.value())){
				return;
			}
			clazz = clazz.getSuperclass();
		}while(clazz != Object.class && clazz != null);
		writeBoolean(false);
	}

	@SneakyThrows({IllegalAccessException.class, ClassNotFoundException.class})
	private void writeField(Field field, Object instance){
		if(!field.isAccessible()){
			field.setAccessible(true);
		}
		Class<?> type = field.getType();
		if(type.isAssignableFrom(byte.class)){
			writeByte(field.getByte(instance));
		}else if(type.isAssignableFrom(short.class)){
			writeShort(field.getShort(instance));
		}else if(type.isAssignableFrom(int.class)){
			writeInt(field.getInt(instance));
		}else if(type.isAssignableFrom(long.class)){
			writeLong(field.getLong(instance));
		}else if(type.isAssignableFrom(float.class)){
			writeFloat(field.getFloat(instance));
		}else if(type.isAssignableFrom(double.class)){
			writeDouble(field.getDouble(instance));
		}else if(type.isAssignableFrom(boolean.class)){
			writeBoolean(field.getBoolean(instance));
		}else if(type.isAssignableFrom(char.class)){
			writeChar(field.getChar(instance));
		}else if(type.isAssignableFrom(String.class)){
			writeString((String) field.get(instance));
		}else if(type.isAssignableFrom(Class.class)){
			writeString(((Class) field.get(instance)).getName());
		}else if(type.isEnum()){
			writeString(((Enum) field.get(instance)).name());
		}else if(type.isAssignableFrom(Collection.class)){
			Type genericType = ((ParameterizedType) field.getGenericType()).getActualTypeArguments()[0];
			Class<?> classE = Class.forName(genericType.getTypeName());
			Collection coll = (Collection) field.get(instance);
			writeInt(coll.size());
			for(Object o : coll){
				writeDynType(classE, o);
			}
		}else if(type.isArray()){
			Class<?> classComp = type.getComponentType();
			Object array = field.get(instance);
			int length = Array.getLength(array);
			writeInt(length);
			for(int i = 0; i < length; i++){
				writeDynType(classComp, Array.get(array, i));
			}
		}else if(type.isAssignableFrom(Map.class)){
			Type[] genericTypes = ((ParameterizedType) field.getGenericType()).getActualTypeArguments();
			Class<?> classK = Class.forName(genericTypes[0].getTypeName());
			Class<?> classV = Class.forName(genericTypes[1].getTypeName());
			Map map = (Map) field.get(instance);
			writeInt(map.size());
			for(Object o : map.entrySet()){
				Map.Entry entry = (Map.Entry) o;
				writeDynType(classK, entry.getKey());
				writeDynType(classV, entry.getValue());
			}
		}else if(type.getDeclaredAnnotation(SavedObject.class) != null){
			writeSavedObject(field.get(instance));
		}else{
			throw new UnsupportedOperationException("Cannot write type " + type.getName());
		}
	}

	private void writeDynType(Class<?> type, Object value){
		if(type.isAssignableFrom(byte.class)){
			writeByte((Byte) value);
		}else if(type.isAssignableFrom(short.class)){
			writeShort((Short) value);
		}else if(type.isAssignableFrom(int.class)){
			writeInt((Integer) value);
		}else if(type.isAssignableFrom(long.class)){
			writeLong((Long) value);
		}else if(type.isAssignableFrom(float.class)){
			writeFloat((Float) value);
		}else if(type.isAssignableFrom(double.class)){
			writeDouble((Double) value);
		}else if(type.isAssignableFrom(boolean.class)){
			writeBoolean((Boolean) value);
		}else if(type.isAssignableFrom(char.class)){
			writeChar((Character) value);
		}else if(type.isAssignableFrom(String.class)){
			writeString((String) value);
		}else if(type.isAssignableFrom(Class.class)){
			writeString(((Class) value).getName());
		}else if(type.isEnum()){
			writeString(((Enum) value).name());
		}else if(type.getDeclaredAnnotation(SavedObject.class) != null){
			writeSavedObject(value);
		}else if(type.isArray()){
			Class<?> classComp = type.getComponentType();
			int length = Array.getLength(value);
			writeInt(length);
			for(int i = 0; i < length; i++){
				writeDynType(classComp, Array.get(value, i));
			}
		}else{
			throw new UnsupportedOperationException("Cannot write parameterized type " + type.getName());
		}
	}

	/**
	 * Writes endianness-dependent bytes to the wrapped output stream
	 *
	 * @param bytes bytes in big endian
	 */
	@SneakyThrows(IOException.class)
	private void writeBytes(byte... bytes){
		if(byteOrder == ByteOrder.LITTLE_ENDIAN){
			ArrayUtils.reverse(bytes);
		}
		write(bytes);
	}
}
