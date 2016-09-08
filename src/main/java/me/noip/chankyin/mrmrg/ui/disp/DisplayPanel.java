package me.noip.chankyin.mrmrg.ui.disp;

import lombok.Getter;
import me.noip.chankyin.mrmrg.object.Project;
import me.noip.chankyin.mrmrg.object.Substance;
import me.noip.chankyin.mrmrg.object.viewport.Dimensions2D;
import me.noip.chankyin.mrmrg.ui.ProjectScreen;

import javax.swing.JPanel;
import java.awt.Graphics;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import static java.awt.Color.BLACK;
import static java.awt.Color.WHITE;

public class DisplayPanel extends JPanel{
	@Getter private ProjectScreen projectScreen;
	@Getter private Graphics temporalGraphics;
	@Getter private SmallPrint.GeneralInfo smallPrint = new SmallPrint.GeneralInfo();

	public DisplayPanel(ProjectScreen projectScreen){
		this.projectScreen = projectScreen;

		MouseAdapter mouseAdapter = new DisplayPanelMouseListener();
		addMouseListener(mouseAdapter);
		addMouseMotionListener(mouseAdapter);

		addComponentListener(new ComponentAdapter(){
			@Override
			public void componentResized(ComponentEvent e){
				onResize();
			}
		});
		getProjectScreen().getSubstanceChangeCallbacks().add(this::repaint);
	}

	private void onResize(){
		getProject().setViewportAspect(getWidth() / (double) getHeight());
	}

	@Override
	protected void paintComponent(Graphics g){
		super.paintComponent(temporalGraphics = g.create());

		g.setColor(BLACK);
		g.fillRect(0, 0, getWidth(), getHeight());

		for(Substance substance : getProject().getDisplayedSubstances().values()){
			substance.draw(this);
		}

		// TODO paint trace

		g.setColor(WHITE);
		int y = 0;
		for(String line : smallPrint.toString().split("\n")){
			y += g.getFontMetrics().getHeight();
			g.drawString(line, 10, y);
		}
	}

	public int xProjectToDisplay(double x){
		return xProjectToDisplay(x, true);
	}

	public int xProjectToDisplay(double x, boolean absolute){
		Dimensions2D dim = getProject().getViewportDimensions();
		if(absolute){
			x -= dim.x0;
		}
		return (int) (x * getWidth() / dim.width);
	}

	public double xDisplayToProject(int x){
		return xDisplayToProject(x, true);
	}

	public double xDisplayToProject(int x, boolean absolute){
		Dimensions2D dim = getProject().getViewportDimensions();
		return (absolute ? dim.x0 : 0) + x * dim.width / getWidth();
	}

	public int yProjectToDisplay(double y){
		return yProjectToDisplay(y, true);
	}

	public int yProjectToDisplay(double y, boolean absolute){
		Dimensions2D dim = getProject().getViewportDimensions();
		if(absolute){
			y -= dim.y0;
		}
		return (int) (y * getHeight() / dim.height);
	}

	public double yDisplayToProject(int y){
		return yDisplayToProject(y, true);
	}

	public double yDisplayToProject(int y, boolean absolute){
		Dimensions2D dim = getProject().getViewportDimensions();
		return (absolute ? dim.y0 : 0) + y * dim.height / getHeight();
	}

	public Project getProject(){
		return getProjectScreen().getProject();
	}

	private class DisplayPanelMouseListener extends MouseAdapter{
		@Override
		public void mouseEntered(MouseEvent e){
			smallPrint.mouseCoords = new SmallPrint.MouseCoords();
			mouseMoved(e);
		}

		@Override
		public void mouseMoved(MouseEvent e){
			smallPrint.mouseCoords.x = xDisplayToProject(e.getX());
			smallPrint.mouseCoords.y = yDisplayToProject(e.getY());

			repaint();
		}

		@Override
		public void mouseExited(MouseEvent e){
			smallPrint.mouseCoords = null;
			repaint();
		}
	}
}
