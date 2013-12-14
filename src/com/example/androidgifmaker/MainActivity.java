package com.example.androidgifmaker;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.view.Menu;
import android.view.View;
import android.widget.Toast;

public class MainActivity extends Activity {
	
	ProgressDialog progressBar;
	private ArrayList<Bitmap> bitmaps; //Add your bitmaps from internal or external storage. 
	

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		bitmaps = new ArrayList<Bitmap>();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
	
	public void makeGif(View view) {
		String name = "MyGif";
		Toast.makeText(
				this,
				"The .gifs is now saving. This will take ~3 sec per frame",
				Toast.LENGTH_LONG).show();
		
	    progressBar = ProgressDialog.show(this, "Converting...", "~3 sec/frame", true, false);
	    
	    GifThread gt = new GifThread(name);
		gt.start();
		
		Toast.makeText(
				this,
				"You can access the gif in your SD Card storage, under the file Flippy. This directory is: "
						+ Environment.getExternalStorageDirectory().toString()
						+ "/Gifs. Or, you can see your saved .gifs in the Gallery, in the album \"Gifs\"",
				Toast.LENGTH_LONG).show();
	}
	
	private class GifThread extends Thread{
		private String name; 
		
		public GifThread(String proj) { // ONLY WORKS AFTER SAVING
				name=proj;
		}
		
		@Override 
		public void run(){
			String root = Environment.getExternalStorageDirectory().toString();
			File myDir = new File(root + "/Gifs/");
			if(!myDir.exists())
				myDir.mkdirs();
			String fname = name;
			File file = new File(myDir, fname + ".gif");
			if (file.exists()){
				file.delete();
			}
			try {
				FileOutputStream out = new FileOutputStream(file);
				AnimatedGifMaker gifs = new AnimatedGifMaker();
				gifs.start(out);
				gifs.setDelay(200);
				gifs.setRepeat(0);
				gifs.setTransparent(new Color());
	
				for (int i = 0; i < bitmaps.size(); i++) {
					gifs.addFrame(bitmaps.get(i));
				}
				gifs.finish();
				sendBroadcast(new Intent(Intent.ACTION_MEDIA_MOUNTED,
						Uri.parse("file://"
								+ Environment.getExternalStorageDirectory()))); // uM
																				// HACK
			} catch (Exception e) {
				e.printStackTrace();
			}
			handler.sendEmptyMessage(0);
		}
		@SuppressLint("HandlerLeak")
		private Handler handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                progressBar.dismiss();
            }
        };
	}
}
