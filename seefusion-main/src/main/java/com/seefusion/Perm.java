package com.seefusion;

public class Perm {
	// binary permissions
	static Perm NONE = new Perm(0);

	static Perm LOGGEDIN = new Perm(1);

	static Perm KILL = new Perm(4);

	static Perm CONFIG = new Perm(8);
	
	int perm = 0;
	
	boolean isMutable = true;
	
	Perm() {
	}
	
	private Perm(int perm) {
		this.perm = perm;
	}
	
	Perm(Perm... perms) {
		for(Perm perm : perms) {
			this.perm |= perm.perm;			
		}
	}

	Perm lock() {
		this.isMutable = false;
		return this;
	}
	
	boolean mayI(Perm request) {
		return (request.perm & this.perm) == request.perm;
	}

	public Perm whyCantI(Perm reqPerm) {
		return new Perm( (this.perm ^ reqPerm.perm) & reqPerm.perm);
	}
	
	boolean has(Perm perm) {
		return (this.perm & perm.perm) != 0;
	}

	public void add(Perm perm) {
		if(!isMutable) {
			throw new UnsupportedOperationException();
		}
		this.perm = this.perm | perm.perm;
	}
	
	@Override
	@SuppressWarnings("PMD.OverrideBothEqualsAndHashcode")
	public boolean equals(Object o) {
		if(o == null) {
			return false;
		}
		if(o instanceof Perm) {
			return this.perm == ((Perm)o).perm;
		}
		else if(o instanceof Integer) {
			return this.perm == new Perm((Integer) o).perm;
		}
		return false;
	}
	
	@Override
	public String toString() {
		StringBuilder ret = new StringBuilder(40);
		ret.append("Perm: ");
		if(this.mayI(Perm.LOGGEDIN)) ret.append("LoggedIn ");
		if(this.mayI(Perm.KILL)) ret.append("Kill ");
		if(this.mayI(Perm.CONFIG)) ret.append("Config ");
		return ret.toString();
	}
	
}
