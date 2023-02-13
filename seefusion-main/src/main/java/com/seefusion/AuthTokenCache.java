package com.seefusion;

import java.util.LinkedHashMap;
import java.util.Map;

public class AuthTokenCache extends LinkedHashMap<String, AuthToken> {

	private static final long serialVersionUID = 1L;

	static final int USER_LIMIT = 500;
	
	private long sessionDurationMs;
	
	AuthTokenCache(int sessionDurationMinutes) {
		super( USER_LIMIT / 5 , (float)0.9, true );
		this.sessionDurationMs = (long)sessionDurationMinutes * 60L * 1000L;
	}
	
	@Override
    protected boolean removeEldestEntry(Map.Entry<String, AuthToken> eldest) {
        return size() > USER_LIMIT;
    }
	
	@Override
	public AuthToken get(Object key) {
		AuthToken ret = super.get(key);
		debug("token " + key + ": " + ret);
		if(ret==null) {
			// meh
		}
		else if(ret.getLastSeen() + sessionDurationMs < System.currentTimeMillis()) {
			// session expired
			this.remove(key);
			return null;
		}
		else {
			ret.touch();
		}
		return ret;
	}

	public AuthToken createToken(Perm userPerm) {
		AuthToken ret = new AuthToken(userPerm);
		debug("new session: " + ret.getToken());
		this.put(ret.getToken(), ret);
		return ret;
	}

	private void debug(String msg) {
		// Logger.debug("AuthTokenCache: " + msg);
	}
	
}
