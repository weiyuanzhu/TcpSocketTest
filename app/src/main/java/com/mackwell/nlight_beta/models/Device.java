package com.mackwell.nlight_beta.models;




import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.text.DecimalFormat;
import java.util.EnumSet;
import java.util.List;

import android.os.Parcel;
import android.os.Parcelable;
import java.util.*;

import com.mackwell.nlight_beta.messageType.EmergencyMode;
import com.mackwell.nlight_beta.messageType.EmergencyModeFlag;
import com.mackwell.nlight_beta.messageType.EmergencyStatus;
import com.mackwell.nlight_beta.messageType.EmergencyStatusFlag;
import com.mackwell.nlight_beta.messageType.FailureStatus;
import com.mackwell.nlight_beta.messageType.FailureStatusFlag;
import com.mackwell.nlight_beta.util.Constants;

public class Device  implements Parcelable{
	//05 Feb 2014
	
	public static final int OK = 0;
	public static final int FAULTY = 1;
	public static final int LOADING = 2;
	
	
	
	private Calendar cal;
	
	private int address;
	

	private String location;
	private int failureStatus;
	private boolean communicationStatus; // false : communication lost / true : communication ok
	private int emergencyStatus;
	private int emergencyMode;
	private int battery;
	private long serialNumber;
	private BigInteger GTIN;
	private int[] gtinArray;
	private int dtTime;
	private int lampOnTime;
	private int lampEmergencyTime;
	private int feature;
	
	private int currentStatus;
	
	private boolean faulty;
	
	public Device(Parcel source) {
		this();
		readFromParcel(source);
	}
	
	public Device()
	{	
		address = 0;
		location = "test";
		failureStatus = 0;
		communicationStatus = true; 
		emergencyStatus = 0;
		emergencyMode = 0;
		battery = 254;
		serialNumber = 0;
		GTIN = BigInteger.valueOf(0);
		dtTime = 0;
		lampOnTime = 0;
		lampEmergencyTime = 0;
		feature = 0;
		gtinArray = new int[]{0,0,0,0,0,0};
	}
	
	public Device(int add, boolean cs,String loc,int fs,int es,int em,int bat,long sn,int[] gtin)
	{
		address = add;
		communicationStatus = cs;
		location = loc;
		failureStatus = fs;
		emergencyStatus = es;
		emergencyMode = em;
		battery = bat;
		serialNumber = sn;
		dtTime = 0;
		lampOnTime = 0;
		lampEmergencyTime = 0;
		feature = 0;
		gtinArray = gtin;
		cal = Calendar.getInstance();
	}
	
	public Device(List<Integer> device,List<List<Integer>> eepRom)
	{
		address = device.get(0);
		failureStatus = device.get(1);
		communicationStatus = device.get(2) == 0 ? true : false;
		emergencyStatus = device.get(3);
		emergencyMode = device.get(4);
		battery = device.get(5);		
		serialNumber = device.get(9) + device.get(8) * 256 + device.get(7)* 65536 + device.get(6)*16777216L;		
		GTIN = BigInteger.valueOf(device.get(15) + device.get(14) * 256 + device.get(13)*65535 + device.get(12)* 16777216L + device.get(11)* 4294967296L + device.get(10)*1099511627776L);
		dtTime = device.get(16)*2;
		lampOnTime = device.get(17);
		lampEmergencyTime = device.get(18);
		feature = device.get(19);
		location = getDeviceLocation(device,eepRom);
		
		gtinArray = new int[]{0,0,0,0,0,0};
		for(int i=0; i< gtinArray.length; i++)
		{
			int temp = 15-i;
			gtinArray[i] = device.get(temp);
		}
		
		cal = Calendar.getInstance();
		currentStatus = 0;
	}

	public void updateDevice(List<Integer> device)
	{
		failureStatus = device.get(4);
		communicationStatus = device.get(5) == 0 ? true : false;
		emergencyStatus = device.get(6);
		emergencyMode = device.get(7);
		battery = device.get(8);		
		dtTime = device.get(19)*2;
		lampOnTime = device.get(20);
		lampEmergencyTime = device.get(21);
		feature = device.get(22);
		
		cal = Calendar.getInstance();
		currentStatus = 0;
		
		
		
	}

	@Override
	public String toString() {
		String deviceStr = "Address: " + address + " FS: " + failureStatus  + " serialNumber: " + serialNumber + " GTIN: " + GTIN ;
		return deviceStr;
		
	}


	@Override
	public int describeContents() {
		// TODO Auto-generated method stub
		return 0;
	}


	@Override
	public void writeToParcel(Parcel dest, int flags) 
	{
		dest.writeInt(address);
		dest.writeString(location);
		dest.writeInt(failureStatus);
		dest.writeByte((byte)(communicationStatus ? 1 : 0));
		dest.writeInt(emergencyStatus);
		dest.writeInt(emergencyMode);
		dest.writeInt(battery);
		dest.writeLong(serialNumber);
		dest.writeInt(dtTime);
		dest.writeInt(lampOnTime);
		dest.writeInt(lampEmergencyTime);
		dest.writeInt(feature);
		dest.writeIntArray(gtinArray);
		
		dest.writeInt(currentStatus);
		
		
		
		
	}
	
