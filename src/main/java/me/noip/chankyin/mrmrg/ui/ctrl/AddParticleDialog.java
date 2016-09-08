package me.noip.chankyin.mrmrg.ui.ctrl;

import lombok.Getter;
import me.noip.chankyin.mrmrg.math.VectorD;
import me.noip.chankyin.mrmrg.object.Particle;
import me.noip.chankyin.mrmrg.ui.comp.JNumberField;
import me.noip.chankyin.mrmrg.ui.comp.SquareColorRenderer;
import me.noip.chankyin.mrmrg.utils.Size;

import javax.swing.*;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import static java.awt.Color.RED;
import static java.awt.Color.WHITE;

public class AddParticleDialog extends JDialog{
	@Getter private final ParticlesControl particlesControl;
	private final JTextField posx, posy;
	private final JTextField velx, vely;
	private final JTextField mass;
	private final JTextField charge;
	private final JTextField collisionRadius;
	private final SquareColorRenderer particleColor;
	private final JLabel warningLabel;

	private final Particle.ParticleBuilder builder;
	private JButton button;

	public AddParticleDialog(ParticlesControl particlesControl){
		super(particlesControl.getControlPanel().getProjectScreen(), "Add particle", true);
		this.particlesControl = particlesControl;

		posx = new JNumberField(10);
		posy = new JNumberField(10);
		velx = new JNumberField(10);
		vely = new JNumberField(10);
		mass = new JNumberField(10);
		charge = new JNumberField(10);
		collisionRadius = new JNumberField(10);
		particleColor = new SquareColorRenderer(getBackground(), WHITE);
		particleColor.addMouseListener(new MouseAdapter(){
			@Override
			public void mouseClicked(MouseEvent e){
				Color color = JColorChooser.showDialog(
						AddParticleDialog.this, "Select particle color", particleColor.getColor());
				if(color != null){
					particleColor.setColor(color);
				}
			}
		});
		warningLabel = new JLabel(" ");
		warningLabel.setForeground(RED);
		button = new JButton("Add particle");
		button.addActionListener(e -> submit());

		getContentPane().setLayout(new GridBagLayout());
		addComps();
		pack();

		builder = Particle.builder().id(getParticlesControl().getControlPanel().getProjectScreen().getProject().getNextSubstanceId());
	}

	private void addComps(){
		GridBagConstraints left = new GridBagConstraints();
		left.gridx = 0;
		left.gridy = 0;
		left.gridwidth = 1;
		left.gridheight = 1;
		GridBagConstraints right = new GridBagConstraints();
		right.gridx = 1;
		right.gridy = 0;
		right.gridwidth = 4;
		right.gridheight = 1;

		Dimension leftDim = new Dimension(90, 16);
		Dimension rightDim = new Dimension(180, 16);
		mass.setMinimumSize(rightDim);
		charge.setMinimumSize(rightDim);
		collisionRadius.setMinimumSize(rightDim);
		particleColor.setPreferredSize(rightDim);

		JLabel lbl = new JLabel("Position:", SwingConstants.RIGHT);
		lbl.setMinimumSize(leftDim);
		getContentPane().add(lbl, left);
		addXY((GridBagConstraints) right.clone(), new JTextField[]{posx, posy});

		left.gridy++;
		right.gridy++;
		lbl = new JLabel("Velocity:", SwingConstants.RIGHT);
		lbl.setMinimumSize(leftDim);
		getContentPane().add(lbl, left);
		addXY((GridBagConstraints) right.clone(), new JTextField[]{velx, vely});

		left.gridy++;
		right.gridy++;
		lbl = new JLabel("Mass:", SwingConstants.RIGHT);
		lbl.setMinimumSize(leftDim);
		getContentPane().add(lbl, left);
		getContentPane().add(mass, right);

		left.gridy++;
		right.gridy++;
		lbl = new JLabel("Charge:", SwingConstants.RIGHT);
		lbl.setMinimumSize(leftDim);
		getContentPane().add(lbl, left);
		getContentPane().add(charge, right);
		left.gridy++;
		right.gridy++;

		lbl = new JLabel("Collision radius:", SwingConstants.RIGHT);
		lbl.setMinimumSize(leftDim);
		getContentPane().add(lbl, left);
		getContentPane().add(collisionRadius, right);
		left.gridy++;
		right.gridy++;

		getContentPane().add(new JLabel("Color:", SwingConstants.RIGHT), left);
		getContentPane().add(particleColor, right);
		left.gridy++;
		right.gridy++;

		left.gridwidth = 5;
		getContentPane().add(warningLabel, left);
		left.gridy++;

		getContentPane().add(button, left);
	}

	private void addXY(GridBagConstraints right, @Size(2) JTextField[] fields){
		Dimension rightLabelDim = new Dimension(30, 16);
		Dimension rightFieldDim = new Dimension(60, 16);

		fields[0].setMinimumSize(rightFieldDim);
		fields[1].setMinimumSize(rightFieldDim);

		right.weightx = 1;
		right.gridwidth = 1;
		JLabel lbl = new JLabel("X:");
		lbl.setMinimumSize(rightLabelDim);
		getContentPane().add(lbl, right);

		right.weightx = 2;
		right.gridx++;
		getContentPane().add(fields[0], right);

		right.weightx = 1;
		right.gridx++;
		lbl = new JLabel("Y:");
		lbl.setMinimumSize(rightLabelDim);
		getContentPane().add(lbl, right);

		right.weightx = 2;
		right.gridx++;
		getContentPane().add(fields[1], right);
	}

	private void submit(){
		double posx, posy, velx, vely, mass, charge, collisionRadius;
		try{
			posx = parseDouble(this.posx);
			posy = parseDouble(this.posy);
			velx = parseDouble(this.velx);
			vely = parseDouble(this.vely);
			mass = parseDouble(this.mass);
			if(mass <= 0){
				warningLabel.setText("Mass must be positive!");
				this.mass.requestFocusInWindow();
				return;
			}
			charge = parseDouble(this.charge);
			collisionRadius = parseDouble(this.collisionRadius);
			if(collisionRadius <= 0){
				warningLabel.setText("Radius must be positive!");
				this.collisionRadius.requestFocusInWindow();
				return;
			}
		}catch(NumberFormatException e){
			return;
		}
		int rgb = particleColor.getColor().getRGB();
		Particle particle = builder
				.project(getParticlesControl().getControlPanel().getProjectScreen().getProject())
				.position(new VectorD(posx, posy))
				.velocity(new VectorD(velx, vely))
				.mass(mass)
				.charge(charge)
				.collisionRadius(collisionRadius)
				.rgb(rgb)
				.build();
		getParticlesControl().addParticle(particle);
		dispose();
	}

	private double parseDouble(JTextField field){
		try{
			return Double.parseDouble(field.getText());
		}catch(NumberFormatException e){
			warningLabel.setText("This is not a valid number!");
			field.requestFocusInWindow();
			throw e;
		}
	}
}
