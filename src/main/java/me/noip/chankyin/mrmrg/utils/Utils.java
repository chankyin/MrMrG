package me.noip.chankyin.mrmrg.utils;

import lombok.SneakyThrows;
import lombok.experimental.UtilityClass;

import java.io.File;
import java.io.IOException;
import java.util.function.BiFunction;
import java.util.function.Function;

@UtilityClass
public class Utils{
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

	@SneakyThrows({IOException.class})
	public static String filePathForUser(File file){
		if(file == null){
			return "Not saved";
		}
		return file.getCanonicalPath();
	}
}
