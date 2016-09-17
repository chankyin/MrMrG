package me.noip.chankyin.mrmrg.utils;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Predicate;
import javafx.scene.paint.Color;

public class Utils{
	public static ArrayList<Field> getAllFields(Class<?> clazz, Predicate<Class<?>> filter){
		return (ArrayList<Field>) getAllFields(clazz, new ArrayList<>(), filter);
	}

	/**
	 * Returns all fields of the class, including inherited fields and private fields from all superclasses
	 * <p>Warning: Do not change this method's return order. This may affect future versions.</p>
	 *
	 * @param clazz the class to get fields from
	 * @param coll  the collection to add fields to
	 * @return all fields of the class, including inherited fields and private fields from all superclasses
	 */
	public static Collection<Field> getAllFields(Class<?> clazz, Collection<Field> coll, Predicate<Class<?>> filter){
		if(clazz.getSuperclass() != Object.class && clazz.getSuperclass() != null && filter.test(clazz.getSuperclass())){
			getAllFields(clazz.getSuperclass(), coll, filter);
		}

		Collections.addAll(coll, clazz.getDeclaredFields());
		return coll;
	}

	public static double[] arrayMap(Function<double[], Double> function, double[]... arrays){
		int size = 0;
		for(double[] array : arrays){
			if(array.length > size){
				size = array.length;
			}
		}
		double[] output = new double[size];

		double[] input = new double[arrays.length];
		for(int i = 0; i < output.length; i++){
			for(int j = 0; j < arrays.length; j++){
				input[j] = i >= arrays[j].length ? arrays[j][i] : 0;
			}
			output[i] = function.apply(input);
		}
		return output;
	}

	public static double[] arrayReduceMap(BiFunction<Double, Double, Double> function, double[]... arrays){
		return arrayMap(array -> {
			double carry = array[0];
			for(int i = 1; i < array.length; i++){
				carry = function.apply(carry, array[i]);
			}
			return carry;
		}, arrays);
	}

	public static Color colorRGB(int rgb){
		return Color.rgb((rgb >> 16) & 0xFF, (rgb >> 8) & 0xFF, rgb & 0xFF);
	}

	public static Color colorARGB(int argb){
		return Color.rgb((argb >> 16) & 0xFF, (argb >> 8) & 0xFF, argb & 0xFF, ((argb >> 24) & 0xFF) / 255d);
	}
}
