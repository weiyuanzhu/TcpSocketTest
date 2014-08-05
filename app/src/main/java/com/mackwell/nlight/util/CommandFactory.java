package com.mackwell.nlight.util;

import java.util.ArrayList;
import java.util.List;
import java.util.*;

public class CommandFactory {
	
	static final int UART_STOP_BIT_H = 0x5A;
	static final int UART_STOP_BIT_L = 0xA5;
	static final int UART_NEW_LINE_H = 0x0D;
	static final int UART_NEW_LINE_L = 0x0A;

	
	public static List<char[]> stopTest(int address)
	{
		List<char[]> commandList = new ArrayList<char[]>();
		
		List <Integer> list = new ArrayList<Integer>();
		
		list.add(0x02);
		list.add(0xA1);
		list.add(0x62);
		list.add(address);
		int checksum = CRC.calcCRC(list, list.size());
		list.add(CRC.getUnsignedInt(checksum));
		list.add(CRC.getUnsignedInt(checksum >> 8));
		
		list.add(UART_STOP_BIT_H);
		list.add(UART_STOP_BIT_L);
		list.add(UART_NEW_LINE_H);
		list.add(UART_NEW_LINE_L);
		
		System.out.println(list);
		
		char[] command = new char[list.size()];
		for(int j=0; j<list.size();j++)		
		{
			command[j] = (char) list.get(j).intValue();
			
		}
		
		commandList.add(command);
		
		return commandList;
		
	}
	
	
	
	public static List<char[]> ftTest(int address)
	{
		List<char[]> commandList = new ArrayList<char[]>();
		
		List <Integer> list = new ArrayList<Integer>();
		
		list.add(0x02);
		list.add(0xA1);
		list.add(0x60);
		list.add(address);
		int checksum = CRC.calcCRC(list, list.size());
		list.add(CRC.getUnsignedInt(checksum));
		list.add(CRC.getUnsignedInt(checksum >> 8));
		
		list.add(UART_STOP_BIT_H);
		list.add(UART_STOP_BIT_L);
		list.add(UART_NEW_LINE_H);
		list.add(UART_NEW_LINE_L);
		
		System.out.println(list);
		
		char[] command = new char[list.size()];
		for(int j=0; j<list.size();j++)		
		{
			command[j] = (char) list.get(j).intValue();
			
		}
		
		commandList.add(command);
		
		return commandList;
		
	}
	
	public static List<char[]> getOverallStatus()
	{
		List<char[]> commandList = new ArrayList<char[]>();
		
		List <Integer> list = new ArrayList<Integer>();
		
		list.add(0x02);
		list.add(0xA0);
		list.add(0x32);
		int checksum = CRC.calcCRC(list, list.size());
		list.add(CRC.getUnsignedInt(checksum));
		list.add(CRC.getUnsignedInt(checksum >> 8));
		
		list.add(UART_STOP_BIT_H);
		list.add(UART_STOP_BIT_L);
		list.add(UART_NEW_LINE_H);
		list.add(UART_NEW_LINE_L);
		
		System.out.println(list);
		
		char[] command = new char[list.size()];
		for(int j=0; j<list.size();j++)		
		{
			command[j] = (char) list.get(j).intValue();
			
		}
		
		commandList.add(command);
		
		return commandList;
	}
	
	public static List<char[]> getPanelInfo(){
		
		List<char[]> commandList = new ArrayList<char[]>();
		
		for(int i = 0; i<16; i++){
			List <Integer> list = new ArrayList<Integer>();
			
			
			list.add(0x02);
			list.add(0xA5);
			list.add(0x40);
			list.add(i);
	
			int checksum = CRC.calcCRC(list, list.size());
			list.add(CRC.getUnsignedInt(checksum));
			list.add(CRC.getUnsignedInt(checksum >> 8));
			
			list.add(UART_STOP_BIT_H);
			list.add(UART_STOP_BIT_L);
			list.add(UART_NEW_LINE_H);
			list.add(UART_NEW_LINE_L);
			
			
			System.out.println(list);
			
			char[] command = new char[list.size()];
			for(int j=0; j<list.size();j++)		
			{
				command[j] = (char) list.get(j).intValue();
				
			}
			//System.out.println(command);
			
			commandList.add(command);
		}
		return commandList;

	}

}
