package net.srcz.android.screencast.client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;

import android.os.IBinder;
import android.os.RemoteException;
import android.os.ServiceManager;
import android.os.SystemClock;
import android.view.IWindowManager;
import android.view.KeyEvent;
import android.view.MotionEvent;

public class ClientHandler {
	IBinder wmbinder = ServiceManager.getService( "window" );
	final IWindowManager wm = IWindowManager.Stub.asInterface( wmbinder );
  Socket s;

	public ClientHandler(Socket s) throws IOException, RemoteException {
		this.s = s;
		Thread tHandleCmd = new Thread() {
			public void run() {
				handleCmd();
			}
		};
		tHandleCmd.start();

		try {
			tHandleCmd.join();
		} catch (InterruptedException e) {
			// ignored
		}
		if(Main.debug)
			System.out.println("Exited threads without exception");
	}

	private void handleCmd() {
		try {
			InputStream is = s.getInputStream();
			BufferedReader r = new BufferedReader(new InputStreamReader(is));
			if(Main.debug)
				System.out.println("Setup Command Handler");
			while(true) {
	    		String line = r.readLine();
	    		if(line == null) {
	    			r.close();
	    			s.close();
	    			break;
	    		}
	    		if(Main.debug)
	    			System.out.println("Received : "+line);
	    		try {
	    			handleCommand(line);
	    		} catch(Exception ex) {
	    			ex.printStackTrace();
	    		}
			}
		} catch(Exception ex) {
			if(Main.debug)
				ex.printStackTrace();
		}
		if(Main.debug)
			System.out.println("Exited handleCommand thread.");
	}

	/**
	 * handles command line arguement
	 * @param line
	 * @throws RemoteException
	 */
	private void handleCommand(String line) throws RemoteException {
		String[] paramList = line.split("/");
		String type = paramList[0];
		if(type.equals("quit")) {
			System.exit(0);
			return;
		}
		if(type.equals("pointer")) {
			wm.injectPointerEvent(getMotionEvent(paramList), false);
			return;
		}
		if(type.equals("key")) {
			wm.injectKeyEvent(getKeyEvent(paramList), false);
			return;
		}
		if(type.equals("trackball")) {
			wm.injectTrackballEvent(getMotionEvent(paramList), false);
			return;
		}

		throw new RuntimeException("Invalid type : "+type);
	}

	private static MotionEvent getMotionEvent(String[] args) {
		long time = SystemClock.uptimeMillis();
		int action = Integer.parseInt(args[1]);
		float x = Float.parseFloat(args[2]);
		float y = Float.parseFloat(args[3]);
		return MotionEvent.obtain(time, time, action, x, y, 0);
	}

	private static KeyEvent getKeyEvent(String[] args) {
		int action = Integer.parseInt(args[1]);
		int code = Integer.parseInt(args[2]);
		return new KeyEvent(action, code);
	}
}
