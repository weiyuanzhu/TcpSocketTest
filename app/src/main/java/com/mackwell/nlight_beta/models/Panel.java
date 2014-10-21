package com.mackwell.nlight_beta.models;

import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.text.DecimalFormat;
import java.util.List;

import android.os.*;

public class Panel  implements Parcelable {
	
	public static final double FLASH_MEMORY = 7549747; // 90% of 8M bytes (8288608 bits)
	public static final int OK = 0x01;
	public static final int NOT_CONFIG = 0x02;
	public static final int FAULT = 0x03;
	public static final int CLOCK_NOT_SYNC = 0x04;
	public static final int RESERVED = 0x05;
	public static final int GROUP_TEST_MISS = 0x06;
	public static final int GROUP_TEST_MISS_AND_FAULT = 0x07;
	public static final int MEMORY_FULL = 0x08;
	public static final int MEMORY_FAILURE = 0x09;
	
	
	
	private Loop loop1;
	private Loop loop2;
	
	private String ip;
    private String macString;
//    private byte[] macAddress;

	private String panelLocation;
	private String contact;
	private String tel;
	private String mobile;
	private String version;
	private String id;
	private String passcode;
	
	private long reportUsageLong;
	private String reportUsage;
	
	private Long serialNumber;
	private BigInteger gtin;
	private int[] gtinArray;

	
	private int deviceNumber;
	
	private int overAllStatus;
	private int faultDeviceNo;
	
	private boolean engineerMode = false;
	
	
	public Panel()
	{
		//init 
		gtinArray = new int[6]; 
	}

	public Panel(String ip,String mac)

	{
		setIp(ip);
        macString = mac;
		panelLocation = "";
		contact = "Mackwell Engineer";
		loop1 = new Loop("Loop1");
		loop2 = new Loop("Loop2");
		tel = "01922 458 255";
		mobile = "0742600000";
		version = "V 1.3.0";
		id = "test";
		passcode= "1111";
		reportUsageLong = 3024000;
		reportUsage = "1%";
		serialNumber = (long) 1234567;
		gtinArray = new int[]{6,5,4,3,2,1};
		overAllStatus = 0;
		
	}
	
	

	public Panel(Parcel source)
	{
		this();
		readFromParcel(source);
	}

    public Panel(List<List<Integer>> eepRom, List<List<List<Integer>>> deviceList, String ip) throws UnsupportedEncodingException
    {
        gtinArray = new int[6];

        this.ip = ip;


        this.panelLocation = new String(getBytes(eepRom.get(60)),"UTF-8");

        String con = new String(getBytes(eepRom.get(61)),"UTF-8");
        this.contact = con.contains("?")? "-" : con;


        String tel = new String(getBytes(eepRom.get(62)),"UTF-8");
        this.tel = tel.contains("?")? "-" : tel;

        String mob = new String(getBytes(eepRom.get(63)),"UTF-8");
        this.mobile = mob.contains("?")? "-" : mob;


        this.version = new String(getBytes(eepRom.get(13)),0,16,"UTF-8");

        this.serialNumber = eepRom.get(3).get(9) + eepRom.get(3).get(8) * 256 + eepRom.get(3).get(7) * 65536 +
                eepRom.get(3).get(6) * 16777216L;

        this.gtin = BigInteger.valueOf(eepRom.get(3).get(5) + eepRom.get(3).get(4) * 256 +
                eepRom.get(3).get(3) * 65536 + eepRom.get(3).get(2) * 16777216L +
                eepRom.get(3).get(1) * 4294967296L + eepRom.get(3).get(0) * 1099511627776L);

        this.gtinArray = new int[]{0,0,0,0,0,0};
        for(int i=0; i< gtinArray.length; i++)
        {
            int temp = 5-i;
            gtinArray[i] = eepRom.get(3).get(temp);
        }


        this.passcode = String.valueOf(eepRom.get(51).get(0) * 256 + eepRom.get(51).get(1));

        reportUsageLong = eepRom.get(15).get(3) + eepRom.get(15).get(4) * 256 + eepRom.get(15).get(5) * 65536 +
                eepRom.get(15).get(6) * 16777216L;

        //this.reportUsage = (new DecimalFormat("#.#####").format(reportUsage / FLASH_MEMORY) +"%"); // update report usage
        this.reportUsage = (new DecimalFormat("#.#####").format(reportUsageLong / FLASH_MEMORY) +"%"); // update report usage

        System.out.println("================Panel Info========================");
        System.out.println(this.toString());

        loop1 = new Loop(deviceList.get(0),eepRom,"Loop1");
        loop2 = new Loop(deviceList.get(1),eepRom,"Loop2");

    }

