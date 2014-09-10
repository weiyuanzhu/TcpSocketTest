package com.mackwell.nlight_beta.messageType;

import java.util.EnumSet;

public class FailureStatusFlag extends BitFieldFlag<FailureStatus>{
	/**
     * Translates a numeric status code into a Set of FailureStatus enums
     * @param value  numeric FailureStatus
     * @return EnumSet representing DALI Failure Status
     */
	public EnumSet<FailureStatus> getFlagStatus(int value) {
		EnumSet<FailureStatus> Flags = EnumSet.noneOf(FailureStatus.class);
		for(FailureStatus statusFlag: FailureStatus.values()) {
			int flagValue = statusFlag.getFlag();
			if((flagValue & value) == flagValue) {
				Flags.add(statusFlag);
			}
		}
		return Flags;
	}
//	Set<FailureStatus> statusFlags = EnumSet.of(
//			FailureStatus.LAMP_FAILURE,
//			FailureStatus.DURATION_TEST_FAILURE);
}
