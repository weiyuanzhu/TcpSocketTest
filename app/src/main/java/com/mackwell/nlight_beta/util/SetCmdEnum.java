/**
 * 
 */
package com.mackwell.nlight_beta.util;

import java.util.ArrayList;
import java.util.List;

/**
 * @author weiyuan zhu
 *
 */
public enum SetCmdEnum {
	
	SET_GROUP(0x80),
	SET_GROUP_NAME(0x81),
	SET_DEVICE_NAME(0x82),
	SET_DEVICE_INFO(0x83),
	SET_PANEL_NAME(0x84),
	SET_RTC(0x85),
	SET_CONTACT_NAME(0x86),
	SET_CONTACT_NUMBER(0x87),
	SET_CONTACT_MOBILE(0x88),
	SET_PASSCODE(0x89),
	SET_LOGGING_FLAG(0x8A),
	SET_DFU_INIT(0x8E),
	SET_DFU_UPDATE(0x8F),
	SET_DFU_RESULT(0x90),
	UART_SET_I2C_UPLOAD(0x91);
	
	/**
	 * Description : Cmd_SET.java - N-light SET command
	 * Author      : Jieyin Zhang
	 * History: DEV V0.0.1 31/03/2013 Document start
	 */
	private int value;
	
	private SetCmdEnum (int value) {
		this.value = value;
	}
	
	/**
	 * Get the enum numeric value
	 * @return numeric value of the enum item
	 */
	int getValue() {
		return value;
	}
	
	/**
	 * Get the array of command that is ready to be sent
	 * @param buffer command parameter
	 * @return command array
	 */
	public List<char[]> set (List<Integer> buffer) {
		List<char[]> commandList = new ArrayList<char[]>();
		
		List<Integer> txBuffer = new ArrayList<Integer>();
		
		txBuffer.add(Constants.HOST_ID);
		txBuffer.add(Constants.MASTER_SET);
		txBuffer.add(getValue());
		txBuffer.addAll(buffer);
		
		int checksum = CRC.calcCRC(txBuffer, txBuffer.size());
		txBuffer.add(CRC.getUnsignedInt(checksum));
		txBuffer.add(CRC.getUnsignedInt(checksum >> 8));
		
		txBuffer.add(Constants.UART_STOP_BIT_H);
		txBuffer.add(Constants.UART_STOP_BIT_L);
		txBuffer.add(Constants.UART_NEW_LINE_H);
		txBuffer.add(Constants.UART_NEW_LINE_L);
		
		
		char[] command = new char[txBuffer.size()];
		for(int j=0; j<txBuffer.size();j++)		
		{
			command[j] = (char) txBuffer.get(j).intValue();
			System.out.print(txBuffer.get(j).intValue() + " ");
			
		}
		
		commandList.add(command);
		
		return commandList;
	}
	
	
	
	
	
	
}
