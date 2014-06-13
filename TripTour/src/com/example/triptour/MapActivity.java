package com.example.triptour;

import java.text.DecimalFormat;
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

import android.app.ActionBar;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Vibrator;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;
import android.widget.AdapterView.OnItemSelectedListener;

public class MapActivity extends android.support.v4.app.FragmentActivity 
implements android.location.LocationListener, OnClickListener
{
	GoogleMap mapa;
	LocationManager handle;
	private String provider;
	Location loc;
	LatLng MiUbicacion, coord;
	String itm_nombre, itm_direccion, itm_promedio, itm_distancia, itm_latitude, itm_longitude, waypoints, url,
	rta_nombre, rta_promedio, rta_distancia, rta_duracion, rta_coordenadas, rta_mode, promedio_itm, latLong, usuario, hora,
	minuto, disItem;
	Marker itemMarker;
	//double itm_latitud, itm_longitud;
	ArrayList<String> nombre = new ArrayList<String>();
	ArrayList<String> direccion = new ArrayList<String>();
	ArrayList<String> promedio = new ArrayList<String>();
	ArrayList<String> distancia = new ArrayList<String>();
	ArrayList<String> latitude = new ArrayList<String>();
	ArrayList<String> longitude = new ArrayList<String>();
	Double radioBusqueda;
	int id, indice, categoriaFind, transporteFind, categoriaRecomendation, transporteRecomendation, transporteRoute;
	String[] tokens, tokensNombre, tokensPromedio, tokenDuracion, tokenDistancia;
	ArrayAdapter<String> adaptadorCategoria, adaptadorTransporte;
	private Spinner spCategoria, spTransporte;
	private List<String> categorias = new ArrayList<String>();
	private List<String> transporte = new ArrayList<String>();
	LayoutInflater liFind, liRecomendation, liRecomendationRoute;
	View promptFind, promptRecomendation, promptRecomendationRoute;
	EditText duracion;
	DecimalFormat decimales = new DecimalFormat("0.0");
	
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.map_activity);
		
		mapa = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map)).getMap();
		mapa.setMapType(GoogleMap.MAP_TYPE_NORMAL);
		//Boton para sentra ubicaion en el mapa
		mapa.setMyLocationEnabled(true);
		
		ActionBar actionBar = getActionBar();
		actionBar.setDisplayHomeAsUpEnabled(true);
		
		centrarMapa();
		//Categorias disponibles
		categorias.add("Bar");
		categorias.add("Zoologico");
		categorias.add("Museo");
		categorias.add("Parques");
		categorias.add("Parque de diversiones");
		categorias.add("Deportes");
		categorias.add("Restaurant");
		categorias.add("Senderismo");
		categorias.add("Artesania");
		categorias.add("Patrimonio");
		//Tipos de transporte
		transporte.add("Driving/Automovil");
		transporte.add("Walking/Caminando");
		
		coord = Ubicacion();
		
		waypoints = "&origin=" + coord.latitude + "," + coord.longitude + "&waypoints=optimize:true|";
		
		Bundle mapActivity = getIntent().getExtras();
		id = mapActivity.getInt("id");
		usuario = mapActivity.getString("user");
		
		//Si corresponde a 1 solo item
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
		        .snippet(promedio.get(i)+"/"+distancia.get(i)));
				 
				 mapa.setInfoWindowAdapter(new PopupAdapter(getLayoutInflater()));
			 }
			radioBusqueda = Double.parseDouble(mapActivity.getString("radioBusqueda"));
		}
		//Si coorresponde a 1 solo item
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
	        .snippet(itm_promedio+"/"+itm_distancia));
			
			mapa.setInfoWindowAdapter(new PopupAdapter(getLayoutInflater()));
			
			waypoints = waypoints + "|" + Double.parseDouble(itm_latitude) + "," 
						+ Double.parseDouble(itm_longitude);
			
			//Dibujo ruta entre puntos
			url = getMapsApiDirectionsUrl(waypoints);
		    ReadTask downloadTask = new ReadTask();
		    downloadTask.execute(url);
		    radioBusqueda = Double.parseDouble(mapActivity.getString("radioBusqueda"));
		}
		//Si corresponde a rutas
		else if (id==2)
		{		
			rta_nombre = mapActivity.getString("rta_nombre");
			rta_promedio = mapActivity.getString("rta_promedio");
			rta_distancia = mapActivity.getString("rta_distancia");
			rta_duracion = mapActivity.getString("rta_duracion");
			rta_coordenadas = mapActivity.getString("rta_coordenadas");
			rta_mode = mapActivity.getString("rta_mode");
			radioBusqueda = Double.parseDouble(mapActivity.getString("distMaxima"));
			promedio_itm = mapActivity.getString("promedio_itm");
			disItem = mapActivity.getString("disItem");
			//Divide coordenadas y nombre de la ruta
			String delims = ",";
			tokens = rta_coordenadas.split(delims);
			tokensNombre = rta_nombre.split(delims);
			tokensPromedio = promedio_itm.split(delims);
			tokenDistancia = disItem.split(delims);		
			indice = 0;
			for(int i=0; i < (tokens.length)-1; i++)
			 {
				 mapa.addMarker(new MarkerOptions()
		        .position(new LatLng(Double.parseDouble(tokens[i]), Double.parseDouble(tokens[i+1])))
		        .title(String.valueOf(tokensNombre[indice]))
		        .snippet(tokensPromedio[indice]+"/"+decimales.format((Double.parseDouble(tokenDistancia[indice]))/1000)));
				 
				mapa.setInfoWindowAdapter(new PopupAdapter(getLayoutInflater()));
				
				waypoints = waypoints + "|" + tokens[i] + "," + tokens[i+1];
				
				indice++;
				i++;
			 }	
			//Dibujo ruta entre puntos
			url = getMapsApiDirectionsUrl(waypoints);
		    ReadTask downloadTask = new ReadTask();
		    downloadTask.execute(url);
		}
		
		
		
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
	        	pomptFind();
	            return true;
	            
	        case R.id.recomendation:
	        	if(usuario.equals("SR"))
	        	{
	        		Vibrator vibrator =(Vibrator)getSystemService(Context.VIBRATOR_SERVICE);
	        		vibrator.vibrate(200);
	        		Toast.makeText(MapActivity.this, "Debe estar registrado", Toast.LENGTH_LONG).show();
	        	}
	        	else
	        	{
	        		promptRecomendation();
	        	}
	            return true;
	            
	        case R.id.recomendation_route:
	        	if(usuario.equals("SR"))
	        	{
	        		Vibrator vibrator =(Vibrator)getSystemService(Context.VIBRATOR_SERVICE);
	        		vibrator.vibrate(200);
	        		Toast.makeText(MapActivity.this, "Debe estar registrado", Toast.LENGTH_LONG).show();
	        	}
	        	else
	        	{
	        	promptRecomendationRoute();		        		
	        	}
	            return true;

	        case R.id.evaluacion:
	        	return true;

	        case R.id.alojamiento:
				Intent intent = new Intent(Intent.ACTION_VIEW);
				intent.setData(Uri.parse("http://www.booking.com/"));
				startActivity(intent);
				return true;

	        case R.id.clima:				
				loc = getMiUbicacion();
				Climate cl = new Climate();
				latLong = cl.climaURL(String.valueOf(loc.getLatitude()), String.valueOf(loc.getLongitude()));
				Intent clima = new Intent(Intent.ACTION_VIEW);
				clima.setData(Uri.parse(latLong));
				startActivity(clima);
				return true;

	        case R.id.cambio_moneda:
				Intent changeMoney = new Intent(this,ChangeMoneyActivity.class);
				changeMoney.putExtra("user", usuario);
				startActivity(changeMoney);
				return true;

			case R.id.preferencias:
				return true;
				
			case android.R.id.home:
				Intent home = new Intent(this,HomeActivity.class);
				home.putExtra("usr_nick", usuario);
				startActivity(home);
				return true;
	    }
	    return false;
	}

	private void pomptFind() 
	{
		// TODO Auto-generated method stub
		liFind = LayoutInflater.from(this);
		promptFind = liFind.inflate(R.layout.prompt_find_activity, null);

		spCategoria = (Spinner)promptFind.findViewById(R.id.spCategoria);
		adaptadorCategoria = new ArrayAdapter<String>
			(this,android.R.layout.simple_spinner_item, categorias);
		adaptadorCategoria.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spCategoria.setAdapter(adaptadorCategoria);
		
		spTransporte = (Spinner)promptFind.findViewById(R.id.spTransporte);
		adaptadorTransporte = new ArrayAdapter<String>
			(this,android.R.layout.simple_spinner_item, transporte);
		adaptadorTransporte.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spTransporte.setAdapter(adaptadorTransporte);
		
		spCategoria.setOnItemSelectedListener(new OnItemSelectedListener()
		{

			@Override
			public void onItemSelected(AdapterView<?> arg0, View arg1,
					int arg2, long arg3) {
				// TODO Auto-generated method stub
				categoriaFind = arg2+1;
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
				// TODO Auto-generated method stub
				
			}
			
		});
		
		spTransporte.setOnItemSelectedListener(new OnItemSelectedListener()
		{

			@Override
			public void onItemSelected(AdapterView<?> arg0, View arg1,
					int arg2, long arg3) {
				// TODO Auto-generated method stub
				transporteFind = arg2+1;
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
				// TODO Auto-generated method stub	
			}
			
		});
		
		AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
		alertDialogBuilder.setView(promptFind);
		
		// Mostramos el mensaje del cuadro de dialogo
		alertDialogBuilder
		.setCancelable(false)
		.setPositiveButton("OK", new DialogInterface.OnClickListener() {
		public void onClick(DialogInterface dialog,int id) {
		// Rescatamos el nombre del EditText y lo mostramos por pantalla
			find();
		}
		})
		.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
		public void onClick(DialogInterface dialog,int id) {
		// Cancelamos el cuadro de dialogo
		dialog.cancel();
		}
		});
		// Creamos un AlertDialog y lo mostramos
		AlertDialog alertDialog = alertDialogBuilder.create();
		alertDialog.show();
		
	}

	public void find()
	{
		Intent find = new Intent(this,FindActivity.class);
		find.putExtra("user", usuario);
		find.putExtra("categoria", String.valueOf(categoriaFind));
		find.putExtra("transporte", String.valueOf(transporteFind));
		startActivity(find);
	}
	
	private void promptRecomendation() 
	{
		// TODO Auto-generated method stub
		liRecomendation = LayoutInflater.from(this);
		promptRecomendation = liRecomendation.inflate(R.layout.prompt_recomendation_activity, null);

		spCategoria = (Spinner)findViewById(R.id.spCategoria);
		spCategoria = (Spinner)promptRecomendation.findViewById(R.id.spCategoria);
		adaptadorCategoria = new ArrayAdapter<String>
			(this,android.R.layout.simple_spinner_item, categorias);
		adaptadorCategoria.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spCategoria.setAdapter(adaptadorCategoria);
		
		spTransporte = (Spinner)promptRecomendation.findViewById(R.id.spTransporte);
		adaptadorTransporte = new ArrayAdapter<String>
			(this,android.R.layout.simple_spinner_item, transporte);
		adaptadorTransporte.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spTransporte.setAdapter(adaptadorTransporte);
		
		spCategoria.setOnItemSelectedListener(new OnItemSelectedListener()
		{

			@Override
			public void onItemSelected(AdapterView<?> arg0, View arg1,
					int arg2, long arg3) {
				// TODO Auto-generated method stub
				categoriaRecomendation = arg2+1;
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
				// TODO Auto-generated method stub
				
			}
			
		});
		
		spTransporte.setOnItemSelectedListener(new OnItemSelectedListener()
		{

			@Override
			public void onItemSelected(AdapterView<?> arg0, View arg1,
					int arg2, long arg3) {
				// TODO Auto-generated method stub
				transporteRecomendation = arg2+1;
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
				// TODO Auto-generated method stub	
			}
			
		});
		
		AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
		alertDialogBuilder.setView(promptRecomendation);
		
		// Mostramos el mensaje del cuadro de dialogo
		alertDialogBuilder
		.setCancelable(false)
		.setPositiveButton("OK", new DialogInterface.OnClickListener() 
		{
			public void onClick(DialogInterface dialog,int id) 
			{
				// Rescatamos el nombre del EditText y lo mostramos por pantalla
				recomendation();
			}
		})
		.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() 
		{
			public void onClick(DialogInterface dialog,int id) 
			{
				// Cancelamos el cuadro de dialogo
				dialog.cancel();
			}
		});
		// Creamos un AlertDialog y lo mostramos
		AlertDialog alertDialog = alertDialogBuilder.create();
		alertDialog.show();
	}

	public void recomendation()
	{
		Intent recomendation = new Intent(this,RecomendationActivity.class);
		recomendation.putExtra("user", usuario);
		recomendation.putExtra("categoria", String.valueOf(categoriaRecomendation));
		recomendation.putExtra("transporte", String.valueOf(transporteRecomendation));
		startActivity(recomendation);
	}
	
	private void promptRecomendationRoute() 
	{
		// TODO Auto-generated method stub
		liRecomendationRoute = LayoutInflater.from(this);
		promptRecomendationRoute = liRecomendationRoute.inflate(R.layout.prompt_recomendation_route_activity, null);
		
		spTransporte = (Spinner)promptRecomendationRoute.findViewById(R.id.spTransporte);
		adaptadorTransporte = new ArrayAdapter<String>
			(this,android.R.layout.simple_spinner_item, transporte);
		adaptadorTransporte.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spTransporte.setAdapter(adaptadorTransporte);
		
		duracion = (EditText)promptRecomendationRoute.findViewById(R.id.duracion);
		
		spTransporte.setOnItemSelectedListener(new OnItemSelectedListener()
		{

			@Override
			public void onItemSelected(AdapterView<?> arg0, View arg1,
					int arg2, long arg3) {
				// TODO Auto-generated method stub
				transporteRoute = arg2+1;
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
				// TODO Auto-generated method stub	
			}
			
		});
		
		AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
		alertDialogBuilder.setView(promptRecomendationRoute);
		
		// Mostramos el mensaje del cuadro de dialogo
		alertDialogBuilder
		.setCancelable(false)
		.setPositiveButton("OK", new DialogInterface.OnClickListener() 
		{
			public void onClick(DialogInterface dialog,int id) 
			{
				// Rescatamos el nombre del EditText y lo mostramos por pantalla
				tokenDuracion = (duracion.getText().toString()).split(":");
				hora = tokenDuracion[0];
				minuto = tokenDuracion[1];
				recomendationRoute();
			}
		})
		.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() 
		{
			public void onClick(DialogInterface dialog,int id) 
			{
				// Cancelamos el cuadro de dialogo
				dialog.cancel();
			}
		});
		// Creamos un AlertDialog y lo mostramos
		AlertDialog alertDialog = alertDialogBuilder.create();
		alertDialog.show();
	}

	public void recomendationRoute()
	{
		Intent recomendationRoute = new Intent(this,RecomendationRouteActivity.class);
		recomendationRoute.putExtra("user", usuario);	
		recomendationRoute.putExtra("transporte", String.valueOf(transporteRoute));
		recomendationRoute.putExtra("hora", hora);
		recomendationRoute.putExtra("minuto", minuto);
		startActivity(recomendationRoute);
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
