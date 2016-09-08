package me.noip.chankyin.mrmrg.ui.ctrl;

import lombok.Getter;
import me.noip.chankyin.mrmrg.math.VectorD;
import me.noip.chankyin.mrmrg.object.Particle;
import me.noip.chankyin.mrmrg.ui.comp.ColumnGroup;
import me.noip.chankyin.mrmrg.ui.comp.GroupableTableHeader;

import javax.swing.BoxLayout;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableColumnModel;
import java.util.List;
import java.util.stream.Collectors;

import static java.lang.Math.toDegrees;

public class StatusControl extends JPanel{
	@Getter private final ControlPanel controlPanel;
	private final Object[] COLUMNS = {
			"ID", "Mass", "Charge",             // Info
			"X", "Y",                           // Position
			"X", "Y", "Magnitude", "Direction", // Velocity
			"X", "Y", "Magnitude", "Direction", // Net Force
	};

	private DefaultTableModel model;
	private JTable table;

	public StatusControl(ControlPanel controlPanel){
		this.controlPanel = controlPanel;
		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		initGUI();

		getControlPanel().getProjectScreen().getSubstanceChangeCallbacks().add(this::update);
	}

	private void initGUI(){
		Object[][] data = getRowData();
		model = new DefaultTableModel(data, COLUMNS){
			@Override
			public boolean isCellEditable(int row, int column){
				return false;
			}
		};
		table = new JTable(model){
			@Override
			protected JTableHeader createDefaultTableHeader(){
				return new GroupableTableHeader(columnModel);
			}
		};
		groupColumns();
		add(new JScrollPane(table));
	}

	private Object[][] getRowData(){
		List<Particle> particles = getControlPanel().getProjectScreen().getProject().getDisplayedSubstances().values().stream()
				.filter(substance -> substance instanceof Particle)
				.map(substance -> (Particle) substance)
				.collect(Collectors.toList());
		Object[][] data = new Object[particles.size()][];
		for(int i = 0; i < data.length; i++){
			Particle particle = particles.get(i);
			VectorD pos = particle.getPosition();
			VectorD vel = particle.getVelocity();
			VectorD netForce = particle.getNetForces();
			double velDir = toDegrees(vel.getDirectionBearing());
			if(velDir <= 0){ // <= 0 in order to flip to 0.0 when subtracted from 360.0
				velDir += 360.0;
			}
			double forceDir = toDegrees(netForce.getDirectionBearing());
			if(forceDir <= 0){
				forceDir += 360.0;
			}
			Object[] array = new Object[]{
					particle.getId(), particle.getMass(), particle.getCharge(),
					pos.getX(), pos.getY(),
					vel.getX(), vel.getY(), vel.modulus(), 360.0 - velDir,
					netForce.getX(), netForce.getY(), netForce.modulus(), 360.0 - forceDir
			};
			data[i] = array;
		}
		return data;
	}

	private void groupColumns(){
		TableColumnModel columnModel = table.getColumnModel();

		ColumnGroup info = new ColumnGroup("Info");
		info.add(columnModel.getColumn(0));
		info.add(columnModel.getColumn(1));
		info.add(columnModel.getColumn(2));
		ColumnGroup pos = new ColumnGroup("Position");
		pos.add(columnModel.getColumn(3));
		pos.add(columnModel.getColumn(4));
		ColumnGroup vel = new ColumnGroup("Velocity");
		vel.add(columnModel.getColumn(5));
		vel.add(columnModel.getColumn(6));
		vel.add(columnModel.getColumn(7));
		vel.add(columnModel.getColumn(8));
		ColumnGroup netForce = new ColumnGroup("Net Force");
		netForce.add(columnModel.getColumn(9));
		netForce.add(columnModel.getColumn(10));
		netForce.add(columnModel.getColumn(11));
		netForce.add(columnModel.getColumn(12));

		GroupableTableHeader header = (GroupableTableHeader) table.getTableHeader();
		header.addColumnGroup(info);
		header.addColumnGroup(pos);
		header.addColumnGroup(vel);
		header.addColumnGroup(netForce);
	}

	public void update(){
//		model.setDataVector(getRowData(), COLUMNS);
		remove(0);
		initGUI();
		System.out.println("Updated");
	}
}
