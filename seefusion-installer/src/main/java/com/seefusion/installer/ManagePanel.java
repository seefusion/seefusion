package com.seefusion.installer;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.LinkedList;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import javax.swing.JTextArea;
import javax.swing.SwingConstants;

public class ManagePanel extends LookAndFeelHelper {

	private static final long serialVersionUID = 4386418016476061877L;

	ManagePanel() {
		super();

		BoxLayout layout = new BoxLayout(this, BoxLayout.Y_AXIS);
		this.setLayout(layout);
		
		add(Box.createVerticalStrut(20));
		
		try {
			InstallationFinder finder = new InstallationFinder();
			LinkedList<Installation> installations = finder.findInstallations();
			
			JPanel line = new JPanel();
			line.setLayout(new BoxLayout(line, BoxLayout.LINE_AXIS));
			line.setBackground(Color.WHITE);

			if(installations.size() == 0) {
				line.add(boldLabel("No ColdFusion or Tomcat installation was automatically found."));
			}
			else if(installations.size() == 1) {
				line.add(boldLabel("SeeFusion has found the following instance:"));
			}
			else {
				line.add(boldLabel("SeeFusion has found the following instances:"));
			}
			line.add(Box.createHorizontalGlue());
			this.add(line, BorderLayout.LINE_START);
			
			this.add(Box.createVerticalStrut(10));
			
			this.add(new JSeparator(SwingConstants.HORIZONTAL));

			this.add(Box.createVerticalStrut(10));
			
			boolean firstInstallation = true;
			for( Installation installation : installations ) {
				if(firstInstallation) {
					firstInstallation = false;
				}
				else {
					this.add(new JSeparator(SwingConstants.HORIZONTAL));
				}
				JPanel installationPanel = new InstallationPanel(installation);
				installationPanel.setMaximumSize(new Dimension(9999, installationPanel.getPreferredSize().height));
				add(installationPanel);
				this.add(Box.createVerticalStrut(10));
			}
		}
		catch(Exception e) {
			JLabel errLabel = new JLabel(e.getMessage());
			boldFont(errLabel);
			this.add(errLabel);
			StringWriter sw = new StringWriter();
			PrintWriter pw = new PrintWriter(sw);
			e.printStackTrace(pw);
			JTextArea exceptionText = new JTextArea(sw.toString());
			exceptionText.setEditable(false);
			this.add(exceptionText);
		}
		this.add(Box.createVerticalGlue());
	}
	
}
