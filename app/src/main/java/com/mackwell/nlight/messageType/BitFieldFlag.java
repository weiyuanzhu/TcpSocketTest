package com.mackwell.nlight.messageType;

import java.util.Set;
import java.util.*;

public class BitFieldFlag<T extends BitField> {
	/**
     * Translates a set of enums into a numeric status code
     * @param flags  enum Set
     * @return numeric representation of the failure status 
     */
	int getFlagValue(Set<T> flags) {
		int value = 0;
		for(T flag : flags) {
			value |= flag.getFlag();
		}
		return value;
	}
	
	
}
