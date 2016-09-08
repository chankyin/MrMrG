package me.noip.chankyin.mrmrg.object;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.SneakyThrows;
import me.noip.chankyin.mrmrg.MrMrG;
import me.noip.chankyin.mrmrg.math.VectorD;
import me.noip.chankyin.mrmrg.object.viewport.DimAdjuster;
import me.noip.chankyin.mrmrg.object.viewport.Dimensions2D;
import me.noip.chankyin.mrmrg.ui.ProjectScreen;
import me.noip.chankyin.mrmrg.utils.ProjectInputStream;
import me.noip.chankyin.mrmrg.utils.ProjectOutputStream;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@NoArgsConstructor
public class Project{
	public final static short FORMAT_VERSION = 1;

	@Getter private File location = null;
	@Getter private String name;
	private int nextSubstanceId;
	@Getter private Map<Integer, Substance> originalSubstances;
	/**
	 * @return Time interval in seconds between two consecutive frames
	 */
	@Getter private double frameRate;
	/**
	 * @return Number of simulated seconds per real-life second
	 */
	@Getter private double timeRatio;
	@Getter private List<DimAdjuster> dimAdjusters;

	// simulator temporary properties
	private Map<Integer, Substance> substances = null;
	@Getter private Dimensions2D viewportDimensions;
	private double timeElapsed;

	@Getter @Setter private ProjectScreen currentScreen;

	public Project(String name, int nextSubstId, Map<Integer, Substance> substs, double frameRate, double timeRatio, Dimensions2D viewportDimensions){
		this();
		init(name, nextSubstId, substs, frameRate, timeRatio, viewportDimensions, null);
	}

	public void init(String name, int nextSubstanceId, Map<Integer, Substance> substs, double frameRate, double timeRatio,
	                 Dimensions2D viewportDimensions, File location){
		this.name = name;
		this.nextSubstanceId = nextSubstanceId;
		originalSubstances = substs;
		this.frameRate = frameRate;
		this.timeRatio = timeRatio;
		this.viewportDimensions = viewportDimensions;
		this.location = location;
	}

	public Project(String name){
		this(name, 0, new HashMap<>(), 1d / 20d, 1d, new Dimensions2D(-50, 50, -50, 50));
		dimAdjusters = new ArrayList<>(0);
//		BestFitParticlesDimAdjuster adjuster = new BestFitParticlesDimAdjuster().init(this, FourSides.defaultInstance());
//		dimAdjusters = new ArrayList<>(Collections.singletonList(adjuster));
	}

	public static Project read(File file, ProjectInputStream is) throws IOException{
		short formatVersion = is.readShort();
		if(formatVersion != FORMAT_VERSION){
			if(formatVersion == 0){
				// UPDATE<1.0 add handlers here
			}else{
				throw new RuntimeException("Unsupported format");
			}
		}
		Project project = new Project();
		project.init(
				is.readString(),        // name
				is.readInt(),           // nextSubstanceId
				is.readArray(s -> Particle.read(s, project)).stream().collect(Collectors.toMap(Particle::getId, p -> p))
				,                       // particles
				is.readDouble(),        // frameRate
				is.readDouble(),        // timeRatio
				Dimensions2D.read(is),  // viewportDimensions
				file
		);
		project.dimAdjusters = is.readArray(s -> DimAdjuster.readAdjuster(project, s));
		return project;
	}

	@SneakyThrows({IOException.class})
	public void saveProject(){
		write(new ProjectOutputStream(new FileOutputStream(location)));
	}

	public void saveProject(String name) throws FileNotFoundException{
		saveProject(MrMrG.dataDir, name);
	}

	public void saveProject(File dir, String name) throws FileNotFoundException{
		File file = new File(dir, name + ".setup");
		write(new ProjectOutputStream(new FileOutputStream(file)));
	}

	@SneakyThrows({IOException.class})
	public void write(ProjectOutputStream os){
		os.writeShort(FORMAT_VERSION);
		os.writeString(name);
		os.writeInt(nextSubstanceId);
		os.writeArray(originalSubstances.values());
		os.writeDouble(frameRate);
		os.writeDouble(timeRatio);
		os.writeArray(dimAdjusters);
	}

	public Project addSubstance(Substance substance){
		getOriginalSubstances().put(substance.getId(), substance);
		if(getCurrentScreen() != null){
			getCurrentScreen().repaint();
		}
		return this;
	}

	public void simulate(double virtualTimeElapsed){
		for(Substance substance : getDisplayedSubstances().values()){
			VectorD force = substance.getNetForces();
			if(!VectorD.ZERO_2.equals(force)){
				continue;
			}
			VectorD dvDt = force.divide(substance.getMass()) // N/kg = ms-2
					.multiply(virtualTimeElapsed); // ms-1 = ms-2 * s
			substance.addVelocity(dvDt);
		}
		for(Substance substance : getDisplayedSubstances().values()){
			substance.move(virtualTimeElapsed);
		}
	}

	public int getNextSubstanceId(){
		return nextSubstanceId++;
	}

	public void setFrameRate(double frameRate){
		this.frameRate = frameRate;
		if(currentScreen != null){
			currentScreen.getSimulator().setDelay((int) (1000 * frameRate));
		}
	}

	public Map<Integer, Substance> getDisplayedSubstances(){
		if(getCurrentScreen() != null && getCurrentScreen().isSimulating()){
			if(substances == null){
				substances = copySubstances();
			}
			return substances;
		}

		return originalSubstances;
	}

	public void resetSimulation(){
		resetSubstances();
	}

	public void resetSubstances(){
		substances = null;
	}

	private Map<Integer, Substance> copySubstances(){
		System.err.println("copySubstances");
		return originalSubstances.entrySet().stream()
				.collect(Collectors.toMap(Map.Entry::getKey, entry -> entry.getValue().copy()));
	}

	public void setViewportAspect(double d){
		Dimensions2D dim = getViewportDimensions();
		double newWidth = d * (dim.y1 - dim.y0);
		double midWidth = (dim.x0 + dim.x1) / 2;
		Dimensions2D newDim = new Dimensions2D(midWidth - newWidth / 2, midWidth + newWidth / 2, dim.y0, dim.y1);
		for(DimAdjuster adjuster : getDimAdjusters()){
			newDim = adjuster.adjust(newDim);
		}
		setViewportDimensions(newDim);
	}

	public void setViewportDimensions(Dimensions2D viewportDimensions){
		this.viewportDimensions = viewportDimensions;
	}

}
