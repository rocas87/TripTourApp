package com.example.triptour;

import com.google.android.gms.location.LocationClient;

import android.app.Activity;
import android.content.Intent;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

public class HomeActivity extends Activity implements android.location.LocationListener, OnClickListener {
	
	TextView usuario;
	Button btnFind, btnRecomendacion, btnRutas, btnEvaluacion, btnAlojamiento, 
		   btnClima, btnCambio, btnConfiguracion;
	String user;
	Location loc;
	LocationClient mLocationClient;
	LocationManager handle;
	private String provider;
	
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
			Intent intent = new Intent(Intent.ACTION_VIEW);
			intent.setData(Uri.parse("http://www.booking.com/"));
			startActivity(intent);
			break;
		case R.id.btnClima:
			loc = getMiUbicacion();
			Climate cl = new Climate();
			Intent clima = new Intent(Intent.ACTION_VIEW);
			clima.setData(Uri.parse(cl.climaURL(String.valueOf(loc.getLatitude()), String.valueOf(loc.getLongitude()))));
			startActivity(clima);
			break;
		case R.id.btnCambio:
			Intent changeMoney = new Intent(this,ChangeMoneyActivity.class);
			startActivity(changeMoney);
			break;
		case R.id.btnConfiguracion:
			break;
		}
	}

	@Override
	public void onLocationChanged(Location location) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onProviderDisabled(String provider) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onProviderEnabled(String provider) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onStatusChanged(String provider, int status, Bundle extras) {
		// TODO Auto-generated method stub
		
	}
	public Location getMiUbicacion()
	{
		//Llamo al servico de localizacion	        
	    handle = (LocationManager)getSystemService(LOCATION_SERVICE);
	    //Clase criteria permite decidir mejor poveedor de posicion
	    Criteria c = new Criteria();
	    //obtiene el mejor proveedor en función del criterio asignado
	    //ACCURACY_FINE(La mejor presicion)--ACCURACY_COARSE(PRESISION MEDIA)
	    c.setAccuracy(Criteria.ACCURACY_COARSE);
	    //Indica si es necesaria la altura por parte del proveedor
	    c.setAltitudeRequired(false);
	    provider = handle.getBestProvider(c, true);
	    //Se activan las notificaciones de localización con los parámetros: 
	    //proveedor, tiempo mínimo de actualización, distancia mínima, Locationlistener
	    handle.requestLocationUpdates(provider, 60000, 5,this);
	    //Obtiene la ultima posicion conocida por el proveedor
	    loc = handle.getLastKnownLocation(provider);
	  
	    return loc;
	}

}
