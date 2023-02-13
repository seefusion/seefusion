/**
 * 
 */
package com.seefusion.installer;

import java.awt.BorderLayout;
import java.awt.Rectangle;

import javax.swing.JPanel;
import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.WindowConstants;

/**
 * @author Daryl
 * 
 */
class MainFrame extends JFrame {

	private static final long serialVersionUID = 2081686404527867617L;

	private JPanel jContentPane = null;

	private static final int WIDTH = 720;

	private static final int HEIGHT = 600;

	/**
	 * This is the default constructor
	 */
	public MainFrame() {
		super();
		
		jContentPane = new JPanel();
		jContentPane.setLayout(new BorderLayout());
		jContentPane.add(new TitlePanel(), BorderLayout.NORTH);
		jContentPane.add(new JScrollPane(new ManagePanel()), BorderLayout.CENTER);
		
		this.setContentPane(jContentPane);
		this.setTitle("SeeFusion Instance Manager");
		this.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
		setSize();
	}

	void setSize() {
		Rectangle bounds = this.getGraphicsConfiguration().getBounds();
		setLocation(bounds.x + bounds.width / 2 - WIDTH / 2, bounds.y + bounds.height / 2 - WIDTH / 2);
		pack();
		this.setSize(WIDTH, HEIGHT);
		setVisible(true);
	}

}
