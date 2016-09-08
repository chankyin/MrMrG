package me.noip.chankyin.mrmrg.ui.ctrl;

import lombok.Getter;
import me.noip.chankyin.mrmrg.ui.ProjectScreen;

import javax.swing.JTabbedPane;

@Getter
public class ControlPanel extends JTabbedPane{
	private final ProjectScreen projectScreen;
	private final ParticlesControl particlesControl;
	private final SimulationControl simulationControl;
	private final StatusControl statusControl;
	private final SaveControl saveControl;

	public ControlPanel(ProjectScreen projectScreen){
		this.projectScreen = projectScreen;

		particlesControl = new ParticlesControl(this);
		addTab("Particles", particlesControl);
		simulationControl = new SimulationControl(this);
		addTab("Simulation", simulationControl);
		statusControl = new StatusControl(this);
		addTab("Substance status", statusControl);
		saveControl = new SaveControl(this);
		addTab("Save", saveControl);
	}
}
