/**
 * 
 */
package com.seefusion.installer;

import java.awt.Color;
import java.awt.Component;
import java.awt.Font;

import javax.swing.JLabel;
import javax.swing.JPanel;

/**
 * @author Daryl
 *
 */
abstract class LookAndFeelHelper extends JPanel {
	
	static final int INDENT_WIDTH = 20;

	/**
	 * 
	 */
	private static final long serialVersionUID = 4352858905878456905L;
	
	private Font boldFont = new Font("Arial", Font.BOLD, 16);
	private Font normalFont = new Font("Arial", Font.PLAIN, 16);
	private Font italicFont = new Font("Arial", Font.ITALIC, 16);

	protected LookAndFeelHelper() {
		this.setBackground(new Color(0xFFFFFF));
	}
	
	JLabel italicLabel(String txt, int placement) {
		JLabel label = new JLabel(txt, placement);
		label.setVerticalAlignment(JLabel.CENTER);
        italicFont(label);
        return label;
	}

	JLabel italicLabel(String txt) {
		JLabel label = new JLabel(txt);
		label.setVerticalAlignment(JLabel.CENTER);
        italicFont(label);
        return label;
	}

	JLabel boldLabel(String txt, int placement) {
		JLabel label = new JLabel(txt, placement);
		label.setVerticalAlignment(JLabel.CENTER);
        boldFont(label);
        return label;
	}
	
	JLabel boldLabel(String txt) {
		JLabel label = new JLabel(txt);
		label.setVerticalAlignment(JLabel.CENTER);
        boldFont(label);
        return label;
	}

	JLabel label(String txt, int placement) {
		JLabel label = new JLabel(txt, placement);
		label.setVerticalAlignment(JLabel.CENTER);
		normalFont(label);
        return label;
	}
	
	JLabel label(String txt) {
		JLabel label = new JLabel(txt);
		label.setVerticalAlignment(JLabel.CENTER);
		normalFont(label);
        return label;
	}
	
	Component normalFont(Component c) {
		c.setFont(normalFont);
		return c;
	}
	Component boldFont(Component c) {
		c.setFont(boldFont);
		return c;
	}
	Component redBoldFont(Component c) {
		c.setFont(boldFont);
		c.setForeground(new Color(0xFF0000));
		return c;
	}
	Component italicFont(Component c) {
		c.setFont(italicFont);
		return c;
	}
	
}
