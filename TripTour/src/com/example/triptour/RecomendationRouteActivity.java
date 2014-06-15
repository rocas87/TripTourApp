package com.example.triptour;

import java.text.DecimalFormat;
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
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
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

public class RecomendationRouteActivity extends Activity implements android.location.LocationListener{

	TextView txtUsuario, Seleccionado, txtResults, txtMode;
	ListView lista;
	Button btnMapa;
	List<NameValuePair> params;
	ArrayList<String> itm_id = new ArrayList<String>();
	ArrayList<String> nombre = new ArrayList<String>();
	ArrayList<String> promedio = new ArrayList<String>();
	ArrayList<String> distancia = new ArrayList<String>();
	ArrayList<String> duracion = new ArrayList<String>();
	ArrayList<String> coordenadas = new ArrayList<String>();
	ArrayList<String> promedio_itm = new ArrayList<String>();
	ArrayList<String> direcciones = new ArrayList<String>();
	ArrayList<String> disItem = new ArrayList<String>();
	ArrayList<String> promRuta = new ArrayList<String>();
	String usuario, latitud, longitud, php, res, itm_nombre, itm_direccion, itm_promedio, itm_distancia, 
		   itm_latitude, itm_longitude, mode, medioTransporte, tiempoRecorrido, distMaxima, dia, hora, minuto, latLong, prom;
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
	EditText tiempo;
	String [] tokenDuracion, tokenDireccion, tokenRating;
	JSONObject jsonObject;
	
