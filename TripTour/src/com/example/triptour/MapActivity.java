package com.example.triptour;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.json.JSONObject;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.GoogleMap.OnMarkerClickListener;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

import android.content.Context;
import android.graphics.Color;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Toast;

public class MapActivity extends android.support.v4.app.FragmentActivity 
implements android.location.LocationListener, OnClickListener
{
	GoogleMap mapa;
	LocationManager handle;
	private String provider;
	Location loc;
	LatLng MiUbicacion, coord;
	String itm_nombre, itm_direccion, itm_promedio, itm_distancia, itm_latitude, itm_longitude, waypoints;
	Marker itemMarker;
	//double itm_latitud, itm_longitud;
	ArrayList<String> nombre = new ArrayList<String>();
	ArrayList<String> direccion = new ArrayList<String>();
	ArrayList<String> promedio = new ArrayList<String>();
	ArrayList<String> distancia = new ArrayList<String>();
	ArrayList<String> latitude = new ArrayList<String>();
	ArrayList<String> longitude = new ArrayList<String>();
	Double radioBusqueda;
	int id;
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.map_activity);
		
		mapa = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map)).getMap();
		mapa.setMapType(GoogleMap.MAP_TYPE_NORMAL);
		//Boton para sentra ubicaion en el mapa
		mapa.setMyLocationEnabled(true);
		
		centrarMapa();
		
		coord = Ubicacion();
		
		Bundle mapActivity = getIntent().getExtras();
		id = mapActivity.getInt("id");
		if(id==0)
		{
			nombre = mapActivity.getStringArrayList("itm_nombre");
			direccion = mapActivity.getStringArrayList("itm_direccion");
			promedio = mapActivity.getStringArrayList("itm_promedio");
			distancia = mapActivity.getStringArrayList("itm_distancia");
			latitude = mapActivity.getStringArrayList("itm_latitud");
			longitude = mapActivity.getStringArrayList("itm_longitud");
			
			waypoints = "waypoints=optimize:true|"+ coord.latitude + "," + coord.longitude;
			
			for(int i=0; i< nombre.size(); i++)
			 {
				 mapa.addMarker(new MarkerOptions()
		        .position(new LatLng(Double.parseDouble(latitude.get(i)), Double.parseDouble(longitude.get(i))))
		        .title(String.valueOf(nombre.get(i)+"\n Average:"+promedio.get(i)+"\n Distance:"+distancia.get(i))));
				 
				waypoints = waypoints + "|" + "|" + Double.parseDouble(latitude.get(i)) + "," +
							Double.parseDouble(longitude.get(i));
				//Dibujo ruta entre mas de 2 puntos
				String url = getMapsApiDirectionsUrl(waypoints);
			    ReadTask downloadTask = new ReadTask();
			    downloadTask.execute(url);
			 }
		}
		else
		{
			itm_nombre = mapActivity.getString("itm_nombre");
			itm_direccion = mapActivity.getString("itm_direccion");
			itm_promedio = mapActivity.getString("itm_promedio");
			itm_distancia = mapActivity.getString("itm_distancia");
			itm_latitude = mapActivity.getString("itm_latitud");
			itm_longitude = mapActivity.getString("itm_longitud");
			
			mapa.addMarker(new MarkerOptions()
	        .position(new LatLng(Double.parseDouble(itm_latitude), Double.parseDouble(itm_longitude)))
	        .title(String.valueOf(itm_nombre+"\n Average:"+itm_promedio+"\n Distance:"+itm_distancia)));
			waypoints = "waypoints=optimize:true|"+ coord.latitude + "," + coord.longitude
			            + "|" + "|" + Double.parseDouble(itm_latitude) + "," + Double.parseDouble(itm_longitude);
			//Dibujo ruta entre mas de 2 puntos
			String url = getMapsApiDirectionsUrl(waypoints);
		    ReadTask downloadTask = new ReadTask();
		    downloadTask.execute(url);
		}
		radioBusqueda = Double.parseDouble(mapActivity.getString("radioBusqueda"));
		 		
		// Instantiates a new CircleOptions object and defines the center and radius
		CircleOptions circleOptions = new CircleOptions();
		// Indico las coordenadas del centro y el radio en metros
		circleOptions.center(coord).radius(radioBusqueda*1000);
		circleOptions.strokeColor(Color.RED);
		circleOptions.strokeWidth((float) 2.0);
		// Get back the mutable Circle
		Circle circle = mapa.addCircle(circleOptions);
						
		mapa.setOnMarkerClickListener(new OnMarkerClickListener() {
			public boolean onMarkerClick(Marker marker) {
				Toast.makeText(	MapActivity.this,marker.getTitle(), 0);
				return false;
			}
		});
	}
	
	public void centrarMapa()
	{
		//Llamo al servico de localizacion	        
	    handle = (LocationManager)getSystemService(Context.LOCATION_SERVICE);
	    //Clase criteria permite decidir mejor poveedor de posicion
	    Criteria c = new Criteria();
	    //obtiene el mejor proveedor en función del criterio asignado
	    //ACCURACY_FINE(La mejor presicion)--ACCURACY_COARSE(PRESISION MEDIA)
	    c.setAccuracy(Criteria.ACCURACY_FINE);
	    //Indica si es necesaria la altura por parte del proveedor
	    c.setAltitudeRequired(false);
	    provider = handle.getBestProvider(c, true);
	    //Se activan las notificaciones de localización con los parámetros: 
	    //proveedor, tiempo mínimo de actualización, distancia mínima, Locationlistener
	    handle.requestLocationUpdates(provider, 60000, 5, this);
	    //Obtiene la ultima posicion conocida por el proveedor
	    loc = handle.getLastKnownLocation(provider);
	    
	    if(loc != null){
		    //Obtenemos la última posición conocida dada por el proveedor
		    MiUbicacion = new LatLng(loc.getLatitude(),loc.getLongitude());
			configGPS(MiUbicacion);
		}
	}
	
	private void configGPS(LatLng latlong) 
	{
		// TODO Auto-generated method stub
		//Se indica el latitud y longitud y zoom
		CameraPosition cameraPosition = new CameraPosition.Builder().target(latlong).zoom(12).build();	
		//Se mueve la camara a lo indicado anteriormente
		mapa.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
	}
	
	public LatLng Ubicacion()
	{
		MiUbicacion = new LatLng(loc.getLatitude(),loc.getLongitude());
		return MiUbicacion;
	}
	@Override
	public void onClick(View arg0) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void onLocationChanged(Location arg0) {
		// TODO Auto-generated method stub
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
	private String getMapsApiDirectionsUrl(String waypoints) {
	    String sensor = "sensor=false";
	    String params = waypoints + "&" + sensor;
	    String output = "json";
	    String url = "https://maps.googleapis.com/maps/api/directions/"
	        + output + "?" + params;
	    //tv1.setText(url);
	    return url;
	  }
	 
	  private class ReadTask extends AsyncTask<String, Void, String> {
	    @Override
	    protected String doInBackground(String... url) {
	      String data = "";
	      try {
	        HttpConnection http = new HttpConnection();
	        data = http.readUrl(url[0]);
	      } catch (Exception e) {
	        Log.d("Background Task", e.toString());
	      }
	      return data;
	    }
	 
	    @Override
	    protected void onPostExecute(String result) {
	      super.onPostExecute(result);
	      new ParserTask().execute(result);
	    }
	  }
	 
	  private class ParserTask extends
	      AsyncTask<String, Integer, List<List<HashMap<String, String>>>> {
	 
	    @Override
	    protected List<List<HashMap<String, String>>> doInBackground(
	        String... jsonData) {
	 
	      JSONObject jObject;
	      List<List<HashMap<String, String>>> routes = null;
	 
	      try {
	        jObject = new JSONObject(jsonData[0]);
	        PathJSONParser parser = new PathJSONParser();
	        routes = parser.parse(jObject);
	      } catch (Exception e) {
	        e.printStackTrace();
	      }
	      return routes;
	    }
	 
	    @Override
	    protected void onPostExecute(List<List<HashMap<String, String>>> routes) {
	      ArrayList<LatLng> points = null;
	      PolylineOptions polyLineOptions = null;
	 
	      // traversing through routes
	      for (int i = 0; i < routes.size(); i++) {
	        points = new ArrayList<LatLng>();
	        polyLineOptions = new PolylineOptions();
	        List<HashMap<String, String>> path = routes.get(i);
	 
	        for (int j = 0; j < path.size(); j++) 
	        {
	          HashMap<String, String> point = path.get(j);
	 
	          double lat = Double.parseDouble(point.get("lat"));
	          double lng = Double.parseDouble(point.get("lng"));
	          LatLng position = new LatLng(lat, lng);
	 
	          points.add(position);
	        }
	        polyLineOptions.addAll(points);
	        polyLineOptions.width(2);
	        polyLineOptions.color(Color.BLUE);
	      }
	      mapa.addPolyline(polyLineOptions);
	    }
	  }
}
