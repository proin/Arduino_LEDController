package com.proinlab.arduinoledcontroller;

import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

import com.android.future.usb.UsbAccessory;
import com.android.future.usb.UsbManager;

import android.os.ParcelFileDescriptor;
import android.util.Log;

public class AdkHandler implements Runnable {

	
    ParcelFileDescriptor mFileDescriptor;
    FileInputStream mInputStream;
    FileOutputStream mOutputStream;

	@Override
	public void run() {
		// TODO Auto-generated method stub
	}
	
	public void open(UsbManager usbManager, UsbAccessory accessory){
	     mFileDescriptor = usbManager.openAccessory(accessory);
	     if (mFileDescriptor != null) {
	          FileDescriptor fd = mFileDescriptor.getFileDescriptor();
	          mInputStream = new FileInputStream(fd);
	          mOutputStream = new FileOutputStream(fd);
	          Log.d(AdkHandler.class.getName(), "보드 연결 성공");
	     } else {
	          Log.d(AdkHandler.class.getName(), "보드 연결 실패");
	     }
	}
	
	public void close(){
	     try {
	          if (mFileDescriptor != null) {
	               mFileDescriptor.close();
	          }
	     } catch (IOException e) {
	     } finally {
	          mFileDescriptor = null;
	          mInputStream = null;
	          mOutputStream = null;
	     }
	}
	
	public boolean isConnected(){
      return (mInputStream != null && mOutputStream != null);
	}
	
	public void write(byte command, byte target, int value) {
	     byte[] buffer = new byte[3];
	     if (value > 255)
	          value = 255; // 디지털 데이터를 0, 1을 사용하고 아날로그 데이터는 0 ~ 255의 범위의 값을 쓸 수 있다.

	     buffer[0] = command;
	     buffer[1] = target;
	     buffer[2] = (byte) value;
	     if (mOutputStream != null && buffer[1] != -1) {
	          try {
	               mOutputStream.write(buffer);
	          } catch (IOException e) {
	               Log.e(AdkHandler.class.getName(), "write failed", e);
	          }
	     }
	}
}