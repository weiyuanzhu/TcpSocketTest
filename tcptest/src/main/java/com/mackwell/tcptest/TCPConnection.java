package com.mackwell.tcptest;


import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.lang.ref.WeakReference;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;


public class TCPConnection {
	
	
	
	
	//interface for callback
		public interface CallBack 
		{
			public void receive(List<Integer> rx, String ip);
			public void onError(String ip);
		}
	
	private ExecutorService txExec;
		
	private int panelInfoPackageNo;
	private boolean rxCompleted;
	
	
	
	
	//callback for TCP Connection
	private WeakReference<CallBack> mCallBack;
	
	private int port;
	private String ip;
	
	private boolean isListening = false; // a flag for keep/stop the socket listening 
	
	public synchronized boolean  isListening() {
		return isListening;
	}


	
	
	//set this.isClosed
	public synchronized void setListening(boolean isListening)
	{
		this.isListening = isListening;
	}


	
	
	private List<Integer> rxBuffer; //buffer for receive data
	

	private List<char[]> commandList;

	
	private Socket socket;
	private PrintWriter out;
	private InputStream in; 
	

	
	
	//Constructor , requires a delegation(callback) object for callback
	public TCPConnection(CallBack callBack, String ip)
	{
		this.ip = ip;
		this.isListening = false;
		this.rxCompleted = false;
		this.rxBuffer = new ArrayList<Integer>();
		this.port = 500;
		this.mCallBack = new WeakReference<CallBack>(callBack);
		
		//create a single thread pool for tx
		txExec = Executors.newSingleThreadExecutor();
		
		//create a separate thread for rx only, this thread will block
		Thread rxThread = new Thread(rx);
		rxThread.start();
	}

	
	
	
	/*	function for pull data from panel
	 *  this function will start a new background thread to receive data from panel
	 * 
	 * */
	 
	

	
	public void fetchData(List<char[]> commandList){
		
		this.commandList = commandList;
        setListening(true);
		txExec.execute(tx);
		System.out.println("-------------------Connection started on thread:-------> " );
		
		
	}

