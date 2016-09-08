package me.noip.chankyin.mrmrg.ui.ctrl;

import lombok.Getter;
import me.noip.chankyin.mrmrg.ui.ProjectScreen;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JPanel;

public class SimulationControl extends JPanel{
	@Getter private final ControlPanel controlPanel;

	public SimulationControl(ControlPanel controlPanel){
		this.controlPanel = controlPanel;

		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		JPanel buttons = new JPanel();
		buttons.setLayout(new BoxLayout(buttons, BoxLayout.X_AXIS));
		JButton startPauseButton = new JButton("Start");
		startPauseButton.addActionListener(e -> {
			ProjectScreen screen = getControlPanel().getProjectScreen();
			if(screen.getSimulator().isRunning()){
				screen.pauseSimulation();
				startPauseButton.setText("Resume");
			}else{
				screen.startSimulation();
				startPauseButton.setText("Pause");
			}
		});
		buttons.add(startPauseButton);
		JButton restartButton = new JButton("Restart");
		restartButton.addActionListener(e -> {
			getControlPanel().getProjectScreen().resetSimulation();
			getControlPanel().getProjectScreen().getProject().resetSimulation();
			getControlPanel().getProjectScreen().getDisplayPanel().repaint();
		});
		buttons.add(restartButton);
		add(buttons);
	}
}
