package com.aide.android.sms.reader;

import android.Manifest;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;
import java.util.ArrayList;
import android.widget.SearchView;

public class MainActivity extends AppCompatActivity 
{
	ArrayAdapter aa;
	
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
		
		getContactsName();
	}
	
	public void getContactsName() {
		// Check permission
		if(ContextCompat.checkSelfPermission(this, Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED) {
			ActivityCompat.requestPermissions(this, new String[] {Manifest.permission.READ_CONTACTS}, 0);
		}
		
		// Show Contact List
		ContentResolver creslover = getContentResolver();
		Uri uri = ContactsContract.CommonDataKinds.Phone.CONTENT_URI;
		Cursor c = creslover.query(uri, null, null, null, null);
		
		// Decarle ListView
		SearchView search = (SearchView) findViewById(R.id.mainSearchView1);
		ListView lv1 = (ListView) findViewById(R.id.mainListView1);
		ArrayList conNames = new ArrayList<String>();
		
		if(c.getCount() > 0) {
			while(c.moveToNext()) {
				String contantName = c.getString(c.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
				conNames.add(contantName);
			}
		}
		
		// Set Adapter
		aa = new ArrayAdapter<String>(this, R.layout.listview_custom, conNames);
		lv1.setAdapter(aa);
		
		// Click Listener 
		lv1.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> p1, View p2, int p3, long p4)
			{
				String indexItem = (String) p1.getItemAtPosition(p3);
				Toast.makeText(getApplicationContext(), indexItem, Toast.LENGTH_SHORT).show();
					
				Intent i = new Intent(MainActivity.this, ContactPage.class);
				i.putExtra("con_name", indexItem);
				startActivity(i);
			}	
		});
		
		search.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
			@Override 
			public boolean onQueryTextSubmit(String query) {
				MainActivity.this.aa.getFilter().filter(query);
				return false;
			}
			
			@Override
			public boolean onQueryTextChange(String p1)
			{
				MainActivity.this.aa.getFilter().filter(p1);
				return false;
			}
		});
	}

	public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
		// Request Permission Again in Alert
		if(requestCode == 1000) {
			if(grantResults[0] == PackageManager.PERMISSION_GRANTED) {
				Toast.makeText(getApplicationContext(), "Granted Permission", Toast.LENGTH_SHORT).show();
			}
		}
	}
	

	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
		MenuInflater infFilter = getMenuInflater();
		infFilter.inflate(R.menu.arshive, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item)
	{
		switch(item.getItemId()) {
			case R.id.show: {
					final Cursor messeageData = getContentResolver().query(Uri.parse("content://sms"), null, null, null, null);
					messeageData.moveToFirst();

					SharedPreferences sh = getSharedPreferences("smsreader", MODE_PRIVATE);
					Toast.makeText(getApplicationContext(), sh.getString("arshive", ""), Toast.LENGTH_SHORT).show();
				}
		}

		return super.onOptionsItemSelected(item);
	}
}