	public void closeConnection()
	{
		isListening = false;
		try {
			if(socket != null)  
			{		
				out.close();
				in.close();
				socket.close();			
			}
			
		} catch (IOException ex) {
			ex.printStackTrace();
		}
		
		
	}
	
	
	/*	runnable for thread
	 *	thread will keep listening on socket.inputstream until received data is complete
	 *	and then calls its delegate to 
	 * 
	*/
	Runnable tx = new Runnable(){

		@Override
		public void run() {


			panelInfoPackageNo = 0;
			System.out.println(Thread.currentThread().toString() + "Tx thread starts");
			
			//char[] getPackageTest = new char[] {2, 165, 64, 15, 96, 0,0x5A,0xA5,0x0D,0x0A};
			//char[] getConfig = new char[] {0x02,0xA0,0x21,0x68,0x18,0x5A,0xA5,0x0D,0x0A};
			
			for(int i=0; i<commandList.size(); i++){
				
				char[] command = (char[]) commandList.get(i);
			
				try {
					// init socket and in/out stream
					
					
					
					if(socket == null ||  socket.isClosed())
					{
						socket = new Socket(ip,port);	
						socket.setSoTimeout(5000);
						socket.setReceiveBufferSize(80000);
						out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(socket.getOutputStream(),"ISO8859_1")),false);
						in = socket.getInputStream();
						
						System.out.println("\nConnected to: " + socket.getInetAddress() + ": "+  socket.getPort());
					}
					

					// send command to panel
					out.print(command);
					out.flush();
					
					Thread.yield();
					//TimeUnit.SECONDS.sleep(1);
					
	
					/*
					 *   Receive bytes from panel and put in rxBuffer arrayList
					 */
			
					/*int count = 0;
					while (count == 0) {
						   count = in.available();
						  }
					rx = new byte[count];
					
					int readCount = 0; 
					while (readCount < count) {
						
					   readCount += in.read(rx, readCount, count - readCount);
					}
	
					*/
					
					
						
						/*for(int j=0; j<rxBuffer.size();j+=1033)
						{
							System.out.println("------------------------Package " + j + "---------------------------");
							for(int k = j; i < j+1033;k++)
							{		
								System.out.print(rxBuffer.get(k)+ " ");
							}
							System.out.println();
						}*/
						
				}
				
				
			
				
				catch(Exception ex)
				{
					ex.printStackTrace();
					mCallBack.get().onError(ip);
				}
				finally
				{		
					try {
						TimeUnit.MILLISECONDS.sleep(Constants.GAP_BETWEEN_COMMANDS);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					System.out.println("Tx: complete (finally)");
						
					
				}		
		
			}
			
			//finished all commands in the commandList
			//Thread.yield();
		}
	
	};
	
	
	Runnable rx = new Runnable(){

		@Override
		public void run() {
			int data;
			
			// TimeUnit.SECONDS.sleep(3); get all panel data test
			try{
				
				if(socket == null ||  socket.isClosed())
				{
					socket = new Socket(ip,port);	
					
					setListening(false);
					
					socket.setSoTimeout(5000);
					socket.setReceiveBufferSize(80000);
					out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(socket.getOutputStream(),"ISO8859_1")),false);
					in = socket.getInputStream();
					
					System.out.println("\nConnected to: " + socket.getInetAddress() + ": "+  socket.getPort());
				}
				
				while(!socket.isClosed())
				{	
					//checks if a package is complete
					//and call callback
					
					
					/*if(in.available()==0 && !rxBuffer.isEmpty() && (data == Constants.UART_NEW_LINE_L) && 
	        				rxBuffer.get(rxBuffer.size() - 2).equals(Constants.UART_NEW_LINE_H) &&
	        				rxBuffer.get(rxBuffer.size() - 3).equals(Constants.UART_STOP_BIT_L) &&
	        				rxBuffer.get(rxBuffer.size() - 4).equals(Constants.UART_STOP_BIT_H))   // check finished bit; to be changed 
					{
						//System.out.println(rxBuffer.get(rxBuffer.size()-23));
						
						//mCallback.get() to get mCallBack instance, for it is  weakReference
						
						panelInfoPackageNo++ ;
						
						if(panelInfoPackageNo == commandList.size()){
							System.out.println(" All packages received");
							rxCompleted = true;
						}
						
						mCallBack.get().receive(rxBuffer,ip);
						System.out.println("rxBuffer size: " + rxBuffer.size());
						rxBuffer.clear();
					}*/
					
					//reading data from stream
					if(isListening())
					{
						data = in.read();
						rxBuffer.add(data);
						
						if(!rxBuffer.isEmpty() && (data == Constants.UART_NEW_LINE_L) && 
		        				rxBuffer.get(rxBuffer.size() - 2).equals(Constants.UART_NEW_LINE_H) &&
		        				rxBuffer.get(rxBuffer.size() - 3).equals(Constants.UART_STOP_BIT_L) &&
		        				rxBuffer.get(rxBuffer.size() - 4).equals(Constants.UART_STOP_BIT_H))   // check finished bit; to be changed 
						{
							//System.out.println(rxBuffer.get(rxBuffer.size()-23));
							
							//mCallback.get() to get mCallBack instance, for it is  weakReference
							
							panelInfoPackageNo++ ;
							
							if(panelInfoPackageNo == commandList.size()){
								System.out.println(" All packages received");
								rxCompleted = true;
							}
							
							mCallBack.get().receive(rxBuffer,ip);
							System.out.println("rxBuffer size: " + rxBuffer.size());
							rxBuffer.clear();
						
						}
					}
					
					//keep listening while available until isClosed flag is set to true
					
					else
					{
						try {
							TimeUnit.MILLISECONDS.sleep(10);
							//System.out.println("Rx thread keep listening");
							//System.out.println("rx thread is Listening");
						} catch (InterruptedException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}	
			
				
				}
			}
			catch(IOException e){
				e.printStackTrace();
			}
			finally{
				System.out.println("Finally, RX: closing thread");
					
				try {
					if(socket != null && !socket.isClosed())  
					{		
						out.close();
						in.close();
						socket.close();			
					}
						
				} catch (IOException ex) {
					ex.printStackTrace();

				}
				
				
			}
			
		}
		
		
		
	};

	public String getIp() {
		return ip;
	}


	public int getPanelInfoPackageNo() {
		return panelInfoPackageNo;
	}


	public boolean isRxCompleted() {
		return rxCompleted;
	}


    public static void printSocketInformation(Socket socket)
    {
        try
        {
            System.out.format("Port:                 %s\n",   socket.getPort());
            System.out.format("Canonical Host Name:  %s\n",   socket.getInetAddress().getCanonicalHostName());
            System.out.format("Host Address:         %s\n\n", socket.getInetAddress().getHostAddress());
            System.out.format("Local Address:        %s\n",   socket.getLocalAddress());
            System.out.format("Local Port:           %s\n",   socket.getLocalPort());
            System.out.format("Local Socket Address: %s\n\n", socket.getLocalSocketAddress());
            System.out.format("Receive Buffer Size:  %s\n",   socket.getReceiveBufferSize());
            System.out.format("Send Buffer Size:     %s\n\n", socket.getSendBufferSize());
            System.out.format("Keep-Alive:           %s\n",   socket.getKeepAlive());
            System.out.format("SO Timeout:           %s\n",   socket.getSoTimeout());
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }



	
	

}