	public void readFromParcel(Parcel source)
	{
		address = source.readInt();
		location = source.readString();
		failureStatus = source.readInt();
		communicationStatus = source.readByte()!= 0;
		emergencyStatus = source.readInt();
		emergencyMode = source.readInt();
		battery = source.readInt();
		serialNumber = source.readLong();
		dtTime = source.readInt();
		lampOnTime = source.readInt();
		lampEmergencyTime = source.readInt();
		feature = source.readInt();
		source.readIntArray(gtinArray);
		cal = Calendar.getInstance();
		currentStatus = source.readInt();
	}
	
	public static final Parcelable.Creator<Device> CREATOR = new Parcelable.Creator<Device>() {

		@Override
		public Device createFromParcel(Parcel source) {
			
			return new Device(source);
		}

		@Override
		public Device[] newArray(int size) {
			// TODO Auto-generated method stub
			return null;
		}
		
		
	};
	
	
	//getters


	public int getAddress() {
		return address;
	}
	
	public String getLocation() {
		return location.replaceAll("\\s+$", "");
	}
	
	

	public void setLocation(String location) {
		this.location = location;
	}

	public int getFailureStatus() {
		return failureStatus;
	}
	
	public void setFailureStatus(int failureStatus) {
		this.failureStatus = failureStatus;
	}

	public String getFailureStatusText() {
		StringBuilder sb = new StringBuilder();
		
		EnumSet<FailureStatus> fsSet = new FailureStatusFlag().getFlagStatus(failureStatus);
		
		if(communicationStatus){
			if (fsSet.size()==0)
			{
				sb.append("All OK");
			}
			else{
				for(FailureStatus fs : fsSet)
				{
					sb.append(fs.getDescription()+" , ");
				}
				System.out.println(sb);
				
				//trim last ","
				sb.deleteCharAt(sb.length()-2);
				
			}
		}
		else{
			sb.append("-");
		}
		return sb.toString();
		
	}
	
	public List<Integer> getFailureStringIds(){
		ArrayList<Integer> failureStringIds = new ArrayList<Integer>();
		
		EnumSet<FailureStatus> emSet = new FailureStatusFlag().getFlagStatus(failureStatus);
		if (emSet.size()==0)
		{
			failureStringIds.add(FailureStatus.ALL_OK.getStringId());
		}
		else{
			for(FailureStatus em : emSet)
			{
				failureStringIds.add(em.getStringId());
				
			}
			
		}
		
		return failureStringIds;
	}

	public boolean isCommunicationStatus() {
		return communicationStatus;
	}
	
	public String getCommunicationStatusText(){
		
		return communicationStatus ? "OK" : "Device lost";
	}

	public int getEmergencyStatus() {
		return emergencyStatus;
	}
	
	public String getEmergencyStatusText() {
		StringBuilder sb = new StringBuilder();
		
		EnumSet<EmergencyStatus> esSet = new EmergencyStatusFlag().getFlagStatus(emergencyStatus);
		
		if(communicationStatus){
			if (esSet.size()==0)
			{
				sb.append("-");
			}
			else{
				for(EmergencyStatus es : esSet)
				{
					sb.append(es.getDescription()+" , ");
				}
				System.out.println(sb);
				sb.deleteCharAt(sb.length()-2);
				
			}
			
		}
		else{
			sb.append("-");
		}
		return sb.toString();
	}
	
	public List<Integer> getEmergencyStatusStringIds(){
		ArrayList<Integer> emergencyStatusStringIds = new ArrayList<Integer>();
		
		EnumSet<EmergencyStatus> emSet = new EmergencyStatusFlag().getFlagStatus(emergencyStatus);
		if (emSet.size()==0)
		{
			emergencyStatusStringIds.add(EmergencyStatus.NORMAL.getStringId());
		}
		else{
			for(EmergencyStatus em : emSet)
			{
				emergencyStatusStringIds.add(em.getStringId());
				
			}
			
		}
		
		return emergencyStatusStringIds;
	}

	public int getEmergencyMode() {
		return emergencyMode;
	}
	
	public String getEmergencyModeText() {
		StringBuilder sb = new StringBuilder();
		
		EnumSet<EmergencyMode> emSet = new EmergencyModeFlag().getFlagStatus(emergencyMode);
		
		if(communicationStatus){
			if (emSet.size()==0)
			{
				sb.append("Normal Mode");
			}
			else{
				for(EmergencyMode em : emSet)
				{
					sb.append(em.getDescription()+" , ");
					
				}
				System.out.println(sb);
				sb.deleteCharAt(sb.length()-2);
				
			}
			
		}
		else{
			sb.append("-");
		}
		return sb.toString();
	}
	
