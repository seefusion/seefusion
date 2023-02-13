package com.seefusion;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Random;

public class Password {

	static String NO_CHANGE = "(No Change)";
	String salt;
	String password;
	
	public Password() {
		this.salt = "";
		this.password = "";
	}
	
	public Password(String password) {
		// salt will be ignored if this is a password in "password:salt" format
		this(password, Long.toString(new Random().nextLong()));
	}

	Password(String password, String salt) {
		if(password==null || password.length()==0) {
			this.password="";
			this.salt="";
		}
		else if(password.length() > 44 && password.charAt(44) == ':') {
			this.password = password.substring(0, 44);
			this.salt = password.substring(45);
		}
		else {
			try {
				this.salt = salt;
				MessageDigest digest = MessageDigest.getInstance("SHA-256");
				byte[] hash = digest.digest((password + salt).getBytes("UTF-8"));
				this.password = Base64.encodeBytes(hash);
			} catch (NoSuchAlgorithmException e) {
				// Should never happen
				e.printStackTrace();
			} catch (UnsupportedEncodingException e) {
				// Should never happen
				e.printStackTrace();
			}
		}
	}
	
	@Override
	public String toString() {
		if(isBlank()) {
			return "";
		}
		return password + ":" + salt;
	}

	public boolean isBlank() {
		return password.length()==0;
	}

	public static String safe(String value) {
		return value.replaceAll("[\\s],;", "");
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((password == null) ? 0 : password.hashCode());
		result = prime * result + ((salt == null) ? 0 : salt.hashCode());
		return result;
	}

	public boolean equals(String other) {
		return _equals(other);
	}
	
	@Override
	public boolean equals(Object other) {
		return _equals(other);
	}
	
	private boolean _equals(Object other) {
		if(other==null) {
			return this.password.length()==0;
		}
		if(other instanceof Password) {
			return ((Password) other).password.equals(this.password);
		}
		if(other instanceof String) {
			if(((String) other).length()==0) {
				return this.password.length()==0;
			}
			Password otherP = new Password((String)other, this.salt);
			return otherP.equals(this);
		}
		return false;
	}
}
