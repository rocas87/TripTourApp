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
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Vibrator;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class FindActivity extends Activity implements android.location.LocationListener{

	TextView txtUsuario, Seleccionado;
	ListView lista;
	List<NameValuePair> params;
	ArrayList<String> nombre = new ArrayList<String>();
	ArrayList<String> direccion = new ArrayList<String>();
	ArrayList<String> promedio = new ArrayList<String>();
	ArrayList<String> distancia = new ArrayList<String>();
	String usuario, tit, desc,latitud, longitud, categoria, radioBusqueda, 
			php, res, itm_nombre, itm_direccion;
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
			setContentView(R.layout.find_activity);

			txtUsuario = (TextView)findViewById(R.id.txtUsuario);
			
			Bundle find = getIntent().getExtras();
			usuario = find.getString("user");
			txtUsuario.setText(usuario);
			
			lista = (ListView)findViewById(R.id.lista);
			
			// Parametros forsados por el momento
			categoria = "1";
			radioBusqueda = "1.5";
						
			loc = getMiUbicacion();
			latitud = String.valueOf(loc.getLatitude());
			longitud = String.valueOf(loc.getLongitude());
					
			params = new ArrayList<NameValuePair>();
			params.add(new BasicNameValuePair("latitud",latitud));
			params.add(new BasicNameValuePair("longitud",longitud));
			params.add(new BasicNameValuePair("categoria",categoria));
			params.add(new BasicNameValuePair("radioBusqueda",radioBusqueda));
			
			php = "/servtriptour/busqueda.php";
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
						    nombre.add(jsonObject.getString("itm_nombre"));
						    direccion.add(jsonObject.getString("itm_direccion"));
						    promedio.add(jsonObject.getString("itm_promedio"));
						    distancia.add(jsonObject.getString("distancia"));
						    if(jsonArray.length() == 0)
						    {
						    	Vibrator vibrator =(Vibrator)getSystemService(Context.VIBRATOR_SERVICE);
							    vibrator.vibrate(200);
						    	Toast.makeText(FindActivity.this,"No se encontraron resultados",
										Toast.LENGTH_LONG).show();
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
												 llenaLista(nombre,direccion,promedio,distancia);
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
								final ArrayList<String> promedio, final ArrayList<String> distancia)
		{
			AdaptadorTitulares adap = new AdaptadorTitulares(this,nombre,direccion,
															promedio,distancia);
			lista.setAdapter(adap);
			lista.setOnItemClickListener(new OnItemClickListener()
			{
				@Override
				public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
					// TODO Auto-generated method stub
					Toast.makeText(FindActivity.this,"Seleccionaste: "+nombre.get(position),
							Toast.LENGTH_LONG).show();
				}
			});
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