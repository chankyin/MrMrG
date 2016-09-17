package me.noip.chankyin.mrmrg.physics;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import lombok.*;

import me.noip.chankyin.mrmrg.physics.simulator.Simulator;
import me.noip.chankyin.mrmrg.ui.render.viewport.Viewport;
import me.noip.chankyin.mrmrg.ui.render.viewport.Viewport2D;
import me.noip.chankyin.mrmrg.utils.Nullable;
import me.noip.chankyin.mrmrg.utils.io.SavedObject;
import me.noip.chankyin.mrmrg.utils.io.SavedObjectInputStream;
import me.noip.chankyin.mrmrg.utils.io.SavedProperty;

@SavedObject(1)
@NoArgsConstructor
public class Project{
	@SavedProperty(1) @Getter @Setter private String name;
	@SavedProperty(1) private int nextId = 0;
	@SavedProperty(1) @Getter private int dimension;
	@SavedProperty(1) @Getter private SubstanceMap substances;
	@SavedProperty(1) @Getter @Setter private Viewport viewport;
	@SavedProperty(1) @Getter @Setter private double framePeriod;
	@SavedProperty(1) @Getter @Setter private double timeRatio;

	@Nullable @Getter @Setter(AccessLevel.PRIVATE) private File location = null;

	public static Project createDefault(String name){
		Project project = new Project();
		project.name = name;
		project.dimension = 2;
		project.substances = new SubstanceMap(project.dimension);
		project.viewport = new Viewport2D(-200, 200, -200, 200);
		project.framePeriod = 0.05;
		project.timeRatio = 1d;
		return project;
	}

	public int getNextId(){
		return nextId++;
	}

	public Simulator simulate(){
		Simulator.SimulatorBuilder builder = Simulator.builder()
				.dimension(dimension)
				.viewport(viewport)
				.timeRatio(timeRatio)
				.substanceMap(substances.copy());
		return builder.build();
	}

	@SneakyThrows({IOException.class})
	public static Project fromFile(File file){
		@Cleanup SavedObjectInputStream is = new SavedObjectInputStream(new FileInputStream(file));
		return ((Project) is.readSavedObject(null)).setLocation(file);
	}
}
