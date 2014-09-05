package com.mackwell.nlight.util;

public class Constants {
	
	public static final int GAP_BETWEEN_COMMANDS = 500;
	
	public static final int TEXT_MAX = 32;
	public static final int PASSCODE_MAX = 4;
	
	public static final int ALL_DEVICES_AUTO_REFRESH_FREQUENCY = 60;
	public static final int SELECTED_DEVICE_AUTO_REFRESH_FREQUENCY = 5;
	
	
	public static final int ALL_OK = 0;
	public static final int FAULT = 1;
	public static final int WARNING = 2;
	
	public static final int UART_STOP_BIT_H = 0x5A;
	public static final int UART_STOP_BIT_L = 0xA5;
	public static final int UART_NEW_LINE_H = 0x0D;
	public static final int UART_NEW_LINE_L = 0x0A;
	
	public static final Integer HOST_ID = 0x02;

	public static final int MASTER_GET = 0xA0;
    public static final int GET_INIT = 0x21;
	public static final int GET_REPORT_EMPTY = 0x22;
	public static final int GET_FACTORY_RESET = 0x23;
    public static final int GET_FLASH_RESET = 0x24;
    public static final int GET_REPORT = 0x25;
    public static final int GET_LIST = 0x26;
    public static final int UPDATE_LIST = 0x27;
    public static final int GET_DATE_TIME = 0x29;
    public static final int GET_SCHEDULE_TEST_STATUS = 0x30;
    public static final int GET_SCHEDULE_DATA = 0x31;
	
	public static final int MASTER_TOGGLE = 0xA1;
    public static final int REMOVE_DEVICE = 0x67;
    public static final int REMOVE_LOOP = 0x68;
    public static final int COMMISSION_LOOP = 0x69;
    public static final int RETEST_GROUP = 0x6C;
    public static final int EEPROM_CORRUPT_FLAG = 0x6E;
	
	public static final int MASTER_SET = 0xA2;
    public static final int SET_GROUP = 0x80;
    public static final int SET_GROUP_NAME = 0x81;
    public static final int SET_DEVICE_NAME = 0x82;
    public static final int SET_PANEL_NAME = 0x84;
    public static final int SET_RTC = 0x85;
    public static final int SET_CONTACT_NAME = 0x86;
    public static final int SET_CONTACT_NUMBER = 0x87;
    public static final int SET_CONTACT_MOBILE = 0x88;
    public static final int SET_PASSCODE = 0x89;
    public static final int SET_LOGGING_FLAG = 0x8A;

    public static final int SET_DFU_UPDATING = 0x8F;
    public static final int SET_DFU_RESULT = 0x90;
    public static final int SET_I2C_UPLOAD = 0x91;

    public static final int USB_IN_CONTROL = 0xAC;
    public static final int FINISH = 0xAD;
    public static final int INITFINISH = 0xAE;
    public static final int ERROR = 0xAF;
	
	/* Report title */
	private static final int REPORT_START_H = 0x55;
	private static final int REPORT_START_M = 0xAA;
	private static final int REPORT_START_L = 0xFF;
	private static final int[] REPORT_TITLE = new int[] { REPORT_START_H, REPORT_START_M, REPORT_START_L };
	
	/* Message types for updating UI thread */
	static final int PRINT_TEXT = 0x01;
	static final int SET_TABLE_DEVICE = 0x02;
	static final int REFRESH_TREE = 0x03;
	static final int REFRESH_STATUS = 0x04;
	static final int SET_TABLE_REPORT = 0x05;
	static final int UPDATE_TREE = 0x06;
	static final int REFRESH_SERVICE = 0x07;
	static final int SET_I2C = 0x08;
	static final int PRINT_ERROR_TEXT = 0x09;
	
	private static final double FLASH_MEMORY = 7549747; // 90% of 8M bytes (8288608 bits)
	
	public static final int LOOP_MASK = 0x40;
	
	public static final int LOOP_ID = 0x80;
	public static final int DEVICE_ID = 0x3F;
	public static final int DEVICE_LOST_BYTE = 0x40;

	public static final String FIND_PANELS = "FIND";
	
}