	public void updatePanel(List<List<Integer>> eepRom, List<List<List<Integer>>> deviceList, String ip) throws UnsupportedEncodingException
	{
		gtinArray = new int[6];
		
		this.ip = ip;

		this.panelLocation = new String(getBytes(eepRom.get(60)),"UTF-8");
		
		String con = new String(getBytes(eepRom.get(61)),"UTF-8");
		this.contact = con.contains("?")? "-" : con;
		
		
		String tel = new String(getBytes(eepRom.get(62)),"UTF-8");
		this.tel = tel.contains("?")? "-" : tel;
		
		String mob = new String(getBytes(eepRom.get(63)),"UTF-8");
		this.mobile = mob.contains("?")? "-" : mob;
		
		
		this.version = new String(getBytes(eepRom.get(13)),0,16,"UTF-8");
		
		this.serialNumber = eepRom.get(3).get(9) + eepRom.get(3).get(8) * 256 + eepRom.get(3).get(7) * 65536 + 
				eepRom.get(3).get(6) * 16777216L;
		
		this.gtin = BigInteger.valueOf(eepRom.get(3).get(5) + eepRom.get(3).get(4) * 256 + 
				eepRom.get(3).get(3) * 65536 + eepRom.get(3).get(2) * 16777216L + 
				eepRom.get(3).get(1) * 4294967296L + eepRom.get(3).get(0) * 1099511627776L);
		
		this.gtinArray = new int[]{0,0,0,0,0,0};
		for(int i=0; i< gtinArray.length; i++)
		{
			int temp = 5-i;
			gtinArray[i] = eepRom.get(3).get(temp);
		}
		
		
		this.passcode = String.valueOf(eepRom.get(51).get(0) * 256 + eepRom.get(51).get(1));
		
		reportUsageLong = eepRom.get(15).get(3) + eepRom.get(15).get(4) * 256 + eepRom.get(15).get(5) * 65536 + 
				eepRom.get(15).get(6) * 16777216L;
		
		//this.reportUsage = (new DecimalFormat("#.#####").format(reportUsage / FLASH_MEMORY) +"%"); // update report usage
		this.reportUsage = (new DecimalFormat("#.#####").format(reportUsageLong / FLASH_MEMORY) +"%"); // update report usage
		
		System.out.println("================Panel Info========================");
		System.out.println(this.toString());
		
		loop1 = new Loop(deviceList.get(0),eepRom,"Loop1");
		loop2 = new Loop(deviceList.get(1),eepRom,"Loop2");

	}
	
	public static final Parcelable.Creator<Panel> CREATOR = new Parcelable.Creator<Panel>(){

		@Override
		public Panel createFromParcel(Parcel source) {

			return new Panel(source);
		}

		@Override
		public Panel[] newArray(int size) {
			// TODO Auto-generated method stub
			return null;
		}
		
	};
	
	
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
	
	public String toString()
	{
		String description = "\nLocation: " + panelLocation + "\ncontact: " + contact + "\nTel: " + tel + "\nSerialNumber: " + serialNumber + "\nGTIN: " + gtin + "\nVersion: " + version
				+ "\nReport Usage: " + reportUsage + "\nPasscode: " + passcode;
		return description;
			
	}


	//getters

	public boolean isEngineerMode() {
		return engineerMode;
	}

	public void setEngineerMode(boolean engineerMode) {
		this.engineerMode = engineerMode;
	}

	public String getPanelLocation() {
		return panelLocation;
	}


	public void setPanelLocation(String panelLocation) {
		this.panelLocation = panelLocation;
	}

	public String getContact() {
		return contact;
	}


	
	public void setContact(String contact) {
		this.contact = contact;
	}

	public String getTel() {
		return tel;
	}


	public String getMobile() {
		return mobile;
	}


	public String getVersion() {
		return version;
	}


	public String getId() {
		return id;
	}


	public Long getSerialNumber() {
		return serialNumber;
	}

    public String getMacString() {
        return macString;
    }

    public void setMacString(String macString) {
        this.macString = macString;
    }

    public void setSerialNumber(Long serialNumber) {
		this.serialNumber = serialNumber;
	}

	public BigInteger getGtin() {
		return gtin;
	}

	public void setGtin(BigInteger gtin) {
		this.gtin = gtin;
	}

	public String getPasscode() {
		return passcode;
	}


