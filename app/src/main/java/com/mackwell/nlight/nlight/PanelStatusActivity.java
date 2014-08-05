package com.mackwell.nlight.nlight;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.mackwell.nlight.R;
import com.mackwell.nlight.R.drawable;

public class PanelStatusActivity extends BaseActivity {
	
	private int status;
	private List <Integer> txBuffer = new ArrayList<Integer>();
	private List <Integer> rxBuffer = new ArrayList<Integer>();
	private String ip = null;
	private String location = null;
	
	
	private TextView statusTextView = null;
	private ImageView imageView = null;
	private TextView locationTextView = null;
	private ProgressBar load = null;
	private Toast toast = null;
	
	private Socket socket = null;
	private PrintWriter out = null;
	private InputStream in = null;
	
	private Handler myHandler;
	private int faultFlag = 0;
	private int okFlag = 0;

	@SuppressLint("HandlerLeak")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_panel_status);
		
		statusTextView = (TextView) findViewById(R.id.textView1);
		locationTextView = (TextView) findViewById(R.id.textView2);
		imageView = (ImageView) findViewById(R.id.imageView1);
		load = (ProgressBar) findViewById(R.id.progressBar1);
		
		Intent intent = getIntent();
		
		ip = intent.getStringExtra("ip");
		location = intent.getStringExtra("location");
		
		locationTextView.setText(location);
		statusTextView.setText("Loading Status");
		
		new Thread(overallStatus).start();
		
		System.out.println("second activity:" + ip);
		
		
		
		
		myHandler =  new Handler() {
			  @Override
			  public void handleMessage(Message msg) 
			  {
				  
				  System.out.println("Message :" + msg.arg1);
				  
				  switch(msg.arg1)
				  {
				  	case 1 : 
				  	{
				  		statusTextView.setText("OK");
				  		imageView.setImageResource(drawable.greentick);
				  		load.setVisibility(View.INVISIBLE);
						imageView.setVisibility(View.VISIBLE);
						
						if(okFlag==0)
						{
							toast = Toast.makeText(PanelStatusActivity.this,
								     "Panel is ok", Toast.LENGTH_SHORT);
							
							toast.setGravity(Gravity.CENTER, 0, 400);
							toast.show();
							
						}
						
						okFlag = 1;
						faultFlag =0;
						
						break;
				  	}
				  	case 2 : 
				  		{
				  			statusTextView.setText("System not configured"); 
				  			imageView.setImageResource(drawable.redcross);
				  			load.setVisibility(View.INVISIBLE);
							imageView.setVisibility(View.VISIBLE);
				  			
				  			break;
				  		}
				  	case 3 : 
				  	{
			  			statusTextView.setText("Fault(s) found"); 
			  			load.setVisibility(View.INVISIBLE);
						imageView.setVisibility(View.VISIBLE);
			  			
			  			imageView.setImageResource(drawable.redcross);
			  			
			  			if(faultFlag==0)
						{
							toast = Toast.makeText(PanelStatusActivity.this,
								     "Fault(s) found", Toast.LENGTH_SHORT);
							
							toast.setGravity(Gravity.CENTER, 0, 400);
							toast.show();
							
						}
						
						faultFlag = 1;
						okFlag = 0;
			  			
			  			
			  			break;
			  		}
				  	case 4 : 
				  	{	
				  		statusTextView.setText("clock not synchronised");
				  		imageView.setImageResource(drawable.ambertick);
				  		load.setVisibility(View.INVISIBLE);
						imageView.setVisibility(View.VISIBLE);
				  		break;
				  	}
				  	default: break;
				  		
					  
				  }
				  
				  
				  
				  
				  
				  new Thread(overallStatus).start();
				  
				  
			  }
			  
			  
			 };;
			 
		
		
		
		
		
		
		
		
		
	}

	Runnable overallStatus = new Runnable()
	{
		
		public void run()	
		{
			
			try {
				Thread.sleep(3000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			int port = 500;
		
			char[] buffer = new char[] {0x02,0xA0,0x32,0x29,0xD5,0x5A,0xA5,0x0D,0x0A};
			
			String test = new String(buffer,0,buffer.length);
    		System.out.println("\n----:  " +  test);
        	System.out.println("outprint string= " + test);
			
			try {
				
				socket = new Socket(ip,port);
				out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(socket.getOutputStream(),"ISO8859_1")),false);

				in = socket.getInputStream();
				
				//in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			        
				    	
			      
				System.out.println("Connect to: " + socket.getInetAddress() + ": "+  socket.getPort());
				
				//String test2 = "this is a test";
				
				out.print(buffer);
				out.flush();
				
				/*String inputLine;
			      while ((inputLine = in.readLine()) != null) {
			            	System.out.println(inputLine);
			                //out.println(inputLine);
			    }*/
			
				
				
				
				int data = 0;
				
				Thread.sleep(2000);
				
				//System.out.println("available:" + in.available());
				while (in.available()>0)
				{
					data = in.read();
					rxBuffer.add(data);
					System.out.print(data + " ");
					
					
				}
				
				for(Integer i:rxBuffer)
				{
					Log.d("debug",i.toString());
					
				}
				
				
				
					
					
				if(!rxBuffer.isEmpty())
				{
					status = rxBuffer.get(3);
					System.out.println("status: " + status);
					Message message = Message.obtain();
					message.arg1 = status;
						
					
					myHandler.sendMessage(message);
					
				
					rxBuffer.clear();
				}
				else{
					
					Message message = Message.obtain();
					message.arg1 = status;
						
					myHandler.sendMessage(message);
				
					rxBuffer.clear();
					
					
				}
			
				
				
				
			}
			catch(Exception ex){
				ex.printStackTrace();
			}
			finally{
				
				try {
					socket.close();
				} catch (IOException ex) {
					// TODO Auto-generated catch block
					ex.printStackTrace();
				}
			}
		
		}
		
	};
	
	public void showPanalInfo(View v)
	{
		Intent i = new Intent(this,PanelInfoActivity.class);
		startActivity(i);
		
	}
	
	public void updateStatus(String str)
	{
		TextView textView = (TextView) findViewById(R.id.textView1);
		System.out.println(str);
		textView.setText(str);
		
	}
	
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
	
	public Handler getHandler()
	{
		return this.myHandler;
	}
	
}
