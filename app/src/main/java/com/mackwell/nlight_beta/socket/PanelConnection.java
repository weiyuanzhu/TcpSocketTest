package com.mackwell.nlight_beta.socket;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.lang.ref.WeakReference;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import com.mackwell.nlight_beta.util.Constants;



public class PanelConnection {

    public static final int CONNECTION_TIMEOUT = 10000; // 3 seconds
    public static final int READ_TIMEOUT = 5000;    // 5 seconds

	
	//interface for callback
		public interface CallBack 
		{
			public void receive(List<Integer> rx,String ip);
			public void onError(String ip, Exception e);
		}
	

	private int panelInfoPackageNo;
	private boolean rxCompleted;
    private boolean error;
	
	
	
	
	//callback for TCP Connection
	private WeakReference<CallBack> mCallBack;
	
	private int port;
	private String ip;
	
	private boolean isListening; // a flag for keep/stop the socket listening


	
	public synchronized boolean  isListening() {
		return isListening;
	}

	//set this.isClosed
	public synchronized void setListening(boolean isListening)
	{
		this.isListening = isListening;
	}


	private Socket socket;
	
	private List<Integer> rxBuffer; //buffer for receive data
	

	private List<char[]> commandList;

	private PrintWriter out;
	private InputStream in; 
	

	
	
	//Constructor , requires a delegation object for callback
	public PanelConnection(CallBack callBack, String ip)
	{
		this.ip = ip;
		this.isListening = false;
		this.rxCompleted = false;
        this.error = false;
		this.rxBuffer = new ArrayList<Integer>();
		this.port = 500;
		this.mCallBack = new WeakReference<CallBack>(callBack);
		
	}

	
	
	
	/*	function for pull data from panel
	 *  this function will start a new background thread to receive data from panel
	 * 
	 * */
	 
	

	
	public void fetchData(List<char[]> commandList){
		
		this.commandList = commandList;
		new Thread(fetch).start();
		System.out.println("Connection started on thread:-------> " );
		
		
	}

	public void closeConnection()
	{
		isListening = false;
		try {
			if(socket != null && socket.isConnected())
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
	Runnable fetch = new Runnable(){

		@Override
		public void run() {
			panelInfoPackageNo = 0;
			System.out.println(Thread.currentThread().toString() + "Slayver starts");
			
			//char[] getPackageTest = new char[] {2, 165, 64, 15, 96, 0,0x5A,0xA5,0x0D,0x0A};
			//char[] getConfig = new char[] {0x02,0xA0,0x21,0x68,0x18,0x5A,0xA5,0x0D,0x0A};
			
			for(int i=0; i<commandList.size(); i++){
				
				char[] command = commandList.get(i);
			
				try {
					// init socket and in/out stream
					
					setListening(true);

                    //re-create if socket not exist or has been closed
					if(socket == null || socket.isClosed())
					{
//						socket = new Socket(ip,port);
                        socket = new Socket();

                        //set socket read() timeout
						socket.setSoTimeout(READ_TIMEOUT);

                        socket.setKeepAlive(true);

                        //connect socket to server with a 3secs timeout
                        socket.connect(new InetSocketAddress(ip,port),CONNECTION_TIMEOUT);
						//socket.setReceiveBufferSize(20000);

                        out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(socket.getOutputStream(), "ISO8859_1")), false);
                        in = socket.getInputStream();

					}

                    // send command to panel if socket is connected to server and not closed
                    if(socket.isConnected() && !socket.isClosed()) {
                        //TCPConnection.printSocketInformation(socket);

                        out.print(command);
                        out.flush();
                    }
	
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
					
					int data = 0;
					
					//TimeUnit.SECONDS.sleep(3);
					while(isListening() && !socket.isClosed() && socket.isConnected())
					{	
						//checks if a package is complete
						//and call callback
						if(in.available()==0 && !rxBuffer.isEmpty() && (data == Constants.UART_NEW_LINE_L) && 
		        				rxBuffer.get(rxBuffer.size() - 2).equals(Constants.UART_NEW_LINE_H) &&
		        				rxBuffer.get(rxBuffer.size() - 3).equals(Constants.UART_STOP_BIT_L) &&
		        				rxBuffer.get(rxBuffer.size() - 4).equals(Constants.UART_STOP_BIT_H))   // check finished bit; to be changed 
						{
							//System.out.println(rxBuffer.get(rxBuffer.size()-23));
							
							//mCallback.get() to get mCallBack instance, for it is  weakReference
							
							panelInfoPackageNo ++ ;
							
							if(panelInfoPackageNo == commandList.size()){
								System.out.println(" All packages received");
								rxCompleted = true;
							}
							
							mCallBack.get().receive(rxBuffer,ip);
							System.out.println("rxBuffer size: " + rxBuffer.size());
							rxBuffer.clear();
						}
						
						//reading data from stream
//						if(in.available()>0)
                        if(isListening() && !socket.isClosed())
						{
							data = in.read();
                            if (data==-1) {
                                throw new TCPConnection.PanelResetException();

                            }

							rxBuffer.add(data);
		
						}
						
						//keep listening while available until isClosed flag is set to true
						
						else
						{
							TimeUnit.MILLISECONDS.sleep(100);
						}	
						
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
				
					
			
				} catch(SocketTimeoutException e)
                {
                    e.printStackTrace();
                    error = true;
                    setListening(false);
                    mCallBack.get().onError(ip,e);

                } catch (TCPConnection.PanelResetException e1){
                    e1.printStackTrace();
                    error = true;
                    setListening(false);
                    mCallBack.get().onError(ip,e1);
                }

                catch(Exception ex)
				{
					ex.printStackTrace();
                    error = true;
                    setListening(false);
					mCallBack.get().onError(ip,ex);
				} finally{

                    //close socket if all packages are received
                    if(panelInfoPackageNo == commandList.size()){
                        System.out.println("Finally: closing socket");
                        rxCompleted = true;
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
                    else if(error) {

                        //break out the command list for loop if there is connection exception
                        try {
                            if(socket != null && socket.isConnected() && !socket.isClosed())
                            {
                                out.close();
                                in.close();
                                socket.close();
                            }

                        } catch (IOException ex) {
                            ex.printStackTrace();

                        }
                        break;
                    }


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






	
	

}
