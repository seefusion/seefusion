package com.seefusion;

enum ProfilingStatus {

	/*  mirrored in flex ProfilingEvent
		public static const STATUS_RUNNING : int = 0;
		public static const STATUS_COMPLETED : int = 1;

	 */

	STATUS_IDLE (0)
	,STATUS_RUNNING (1)
	;
	
	private int value;

	ProfilingStatus(int value) {
		this.value = value;
	}
	
	int getValue() {
		return value;
	}
}
