package com.proinlab.arduinoledcontroller;

import com.android.future.usb.UsbAccessory;
import com.android.future.usb.UsbManager;

import android.R.color;
import android.app.Activity;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.ToggleButton;

public class Arduino_LEDControllerActivity extends Activity {

	private static final String ACTION_USB_PERMISSION = "mob.korea.ac.kr.joystick.USB_PERMISSION";

	private ToggleButton LED1, LED2, LED3, LED4, LED5;
	private ToggleButton LED_YELLOW, LED_RED;
	private ToggleButton RGB_LED_R,RGB_LED_G,RGB_LED_B;
	
	private Button ledgamebtn;
	
	private ImageView _joystick_body;
	private ImageView _joystick_center;
	private int TouchPosY;
	private int TouchPosX;
	private int Gcontroller=0; // Motor Vertical Angle
	private int Bcontorller=0; // Motor Horizontal Angle
	
	
	private TextView menu1, menu2;
	private LinearLayout tab1, tab2;
	private TextView rgb_led_g, rgb_led_b;
	
	private AdkHandler handler;

	private UsbManager mUsbManager;
	private UsbAccessory mAccessory;
	private PendingIntent mPermissionIntent;
	private boolean mPermissionRequestPending;
	
	private final BroadcastReceiver mUsbReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();
			if (ACTION_USB_PERMISSION.equals(action)) {
				synchronized (this) {
					UsbAccessory accessory = UsbManager.getAccessory(intent);
					if (intent.getBooleanExtra(
							UsbManager.EXTRA_PERMISSION_GRANTED, false)) {
					} else {
						Log.d(Arduino_LEDControllerActivity.class.getName(),
								"permission denied for accessory " + accessory);
					}

					openAccessory(accessory);
					mPermissionRequestPending = false;
				}
			} else if (UsbManager.ACTION_USB_ACCESSORY_DETACHED.equals(action)) {
				UsbAccessory accessory = UsbManager.getAccessory(intent);
				if (accessory != null && accessory.equals(mAccessory)) {
					closeAccessory();
				}
			}
		}
	};

	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		
		rgb_led_g = (TextView)findViewById(R.id.green_val);
		rgb_led_b = (TextView)findViewById(R.id.blue_val);
		tab1 = (LinearLayout)findViewById(R.id.btnctrl_layout);
		tab2 = (LinearLayout)findViewById(R.id.joystick_layout);
		menu1 = (TextView)findViewById(R.id.tab1);
        menu2 = (TextView)findViewById(R.id.tab2);
        
        
      	IntentFilter filter = new IntentFilter(ACTION_USB_PERMISSION);
      	filter.addAction(UsbManager.ACTION_USB_ACCESSORY_DETACHED);
      	registerReceiver(mUsbReceiver, filter);
      	
      	mUsbManager = UsbManager.getInstance(this);
		mPermissionIntent = PendingIntent.getBroadcast(this, 0, new Intent(
				ACTION_USB_PERMISSION), 0);
		
		// 0x2, 0x1~0x5
		
		LED1 = (ToggleButton) findViewById(R.id.Led1);
		LED1.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (LED1.isChecked()) {
					if(handler != null && handler.isConnected()){
						handler.write((byte)0x2, (byte)0x1, (byte)0x1);
					}
				} else {
					if(handler != null && handler.isConnected()) handler.write((byte)0x2, (byte)0x1, (byte)0x2);
				}
			}
		});
		
		LED2 = (ToggleButton) findViewById(R.id.Led2);
		LED2.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (LED2.isChecked()) {
					if(handler != null && handler.isConnected()){
						handler.write((byte)0x2, (byte)0x2, (byte)0x1);
					}
				} else {
					if(handler != null && handler.isConnected()) handler.write((byte)0x2, (byte)0x2, (byte)0x2);
				}
			}
		});
		
		LED3 = (ToggleButton) findViewById(R.id.Led3);
		LED3.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (LED3.isChecked()) {
					if(handler != null && handler.isConnected()){
						handler.write((byte)0x2, (byte)0x3, (byte)0x1);
					}
				} else {
					if(handler != null && handler.isConnected()) handler.write((byte)0x2, (byte)0x3, (byte)0x2);
				}
			}
		});
		
		LED4 = (ToggleButton) findViewById(R.id.Led4);
		LED4.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (LED4.isChecked()) {
					if(handler != null && handler.isConnected()){
						handler.write((byte)0x2, (byte)0x4, (byte)0x1);
					}
				} else {
					if(handler != null && handler.isConnected()) handler.write((byte)0x2, (byte)0x4, (byte)0x2);
				}
			}
		});
		
		LED5 = (ToggleButton) findViewById(R.id.Led5);
		LED5.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (LED5.isChecked()) {
					if(handler != null && handler.isConnected()){
						handler.write((byte)0x2, (byte)0x5, (byte)0x1);
					}
				} else {
					if(handler != null && handler.isConnected()) handler.write((byte)0x2, (byte)0x5, (byte)0x2);
				}
			}
		});
		
		// 0x3, 0x1~0x2
		
		LED_RED = (ToggleButton) findViewById(R.id.Leds1);
		LED_RED.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (LED_RED.isChecked()) {
					if(!LED1.isChecked()) LED1.toggle();
					if(!LED3.isChecked()) LED3.toggle();
					if(!LED5.isChecked()) LED5.toggle();
					
					if(LED2.isChecked()) LED2.toggle();
					if(LED4.isChecked()) LED4.toggle();
					if(LED_YELLOW.isChecked()) LED_YELLOW.toggle();
					
					if(handler != null && handler.isConnected()) handler.write((byte)0x3, (byte)0x1, (byte)0x1);
				} else {
					if(LED1.isChecked()) LED1.toggle();
					if(LED3.isChecked()) LED3.toggle();
					if(LED5.isChecked()) LED5.toggle();
					
					if(LED2.isChecked()) LED2.toggle();
					if(LED4.isChecked()) LED4.toggle();
					if(LED_YELLOW.isChecked()) LED_YELLOW.toggle();
					
					if(handler != null && handler.isConnected()) handler.write((byte)0x3, (byte)0x1, (byte)0x2);
				}
			}
		});
		
		LED_YELLOW = (ToggleButton) findViewById(R.id.Leds2);
		LED_YELLOW.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (LED_YELLOW.isChecked()) {
					if(!LED2.isChecked()) LED2.toggle();
					if(!LED4.isChecked()) LED4.toggle();
					
					if(LED1.isChecked()) LED1.toggle();
					if(LED3.isChecked()) LED3.toggle();
					if(LED5.isChecked()) LED5.toggle();
					if(LED_RED.isChecked()) LED_RED.toggle();
					
					if(handler != null && handler.isConnected()) handler.write((byte)0x3, (byte)0x2, (byte)0x1);
				} else {
					if(LED2.isChecked()) LED2.toggle();
					if(LED4.isChecked()) LED4.toggle();
					
					if(LED1.isChecked()) LED1.toggle();
					if(LED3.isChecked()) LED3.toggle();
					if(LED5.isChecked()) LED5.toggle();
					if(LED_RED.isChecked()) LED_RED.toggle();
					
					if(handler != null && handler.isConnected()) handler.write((byte)0x3, (byte)0x2, (byte)0x2);
				}
			}
		});
		
		// 0x4, 
		_joystick_body = (ImageView) findViewById(R.id.joystick_body);
		_joystick_body.setOnTouchListener(MyTouchListener);
		
		_joystick_center = (ImageView) findViewById(R.id.joystick_center);

		RGB_LED_R = (ToggleButton) findViewById(R.id.rgb_red_onoff);
		RGB_LED_R.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (RGB_LED_R.isChecked()) {
					if(handler != null && handler.isConnected()){
						handler.write((byte)0x4, (byte)0x3, (byte)0x1);
					}
				} else {
					if(handler != null && handler.isConnected()) handler.write((byte)0x4, (byte)0x3, (byte)0x2);
				}
			}
		});
		RGB_LED_G = (ToggleButton) findViewById(R.id.rgb_green_onoff);
		RGB_LED_G.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (RGB_LED_G.isChecked()) {
					if(handler != null && handler.isConnected()){
						handler.write((byte)0x4, (byte)0x4, (byte)0x1);
					}
					rgb_led_g.setText(Integer.toString(255));
		  	    	Gcontroller = 255;
				} else {
					if(handler != null && handler.isConnected()) handler.write((byte)0x4, (byte)0x4, (byte)0x2);
					rgb_led_g.setText(Integer.toString(0));
					Gcontroller = 0;
				}
			}
		});
		RGB_LED_B = (ToggleButton) findViewById(R.id.rgb_blue_onoff);
		RGB_LED_B.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (RGB_LED_B.isChecked()) {
					if(handler != null && handler.isConnected()){
						handler.write((byte)0x4, (byte)0x5, (byte)0x1);	
					}
					rgb_led_b.setText(Integer.toString(255));
					Bcontorller = 255;
				} else {
					if(handler != null && handler.isConnected()) handler.write((byte)0x4, (byte)0x5, (byte)0x2);
					rgb_led_b.setText(Integer.toString(0));
					Bcontorller = 0;
				}
			}
		});
		
		// 화면구성 관련
		
		
		menu1.setBackgroundResource(R.drawable.colorblue);
	    menu2.setBackgroundColor(color.white);

		tab1.setVisibility(View.VISIBLE);
		tab2.setVisibility(View.GONE);

        menu1.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
	  	    	menu1.setBackgroundResource(R.drawable.colorblue);
	  	    	menu2.setBackgroundColor(color.white);
	  	    	tab1.setVisibility(View.VISIBLE);
	  	    	tab2.setVisibility(View.GONE);
	  	    	Bcontorller = 0;
	  	    	Gcontroller = 0;
	  	    	if(handler != null && handler.isConnected()){	
					handler.write((byte)0x5, (byte)0x7 , (byte)0x1);
	  	    	}
				rgb_led_g.setText(Integer.toString(Gcontroller));
				rgb_led_b.setText(Integer.toString(Bcontorller));
				if(LED1.isChecked()) LED1.toggle();
				if(LED2.isChecked()) LED2.toggle();
				if(LED3.isChecked()) LED3.toggle();
				if(LED4.isChecked()) LED4.toggle();
				if(LED5.isChecked()) LED5.toggle();
				if(LED_YELLOW.isChecked()) LED_YELLOW.toggle();
				if(LED_RED.isChecked()) LED_RED.toggle();
			}
		});
        menu2.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
	      		menu1.setBackgroundColor(color.white);
	  	    	menu2.setBackgroundResource(R.drawable.colorblue);
	  	    	tab1.setVisibility(View.GONE);
	  	    	tab2.setVisibility(View.VISIBLE);
	  	    	if(handler != null && handler.isConnected()){	
					handler.write((byte)0x5, (byte)0x7 , (byte)0x1);
				}
	  	    	if(RGB_LED_R.isChecked()) RGB_LED_R.toggle();
				if(RGB_LED_G.isChecked()) RGB_LED_G.toggle();
				if(RGB_LED_B.isChecked()) RGB_LED_B.toggle();
			}
		});
        
        ledgamebtn = (Button) findViewById(R.id.gamebtn);
        ledgamebtn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if(handler != null && handler.isConnected()){	
					handler.write((byte)0x5, (byte)0x7 , (byte)0x1);
				}
				if(LED1.isChecked()) LED1.toggle();
				if(LED2.isChecked()) LED2.toggle();
				if(LED3.isChecked()) LED3.toggle();
				if(LED4.isChecked()) LED4.toggle();
				if(LED5.isChecked()) LED5.toggle();
				if(LED_YELLOW.isChecked()) LED_YELLOW.toggle();
				if(LED_RED.isChecked()) LED_RED.toggle();
				gameintent();
			}
		});
        
	}
	
	void gameintent() {
		Intent ledgome_intent = new Intent(this, LEDGame.class);
        startActivity(ledgome_intent);
	}
	
    @Override
	public void onResume() {
		super.onResume();
		UsbAccessory[] accessories = mUsbManager.getAccessoryList();
		UsbAccessory accessory = (accessories == null ? null : accessories[0]);
		if (accessory != null) {
			if (mUsbManager.hasPermission(accessory)) {
				openAccessory(accessory);
			} else {
				synchronized (mUsbReceiver) {
					if (!mPermissionRequestPending) {
						mUsbManager.requestPermission(accessory,
								mPermissionIntent); 
						mPermissionRequestPending = true;
					}
				}
			}
		} else {
			Log.d(Arduino_LEDControllerActivity.class.getName(), "mAccessory is null");
		}
	}
    
    @Override
    public void onPause() {
         super.onPause();
         closeAccessory();
    }

 	@Override
 	protected void onDestroy() {
 		unregisterReceiver(mUsbReceiver);
 		super.onDestroy();
 	}
    
    private void openAccessory(UsbAccessory accessory){
		mAccessory = accessory;
        if(handler == null)
            handler = new AdkHandler();

       handler.open(mUsbManager, mAccessory);
	}
	
	private void closeAccessory(){
        if(handler != null && handler.isConnected())
            handler.close();
        mAccessory = null;
    }
	
	View.OnTouchListener MyTouchListener = new View.OnTouchListener() {
		public boolean onTouch(View v, MotionEvent event) {
			int control_y = 0;
			int control_x = 0;
			int move_centerX = 0;
			int move_centerY = 0;
			
			if (event.getAction() == MotionEvent.ACTION_MOVE) {
				
				TouchPosY = (int) event.getY();
				TouchPosX = (int) event.getX();

				// move controller & send command
				move_centerY = 225 - TouchPosY;
				move_centerX = 225 - TouchPosX;
				double r = Math.sqrt(move_centerY * move_centerY + move_centerX
						* move_centerX);
				if (move_centerY * move_centerY + move_centerX * move_centerX < 210 * 210) {
					_joystick_center.scrollTo(move_centerX, move_centerY);
					control_y = (225 - TouchPosY) / 10;
					control_x = (TouchPosX - 225) / 10;
				} else {
					control_y = (int) (210 * move_centerY / r / 10);
					control_x = (int) -(210 * move_centerX / r / 10);
					_joystick_center.scrollTo(move_centerX, move_centerY);
				}

				Gcontroller = Gcontroller + control_y;
				if(Gcontroller>255) Gcontroller = 255;
				if(Gcontroller<0) Gcontroller = 0;
				Bcontorller = Bcontorller + control_x;
				if(Bcontorller>255) Bcontorller = 255;
				if(Bcontorller<0) Bcontorller = 0;
				
				if(Gcontroller >= 120) if(!RGB_LED_G.isChecked()) RGB_LED_G.toggle();
				if(Bcontorller >= 120) if(!RGB_LED_B.isChecked()) RGB_LED_B.toggle();
				if(Gcontroller < 120) if(RGB_LED_G.isChecked()) RGB_LED_G.toggle();
				if(Bcontorller < 120) if(RGB_LED_B.isChecked()) RGB_LED_B.toggle();
				
				if(handler != null && handler.isConnected()){
					handler.write((byte)0x4, (byte)0x1, Gcontroller);
					handler.write((byte)0x4, (byte)0x2, Bcontorller);
				}
				rgb_led_g.setText(Integer.toString(Gcontroller));
				rgb_led_b.setText(Integer.toString(Bcontorller));
				return true;
			} else {
				_joystick_center.scrollTo(move_centerX, move_centerY);
				return true;
			}

		}
	};
	

}