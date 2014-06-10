package com.example.triptour;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONObject;

import com.google.android.gms.location.LocationClient;

import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Vibrator;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

public class RecomendationActivity extends Activity implements android.location.LocationListener, OnClickListener{

	TextView txtUsuario, Seleccionado, txtResults, txtMode;
	ListView lista;
	Button btnMapa;
	List<NameValuePair> params;
	ArrayList<String> nombre = new ArrayList<String>();
	ArrayList<String> direccion = new ArrayList<String>();
	ArrayList<String> promedio = new ArrayList<String>();
	ArrayList<String> distancia = new ArrayList<String>();
	ArrayList<String> latitude = new ArrayList<String>();
	ArrayList<String> longitude = new ArrayList<String>();
	String usuario, latitud, longitud, categoria, radioBusqueda, medioTransporte, mode, php, res, itm_nombre, itm_direccion,
				    itm_promedio, itm_distancia, itm_latitude, itm_longitude, latLong, hora, minuto;
	int categoriaFind, transporteFind, categoriaRecomendation, transporteRecomendation, transporteRoute;
	Location loc;
	LocationClient mLocationClient;
	LocationManager handle;
	private String provider;
	ProgressDialog pDialog;
	EnviarPost enviar = new EnviarPost();
	JSONArray jsonArray;
	ArrayAdapter<String> adaptadorCategoria, adaptadorTransporte;
	private Spinner spCategoria, spTransporte;
	private List<String> categorias = new ArrayList<String>();
	private List<String> transporte = new ArrayList<String>();
	LayoutInflater liFind, liRecomendation, liRecomendationRoute;
	View promptFind, promptRecomendation, promptRecomendationRoute;
	EditText duracion;
	String [] tokenDuracion;
	
	// TODO Auto-generated method stub
		@Override
		protected void onCreate(Bundle savedInstanceState) {
			super.onCreate(savedInstanceState);
			setContentView(R.layout.recomendation_activity);

			txtUsuario = (TextView)findViewById(R.id.txtUsuario);
			txtResults = (TextView)findViewById(R.id.txtResults);
			txtMode = (TextView)findViewById(R.id.txtMode);
			lista = (ListView)findViewById(R.id.lista);
			
			btnMapa = (Button)findViewById(R.id.btnmap);
			btnMapa.setOnClickListener(this);
			
			ActionBar actionBar = getActionBar();
			actionBar.setDisplayHomeAsUpEnabled(true);
			
			//Categorias disponibles
			categorias.add("Bar");
			categorias.add("Zoologico");
			categorias.add("Museo");
			categorias.add("Parques");
			categorias.add("Parque de diversiones");
			categorias.add("Deportes");
			categorias.add("Restorant");
			//Tipos de transporte
			transporte.add("Driving/Automovil");
			transporte.add("Walking/Caminando");
			
			Bundle recomendation = getIntent().getExtras();
			usuario = recomendation.getString("user");
			categoria = recomendation.getString("categoria");
			medioTransporte = recomendation.getString("transporte");
			
			txtUsuario.setText(usuario);

			if(medioTransporte.equals("1"))
			{
				mode = "driving";
				radioBusqueda = "20";
			}
			else if(medioTransporte.equals("2"))
			{
				mode = "walking";
				radioBusqueda = "5";
			}

			//Obtengo latitud y longitud
			loc = getMiUbicacion();
			latitud = String.valueOf(loc.getLatitude());
			longitud = String.valueOf(loc.getLongitude());
						
			params = new ArrayList<NameValuePair>();
			params.add(new BasicNameValuePair("usuario",usuario));
			params.add(new BasicNameValuePair("latitud",latitud));
			params.add(new BasicNameValuePair("longitud",longitud));
			params.add(new BasicNameValuePair("categoria",categoria));
			params.add(new BasicNameValuePair("radioBusqueda",radioBusqueda));

			php = "/servtriptour/recomendacionV2.php";
			
			pDialog = new ProgressDialog(this);
			pDialog.setMessage("Buscando...");
			pDialog.show();

			Thread tr = new Thread()
			{
				@Override
				public void run()
				{
					try
					{
						res = enviar.enviarPost(params, php);
						jsonArray = new JSONArray(res);
						
						 for (int i = 0; i < jsonArray.length(); i++) 
						 {
							JSONObject jsonObject = jsonArray.getJSONObject(i);
							if(jsonObject.getString("resultado").equals("categoria"))
							{
								runOnUiThread
								 (
										 new Runnable()
										 {
											 @Override
											 public void run() 
											 {
												 Vibrator vibrator =(Vibrator)getSystemService
														 (Context.VIBRATOR_SERVICE);
												 vibrator.vibrate(200);
												 Toast.makeText(RecomendationActivity.this,"No se encontraron" +
												 		"resultados para esta categoria",
												 		Toast.LENGTH_LONG).show();
												 pDialog.dismiss();
											 }
										 }
								 );
							}
							else if(jsonObject.getString("resultado").equals("radioBusqueda"))
							{
								runOnUiThread
								 (
										 new Runnable()
										 {
											 @Override
											 public void run() 
											 {
												 Vibrator vibrator =(Vibrator)getSystemService
														 (Context.VIBRATOR_SERVICE);
												 vibrator.vibrate(200);
												 Toast.makeText(RecomendationActivity.this,"No se encontraron" +
												 		"resultados dentro del radio de busqueda",
												 		Toast.LENGTH_LONG).show();
												 pDialog.dismiss();
											 }
										 }
								 );
							}
							else if(jsonObject.getString("resultado").equals("1"))
							{
								nombre.add(jsonObject.getString("itm_nombre"));
							    direccion.add(jsonObject.getString("itm_direccion"));
							    promedio.add(jsonObject.getString("itm_promedio"));
							    distancia.add(jsonObject.getString("distancia"));
							    latitude.add(jsonObject.getString("itm_latitud"));
							    longitude.add(jsonObject.getString("itm_longitud"));
							    
							    runOnUiThread
								 (
										 new Runnable()
										 {
											 @Override
											 public void run() 
											 {
												 llenaLista(nombre,direccion,promedio,distancia, latitude, longitude);
												 txtResults.setText(String.valueOf(jsonArray.length()));
												 txtMode.setText(mode);
												 pDialog.dismiss();
											 }
										 }
								 );
							}
						 }
					}
					catch (Exception e)
					{

					}
				}			
			};
			tr.start();

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
		        	promptRecomendation();
		            return true;
		            
		        case R.id.recomendation_route:
		        	promptRecomendationRoute();
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
		
		public void llenaLista(final ArrayList<String> nombre, final ArrayList<String> direccion,
								final ArrayList<String> promedio, final ArrayList<String> distancia,
								final ArrayList<String> latitude, final ArrayList<String> longitude)
		{
			AdaptadorTitulares adap = new AdaptadorTitulares(this,nombre,direccion,
															promedio,distancia);
			lista.setAdapter(adap);
			lista.setOnItemClickListener(new OnItemClickListener()
			{
				@Override
				public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
					// TODO Auto-generated method stub
					enviar(String.valueOf(nombre.get(position)), String.valueOf(direccion.get(position)),
					String.valueOf(promedio.get(position)),	String.valueOf(distancia.get(position)),
					String.valueOf(latitude.get(position)), String.valueOf(longitude.get(position)));
				}
			});
		}
		
