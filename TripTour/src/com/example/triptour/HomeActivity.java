package com.example.triptour;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

public class HomeActivity extends Activity implements OnClickListener {
	
	TextView usuario;
	Button btnFind, btnRecomendacion, btnRutas, btnEvaluacion, btnAlojamiento, 
		   btnClima, btnCambio, btnConfiguracion;
	String user;
	
	// TODO Auto-generated method stub
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.homeactivity);
		
		usuario = (TextView)findViewById(R.id.txtUsuario);
		btnFind = (Button)findViewById(R.id.btnFind);
		btnRecomendacion = (Button)findViewById(R.id.btnRecomendacion);
		btnRutas = (Button)findViewById(R.id.btnRutas);
		btnEvaluacion = (Button)findViewById(R.id.btnEvaluacion);
		btnAlojamiento = (Button)findViewById(R.id.btnAlojamiento);
		btnClima = (Button)findViewById(R.id.btnClima);
		btnCambio = (Button)findViewById(R.id.btnCambio);
		btnConfiguracion = (Button)findViewById(R.id.btnConfiguracion);
		
		Bundle home = getIntent().getExtras();
		user = home.getString("usr_nick");
		Log.e("token", user);
		usuario.setText(user);
				
		btnFind.setOnClickListener(this);
		btnRecomendacion.setOnClickListener(this);
		btnRutas.setOnClickListener(this);
		btnEvaluacion.setOnClickListener(this);
		btnAlojamiento.setOnClickListener(this);
		btnClima.setOnClickListener(this);
		btnCambio.setOnClickListener(this);
		btnConfiguracion.setOnClickListener(this);
	}

	@Override
	public void onClick(View arg0) {
		// TODO Auto-generated method stub
		switch(arg0.getId()){
		case R.id.btnFind:
			Log.e("token", "estoy home" + user);
			Intent find = new Intent(this,FindActivity.class);
			find.putExtra("user", user);
			startActivity(find);
			break;
		case R.id.btnRecomendacion:
			Intent recomendation = new Intent(this,RecomendationActivity.class);
			recomendation.putExtra("user", user);
			startActivity(recomendation);
			break;
		case R.id.btnRutas:
			Intent recomendationRoute = new Intent(this,RecomendationRouteActivity.class);
			recomendationRoute.putExtra("user", user);
			startActivity(recomendationRoute);
			break;
		case R.id.btnEvaluacion:
			break;
		case R.id.btnAlojamiento:
			break;
		case R.id.btnClima:
			break;
		case R.id.btnCambio:
			break;
		case R.id.btnConfiguracion:
			break;
		}
	}

}
