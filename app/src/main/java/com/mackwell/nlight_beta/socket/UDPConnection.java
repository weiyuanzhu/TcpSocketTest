package com.mackwell.nlight_beta.socket;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

public class UDPConnection {

    public static final String FIND = "FIND";
    public static final String SETT = "SETT";
    public static final String SETC = "SETC";


	private List<byte[]> panelUDPDataList;
	
	
	private static final int SERVER_PORT = 1460;
	private static final int LISTEN_PORT = 5001;


    private String broadCastIp;
	private DatagramSocket udpSocket = null;
	private DatagramPacket udpTxPacket = null;
	private DatagramPacket udpRxPacket = null;

    private byte[] rxBuffer;

	private boolean isListening = true; // a flag for keep/stop listening on socket
	private UDPCallback mCallback;
	
	private String msg;
	
	private Thread rxThread = null;

    //getters and setters
    public synchronized boolean isListen() {
        return isListening;
    }

    public synchronized void setListen(boolean isListen) {
        this.isListening = isListen;
    }

    public List<byte[]> getPanelList() {
        return panelUDPDataList;
    }


    //interface
	public interface UDPCallback{
		public int addIp(byte[] mac,String ip);
		
	}
	
	
	public UDPConnection(String msg, UDPCallback callback)
	{
		//super();
		panelUDPDataList = new ArrayList<byte[]>();
		this.msg = msg;
		mCallback = callback;

        rxBuffer = new byte[256];

		//start udp listining

	}


	public void tx(String ip,String msg){
		this.msg = msg;
        this.broadCastIp = ip;



		Thread t = new Thread(tx);
		t.start();
		
		
	}
	
	Runnable tx = new Runnable(){
		
		public void run()
		{
			try {
				InetAddress address = InetAddress.getByName(broadCastIp);
				
				if(udpSocket==null){
					udpSocket = new DatagramSocket(LISTEN_PORT);
				}
				
				
				int msg_len = msg == null? 0 : msg.length();
				
				udpTxPacket = new DatagramPacket(msg.getBytes(),msg_len,address,SERVER_PORT);
				
				udpSocket.send(udpTxPacket);
				
				if(rxThread == null){
					rxThread = new Thread(rx);
					rxThread.start();
				}
				
				
			} catch (UnknownHostException e) {
				e.printStackTrace();
			} catch (SocketException e) {
				e.printStackTrace();
			} catch (IOException e){
				e.printStackTrace();
			}
			
			
		
		}	
		};

	
	Runnable rx= new Runnable()
	{

		@Override
		public void run() {

			System.out.println("---------------receiving udp packages------------");


			
			
			try{
				
				if(udpSocket==null){
					udpSocket = new DatagramSocket(LISTEN_PORT);
				}
				
				while(isListening)
				{
                    udpRxPacket= new DatagramPacket(rxBuffer, rxBuffer.length);
					udpSocket.receive(udpRxPacket);
					//byte[] buffer = new byte[rxBuffer.length];
					int i = 0;
					for(byte b : udpRxPacket.getData()) {
						//int a = b & 0xFF;
						rxBuffer[i] = b;
						
						i++;
					}
					
					/*for(int j =0; j<rxBuffer.length;j++)
					{
						System.out.print(rxBuffer[j] + " ");
						
					}*/
					
					panelUDPDataList.add(rxBuffer);
					mCallback.addIp(getMac(rxBuffer),getIp(rxBuffer));

                    //clear buffer
                    rxBuffer = new byte[256];


				}
			}
			catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			finally
			{
					
				if(udpSocket!=null && !udpSocket.isClosed()){
					System.out.println("Finally -- > UDP Socket Closing");
					udpSocket.close();
				}
					
			}
				
		}
	};

	
	/**
	 * Get a list of Panel's IP via UDP broadcast
	 * @return String[] an array of panel IP
	 */
	public String[] getIpList(){
		List<String> panelIpList = new ArrayList<String>();
		
		for(int i=0; i<panelUDPDataList.size();i++)
		{
			StringBuilder sb = new StringBuilder();
			for(int j=11; j<15; j++)
			{
				sb.append(panelUDPDataList.get(i)[j]);
				sb.append(".");
			}
			sb.deleteCharAt(sb.length()-1);
			panelIpList.add(sb.toString());
		}
		
		String[] ips = new String[panelIpList.size()]; 
		for(int i=0; i<ips.length;i++)
		{
			ips[i] = panelIpList.get(i);
			
		}
		
		return ips;
		
	}
	
	
	public  void closeConnection()
	{
		setListen(false);
		
		if(udpSocket!= null && !udpSocket.isClosed())
		{
			udpSocket.close();
			udpSocket = null;
		}
		
	}
	

	
	private String getIp(byte[] buffer)
	{
		StringBuilder sb = new StringBuilder();
		for(int j=11; j<15; j++)
		{
            int a = buffer[j] & 0xFF;
			sb.append(a);
			sb.append(".");
		}
		sb.deleteCharAt(sb.length()-1);
		
		return sb.toString();
	}

    private byte[] getMac(byte[] buffer){

        byte[] macAddress = new byte[6];
        for(int j=4; j<10; j++)
        {
            macAddress[j-4] = buffer[j];
        }

        return macAddress;
    }
}
