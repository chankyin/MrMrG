package me.noip.chankyin.mrmrg.physics;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;

import lombok.Getter;
import lombok.NoArgsConstructor;

import me.noip.chankyin.mrmrg.utils.io.SavedObject;
import me.noip.chankyin.mrmrg.utils.io.SavedProperty;

@SavedObject(1)
@NoArgsConstructor
public class SubstanceMap implements Iterable<Substance>{
	@SavedProperty(1) @Getter private int dimension;
	@SavedProperty(1) @Getter private List<Substance> substances;

	public SubstanceMap(int dimension){
		this.dimension = dimension;
		substances = new ArrayList<>();
	}

	public SubstanceMap(SubstanceMap other){
		substances = other.substances.stream().map(substance -> substance.copy().setSubstanceMap(this))
				.collect(Collectors.toList());
	}

	public SubstanceMap copy(){
		return new SubstanceMap(this);
	}

	@Override
	public Iterator<Substance> iterator(){
		return substances.iterator();
	}
}
