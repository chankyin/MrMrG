package me.noip.chankyin.mrmrg.ui.ctrl;

import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import me.noip.chankyin.mrmrg.object.Particle;

import javax.swing.JTable;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;
import java.awt.Component;

import static me.noip.chankyin.mrmrg.ui.ctrl.DefaultTableHandlers.*;

@RequiredArgsConstructor
public enum ParticlesTableCols implements TableCellRenderer{
	ID("ID", numberRenderer, null){
		@Override
		public Object getTableValue(Particle particle){
			return particle.getId();
		}
	},

	POS_X("Position X", numberRenderer, numberEditor){
		@Override
		public Object getTableValue(Particle particle){
			return particle.getPosition().getX();
		}

		@Override
		public void onCellChange(Particle particle, Object newValue){
			particle.setPosition(particle.getPosition().setX((double) newValue));
		}
	},

	POS_Y("Position Y", numberRenderer, numberEditor){
		@Override
		public Object getTableValue(Particle particle){
			return particle.getPosition().getY();
		}

		@Override
		public void onCellChange(Particle particle, Object newValue){
			particle.setPosition(particle.getPosition().setY((double) newValue));
		}
	},

	VEC_X("Velocity X", numberRenderer, numberEditor){
		@Override
		public Object getTableValue(Particle particle){
			return particle.getVelocity().getX();
		}

		@Override
		public void onCellChange(Particle particle, Object newValue){
			particle.setVelocity(particle.getVelocity().setX((double) newValue));
		}
	},

	VEC_Y("Velocity Y", numberRenderer, numberEditor){
		@Override
		public Object getTableValue(Particle particle){
			return particle.getVelocity().getY();
		}

		@Override
		public void onCellChange(Particle particle, Object newValue){
			particle.setVelocity(particle.getVelocity().setX((double) newValue));
		}
	},

	MASS("Mass", numberRenderer, numberEditor){
		@Override
		public Object getTableValue(Particle particle){
			return particle.getMass();
		}

		@Override
		public void onCellChange(Particle particle, Object newValue){
			particle.setMass(particle.getMass());
		}
	},

	CHARGE("Charge", numberRenderer, numberEditor){
		@Override
		public Object getTableValue(Particle particle){
			return particle.getCharge();
		}

		@Override
		public void onCellChange(Particle particle, Object newValue){
			particle.setCharge(particle.getCharge());
		}
	},

	RADIUS("Radius", numberRenderer, numberEditor){
		@Override
		public Object getTableValue(Particle particle){
			return particle.getCollisionRadius();
		}

		@Override
		public void onCellChange(Particle particle, Object newValue){
			particle.setCollisionRadius((Double) newValue);
		}
	},

	COLOR("Color", colorRenderer, null){
		@Override
		public Object getTableValue(Particle particle){
			return particle.getRgb();
		}

		@Override
		public void onCellChange(Particle particle, Object newValue){
			particle.setRgb(particle.getRgb());
		}
	};

	@Getter @NonNull private final String displayName;
	@Getter @NonNull private final TableCellRenderer renderer;
	@Getter private final TableCellEditor cellEditor;

	public boolean isEditable(){
		return cellEditor != null;
	}

	public abstract Object getTableValue(Particle particle);

	@Override
	public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column){
		if(renderer != null){
			return renderer.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
		}
		throw new InternalError("ParticlesTableCols." + name() + " Renderer is null and not overriden");
	}

	public static String[] displayNames(){
		String[] strings = new String[values().length];
		for(int i = 0; i < strings.length; i++){
			strings[i] = values()[i].getDisplayName();
		}
		return strings;
	}

	public static Object[] addRow(Particle particle){
		Object[] output = new Object[values().length];
		for(int i = 0; i < output.length; i++){
			output[i] = values()[i].getTableValue(particle);
		}
		return output;
	}

	public void onCellChange(Particle particle, Object newValue){
		if(isEditable()){
			throw isEditable() ?
					new InternalError("ParticlesTableCols." + name() + " did not implement onCellChange()") :
					new AssertionError("Property of non-editable column " + name() + "changed");
		}
	}
}
