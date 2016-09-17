package me.noip.chankyin.mrmrg.utils.io;

import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.nio.ByteOrder;
import java.util.*;

import lombok.NonNull;
import lombok.SneakyThrows;

import me.noip.chankyin.mrmrg.utils.Nullable;
import me.noip.chankyin.mrmrg.utils.Utils;

public class SavedObjectInputStream extends FilterInputStream{
	private final ByteOrder byteOrder;
	private final boolean debug;
	private final Map<String, Short> readVersions = new HashMap<>();

	public SavedObjectInputStream(@NonNull InputStream in){
		this(in, false);
	}

	public SavedObjectInputStream(@NonNull InputStream in, boolean debug){
		this(in, ByteOrder.BIG_ENDIAN, debug);
	}

	public SavedObjectInputStream(@NonNull InputStream in, ByteOrder byteOrder){
		this(in, byteOrder, false);
	}

	public SavedObjectInputStream(@NonNull InputStream in, ByteOrder byteOrder, boolean debug){
		super(in);
		this.byteOrder = byteOrder;
		this.debug = debug;
	}

	public byte readByte(){
		return readBytes(1)[0];
	}

	public short readShort(){
		byte[] bytes = readBytes(2);
		return (short) (((bytes[0] & 0xFF) << 8) | (bytes[1] & 0xFF));
	}

	public int readInt(){
		byte[] bytes = readBytes(4);
		return ((bytes[0] & 0xFF) << 24) | ((bytes[1] & 0xFF) << 16) | ((bytes[2] & 0xFF) << 8) | (bytes[3] & 0xFF);
	}

	public long readLong(){
		byte[] bytes = readBytes(8);
		return ((long) (bytes[0] & 0xFF) << 56) | ((long) (bytes[1] & 0xFF) << 48) |
				((long) (bytes[2] & 0xFF) << 40) | ((long) (bytes[3] & 0xFF) << 32) |
				((long) (bytes[4] & 0xFF) << 24) | ((bytes[5] & 0xFF) << 16) |
				((bytes[6] & 0xFF) << 8) | (bytes[7] & 0xFF);
	}

	public long readIntVarSize(int size){
		byte[] bytes = readBytes(size);
		long output = 0L;
		for(int i = 0; i < size; i++){
			output |= (long) (bytes[size - i - 1] & 0xFF) << (i * 8);
		}
		return output;
	}

	public boolean readBoolean(){
		return readByte() != 0;
	}

	public float readFloat(){
		return Float.intBitsToFloat(readInt());
	}

	public double readDouble(){
		return Double.longBitsToDouble(readLong());
	}

	public char readChar(){
		return (char) readShort();
	}

	public String readString(){
		return readString(3);
	}

	@SneakyThrows(IOException.class)
	public String readString(int size){
		int l = (int) readIntVarSize(size);
		byte[] bytes = new byte[l];
		read(bytes);
		return new String(bytes);
	}

	@SneakyThrows({IllegalAccessException.class, InstantiationException.class, ClassNotFoundException.class})
	public Object readSavedObject(@Nullable Object owningObject){
		String className = readHierarchyVersions();
		if(className == null){
			return null;
		}
		Class<?> clazz = Class.forName(className);
		Object object = clazz.newInstance();

		if(owningObject != null){
			for(Field field : Utils.getAllFields(clazz, s -> s.getDeclaredAnnotation(FillWithOwner.class) != null)){
				if(field.getType().isInstance(owningObject)){ // a class may have multiple possible owners
					field.set(object, owningObject);
				}
			}
		}
		for(Field field : Utils.getAllFields(clazz, s -> s.getDeclaredAnnotation(SavedObject.class) != null)){
			short savedVersion = readVersions.get(field.getDeclaringClass().getName());
			SavedProperty fieldAnnotation = field.getAnnotation(SavedProperty.class);
			if(fieldAnnotation != null && fieldAnnotation.value() <= savedVersion &&
					(savedVersion < fieldAnnotation.removed() || fieldAnnotation.removed() == SavedProperty.VERSION_NIL)){
				readField(field, object);
			}
		}

		if(object instanceof Unserialized){
			((Unserialized) object).postUnserialize();
		}

		return object;
	}

	@Nullable
	private String readHierarchyVersions(){
		String ret = null;
		while(readBoolean()){
			String name = readString();
			if(ret == null){
				ret = name;
			}
			short version = readShort();
			readVersions.put(name, version);
		}
		return ret;
	}

