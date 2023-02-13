/**
 * 
 */
package com.seefusion.installer;

import java.net.URL;

import javax.swing.ImageIcon;

/**
 * @author Daryl
 * 
 */
class Icons {

	static ImageIcon logo = null;

	static ImageIcon folder = null;

	static ImageIcon ok = null;

	static ImageIcon redx = null;

	static ImageIcon rtarrow = null;

	static ImageIcon getLogoIcon() {
		if (logo == null) {
			if(Util.isSeeJava()) {
				logo = load("Installer_Logo.gif");
			}
			else {
				logo = load("Installer_Logo.jpg");
			}
		}
		return logo;
	}
	static ImageIcon getFolderIcon() {
		if (folder == null) {
			folder = load("folder.gif");
		}
		return folder;
	}
	static ImageIcon getOKIcon() {
		if (ok == null) {
			ok = load("ok.gif");
		}
		return ok;
	}
	static ImageIcon getNotOKIcon() {
		if (redx == null) {
			redx = load("redx.gif");
		}
		return redx;
	}
	static ImageIcon getRtArrow() {
		if (rtarrow == null) {
			rtarrow = load("rtarrow.gif");
		}
		return rtarrow;
	}

	static ImageIcon load(String src) {
		URL logoURL = MainFrame.class.getResource(src);
		if (logoURL == null) {
			logo = null;
		}
		else {
			logo = new ImageIcon(logoURL);
		}
		return logo;

	}
}
