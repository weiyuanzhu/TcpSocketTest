package com.mackwell.nlight.util;

import com.mackwell.nlight.models.Report;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.*;
import java.util.concurrent.TimeUnit;

public class DataParser {
	
	
	//panel stop and new line byte
	static final int UART_STOP_BIT_H = 0x5A;
	static final int UART_STOP_BIT_L = 0xA5;
	static final int UART_NEW_LINE_H = 0x0D;
	static final int UART_NEW_LINE_L = 0x0A;
    static final List<Integer> REPORT_START_SEQUENCE = Arrays.asList(0x55,0xAA,0xFF);

	static public List<List<Integer>> removeJunkBytes(List<Integer> rxBuffer)
	{
		
			List<List<Integer>> panelData = new ArrayList<List<Integer>>();
			
			System.out.println("==========Removing junk bytes=======");
			System.out.println("rxBuffer.size = " + rxBuffer.size());
			
			for (int i=0,j=3; i<rxBuffer.size()-24; i++)
			{
				if((rxBuffer.get(i) == UART_NEW_LINE_L) && 
        				rxBuffer.get(i - 1)==(UART_NEW_LINE_H) &&
        				rxBuffer.get(i - 2)==(UART_STOP_BIT_L) &&
        				rxBuffer.get(i - 3)==(UART_STOP_BIT_H))
				{
						
					panelData.add(rxBuffer.subList(j, i-5));	
					j = i+4;
				}
				
				
			}
			
			/*System.out.println(parsedData.size());
			
			for(int i=0; i <parsedData.size(); i++)
			{
				System.out.println("parsedDataList " + i + " size: " + parsedData.get(i).size() );
				System.out.println("parsedData: " + parsedData.get(i));
			}*/
			
			return panelData;
	}
	
	public static List<List<Integer>> getEepRom(List<List<Integer>> parsedData)
	{
		List<List<Integer>> eepRom = new ArrayList<List<Integer>>();
		
		for(int i=0; i<16 ;i++)
		{
			List<Integer> list = (List<Integer>) parsedData.get(i);
			for(int j=0;j<list.size();j+=32)
			{
				eepRom.add(list.subList(j, j+32));
				
			}
			
			
		}
		
		System.out.println("===============Getting EEPRom======================");
		System.out.println("EEPROM size: " + eepRom.size());
		return eepRom;
		
	}
	
	
	public static List<List<List<Integer>>> getDeviceList(List<List<Integer>> deviceListData,List<List<Integer>> eepRom)
	{
		List<List<List<Integer>>> deviceList = new ArrayList<List<List<Integer>>>();
		List<List<Integer>> loop1= new ArrayList<List<Integer>>();
		List<List<Integer>> loop2= new ArrayList<List<Integer>>();
		
		deviceList.add(loop1);
		deviceList.add(loop2);
		
		
		
		for(int i=16; i<deviceListData.size() ;i++)
		{			
			int address = deviceListData.get(i).get(0) / 100;
			
			if(address == 0)
			{
				loop1.add(deviceListData.get(i));	
			}
			else loop2.add(deviceListData.get(i));
		}
		
		
		System.out.println("===============Getting Device List======================");
		System.out.println("Device Number: " + (deviceList.get(0).size()+deviceList.get(1).size()));
		return deviceList;
		
		
	}
	
	static public List<Integer> convertString(String str)
	{
		
		List<Integer> buffer = new ArrayList<Integer>();
		byte[] byteArray;
		try {
			byteArray = str.getBytes("UTF-8");
			
			for(byte b : byteArray) {
				buffer.add(CRC.getUnsignedInt(b));
			}
			for(int i = buffer.size(); i < 33; i++) {
				buffer.add(0x20);  // append SPACE '0x20' at the end to fill 32 character space
			}
			
			
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return buffer;

	}

    /**
     * Get the date String in a specific format (DD/MM/YYYY)
     * @param timestamp  list of Integer contains timestamp information
     * @return timestamp  String in the specified format
     */
    public static String getDate(List<Integer> timestamp) {
//		return String.format("%02d", timestamp.get(4)) + "/" + String.format("%02d", timestamp.get(3)) +
//				"/" + String.format("%02d", timestamp.get(1)) + String.format("%02d", timestamp.get(2));
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.DAY_OF_MONTH, timestamp.get(4));
        calendar.set(Calendar.MONTH, timestamp.get(3) - 1);
        calendar.set(Calendar.YEAR, timestamp.get(1) * 100 + timestamp.get(2));
        StringBuilder sb = new StringBuilder();
        sb.append(String.format("%02d", calendar.get(Calendar.DAY_OF_MONTH)));
        sb.append(" ");
        sb.append(calendar.getDisplayName(Calendar.MONTH, Calendar.SHORT, Locale.getDefault()));
        sb.append(" ");
        sb.append(String.format("%02d", calendar.get(Calendar.YEAR)));
        return sb.toString();
    }

    public static List<Report> getReportList (List<Integer> reportRawData){
        ArrayList<Report> reportList = new ArrayList<Report>();
        Report report;

        long startTime = System.nanoTime();

        for(int i=0; i<reportRawData.size()-3;i++) {

            if (reportRawData.subList(i,i+3).containsAll(REPORT_START_SEQUENCE))
            {
                report = new Report();
                report.setFaults(reportRawData.get(i+3));
                report.setStatus(reportRawData.get(i+3)==0? true:false);
                reportList.add(report);
            }


        }

        long timeElapsed = System.nanoTime() - startTime;
        double time = timeElapsed / 1E9;


        System.out.println("Time Elapsed " + time + " seconds");


        return reportList;

    }
	
}




