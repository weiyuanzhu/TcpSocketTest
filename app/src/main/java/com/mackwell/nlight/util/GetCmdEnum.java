/**
 * 
 */
package com.mackwell.nlight.util;

import java.util.ArrayList;
import java.util.List;

import java.util.*;

import com.mackwell.nlight.util.*;

/**
 * @author weiyuan zhu
 *
 */
public enum GetCmdEnum  {
	
	GET_INIT(0x21), 
	GET_FACTORY_RESET(0x23),
	GET_FLASH_RESET(0x24),
	GET_REPORT(0x25),
	GET_LIST(0x26),
	UPDATE_LIST(0x27),
	UPDATE_REPORT(0x28),
	GET_OVERALL_STATUS(0x32);
	
	
	
	private int value;
	
	private GetCmdEnum(int value){
		this.value = value;
	}
	
	public int getValue() {
		return value;
	}
	
	public String toString()
	{
		return super.toString()+ "(" + value + ")";
	}
	
	public List<char[]> get()
	{
		List<char[]> commandList = new ArrayList<char[]>();
		
		List<Integer> txBuffer = new ArrayList<Integer>();
		
		txBuffer.add(Constants.HOST_ID);
		txBuffer.add(Constants.MASTER_GET);
		txBuffer.add(getValue());
		int checksum = CRC.calcCRC(txBuffer, txBuffer.size());
		txBuffer.add(CRC.getUnsignedInt(checksum));
		txBuffer.add(CRC.getUnsignedInt(checksum >> 8));
		
		txBuffer.add(Constants.UART_STOP_BIT_H);
		txBuffer.add(Constants.UART_STOP_BIT_L);
		txBuffer.add(Constants.UART_NEW_LINE_H);
		txBuffer.add(Constants.UART_NEW_LINE_L);
		
		//transfer List<Integer> to char[]
		char[] command = new char[txBuffer.size()];
		for(int j=0; j<txBuffer.size();j++)		
		{
			command[j] = (char) txBuffer.get(j).intValue();
			
		}
		
		
		commandList.add(command);
		System.out.println(txBuffer);
		return commandList;
		
		
	}
	
	public char[] getSingleCommand()
	{
		
		
		List<Integer> txBuffer = new ArrayList<Integer>();
		
		txBuffer.add(Constants.HOST_ID);
		txBuffer.add(Constants.MASTER_GET);
		txBuffer.add(getValue());
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

}
