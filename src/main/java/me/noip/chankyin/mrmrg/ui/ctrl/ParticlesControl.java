package me.noip.chankyin.mrmrg.ui.ctrl;

import lombok.Getter;
import me.noip.chankyin.mrmrg.object.Particle;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;

public class ParticlesControl extends JPanel{
	@Getter private final ControlPanel controlPanel;
	private final JTable table;

	public ParticlesControl(ControlPanel controlPanel){
		this.controlPanel = controlPanel;
		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

		addHeaderButton();

		table = new JTable(new DefaultTableModel(new Object[0][], ParticlesTableCols.displayNames()){
			@Override
			public boolean isCellEditable(int row, int column){
				return ParticlesTableCols.values()[column].isEditable();
			}

			@Override
			public void setValueAt(Object newValue, int row, int column){
				super.setValueAt(newValue, row, column);

				@SuppressWarnings("SuspiciousMethodCalls") Particle particle = (Particle)
						getControlPanel().getProjectScreen().getProject().getOriginalSubstances().get(getValueAt(row, 0));
				ParticlesTableCols.values()[column].onCellChange(particle, newValue);
				getControlPanel().getProjectScreen().getSubstanceChangeCallbacks().forEach(Runnable::run);
			}
		});
		for(ParticlesTableCols model : ParticlesTableCols.values()){
			TableColumn column = table.getColumnModel().getColumn(model.ordinal());
			column.setCellRenderer(model);
			if(model.isEditable()){
				column.setCellEditor(model.getCellEditor());
			}
		}
		table.setFillsViewportHeight(true);

		getControlPanel().getProjectScreen().getProject().getOriginalSubstances().values().stream()
				.filter(substance -> substance instanceof Particle)
				.forEachOrdered(substance -> addParticleRow((Particle) substance));

		JScrollPane scroll = new JScrollPane(table);
		add(scroll);
	}

	private void addHeaderButton(){
		JButton addButton = new JButton("Add Particle");
		addButton.addActionListener(e -> {
			AddParticleDialog dialog = new AddParticleDialog(this);
			dialog.setVisible(true);
		});
		add(addButton);
	}

	public void addParticle(Particle particle){
		addParticleRow(particle);
		getControlPanel().getProjectScreen().getProject().addSubstance(particle);
		getControlPanel().getProjectScreen().getSubstanceChangeCallbacks().forEach(Runnable::run);
	}

	private void addParticleRow(Particle particle){
		((DefaultTableModel) table.getModel()).addRow(ParticlesTableCols.addRow(particle));
	}
}
