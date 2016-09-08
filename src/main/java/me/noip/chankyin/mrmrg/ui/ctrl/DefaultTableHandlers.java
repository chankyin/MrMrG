package me.noip.chankyin.mrmrg.ui.ctrl;

import lombok.experimental.UtilityClass;
import me.noip.chankyin.mrmrg.ui.comp.JNumberField;
import me.noip.chankyin.mrmrg.ui.comp.SquareColorRenderer;

import javax.swing.DefaultCellEditor;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;
import java.text.DecimalFormat;

@UtilityClass
public class DefaultTableHandlers{
	public final static TableCellRenderer defaultRenderer = new DefaultTableCellRenderer();

	public final static TableCellRenderer numberRenderer = new DefaultTableCellRenderer(){
		@Override
		protected void setValue(Object value){
			setText(new DecimalFormat().format(value));
		}
	};
	public final static TableCellRenderer colorRenderer = new SquareColorRenderer();

	public final static TableCellEditor numberEditor = new DefaultCellEditor(new JNumberField()){
		@Override
		public Object getCellEditorValue(){
			Object value = super.getCellEditorValue();
			if(value instanceof String){
				value = Double.parseDouble((String) value);
			}
			return value;
		}
	};
}
