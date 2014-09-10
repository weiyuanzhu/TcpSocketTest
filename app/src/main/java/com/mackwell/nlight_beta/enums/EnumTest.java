package com.mackwell.nlight_beta.enums;

public class EnumTest {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		
		
		//OverallStatus m = OverallStatus.valueOf(OverallStatus, "OK");
		
		
		for(OverallStatus s : OverallStatus.values())
		{
			System.out.println(s.getStatus());
			
		}
		
		

	}

}
