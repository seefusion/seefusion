package com.seefusion;

import java.security.SecureRandom;
import java.util.Date;

class AuthToken {

	private final String token;
	private final Perm perm;
	private long lastSeen;
	
	AuthToken(Perm perm) {
		if(perm==null) {
			throw new IllegalArgumentException("Perm may not be null");
		}
		this.perm = perm;
		byte[] bytes = new byte[8];
		new SecureRandom().nextBytes(bytes);
		token = Base64.encodeBytes( bytes );
		touch();
	}

	String getToken() {
		return token;
	}
	
	Perm getPerm() {
		return perm;
	}
	
	void touch() {
		this.lastSeen = System.currentTimeMillis();
	}
	
	long getLastSeen() {
		return lastSeen;
	}
	
	public String toString() {
		return "AuthToken{token: '" + token + ", perm: " + perm + ", lastSeen:" + new Date(lastSeen).toString() + "}";
	}
	
}
