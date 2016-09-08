package me.noip.chankyin.mrmrg.ui;

import lombok.Getter;
import me.noip.chankyin.mrmrg.MrMrG;
import me.noip.chankyin.mrmrg.math.VectorD;
import me.noip.chankyin.mrmrg.object.Particle;
import me.noip.chankyin.mrmrg.object.Project;
import me.noip.chankyin.mrmrg.object.viewport.DimAdjuster;
import me.noip.chankyin.mrmrg.ui.ctrl.ControlPanel;
import me.noip.chankyin.mrmrg.ui.disp.DisplayPanel;
import me.noip.chankyin.mrmrg.utils.Utils;

import javax.swing.BoxLayout;
import javax.swing.JFrame;
import javax.swing.Timer;
import javax.swing.WindowConstants;
import java.awt.Dimension;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.util.ArrayList;
import java.util.List;

import static java.awt.Color.WHITE;

public class ProjectScreen extends JFrame{
	@Getter private Project project;
	@Getter private Timer simulator;
	@Getter private boolean simulating;

	@Getter private DisplayPanel displayPanel;
	@Getter private ControlPanel controlPanel;
	@Getter private List<Runnable> substanceChangeCallbacks = new ArrayList<>();

	public ProjectScreen(){
		this(new Project("Untitled"));
		project.addSubstance(new Particle(project, project.getNextSubstanceId(), new VectorD(0, 0), new VectorD(0, 0), 1, 0, 1, WHITE.getRGB()));
	}

	public ProjectScreen(Project project){
		this.project = project;
		updateTitle();
		simulator = new Timer((int) (1000 * getProject().getFrameRate()), e -> {
			getProject().simulate(getProject().getTimeRatio() * getProject().getFrameRate());
			for(DimAdjuster adjuster : getProject().getDimAdjusters()){
				getProject().setViewportDimensions(adjuster.adjust(getProject().getViewportDimensions()));
			}

			getSubstanceChangeCallbacks().forEach(Runnable::run);
		});

		getProject().setCurrentScreen(this);
	}

	public void display(){
		getContentPane().setLayout(new BoxLayout(getContentPane(), BoxLayout.Y_AXIS));

		displayPanel = new DisplayPanel(this);
		controlPanel = new ControlPanel(this);

		getContentPane().addComponentListener(new ComponentAdapter(){
			@Override
			public void componentResized(ComponentEvent e){
				onResize();
			}
		});

		getContentPane().add(displayPanel);
		getContentPane().add(controlPanel);

		pack();
		setExtendedState(MAXIMIZED_BOTH);
		setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
		setVisible(true);
	}

	public void onResize(){
		displayPanel.setMinimumSize(new Dimension(150, 150));
		Dimension maxSize = controlPanel.getMaximumSize();
		maxSize.height = 400;
		controlPanel.setMaximumSize(maxSize);
		Dimension prefSize = controlPanel.getPreferredSize();
		prefSize.height = 200;
		controlPanel.setPreferredSize(prefSize);
		Dimension minSize = controlPanel.getMinimumSize();
		minSize.height = 150;
		controlPanel.setMinimumSize(minSize);
		validate();
	}

	public void updateTitle(){
		setTitle(String.format("%s [%s] - %s %s", project.getName(), Utils.filePathForUser(project.getLocation()),
				MrMrG.NAME, MrMrG.VERSION));
	}

	public void startSimulation(){
		simulating = true;
		if(simulator.isRunning()){
			throw new IllegalStateException("Cannot start simulation - is already running!");
		}
		simulator.start();
	}

	public void pauseSimulation(){
		if(!simulating){
			throw new IllegalStateException("Cannot pause simulation - is not simulating!");
		}
		if(!simulator.isRunning()){
			throw new IllegalStateException("Cannot pause simulation - is not running!");
		}
		simulator.stop();
	}

	public void resetSimulation(){
		getProject().resetSubstances();
	}
}
