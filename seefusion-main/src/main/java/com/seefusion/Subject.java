/*
 * Subject.java
 *
 */

package com.seefusion;

/**
 * see Observer and SubjectImpl
 */
interface Subject<T> {

	void addObserver(Observer<T> o);

	void removeObserver(Observer<T> o);

}
