package com.example.triptour;

import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;

public class HomeActivity extends Activity {
	
	TextView usuario;
	
	// TODO Auto-generated method stub
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.homeactivity);
		
		usuario = (TextView)findViewById(R.id.txtUsuario);
		
		Bundle home = getIntent().getExtras();
		usuario.setText(home.getString("usr_nick"));
	}

}
