/**
 * 
 */
package com.seefusion;

/**
 * @author Daryl
 * 
 */
class SubjectImpl<T> implements Subject<T> {

	java.util.WeakHashMap<Observer<T>, String> observers = new java.util.WeakHashMap<Observer<T>, String>();

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.seefusion.Subject#addObserver(com.seefusion.Observer)
	 */
	@Override
	public synchronized void addObserver(Observer<T> o) {
		observers.put(o, "");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.seefusion.Subject#removeObserver(com.seefusion.Observer)
	 */
	@Override
	public synchronized void removeObserver(Observer<T> o) {
		observers.remove(o);
	}

	synchronized void notifyObservers(T object) {
		// notify each observer
		for (Observer<T> o : observers.keySet()) {
			if(o != null) { // is this test actually necessary?
				o.update(object);
			}
		}
	}

	public synchronized boolean hasObservers() {
		return !observers.isEmpty();
	}
}
