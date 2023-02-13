package com.seefusion.installer;

import java.awt.CardLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import javax.swing.JButton;
import javax.swing.JOptionPane;

public class InstallUninstallButton extends LookAndFeelHelper implements ActionListener {

	private static final long serialVersionUID = -5471194661937655084L;
	private static final String INSTALL = "INSTALL";
	private static final String UNINSTALL = "UNINSTALL";
	private static final String WORKING = "WORKING";
	private static final String ERROR = "ERROR";

	static Executor executor = Executors.newCachedThreadPool();
	
	private final Instance instance;
	private CardLayout cardLayout;
	private InstallationException err;

	InstallUninstallButton(Instance instance) {
		JButton button;
		this.instance = instance;
		this.cardLayout = new CardLayout();
		this.setLayout(cardLayout);
		this.setMaximumSize(new Dimension(90, 30));
		
		button = new JButton("Install");
		button.setToolTipText("Click to add SeeFusion to this instance.");
		button.setActionCommand(INSTALL);
		button.addActionListener(this);
		this.add(button, INSTALL);
		
		button = new JButton("Uninstall");
		button.setToolTipText("Click to remove SeeFusion from this instance.");
		button.setActionCommand(UNINSTALL);
		button.addActionListener(this);
		this.add(button, UNINSTALL);
		
		button = new JButton("Error!");
		button.setToolTipText("Click to see error.");
		button.setActionCommand(ERROR);
		button.addActionListener(this);
		this.add(button, ERROR);
		
		Component workingLabel;
		workingLabel = italicLabel("Working...");
		this.add(workingLabel, WORKING);
		
		update();
	}
	
	void update() {
		try {
			if(instance.isInstalled()) {
				cardLayout.show(this, UNINSTALL);
			}
			else {
				cardLayout.show(this, INSTALL);
			}
		} catch (InstallationException e) {
			err = e;
			cardLayout.show(this, ERROR);
		}
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		String cmd = e.getActionCommand();
		if(cmd==null) {
			return;
		}
		if(cmd.equals(ERROR)) {
			JOptionPane.showMessageDialog(getRootPane(), err.getMessage()); 
		}
		else if(cmd.equals(INSTALL)) {
			working();
			executor.execute(new InstallTask(instance, this));
		}
		else if (cmd.equals(UNINSTALL)) {
			executor.execute(new UninstallTask(instance, this));
		}
	}

	private void working() {
		cardLayout.show(this, WORKING);
	}

	void error(InstallationException err) {
		
		this.err = err;
		cardLayout.show(this, ERROR);
		
	}
	
}