	public List<Integer> getEmergencyModeStringIds(){
		ArrayList<Integer> emergencyModeStringIds = new ArrayList<Integer>();
		
		EnumSet<EmergencyMode> emSet = new EmergencyModeFlag().getFlagStatus(emergencyMode);
		if (emSet.size()==0)
		{
			emergencyModeStringIds.add(EmergencyMode.NORMAL_MODE.getStringId());
		}
		else{
			for(EmergencyMode em : emSet)
			{
				emergencyModeStringIds.add(em.getStringId());
				
			}
			
		}
		
		return emergencyModeStringIds;
	}

	public int getBattery() {
		return battery;
	}
	
	
	public String getBatteryLevel()
	{
		DecimalFormat df = new DecimalFormat("#.0");
		double bat = (double) battery/254*100;
		
		if(isCommunicationStatus()){
			switch(battery)
			{
				case 254:
	
					return "100 %";
				case 0:
					return "0 %";
				default: return df.format(bat) + " %";
	
			}
		}else{
			return "-";
		}
	
		
	}

	public long getSerialNumber() {
		return serialNumber;
	}

	public BigInteger getGTIN() {
		return GTIN;
	}

	public int getDtTime() {
		return dtTime;
	}

	public int getLampOnTime() {
		return lampOnTime;
	}

	public int getLampEmergencyTime() {
		return lampEmergencyTime;
	}
	
	public int getLampEmergencyTimeHour()
	{
		
		int i = getLampEmergencyTime()/60 + 1;
		
		
		
		return i;
		
	}

	public int getFeature() {
		return feature;
	}
	
	

	public int[] getGtinArray() {
		return gtinArray;
	}

	public void setGTIN(BigInteger gTIN) {
		GTIN = gTIN;
	}
	
	private String getDeviceLocation(List<Integer> device,List<List<Integer>> eepRom)
	{
		String location = null;
		
		int deviceNameLocation = (device.get(0) & Constants.LOOP_ID) == Constants.LOOP_ID ? 
				(device.get(0) & Constants.DEVICE_ID) + 64 : device.get(0);
		
		int eepRomLocation = deviceNameLocation + Constants.LOOP_ID;

		byte[] deviceLocationArray = getBytes(eepRom.get(eepRomLocation));
		try {
			location = new String(deviceLocationArray,"UTF-8");
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return location;

		
	}
	
	private byte[] getBytes(List<Integer> list)
	{
		byte[] temp = new byte[list.size()];
		
		for (int i=0; i<list.size(); i++)
		{
			temp[i] =  list.get(i).byteValue();
			//System.out.print(temp[i]);
		}
		return temp;
	}

	/**
	 * @return the boolean faulty
	 */
	public boolean isFaulty() {
		//check both failure status and communication status
		//either will cause device faulty
		if(failureStatus==0 && communicationStatus) {
			return false;
		}
		return true;
	}

	/**
	 * @param isFailed the isFailed to set
	 */
	public void setFaulty(boolean faulty) {
		this.faulty = faulty;
	}
	
	

	public Calendar getCal() {
		return cal;
	}

	public void setCal(Calendar cal) {
		this.cal = cal;
	}

	public int getCurrentStatus() {
		EnumSet<EmergencyMode> emSet = new EmergencyModeFlag().getFlagStatus(emergencyMode);
		EnumSet<EmergencyStatus> esSet = new EmergencyStatusFlag().getFlagStatus(emergencyStatus);
		
		if(esSet.contains(EmergencyStatus.IDENTIFICATION_ACTIVE) 
			|| emSet.contains(EmergencyMode.DURATION_TEST_IN_PROGRESS)
			|| emSet.contains(EmergencyMode.FUNCTION_TEST_IN_PROGRESS)){
			return LOADING;
		}
		
		if(failureStatus!=0 || !communicationStatus) {
			return FAULTY;
			
		}
		
		return OK;
	}

	public void setCurrentStatus(int currentStatus) {
		this.currentStatus = currentStatus;
	}

	
	
	//generic method for get status text
	
	//todo
	
	/*public <T>  String  test(T t)
	{
		
		t.getClass().getName();
		StringBuilder sb = new StringBuilder();
		
		EnumSet<t> emSet = new EmergencyModeFlag().getFlagStatus(i);
		
		if (emSet.size()==0)
		{
			return "All OK";
		}
		else{
			for(Enum a : emSet)
			{
				sb.append(((EmergencyMode) a).getDescription()+" , ");
			}
			System.out.println(sb);
			sb.deleteCharAt(sb.length()-2);
			return sb.toString();
		
		 return null;
	}*/

    public static String getFailureStatusText(int failureStatus) {
        StringBuilder sb = new StringBuilder();

        if (failureStatus ==0) {
            return "Device lost";
        }

        EnumSet<FailureStatus> fsSet = new FailureStatusFlag().getFlagStatus(failureStatus);


        if (fsSet.size()==0)
        {
            sb.append("All OK");
        }
        else{
            for(FailureStatus fs : fsSet)
            {
                sb.append(fs.getDescription()+" , ");
            }
            System.out.println(sb);

            //trim last ","
            sb.deleteCharAt(sb.length()-2);

        }


        return sb.toString();

    }


}
