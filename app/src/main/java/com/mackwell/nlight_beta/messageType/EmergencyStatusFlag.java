package com.mackwell.nlight_beta.messageType;

import java.util.EnumSet;


public class EmergencyStatusFlag extends BitFieldFlag<EmergencyStatus>{
	/**
     * Translates a numeric status code into a Set of EmergencyStatus enums
     * @param value  numeric Emergency Status 
     * @return EnumSet representing DALI Emergency Status
     */
	public EnumSet<EmergencyStatus> getFlagStatus(int value) {
		EnumSet<EmergencyStatus> Flags = EnumSet.noneOf(EmergencyStatus.class);
		for(EmergencyStatus statusFlag: EmergencyStatus.values()) {
			int flagValue = statusFlag.getFlag();
			if((flagValue & value) == flagValue) {
				Flags.add(statusFlag);
			}
		}
		return Flags;
	}
}
