/**
 * 
 */
package com.seestack;

import java.util.HashMap;
import java.util.LinkedList;

import com.seefusion.JSONArray;


/**
 * @author Daryl
 *
 */
public class Methods extends LinkedList<Method> {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	public void addInfoFrom(HashMap<String, Info> infos) {
		for(Method m : this) {
			m.addInfo(infos); 
		}
	}

	public JSONArray toJson() {
		JSONArray ret = new JSONArray();
		for(Method m : this) {
			ret.put(m.toJson()); 
		}
		return ret;
	}

	int _hashCode = -1;
	@Override
	public int hashCode() {
		if(_hashCode == -1) {
			final int prime = 31;
			int result = 1;
			for(Method m : this) {
				result = prime * result + m.hashCode();
			}
			_hashCode = result;
		}
		return _hashCode;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Methods other = (Methods) obj;
		return other.hashCode() == this.hashCode();
	}

	@Override
	public boolean add(Method e) {
		_hashCode = -1;
		return super.add(e);
	}

	
}
