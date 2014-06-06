package com.example.triptour;

import com.google.android.gms.location.LocationClient;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

public class HomeActivity extends android.support.v4.app.FragmentActivity 
implements android.location.LocationListener, OnClickListener
{
	String user;
	Location loc;
	LocationClient mLocationClient;
	LocationManager handle;
	private String provider;
	ProgressDialog pd;
	GoogleMap mapa;
	LatLng MiUbicacion;
	
	// TODO Auto-generated method stub
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.homeactivity);
			
		Bundle home = getIntent().getExtras();
		user = home.getString("usr_nick");
		
		mapa = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map)).getMap();
		mapa.setMapType(GoogleMap.MAP_TYPE_NORMAL);
		//Boton para sentra ubicaion en el mapa
		mapa.setMyLocationEnabled(true);
		
		centrarMapa();
	}
	
	@Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.action_bar, menu);
        return true;
    }
	
	public boolean onOptionsItemSelected(MenuItem item) {
	    // Handle presses on the action bar items
	    switch (item.getItemId()) {
	        case R.id.find:
				Intent find = new Intent(this,FindActivity.class);
				find.putExtra("user", user);
				startActivity(find);
	            return true;
	            
	        case R.id.recomendation:
	        	Intent recomendation = new Intent(this,RecomendationActivity.class);
				recomendation.putExtra("user", user);
				startActivity(recomendation);
	            return true;
	            
	        case R.id.recomendation_route:
				Intent recomendationRoute = new Intent(this,RecomendationRouteActivity.class);
				recomendationRoute.putExtra("user", user);
				startActivity(recomendationRoute);
				return true;

	        case R.id.evaluacion:
	        	return true;

	        case R.id.alojamiento:
				Intent intent = new Intent(Intent.ACTION_VIEW);
				intent.setData(Uri.parse("http://www.booking.com/"));
				startActivity(intent);
				return true;

	        case R.id.clima:
	        	pd = new ProgressDialog(this);
	        	pd.setMessage("Buscando ubicacion para pronostico por zona....");
				pd.show();
	        	loc = getMiUbicacion();
				Climate cl = new Climate();
				pd.dismiss();
				Intent clima = new Intent(Intent.ACTION_VIEW);
				clima.setData(Uri.parse(cl.climaURL(String.valueOf(loc.getLatitude()), String.valueOf(loc.getLongitude()))));
				startActivity(clima);
				return true;

	        case R.id.cambio_moneda:
				Intent changeMoney = new Intent(this,ChangeMoneyActivity.class);
				startActivity(changeMoney);
				return true;

			case R.id.preferencias:
				return true;

	    }
	    return false;
	}

	public void centrarMapa()
	{
		//Llamo al servico de localizacion	        
	    handle = (LocationManager)getSystemService(Context.LOCATION_SERVICE);
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
	    handle.requestLocationUpdates(provider, 60000, 5, this);
	    //Obtiene la ultima posicion conocida por el proveedor
	    loc = handle.getLastKnownLocation(provider);
	    MiUbicacion = new LatLng(loc.getLatitude(),loc.getLongitude());
		configGPS(MiUbicacion);
	}
	
	private void configGPS(LatLng latlong) 
	{
		// TODO Auto-generated method stub
		//Se indica el latitud y longitud y zoom
		CameraPosition cameraPosition = new CameraPosition.Builder().target(latlong).zoom(12).build();	
		//Se mueve la camara a lo indicado anteriormente
		mapa.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
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

	@Override
	public void onClick(View arg0) {
		// TODO Auto-generated method stub
		
	}

}
