package com.seefusion;

public abstract class DaoObjectImpl implements DaoObject {
	
	private boolean isPersisted = false;

	@Override
	public boolean isPersisted() {
		return isPersisted;
	}

	@Override
	public void setPersisted(boolean isPersisted) {
		this.isPersisted = isPersisted;
	}
	

}
