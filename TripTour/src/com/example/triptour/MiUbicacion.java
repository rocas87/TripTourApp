package com.example.triptour;

import com.google.android.gms.location.LocationClient;
import com.google.android.gms.maps.model.LatLng;

import android.content.Context;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;

public class MiUbicacion extends android.support.v4.app.FragmentActivity implements android.location.LocationListener{

	private static final String LocationListener = null;
	LocationClient mLocationClient;
	LocationManager handle;
	String provider;
	LatLng Miubicacion;
	Location loc;
	MiUbicacion()
	{
		
	}
	public Location getMiUbicacion()
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
	    provider = handle.getBestProvider(c, false);
	    //Se activan las notificaciones de localización con los parámetros: 
	    //proveedor, tiempo mínimo de actualización, distancia mínima, Locationlistener
	    handle.requestLocationUpdates(LocationListener, 60000, 5, this);
	    //Obtiene la ultima posicion conocida por el proveedor
	    loc = handle.getLastKnownLocation(provider);
	    
	    return loc;
	}

	@Override
	public void onLocationChanged(Location location) {
		// TODO Auto-generated method stub
		Miubicacion = new LatLng(loc.getLatitude(),loc.getLongitude());
	}

	@Override
	public void onProviderDisabled(String arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onProviderEnabled(String arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onStatusChanged(String arg0, int arg1, Bundle arg2) {
		// TODO Auto-generated method stub
		
	}
}
