package me.noip.chankyin.mrmrg.utils;

import lombok.EqualsAndHashCode;
import lombok.Getter;

import java.util.Iterator;

@EqualsAndHashCode(of = {"array"})
public class ImmutableArrayD implements Cloneable, Iterable<Double>{
	@Getter public final int length;
	private final double[] array;

	public ImmutableArrayD(double[] array){
		this.array = array.clone();
		length = array.length;
	}

	public double get(int i){
		return array[i];
	}

	public double[] toArray(){
		return array.clone();
	}

	@SuppressWarnings("unchecked")
	@Override
	public Iterator<Double> iterator(){
		return new Iterator<Double>(){
			private int i = 0;

			@Override
			public boolean hasNext(){
				return i < array.length;
			}

			@Override
			public Double next(){
				return array[i++];
			}
		};
	}
}
