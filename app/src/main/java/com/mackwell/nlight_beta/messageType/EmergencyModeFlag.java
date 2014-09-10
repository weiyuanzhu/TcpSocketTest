package com.mackwell.nlight_beta.messageType;

import java.util.EnumSet;

public class EmergencyModeFlag extends BitFieldFlag<EmergencyMode>{
	/**
     * Translates a numeric status code into a Set of EmergencyMode enums
     * @param value  numeric EmergencyMode 
     * @return EnumSet representing DALI Emergency Mode
     */
	public EnumSet<EmergencyMode> getFlagStatus(int value) {
		EnumSet<EmergencyMode> Flags = EnumSet.noneOf(EmergencyMode.class);
		for(EmergencyMode statusFlag: EmergencyMode.values()) {
			int flagValue = statusFlag.getFlag();
			if((flagValue & value) == flagValue) {
				Flags.add(statusFlag);
			}
		}
		return Flags;
	}
}