	// TODO Auto-generated method stub
		@Override
		protected void onCreate(Bundle savedInstanceState) {
			super.onCreate(savedInstanceState);
			setContentView(R.layout.recomendation_route_activity);

			txtUsuario = (TextView)findViewById(R.id.txtUsuario);
			txtResults = (TextView)findViewById(R.id.txtResult);
			txtMode = (TextView)findViewById(R.id.txtMode);
			lista = (ListView)findViewById(R.id.lista);
				
			ActionBar actionBar = getActionBar();
			actionBar.setDisplayHomeAsUpEnabled(true);
			
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
			
			Bundle recomendationRoute = getIntent().getExtras();
			usuario = recomendationRoute.getString("user");
			medioTransporte = recomendationRoute.getString("transporte");
			hora = recomendationRoute.getString("hora");
			minuto = recomendationRoute.getString("minuto");
			
			txtUsuario.setText(usuario);

			if(medioTransporte.equals("1"))
			{
				mode = "driving";
				distMaxima = "50";
			}
			else if(medioTransporte.equals("2"))
			{
				mode = "walking";
				distMaxima = "10";
			}
			
			dia = "0";
			tiempoRecorrido = dia+","+hora+","+minuto; 

			loc = getMiUbicacion();
			latitud = String.valueOf(loc.getLatitude());
			longitud = String.valueOf(loc.getLongitude());
						
			params = new ArrayList<NameValuePair>();
			params.add(new BasicNameValuePair("usuario",usuario));
			params.add(new BasicNameValuePair("latitud",latitud));
			params.add(new BasicNameValuePair("longitud",longitud));
			params.add(new BasicNameValuePair("distMaxima",distMaxima));
			params.add(new BasicNameValuePair("tiempoRecorrido",tiempoRecorrido));
			params.add(new BasicNameValuePair("mode",mode));
			
			php = "/servtriptour/recomendacion_rutaV2.php";
			
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
							jsonObject = jsonArray.getJSONObject(i);
							if (jsonObject.getString("resultado")=="nada")
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
												 Toast.makeText(RecomendationRouteActivity.this,"No se encontraron rutas disponibles",
														 Toast.LENGTH_LONG).show();
												 pDialog.dismiss();
											 }
										 }
								 );
							}
							else
							{
							itm_id.add(jsonObject.getString("itm_id"));
							nombre.add(jsonObject.getString("rta_nombre"));
							promedio.add(jsonObject.getString("rta_promedio"));
							distancia.add(jsonObject.getString("rta_distancia"));
							duracion.add(jsonObject.getString("rta_duracion"));
							coordenadas.add(jsonObject.getString("rta_coordenadas"));
							promedio_itm.add(jsonObject.getString("itm_promedio"));
							direcciones.add(jsonObject.getString("itm_direccion"));	
						    disItem.add(jsonObject.getString("itm_distancia"));
							}
						 }
						 if(jsonObject.getString("resultado")=="nada")
						 {
							 home();
						 }
						 else
						 {
							 runOnUiThread
								(
									 new Runnable()
									 {
										 @Override
										 public void run()
										 {
										  llenaLista(itm_id,nombre,promedio,distancia,duracion, coordenadas, 
												  promedio_itm, direcciones, disItem);
										  txtResults.setText(String.valueOf(jsonArray.length()));
										  txtMode.setText(mode);
										  pDialog.dismiss();
										 }
									 }
								 );
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
		        	if(usuario.equals("SR"))
		        	{
		        		Vibrator vibrator =(Vibrator)getSystemService(Context.VIBRATOR_SERVICE);
		        		vibrator.vibrate(200);
		        		Toast.makeText(RecomendationRouteActivity.this, "Debe estar registrado", Toast.LENGTH_LONG).show();
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
		        		Toast.makeText(RecomendationRouteActivity.this, "Debe estar registrado", Toast.LENGTH_LONG).show();
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
			
			tiempo = (EditText)promptRecomendationRoute.findViewById(R.id.duracion);
			
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
					tokenDuracion = (tiempo.getText().toString()).split(":");
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
		
		public void home()
		{
			Intent home = new Intent(this,HomeActivity.class);
			home.putExtra("usr_nick", usuario);
			startActivity(home);
		}

		public void llenaLista(final ArrayList<String> itm_id, final ArrayList<String> nombre, 
				final ArrayList<String> promedio, final ArrayList<String> distancia, final ArrayList<String> duracion, 
				final ArrayList<String> coordenadas, final ArrayList<String> promedio_itm, final ArrayList<String> direcciones, 
				final ArrayList<String> disItem)
		{
			promRuta = promedio(promedio_itm);
			AdaptadorRoute adap = new AdaptadorRoute(this,nombre,promRuta,distancia,duracion);
			lista.setAdapter(adap);
			lista.setOnItemClickListener(new OnItemClickListener()
			{
				@Override
				public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
					// TODO Auto-generated method stub
					enviar(String.valueOf(itm_id.get(position)),String.valueOf(nombre.get(position)), String.valueOf(promedio.get(position)),
					String.valueOf(distancia.get(position)), String.valueOf(duracion.get(position)),
					String.valueOf(coordenadas.get(position)), String.valueOf(promedio_itm.get(position)),
					String.valueOf(direcciones.get(position)), String.valueOf(disItem.get(position)));
				}
			});
		}
		
		private ArrayList<String> promedio(ArrayList<String> promedio_itm2) 
		{
			ArrayList<String> notas = new ArrayList<String>();
			String resultado="0";
			// TODO Auto-generated method stub
			for(int i=0; i<promedio_itm2.size(); i++)
			{
				int suma = 0;
				tokenRating = promedio_itm2.get(i).split(",");
				for(int x=0; x<tokenRating.length; x++)
				{
					suma = suma+Integer.parseInt(tokenRating[x]);
				}
				resultado = String.valueOf(suma/tokenRating.length);
				notas.add(resultado);
			}
			return notas;
		}


		public void enviar(String itm_id, String rta_nombre, String rta_promedio, String rta_distancia, 
				String rta_duracion, String rta_coordenadas, String promedio_itm, String direcciones, String disItem)
		{
			Intent mapActivity = new Intent(this,MapActivity.class);
			mapActivity.putExtra("id", 2);
			mapActivity.putExtra("itm_id", itm_id);
			mapActivity.putExtra("rta_nombre", rta_nombre);
			mapActivity.putExtra("rta_promedio", rta_promedio);
			mapActivity.putExtra("rta_distancia", rta_distancia);
			mapActivity.putExtra("rta_duracion", rta_duracion);
			mapActivity.putExtra("rta_coordenadas", rta_coordenadas);
			mapActivity.putExtra("rta_mode", mode);
			mapActivity.putExtra("distMaxima", distMaxima);
			mapActivity.putExtra("promedio_itm", promedio_itm);
			mapActivity.putExtra("direcciones", direcciones);
			mapActivity.putExtra("user", usuario);
			mapActivity.putExtra("disItem", disItem);
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
}
