package com.seefusion.installer;

public class UninstallTask implements Runnable {

	
	private final Instance instance;
	private final InstallUninstallButton button;

	public UninstallTask(Instance instance, InstallUninstallButton button) {
				this.instance = instance;
				this.button = button;
	}

	public void run() {
		try {
			instance.uninstall();
			button.update();
		} catch (InstallationException e) {
			button.error(e);
		}
	}


}
