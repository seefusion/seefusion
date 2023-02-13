package com.seefusion;

import java.sql.SQLException;
import java.util.Timer;
import java.util.logging.Level;
import java.util.logging.Logger;

class Profiler {
	
	private static final Logger LOG = Logger.getLogger(Profiler.class.getName());
	
	private Profile activeProfile;
	Timer timer;
	private ProfileDao profileDao;
	
	Profile getActiveProfile() {
	    return activeProfile;
    }

	Profile stop() {
		Profile ret = null;
		if(activeProfile != null) {
			timer.cancel();
			activeProfile.notifyStopped();
			try {
				profileDao.insert(activeProfile);
			}
			catch (SQLException e) {
				LOG.log(Level.SEVERE, "Unable to store profile", e);
			}
			activeProfile = null;
		}
		return ret;
	}
	
	synchronized boolean start(RequestList reqList, ProfileDao profileDao, String instanceName, String name, long intervalMs, long scheduledDurationMs) {
		boolean ret = false;
		if(activeProfile == null) {
			this.profileDao = profileDao;
			Profile profile = new Profile(instanceName, name, intervalMs, scheduledDurationMs);
			ProfileTask profileTask = new ProfileTask(this, profile, reqList);
			timer = new Timer(true);
			timer.schedule(profileTask, 0, intervalMs);
			profile.notifyStarted();
			activeProfile = profile;
			ret = true;
		}
		return ret;
    }
	
	public JSONObject toJson() {
		if(activeProfile != null) {
			return activeProfile.toJson();
		}
		else {
			return null;
		}
    }

}
