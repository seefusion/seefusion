package com.seefusion.installer;

public class InstallTask implements Runnable {

	
	private final Instance instance;
	private final InstallUninstallButton button;

	public InstallTask(Instance instance, InstallUninstallButton button) {
				this.instance = instance;
				this.button = button;
	}

	public void run() {
		try {
			instance.install();
			button.update();
		} catch (InstallationException e) {
			button.error(e);
		}
	}

}
