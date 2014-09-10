package com.mackwell.nlight_beta.messageType;

import com.mackwell.nlight_beta.R;

public enum EmergencyMode implements BitField{
	REST_MODE("Rest Mode",R.string.emergencyMode_REST_MODE),
	NORMAL_MODE("Normal Mode",R.string.emergencyMode_NORMAL_MODE),
	EMERGENCY_MODE("Emergency Mode",R.string.emergencyMode_EMERGENCY_MODE),
	EXTENDED_EMERGENCY_MODE("Extended Emergency Mode",R.string.emergencyMode_EXTENDED_EMERGENCY_MODE),
	FUNCTION_TEST_IN_PROGRESS("FT in Progress",R.string.emergencyMode_FUNCTION_TEST_IN_PROGRESS),
	DURATION_TEST_IN_PROGRESS("DT in Progress",R.string.emergencyMode_DURATION_TEST_IN_PROGRESS),
	HARDWIRED_INHIBIT("Hardwired Inhibited",R.string.emergencyMode_HARDWIRED_INHIBIT),
	HARDWIRED_SWITCH_ON("Hardwire switch is on",R.string.emergencyMode_HARDWIRED_SWITCH_ON);
	
	private final int flag;
	private String description;
	private int stringId;
	
	EmergencyMode(String des,int id) {
		this.flag = 1 << this.ordinal();
		this.description = des;
		this.stringId = id;
	}
	
	@Override
	public int getFlag() {
		return flag;
	}

	
	@Override
	public String getDescription() {
		return description;
	}

	public int getStringId(){
		return stringId;
	}
	
	@Override
	public String toString() {
		// capitalise first character
				return super.toString().charAt(0) + 
						super.toString().toLowerCase().replaceAll("_", " ").substring(1, super.toString().length()); 
	}
}