	public String getReportUsage() {
		double r = (reportUsageLong / FLASH_MEMORY) * 100;
		int a = (int) Math.round(r) + 1;
		StringBuilder sb = new StringBuilder();
		if(a==1){
			sb.append("< ");
		}
		sb.append(a);
		sb.append("%");
		return sb.toString();
	}


	public int getDeviceNumber() {
		
		if(loop1 != null)
		{
				deviceNumber += loop1.getDeviceNumber();		
		}
		
		if(loop2 != null)
		{
			deviceNumber += loop2.getDeviceNumber();
		}
				
		return deviceNumber;
	}

	
	
	public int[] getGtinArray() {
		return gtinArray;
	}
	
	

	public void setGtinArray(int[] gtinArray) {
		this.gtinArray = gtinArray;
	}

	public Loop getLoop1() {
		return loop1;
	}

	public Loop getLoop2() {
		return loop2;
	}

	public String getIp() {
		return ip;
	}

	public void setIp(String ip) {
		this.ip = ip;
	}
	
	public void setTel(String tel) {
		this.tel = tel;
	}

	public void setMobile(String mobile) {
		this.mobile = mobile;
	}

	public void setPasscode(String passcode) {
		this.passcode = passcode;
	}

	public void setReportUsage(String reportUsage) {
		this.reportUsage = reportUsage;
	}
	
	

	public long getReportUsageLong() {
		return reportUsageLong;
	}

	public void setReportUsageLong(long reportUsageLong) {
		this.reportUsageLong = reportUsageLong;
	}

	public void setOverAllStatus(int overAllStatus) {
		this.overAllStatus = overAllStatus;
	}



	@Override
	public int describeContents() {
		// TODO Auto-generated method stub
		return 0;
	}
	
	

	public int getFaultDeviceNo() {
		int faultDeviceNo = 0;
		
		faultDeviceNo += loop1.getFaultyDevicesNo();
		faultDeviceNo += loop2.getFaultyDevicesNo();
		
		return faultDeviceNo;
	}

	private void setFaultDeviceNo(int faultDeviceNo) {
		this.faultDeviceNo = faultDeviceNo;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		
		dest.writeValue(loop1);
		dest.writeValue(loop2);
		
		dest.writeString(ip);
        dest.writeString(macString);
		dest.writeString(panelLocation);
		dest.writeString(contact);
		dest.writeString(tel);
		dest.writeString(mobile);
		dest.writeString(version);
		dest.writeString(id);
		dest.writeString(passcode);
		dest.writeLong(reportUsageLong);
		dest.writeString(reportUsage);
		
		
		dest.writeLong(serialNumber);
		dest.writeIntArray(gtinArray);
		dest.writeInt(overAllStatus);
        dest.writeByte((byte) (engineerMode ? 1 : 0));
    }
	
	public void readFromParcel(Parcel source)
	{
		
		loop1 = (Loop) source.readValue(Loop.class.getClassLoader());
		loop2 = (Loop) source.readValue(Loop.class.getClassLoader());
		
		ip = source.readString();
        macString = source.readString();
		panelLocation = source.readString();
		contact = source.readString();
		tel  = source.readString();
		mobile  = source.readString();
		version  = source.readString();
		id  = source.readString();
		passcode  = source.readString();
		reportUsageLong = source.readLong();
		reportUsage = source.readString();
		
		serialNumber = source.readLong();
		
		source.readIntArray(gtinArray);
		overAllStatus = source.readInt();
        engineerMode = source.readByte() !=0;

	
	}

	public int getOverAllStatus() {
		
		if(loop1.getStatus()!=0 || loop2.getStatus()!=0)
		{
			overAllStatus = Panel.FAULT;
			
		}else overAllStatus = Panel.OK;
		return overAllStatus;
	}

	private void removeFaultyDevices()
	{
		if(loop1 !=null) loop1.setDeviceList(loop1.removeGoodDevices());
		if(loop2 !=null) loop2.setDeviceList(loop2.removeGoodDevices());
		
	}
	
	public static Panel getPanelWithFaulty(Panel panel) 
	{
		
		
		
		Panel newPanel = panel;
	
		newPanel.removeFaultyDevices();
		
		
		return newPanel;
		
		
		
	}
	
	public int updateDeviceByAddress(int address, List<Integer> device){
		
		if(address<64){
			
			return loop1.upDateDeviceByAddress(address, device);
		}
		else return loop2.upDateDeviceByAddress(address, device);
		
		
	}
	
	
}