		public void enviar(String itm_nombre, String itm_direccion, String itm_promedio, 
							String itm_distancia, String itm_latitude, String itm_longitude)
		{
			Intent mapActivity = new Intent(this,MapActivity.class);
			mapActivity.putExtra("id", 1);
			mapActivity.putExtra("itm_nombre", itm_nombre);
			mapActivity.putExtra("itm_direccion", itm_direccion);
			mapActivity.putExtra("itm_promedio", itm_promedio);
			mapActivity.putExtra("itm_distancia", itm_distancia);
			mapActivity.putExtra("itm_latitud", itm_latitude);
			mapActivity.putExtra("itm_longitud", itm_longitude);
			mapActivity.putExtra("rta_mode", mode);
			mapActivity.putExtra("radioBusqueda", radioBusqueda);
			mapActivity.putExtra("user", usuario);
			startActivity(mapActivity);
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
		    provider = handle.getBestProvider(c, true);
		    //Se activan las notificaciones de localización con los parámetros: 
		    //proveedor, tiempo mínimo de actualización, distancia mínima, Locationlistener
		    handle.requestLocationUpdates(provider, 60000, 5,this);
		    //Obtiene la ultima posicion conocida por el proveedor
		    loc = handle.getLastKnownLocation(provider);

		    return loc;
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
		
		@Override
		public void onClick(View arg0) {
			// TODO Auto-generated method stub
			Intent mapActivity = new Intent(this,MapActivity.class);
			mapActivity.putExtra("id", 0);
			mapActivity.putExtra("itm_nombre", nombre);
			mapActivity.putExtra("itm_direccion", direccion);
			mapActivity.putExtra("itm_promedio", promedio);
			mapActivity.putExtra("itm_distancia", distancia);
			mapActivity.putExtra("itm_latitud", latitude);
			mapActivity.putExtra("itm_longitud", longitude);
			mapActivity.putExtra("rta_mode", mode);
			mapActivity.putExtra("radioBusqueda", radioBusqueda);
			startActivity(mapActivity);
		}
}