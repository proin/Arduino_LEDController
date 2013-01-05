package com.proinlab.arduinoledcontroller;

import com.android.future.usb.UsbAccessory;
import com.android.future.usb.UsbManager;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class LEDGame extends Activity {
	private static final String ACTION_USB_PERMISSION = "mob.korea.ac.kr.joystick.USB_PERMISSION";

	private AdkHandler handler;

	private UsbManager mUsbManager;
	private UsbAccessory mAccessory;
	private PendingIntent mPermissionIntent;
	private boolean mPermissionRequestPending;

	private Button Summit, exit, startbtn, replay;
	private TextView answertxt;
	private EditText anstv[] = new EditText[5];
	private int num[] = new int[5];
	private int ans[] = new int[5];
	
	private int level=1;
	private int level_sec=255/level;

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
		setContentView(R.layout.game);

		IntentFilter filter = new IntentFilter(ACTION_USB_PERMISSION);
		filter.addAction(UsbManager.ACTION_USB_ACCESSORY_DETACHED);
		registerReceiver(mUsbReceiver, filter);

		mUsbManager = UsbManager.getInstance(this);
		mPermissionIntent = PendingIntent.getBroadcast(this, 0, new Intent(
				ACTION_USB_PERMISSION), 0);

		answertxt = (TextView) findViewById(R.id.answer_check);
		anstv[0] = (EditText) findViewById(R.id.answer1);
		anstv[1] = (EditText) findViewById(R.id.answer2);
		anstv[2] = (EditText) findViewById(R.id.answer3);
		anstv[3] = (EditText) findViewById(R.id.answer4);
		anstv[4] = (EditText) findViewById(R.id.answer5);

		// 0x5, 0x1~0x2, 0x1~0x5

		startbtn = (Button) findViewById(R.id.startbtn);
		startbtn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				int i, j;
				for (i = 0; i < 5; i++)
					anstv[i].setText("");

				for (i = 0; i < 5; i++) {
					num[i] = 1 + (int) (Math.random() * 5);
					for (j = 0; j < i; j++) {
						if (num[i] == num[j]) {
							num[i] = 1 + (int) (Math.random() * 5);
							i = i - 1;
							break;
						}
					}
				}

				if (handler != null && handler.isConnected()) {
					for (i = 0; i < 5; i++)
						handler.write((byte) 0x5, (byte) num[i], (byte) level_sec);
				}
			}
		});

		replay = (Button) findViewById(R.id.replaybtn);
		replay.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				int i;
				for (i = 0; i < 5; i++)
					anstv[i].setText("");

				if (handler != null && handler.isConnected()) {
					for (i = 0; i < 5; i++)
						handler.write((byte) 0x5, (byte) num[i], (byte) level_sec);
				}
			}
		});

		Summit = (Button) findViewById(R.id.answer_summit);
		Summit.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				int i;
				int count = 0;
				for (i = 0; i < 5; i++) {
					ans[i] = Integer.parseInt(anstv[i].getText().toString());
				}
				for (i = 0; i < 5; i++)
					anstv[i].setText("");

				for (i = 0; i < 5; i++) {
					if (ans[i] == num[i])
						count++;
				}

				if (count == 5) {
					level++;
					if(level_sec<10) 
						level--;
					level_sec= (int) ( 255/level );
					
					answertxt.setText(Integer.toString(level_sec));
					if (handler != null && handler.isConnected())
						handler.write((byte) 0x5, (byte) 0x6, (byte) 0x2);
				} else {
					answertxt.setText("오답입니다!");
					if (handler != null && handler.isConnected())
						handler.write((byte) 0x5, (byte) 0x6, (byte) 0x1);
				}
			}
		});

		exit = (Button) findViewById(R.id.exitbtn);
		exit.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (handler != null && handler.isConnected()) {
					handler.write((byte) 0x5, (byte) 0x7, (byte) 0x1);
				}
				finish();
			}
		});

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
			Log.d(Arduino_LEDControllerActivity.class.getName(),
					"mAccessory is null");
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

	private void openAccessory(UsbAccessory accessory) {
		mAccessory = accessory;
		if (handler == null)
			handler = new AdkHandler();

		handler.open(mUsbManager, mAccessory);
	}

	private void closeAccessory() {
		if (handler != null && handler.isConnected())
			handler.close();
		mAccessory = null;
	}

}
