/**
 * 
 */
package com.seefusion.installer;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import javax.swing.BoxLayout;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JButton;
import javax.swing.filechooser.FileFilter;

/**
 * @author Daryl
 * 
 */
class FileSelectPanel extends LookAndFeelHelper implements ActionListener {

	private static final long serialVersionUID = -7945503554161058022L;

	private JLabel fileName = null;

	private JButton btnChoose = null;

	private JFileChooser fc;

	private List<ActionListener> actionListeners = new LinkedList<ActionListener>();

	static final int ACTION_UPDATED = 1;
	
	/**
	 * This is the default constructor
	 */
	public FileSelectPanel() {
		super();
		initialize();
	}

	/**
	 * This method initializes this
	 * 
	 * @return void
	 */
	private void initialize() {
		fileName = new JLabel("", JLabel.LEFT);
		fileName.setFont(new Font("Arial", Font.PLAIN, 14));
		this.setSize(300, 40);
		this.setBackground(new Color(0xFFFFFF));
		this.setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
		normalFont(fileName);
		this.add(fileName, null);
		this.add(getBtnChoose(), null);
		fc = new JFileChooser();
		fc.setMultiSelectionEnabled(false);
	}

	/**
	 * This method initializes btnChoose
	 * 
	 * @return javax.swing.JButton
	 */
	private JButton getBtnChoose() {
		if (btnChoose == null) {
			btnChoose = new JButton();
			btnChoose.setPreferredSize(new Dimension(60, 30));
			btnChoose.setIcon(Icons.getFolderIcon());
			btnChoose.addActionListener(this);
		}
		return btnChoose;
	}

	void setSelectedFile(File file) {
		fc.setSelectedFile(file);
		if(file==null) {
			fileName.setText("");
		} else {
			fileName.setText(file.getAbsolutePath());
		}
	}

	void setFileFilter(FileFilter filter) {
		fc.setFileFilter(filter);
	}
	
	void addChoosableFileFilter(FileFilter filter) {
		fc.addChoosableFileFilter(filter);
	}

	File getSelectedFile() {
		return fc.getSelectedFile();
	}

	void setFileSelectionMode(int mode) {
		fc.setFileSelectionMode(mode);
	}

	void addActionListener(ActionListener who) {
		actionListeners.add(who);
	}

	void fireAction(ActionEvent e) {
		for (Iterator<ActionListener> iter = actionListeners.iterator(); iter.hasNext();) {
			ActionListener al = iter.next();
			al.actionPerformed(e);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
	 */
	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == btnChoose) {
			int returnVal = fc.showOpenDialog(this);
			if (returnVal == JFileChooser.APPROVE_OPTION) {
				fileName.setText(fc.getSelectedFile().getAbsolutePath());
				ActionEvent ev = new ActionEvent(this, ACTION_UPDATED, "");
				fireAction(ev);
			}
		}

	}

}
