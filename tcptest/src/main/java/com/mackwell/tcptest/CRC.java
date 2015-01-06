package com.mackwell.tcptest;

import java.util.List;

public class CRC {
	/**
	 * Description : CRC.java - generating 16bit CRC checksum
	 * Author      : Jieyin Zhang
	 * History: DEV V0.0.1 01/03/2013 Document start
	 */
	
	/**
	 * Calculate the CRC checksum
	 * @param number  array of Integer numbers
	 * @param length  length of the array
	 * @return CRC checksum ( 16 bit value )
	 */
	public static int calcCRC(List<Integer> number, int length) {
        int crc = CRC_SEED;

        for (int len = 0; len < length; len++) {
        	crc = crc ^ number.get(len);
            for (int i = 8; i > 0; i--) {
                if ((crc & 0x0001) > 0) {
                    crc = (crc >> 1) ^ CRC_POLY;
                } else {
                    crc >>= 1;
                }
            }
        }
        return crc;
    }
	
	/**
	 * Get the unsigned value from int input
	 * @param b int number
	 * @return unsigned number
	 */
	public static int getUnsignedInt(int b) {
	    return b & 0xFF;
	}
	
	/**
	 * Get the unsigned value from byte input
	 * @param b byte number
	 * @return unsigned number
	 */
	public static int getUnsignedInt(byte b) {
	    return b & 0xFF;
	}
	
	private static final int CRC_POLY = 0xA001;
    private static final int CRC_SEED = 0xFFFF;
}