	@SuppressWarnings("unchecked")
	@SneakyThrows({IllegalAccessException.class, ClassNotFoundException.class})
	private void readField(Field field, Object instance){
		if(!field.isAccessible()){
			field.setAccessible(true);
		}
		Class<?> type = field.getType();
		if(type.isAssignableFrom(byte.class)){
			field.setByte(instance, readByte());
		}else if(type.isAssignableFrom(short.class)){
			field.setShort(instance, readShort());
		}else if(type.isAssignableFrom(int.class)){
			int i = readInt();
			field.setInt(instance, i);
		}else if(type.isAssignableFrom(long.class)){
			field.setLong(instance, readLong());
		}else if(type.isAssignableFrom(float.class)){
			field.setFloat(instance, readFloat());
		}else if(type.isAssignableFrom(double.class)){
			field.setDouble(instance, readDouble());
		}else if(type.isAssignableFrom(boolean.class)){
			field.setBoolean(instance, readBoolean());
		}else if(type.isAssignableFrom(char.class)){
			field.setChar(instance, readChar());
		}else if(type.isAssignableFrom(String.class)){
			field.set(instance, readString());
		}else if(type.isAssignableFrom(Class.class)){
			field.set(instance, Class.forName(readString()));
		}else if(type.isEnum()){
			field.set(instance, Enum.valueOf(type.asSubclass(Enum.class), readString()));
		}else if(type.isAssignableFrom(Collection.class)){
			int length = readInt();
			Type genericType = ((ParameterizedType) field.getGenericType()).getActualTypeArguments()[0];
			Class<?> classE = Class.forName(genericType.getTypeName());
			Collection coll = new ArrayList(length);
			for(int i = 0; i < length; i++){
				coll.add(readDynType(classE, instance));
			}
			field.set(instance, coll);
		}else if(type.isArray()){
			int length = readInt();
			Class<?> classComp = type.getComponentType();
			Object array = Array.newInstance(classComp);
			for(int i = 0; i < length; i++){
				Array.set(array, i, readDynType(classComp, instance));
			}
			field.set(instance, array);
		}else if(type.isAssignableFrom(Map.class)){
			Type[] genericTypes = ((ParameterizedType) field.getGenericType()).getActualTypeArguments();
			Class<?> classK = Class.forName(genericTypes[0].getTypeName());
			Class<?> classV = Class.forName(genericTypes[1].getTypeName());

			int length = readInt();
			Map map = new LinkedHashMap(length);
			for(int i = 0; i < length; i++){
				Object k = readDynType(classK, instance);
				Object v = readDynType(classV, instance);
			}
			field.set(instance, map);
		}else if(type.getDeclaredAnnotation(SavedObject.class) != null){
			field.set(instance, readSavedObject(instance));
		}else{
			throw new UnsupportedOperationException("Cannot write type " + type.getName());
		}
	}

	@SneakyThrows(ClassNotFoundException.class)
	private Object readDynType(Class<?> type, Object owningObject){
		if(type.isAssignableFrom(byte.class)){
			return readByte();
		}else if(type.isAssignableFrom(short.class)){
			return readShort();
		}else if(type.isAssignableFrom(int.class)){
			return readInt();
		}else if(type.isAssignableFrom(long.class)){
			return readLong();
		}else if(type.isAssignableFrom(float.class)){
			return readFloat();
		}else if(type.isAssignableFrom(double.class)){
			return readDouble();
		}else if(type.isAssignableFrom(boolean.class)){
			return readBoolean();
		}else if(type.isAssignableFrom(char.class)){
			return readChar();
		}else if(type.isAssignableFrom(String.class)){
			return readString();
		}else if(type.isAssignableFrom(Class.class)){
			return Class.forName(readString());
		}else if(type.isEnum()){
			return Enum.valueOf(type.asSubclass(Enum.class), readString());
		}else if(type.getDeclaredAnnotation(SavedObject.class) != null){
			return readSavedObject(owningObject);
		}else if(type.isArray()){
			int length = readInt();
			Class<?> classComp = type.getComponentType();
			Object array = Array.newInstance(classComp);
			for(int i = 0; i < length; i++){
				Array.set(array, i, readDynType(classComp, owningObject));
			}
			return array;
		}else{
			throw new UnsupportedOperationException("Cannot read parameterized type " + type.getName());
		}
	}

	/**
	 * Read bytes from the wrapped stream, and flip them if byte order is small-endian.
	 *
	 * @return Big-endian bytes
	 */
	@SneakyThrows(IOException.class)
	private byte[] readBytes(int size){
		byte[] bytes = new byte[size];
		read(bytes);
		return bytes;
	}
}
