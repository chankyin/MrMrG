package me.noip.chankyin.mrmrg.ui.comp;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.Setter;
import sun.swing.DefaultLookup;

import javax.swing.JComponent;
import javax.swing.JTable;
import javax.swing.border.Border;
import javax.swing.table.TableCellRenderer;
import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;

@NoArgsConstructor
public class SquareColorRenderer extends JComponent implements TableCellRenderer{
	@Getter @Setter @NonNull private Color background;
	@Getter private Color color;
	@Getter @Setter private Color arc;

	public SquareColorRenderer(@NonNull Color background, @NonNull Color color){
		this.background = background;
		this.color = color;
		arc = negativeColor(color);
	}

	@Override
	public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column){
		background = isSelected ? table.getSelectionBackground() : table.getBackground();
		color = new Color((int) value);
		arc = negativeColor(color);

		Border border;
		border = DefaultLookup.getBorder(this, ui, hasFocus ?
				isSelected ? "Table.focusSelectedCellHighlightBorder" :
						"Table.focusCellHighlightBorder" :
				"Table.cellNoFocusBorder");
		setBorder(border);
		setToolTipText("#" + Integer.toHexString(color.getRGB() & 0xFFFFFF));
		return this;
	}

	@Override
	public void paintComponent(Graphics g){
		g.setColor(background);
		g.fillRect(0, 0, getWidth(), getHeight());

		int centerX = getWidth() / 2;
		int centerY = getHeight() / 2;
		int halfSide = Math.min(centerX, centerY) - 2;
		g.setColor(color);
		g.fillRect(centerX - halfSide, centerY - halfSide, halfSide * 2, halfSide * 2);

		g.setColor(arc);
		halfSide++;
		g.drawRect(centerX - halfSide, centerY - halfSide, halfSide * 2, halfSide * 2);
	}

	public static Color negativeColor(@NonNull Color base){
		float[] hsb = Color.RGBtoHSB(base.getRed(), base.getGreen(), base.getBlue(), null);
		hsb[0] = 1 - hsb[0];
		hsb[2] = 1 - hsb[2];
		return Color.getHSBColor(hsb[0], hsb[1], hsb[2]);
	}

	public SquareColorRenderer setColor(@NonNull Color color){
		this.color = color;
		arc = negativeColor(color);
		repaint();
		return this;
	}
}
