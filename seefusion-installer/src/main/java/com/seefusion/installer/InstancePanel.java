package com.seefusion.installer;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JLabel;

public class InstancePanel extends LookAndFeelHelper {

	private static final long serialVersionUID = -54746627018668827L;

	InstancePanel(Instance instance) {
		// we want this panel to layout its content horizontally
		this.setLayout(new BoxLayout(this, BoxLayout.LINE_AXIS));

		add(Box.createHorizontalStrut(INDENT_WIDTH * 2));

		add(new InstallUninstallButton(instance));
		
		add(Box.createHorizontalStrut(INDENT_WIDTH));

		add(label("Instance "));
		
		JLabel name = new JLabel(instance.getName(), JLabel.LEFT);
		boldFont(name);
		add(name);
		
		JLabel path = new JLabel(" in " + instance.getDirectory(), JLabel.RIGHT);
		normalFont(path);
		add(path);
		
		add(Box.createHorizontalGlue());
		
	}
	
}
