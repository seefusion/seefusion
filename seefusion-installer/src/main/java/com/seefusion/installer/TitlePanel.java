package com.seefusion.installer;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;


public class TitlePanel extends JPanel {
	
	private static final long serialVersionUID = 2354194017259503776L;

	public TitlePanel() {
		super();
		setBackground(new Color(0xFFFFFF));
		setLayout(new BorderLayout());

		ImageIcon logo = Icons.getLogoIcon();
		JLabel title = new JLabel(
				null,
				logo, JLabel.CENTER);
		add(title, BorderLayout.CENTER);
		
		
		JPanel spacerPanel = new JPanel();
		spacerPanel.setPreferredSize(new Dimension(WIDTH, 10));
		spacerPanel.setBackground(new Color(0xD7D0C8));
		add(spacerPanel, BorderLayout.SOUTH);
	}
	
}
