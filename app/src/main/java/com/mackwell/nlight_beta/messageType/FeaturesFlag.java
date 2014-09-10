package com.mackwell.nlight_beta.messageType;

import java.util.EnumSet;

public class FeaturesFlag extends BitFieldFlag<Features>{
	/**
     * Translates a numeric status code into a Set of Features enums
     * @param value  numeric Features
     * @return EnumSet representing DALI Features
     */
	public EnumSet<Features> getFlagStatus(int value) {
		EnumSet<Features> Flags = EnumSet.noneOf(Features.class);
		for(Features statusFlag: Features.values()) {
			int flagValue = statusFlag.getFlag();
			if((flagValue & value) == flagValue) {
				Flags.add(statusFlag);
			}
		}
		return Flags;
	}
}
