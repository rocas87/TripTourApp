package com.example.triptour;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONObject;

import com.google.android.gms.location.LocationClient;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Vibrator;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class RecomendationActivity extends Activity implements android.location.LocationListener, OnClickListener{

	TextView txtUsuario, Seleccionado;
	ListView lista;
	Button btnMapa;
	List<NameValuePair> params;
	ArrayList<String> nombre = new ArrayList<String>();
	ArrayList<String> direccion = new ArrayList<String>();
	ArrayList<String> promedio = new ArrayList<String>();
	ArrayList<String> distancia = new ArrayList<String>();
	ArrayList<String> latitude = new ArrayList<String>();
	ArrayList<String> longitude = new ArrayList<String>();
	String usuario, latitud, longitud, categoria, radioBusqueda, php, res, itm_nombre, itm_direccion,
				    itm_promedio, itm_distancia, itm_latitude, itm_longitude;
	Location loc;
	LocationClient mLocationClient;
	LocationManager handle;
	private String provider;
	ProgressDialog pDialog;
	EnviarPost enviar = new EnviarPost();

	// TODO Auto-generated method stub
		@Override
		protected void onCreate(Bundle savedInstanceState) {
			super.onCreate(savedInstanceState);
			setContentView(R.layout.recomendation_activity);

			txtUsuario = (TextView)findViewById(R.id.txtUsuario);
			lista = (ListView)findViewById(R.id.lista);
			
			btnMapa = (Button)findViewById(R.id.btnmap);
			btnMapa.setOnClickListener(this);
			
			Bundle find = getIntent().getExtras();
			usuario = find.getString("user");
			txtUsuario.setText(usuario);

			// Parametros forsados por el momento
			categoria = "1";
			radioBusqueda = "10";

			loc = getMiUbicacion();
			latitud = String.valueOf(loc.getLatitude());
			longitud = String.valueOf(loc.getLongitude());

			params = new ArrayList<NameValuePair>();
			params.add(new BasicNameValuePair("usuario",usuario));
			params.add(new BasicNameValuePair("latitud",latitud));
			params.add(new BasicNameValuePair("longitud",longitud));
			params.add(new BasicNameValuePair("categoria",categoria));
			params.add(new BasicNameValuePair("radioBusqueda",radioBusqueda));

			php = "/servtriptour/recomendacion.php";
			
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
						JSONArray jsonArray = new JSONArray(res);
						
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
			mapActivity.putExtra("radioBusqueda", radioBusqueda);
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
			mapActivity.putExtra("radioBusqueda", radioBusqueda);
			startActivity(mapActivity);
		}
}