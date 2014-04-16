package com.example.triptour;

import android.app.Activity;
import android.os.Bundle;
import android.widget.EditText;

public class RegistroActivity extends Activity{

	EditText mail, nick, nombre;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.registroactivity);
	}
}
