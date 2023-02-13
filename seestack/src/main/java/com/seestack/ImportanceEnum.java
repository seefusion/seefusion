/**
 * 
 */
package com.seestack;

/**
 * @author Daryl
 * 
 */
public class ImportanceEnum {

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + _value;
		result = prime * result
				+ ((description == null) ? 0 : description.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		ImportanceEnum other = (ImportanceEnum) obj;
		if (_value != other._value)
			return false;
		if (description == null) {
			if (other.description != null)
				return false;
		} else if (!description.equals(other.description))
			return false;
		return true;
	}

	public static final ImportanceEnum NOTUSER = new ImportanceEnum("BuiltIn", 5);  // for com.adobe.coldfusion components that look like USER code but aren't, eg "new Query()"

	public static final ImportanceEnum USER = new ImportanceEnum("User", 4);

	public static final ImportanceEnum HIGH = new ImportanceEnum("High", 3);

	public static final ImportanceEnum MEDIUM = new ImportanceEnum("Medium", 2);

	public static final ImportanceEnum LOW = new ImportanceEnum("Low", 1);

	public static final ImportanceEnum IDLE = new ImportanceEnum("Idle", 0);

	public static final ImportanceEnum DELEGATE = new ImportanceEnum("Delegate", -1);

	String description;
	int _value;

	public int value() {
		return _value;
	}
	
	private ImportanceEnum(String description, int value) {
		this.description = description;
		this._value = value;
	}

	public String toString() {
		return description;
	}

	/** 
	 * Convert string into appropriate enum
	 * @param importance string (high, medium, low, delegate, user, coldfusion, or idle)
	 * @throws NumberFormatException if unable to parse
	 * @return enum instance representing s
	 */
	public static ImportanceEnum parseImportance(String s) {
		if(s==null) {
			throw new RuntimeException("Can't parse null importance!");
		}
		s = s.toLowerCase();
		if (s.equals("high")) {
			return HIGH;
		}
		else if (s.equals("medium")) {
			return MEDIUM;
		}
		else if (s.equals("low")) {
			return LOW;
		}
		else if (s.equals("delegate")) {
			return DELEGATE;
		}
		else if (s.equals("user")) {
			return USER;
		}
		else if (s.equals("idle")) {
			return IDLE;
		}
		else if (s.equals("notuser")) {
			return NOTUSER;
		}
		else if (s.equals("builtin")) {
			return NOTUSER;
		}
		else  {
			throw new NumberFormatException("Unable to parse " + s + " as Importance.");
		}

	}

}
