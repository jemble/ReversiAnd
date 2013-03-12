package uk.ac.brookes.bourgein.reversiand;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class activity_home extends Activity {
	
	public static final int PICK_CONTACT = 0;
	private Button onePlayerButton;
	private Button twoPlayerButton;
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
         super.onCreate(savedInstanceState);
         setContentView(R.layout.home_screen);
         
         onePlayerButton = (Button)findViewById(R.id.btn_1player);
         twoPlayerButton = (Button)findViewById(R.id.btn_2player);
         
         onePlayerButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent( getApplicationContext(), MainActivity.class);
		         startActivity(intent);
			}
		});
         
         twoPlayerButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent intentContact = new Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI); 
				startActivityForResult(intentContact, PICK_CONTACT);
				
			}
		});
    }
}