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
	String itm_nombre, itm_direccion, itm_promedio, itm_distancia, itm_latitude, itm_longitude, waypoints,
	rta_nombre, rta_promedio, rta_distancia, rta_duracion, rta_coordenadas, rta_mode;
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
	String[] tokens;
	String[] tokensNombre;
	
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
		
		waypoints = "&origin=" + coord.latitude + "," + coord.longitude + "&waypoints=optimize:true|";
		
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
			rta_mode = mapActivity.getString("rta_mode");
						
			for(int i=0; i< nombre.size(); i++)
			 {
				 mapa.addMarker(new MarkerOptions()
		        .position(new LatLng(Double.parseDouble(latitude.get(i)), Double.parseDouble(longitude.get(i))))
		        .title(String.valueOf(nombre.get(i)))
		        .snippet("Average:"+promedio.get(i)+"Distance:"+distancia.get(i)));
				 
				 mapa.setInfoWindowAdapter(new PopupAdapter(getLayoutInflater()));
				
				 waypoints = waypoints + "|" + Double.parseDouble(latitude.get(i)) + "," 
				 + Double.parseDouble(longitude.get(i));
			 }
			
			radioBusqueda = Double.parseDouble(mapActivity.getString("radioBusqueda"));
		}
		else if(id==1)
		{
			itm_nombre = mapActivity.getString("itm_nombre");
			itm_direccion = mapActivity.getString("itm_direccion");
			itm_promedio = mapActivity.getString("itm_promedio");
			itm_distancia = mapActivity.getString("itm_distancia");
			itm_latitude = mapActivity.getString("itm_latitud");
			itm_longitude = mapActivity.getString("itm_longitud");
			rta_mode = mapActivity.getString("rta_mode");
			
			mapa.addMarker(new MarkerOptions()
	        .position(new LatLng(Double.parseDouble(itm_latitude), Double.parseDouble(itm_longitude)))
	        .title(itm_nombre)
	        .snippet("Average:"+itm_promedio+"Distance:"+itm_distancia));
			
			mapa.setInfoWindowAdapter(new PopupAdapter(getLayoutInflater()));
			
			waypoints = waypoints + "|" + Double.parseDouble(itm_latitude) + "," 
						+ Double.parseDouble(itm_longitude);
			
		    radioBusqueda = Double.parseDouble(mapActivity.getString("radioBusqueda"));
		}
		else if (id==2)
		{		
			rta_nombre = mapActivity.getString("rta_nombre");
			rta_promedio = mapActivity.getString("rta_promedio");
			rta_distancia = mapActivity.getString("rta_distancia");
			rta_duracion = mapActivity.getString("rta_duracion");
			rta_coordenadas = mapActivity.getString("rta_coordenadas");
			rta_mode = mapActivity.getString("rta_mode");
			radioBusqueda = Double.parseDouble(mapActivity.getString("distMaxima"));
		
			//Divide coordenadas y nombre de la ruta
			String delims = ",";
			tokens = rta_coordenadas.split(delims);
			tokensNombre = rta_nombre.split(delims);
					
			int largo = (tokens.length)-1;
			for(int i=0; i < largo; i++)
			 {
				 mapa.addMarker(new MarkerOptions()
		        .position(new LatLng(Double.parseDouble(tokens[i]), Double.parseDouble(tokens[i+1])))
		        .title(String.valueOf(rta_nombre))
		        .snippet("Average:"+rta_promedio));
				 
				mapa.setInfoWindowAdapter(new PopupAdapter(getLayoutInflater()));
				
				waypoints = waypoints + "|" + tokens[i] + "," + tokens[i+1];
							    
				i++;
			 }	
		}
		
		//Dibujo ruta entre mas de 2 puntos
		String url = getMapsApiDirectionsUrl(waypoints);
	    ReadTask downloadTask = new ReadTask();
	    downloadTask.execute(url);
		
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
	    String params = waypoints + "&" + "mode=" + rta_mode+ "&" + sensor;
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
