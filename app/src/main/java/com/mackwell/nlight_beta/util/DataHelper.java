package com.mackwell.nlight_beta.util;

import com.mackwell.nlight_beta.models.Report;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.*;

public class DataHelper {
	
	
	//panel stop and new line byte
	static final int UART_STOP_BIT_H = 0x5A;
	static final int UART_STOP_BIT_L = 0xA5;
	static final int UART_NEW_LINE_H = 0x0D;
	static final int UART_NEW_LINE_L = 0x0A;
    static final List<Integer> REPORT_START_SEQUENCE = Arrays.asList(0x55,0xAA,0xFF);
    static final List<Integer> GROUP_OK = Arrays.asList(0x00,0x00,0x00);


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
			for(int i = buffer.size(); i < 32; i++) {
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
    public static Calendar getDateCalendar (List<Integer> timestamp) {
//		return String.format("%02d", timestamp.get(4)) + "/" + String.format("%02d", timestamp.get(3)) +
//				"/" + String.format("%02d", timestamp.get(1)) + String.format("%02d", timestamp.get(2));
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.DAY_OF_MONTH, timestamp.get(3));
        calendar.set(Calendar.MONTH, timestamp.get(2)-1);
        calendar.set(Calendar.YEAR, timestamp.get(0) * 100 + timestamp.get(1));
        calendar.set(Calendar.HOUR_OF_DAY,timestamp.get(4));
        calendar.set(Calendar.MINUTE,timestamp.get(5));

        return calendar;
    }

    public static List<Report> getReportList (List<Integer> reportRawData){
        List<List<Integer>> reportDataList = new ArrayList<List<Integer>>();


        long startTime = System.nanoTime();

        for(int i=0; i<reportRawData.size()-3;i++) {

            if (reportRawData.get(i) == 255) {
                break;
            }
            else if (reportRawData.subList(i, i + 3).containsAll(REPORT_START_SEQUENCE)) {

                int faults = reportRawData.get(i + 3); //faults number in loop1 and loop2
                int nextMark = i + 18 + faults*6; //next number mark of start of next report

                List<Integer> reportData = reportRawData.subList(i, nextMark);
                reportDataList.add(reportData);

                i = nextMark + 1; //skip i to start of next report

            }


        }



        long timeElapsed = System.nanoTime() - startTime;
        double time = timeElapsed / 1E9;


        System.out.println("Time Elapsed " + time + " seconds");

        return getList(reportDataList);

    }

    private static List<Report> getList (List<List<Integer>> reportDataList){

        ArrayList<Report> reportList = new ArrayList<Report>();
        Report report;

        for(int i=0; i<reportDataList.size();i++)
        {
            List<Integer> reportData = reportDataList.get(i);

            report = new Report();
            List<List<Integer>> faultyDeviceList = new ArrayList<List<Integer>>();



            //Loop 1 group totalFaults

            for(int h=0;h<2;h++)
            {

                int groupNumber = 0;
                List<List<Integer>> groupStatusList = new ArrayList<List<Integer>>();

                //10-13 14-27 group status bytes
                for(int j = (h*4+10); j < (14 + h*4);j++)
                {
                    int group = reportData.get(j);
                    int flag = 0x80;


                    for (int k = 0; k<4; k++)
                    {
                        int ft;
                        int dt;

                        ArrayList<Integer> groupStatus = new ArrayList<Integer>();
                        groupStatus.add(groupNumber);



                        groupStatus.add((group & flag)>0 ? 1:0);
                        flag /=2;

                        groupStatus.add((group & flag)>0 ? 1:0);
                        flag /= 2;

                        if(groupStatus.get(1)!=0 || groupStatus.get(2)!=0)  groupStatusList.add(groupStatus);



                        groupNumber ++;
                    } // end of 1 byte




                } // end of 4 bytes

                //set group status to report
                switch(h)
                {
                    case 0: report.setLoop1GroupStatus(groupStatusList);
                        break;
                    case 1: report.setLoop2GroupStatus(groupStatusList);
                        break;
                    default: break;
                }


            }


            //set report properties
            int deviceFaults = reportData.get(3);

            if (deviceFaults!=0) {
                int deviceFlag = 18;

                for (int l=0; l<deviceFaults;l++)
                {
                    ArrayList<Integer> faultyDevice = new ArrayList<Integer>(reportData.subList(deviceFlag,deviceFlag + 6));

                    faultyDeviceList.add(faultyDevice);

                    deviceFlag += 6;
                }



            }

            report.setFaultyDeviceList(faultyDeviceList);

            int totalFaults = deviceFaults + report.getLoop1GroupStatus().size() + report.getLoop2GroupStatus().size();
            report.setFaults(totalFaults);
            report.setFaulty(totalFaults!=0);
            report.setDate(getDateCalendar(reportData.subList(4,10)));

            //add report to report list
            reportList.add(report);

        }


        return reportList;
    }

  public static boolean checkDataIntegrity(List<Integer> rx){
      int size = rx.size();
      int rxLsb = rx.get(size-6);
      int rxMsb = rx.get(size-5);


      int checksum = CRC.calcCRC(rx.subList(0,(rx.size()-6)), (rx.size()-6));
      int lsb = CRC.getUnsignedInt(checksum);
      int msb = CRC.getUnsignedInt(checksum >> 8);

      System.out.println("Data integrity: " + "rxLsb=: " + rxLsb + " lsb=: " + lsb);
      System.out.println("Data integrity: " + "rxMsb=: " + rxMsb + " msb=: " + msb);

      return true;
  }
	
}




