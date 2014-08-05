
package com.mackwell.nlight.util;

import java.util.*;

import com.mackwell.nlight.util.CRC;
import com.mackwell.nlight.util.Constants;


/**
 * @author weiyuan zhu
 *
 */
public enum ToggleCmdEnum {
	
	FT(0x60),
	DT(0x61),
	ST(0x62),
	INHIBIT(0x63),
	RESET_INHIBIT(0x64),
	IDENTIFY(0x65),
	STOP_IDENTIFY(0x66),
	REMOVE_DEVICE(0x67),
	REMOVE_LOOP(0x68),
	COMMISSION_LOOP(0x69),
	DFU(0x6A),
	STOP_COMMISSION(0x6B),
	RETEST_GROUP(0x6C),
	MACKWELL_SPECIAL(0x6D),
	EEPROM_CORRUPT_FLAG(0x6E),
	REFRESH(0x6F);

	private int value;
	
	private ToggleCmdEnum(int value){
		this.value = value;
	}
	
	public int getValue() {
		return value;
	}
	
	public String toString()
	{
		return super.toString()+ "(" + value + ")";
	}
	
	public List<char[]> toggle(int address)
	{
		List<char[]> commandList = new ArrayList<char[]>();
		
		List<Integer> txBuffer = new ArrayList<Integer>();
		
		txBuffer.add(Constants.HOST_ID);
		txBuffer.add(Constants.MASTER_TOGGLE);
		txBuffer.add(getValue());
		txBuffer.add(address);
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
			
			
		}
		
		commandList.add(command);
		
		return commandList;
		
		
	}
	
	public char[] toggleSingleCommand(int address)
	{
		
		
		List<Integer> txBuffer = new ArrayList<Integer>();
		
		txBuffer.add(Constants.HOST_ID);
		txBuffer.add(Constants.MASTER_TOGGLE);
		txBuffer.add(getValue());
		txBuffer.add(address);
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
			
			
		}
		
	
		return command;
		
		
	}
	
	public List<char[]> multiToggleTest(List<Integer> addressList){
		List<char[]> commandList = new ArrayList<char[]>();
		
		for(int i=0; i<addressList.size();i++){
			List<Integer> txBuffer = new ArrayList<Integer>();
			
			txBuffer.add(Constants.HOST_ID);
			txBuffer.add(Constants.MASTER_TOGGLE);
			txBuffer.add(getValue());
			txBuffer.add(addressList.get(i));
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
				
				
			}
			
			commandList.add(command);
		}
		
		//commandList.add(GetCmdEnum.UPDATE_LIST.getSingleCommand());
		
		return commandList;
		
	}
	
	
	

}
