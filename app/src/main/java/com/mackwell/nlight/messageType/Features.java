package com.mackwell.nlight.messageType;

public enum Features implements BitField{
	INTEGRAL_EMERGENCY_CONTROL_GEAR,
	MAINTAINED_CONTROL_GEAR,
	SWITCHED_MAINTAINED_CONTROL_GEAR,
	AUTO_TEST_CAPABILITY,
	ADJUSTABLE_EMERGENCY_LEVEL,
	HARDWIRED_INHIBIT_SUPPORTED,
	PHYSICAL_SELECTION_SUYPPORTED,
	RE_LIGHT_IN_REST_MODE_SUPPORTED;
	
	private final int flag;
	
	Features() {
		this.flag = 1 << this.ordinal();
	}
	
	@Override
	public int getFlag() {
		return flag;
	}
	
	
	
	@Override
	public String getDescription() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String toString() {
		// capitalise first character
				return super.toString().charAt(0) + 
						super.toString().toLowerCase().replaceAll("_", " ").substring(1, super.toString().length()); 
	}
}
