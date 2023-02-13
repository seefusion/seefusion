package com.seefusion.installer;

import java.awt.Color;
import java.io.File;
import java.io.IOException;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;

class InstallationPanel extends LookAndFeelHelper {

	private static final long serialVersionUID = 4223718698863156137L;

	InstallationPanel(Installation installation) {
		// we want this panel to layout its content vertically
		this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		
		// this line has the info about this installation 
		JPanel line = new JPanel();
		line.setLayout(new BoxLayout(line, BoxLayout.LINE_AXIS));  // horizontal, whichever way the culture reads
		line.setBackground(Color.WHITE);
		// indent a little
		line.add(Box.createHorizontalStrut(INDENT_WIDTH));
		// add installation name
		JLabel name = new JLabel(installation.getName());
		boldFont(name);
		line.add(name);
		// add installation path
		File f = new File(installation.getDirectory());
		JLabel path;
		try {
			path = new JLabel(" in " + f.getCanonicalPath());
		} catch (IOException e) {
			path = new JLabel(" in " + installation.getDirectory());
		}
		normalFont(path);
		line.add(path);
		// any extra space goes at the end
		line.add(Box.createHorizontalGlue());
		
		this.add(line);
		
		for(Instance instance : installation.getInstances()) {
			add(new InstancePanel(instance));
		}
	}
	
}
