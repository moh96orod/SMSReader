package com.aide.android.sms.reader;

import android.content.ContentResolver;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v7.app.AppCompatActivity;
import android.telephony.SmsManager;
import android.telephony.SmsMessage;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import java.util.HashMap;
import android.content.Intent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.content.SharedPreferences;

public class ContactPage extends AppCompatActivity 
{
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.con_page);

		// Get Contact Info
		Bundle data = getIntent().getExtras();
		final String con_name = data.getString("con_name");
		
		TextView tv1 = (TextView) findViewById(R.id.conpageTextView1);
		tv1.setText(con_name);
		
		ContentResolver creslover = getContentResolver();
		Uri uri = ContactsContract.CommonDataKinds.Phone.CONTENT_URI;
		Cursor c = creslover.query(uri, null, null, null, null);

		final HashMap con_data = new HashMap<String, String>();

		if(c.getCount() > 0) {
			while(c.moveToNext()) {
				String contantName = c.getString(c.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
				String contantPhone = c.getString(c.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
				con_data.put(contantName, contantPhone);
			}
		}
		
		if(con_data.containsKey(con_name)) {
			Toast.makeText(getApplicationContext(), String.valueOf(con_data.get(con_name)), Toast.LENGTH_SHORT).show();
			
			// Send SMS
			Button sendButton = (Button) findViewById(R.id.conpageButton1);
			sendButton.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v)
				{
					final EditText messageTxt = (EditText) findViewById(R.id.conpageEditText1);
					String txt = messageTxt.getText().toString();
						
					String num = String.valueOf(con_data.get(con_name));
						
					SmsManager smsSetting = SmsManager.getDefault();
					smsSetting.sendTextMessage(num, null, txt, null, null);
						
					Toast.makeText(getApplicationContext(), "Sending...", Toast.LENGTH_SHORT).show();
				}	
			});
			
			// Recive Incoming SMS
			Button reciveSms = (Button) findViewById(R.id.conpageTextView2);
			reciveSms.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v)
				{
					final Cursor messeageData = getContentResolver().query(Uri.parse("content://sms"), null, null, null, null);
					messeageData.moveToFirst();
						
					TextView showMessage = (TextView) findViewById(R.id.conpageTextView3);
					showMessage.setText(messeageData.getString(12));
				}
			});
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
		MenuInflater infFilter = getMenuInflater();
		infFilter.inflate(R.menu.menu, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item)
	{
		switch(item.getItemId()) {
			case R.id.arshive: {
					final Cursor messeageData = getContentResolver().query(Uri.parse("content://sms"), null, null, null, null);
					messeageData.moveToFirst();
					
					SharedPreferences sh = getSharedPreferences("smsreader", MODE_PRIVATE);
					SharedPreferences.Editor edit = sh.edit();
					edit.putString("arshive", messeageData.getString(12));
					edit.commit();
			}
		}
		
		return super.onOptionsItemSelected(item);
	}
}
