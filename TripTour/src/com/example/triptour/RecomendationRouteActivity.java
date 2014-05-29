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
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class RecomendationRouteActivity extends Activity implements android.location.LocationListener{

	TextView txtUsuario, Seleccionado;
	ListView lista;
	Button btnMapa;
	List<NameValuePair> params;
	ArrayList<String> nombre = new ArrayList<String>();
	ArrayList<String> promedio = new ArrayList<String>();
	ArrayList<String> distancia = new ArrayList<String>();
	ArrayList<String> duracion = new ArrayList<String>();
	ArrayList<String> coordenadas = new ArrayList<String>();
	ArrayList<String> promedio_itm = new ArrayList<String>();
	ArrayList<String> direcciones = new ArrayList<String>();
	String usuario, latitud, longitud, php, res, itm_nombre, itm_direccion, itm_promedio, itm_distancia, 
		   itm_latitude, itm_longitude, mode, tiempoRecorrido, distMaxima;
	int dia, hora, minuto;
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
			setContentView(R.layout.recomendation_route_activity);

			txtUsuario = (TextView)findViewById(R.id.txtUsuario);
			lista = (ListView)findViewById(R.id.lista);
						
			Bundle find = getIntent().getExtras();
			usuario = find.getString("user");
			txtUsuario.setText(usuario);

			// Parametros forsados por el momento
			mode = "driving";
			dia = 0;
			hora = 0;
			minuto = 50;
			tiempoRecorrido = dia+","+hora+","+minuto; 
			distMaxima = "50";

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
						JSONArray jsonArray = new JSONArray(res);
						
						 for (int i = 0; i < jsonArray.length(); i++) 
						 {
							JSONObject jsonObject = jsonArray.getJSONObject(i);
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
								
							
							nombre.add(jsonObject.getString("rta_nombre"));
							promedio.add(jsonObject.getString("rta_promedio"));
							distancia.add(jsonObject.getString("rta_distancia"));
							duracion.add(jsonObject.getString("rta_duracion"));
							coordenadas.add(jsonObject.getString("rta_coordenadas"));
							promedio_itm.add(jsonObject.getString("itm_promedio"));
							direcciones.add(jsonObject.getString("itm_direccion"));							
							
							runOnUiThread
							(
								 new Runnable()
								 {
									 @Override
									 public void run()
									 {
									  llenaLista(nombre,promedio,distancia,duracion, coordenadas, 
											  promedio_itm, direcciones);
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
		public void llenaLista(final ArrayList<String> nombre, final ArrayList<String> promedio,
								final ArrayList<String> distancia, final ArrayList<String> duracion,
								final ArrayList<String> coordenadas, final ArrayList<String> promedio_itm,
								final ArrayList<String> direcciones)
		{
			AdaptadorTitulares adap = new AdaptadorTitulares(this,nombre,promedio,distancia,duracion);
			lista.setAdapter(adap);
			lista.setOnItemClickListener(new OnItemClickListener()
			{
				@Override
				public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
					// TODO Auto-generated method stub
					enviar(String.valueOf(nombre.get(position)), String.valueOf(promedio.get(position)),
					String.valueOf(distancia.get(position)), String.valueOf(duracion.get(position)),
					String.valueOf(coordenadas.get(position)), String.valueOf(promedio_itm.get(position)),
					String.valueOf(direcciones.get(position)));
				}
			});
		}
		
		public void enviar(String rta_nombre, String rta_promedio, String rta_distancia, 
				String rta_duracion, String rta_coordenadas, String promedio_itm, String direcciones)
		{
			Intent mapActivity = new Intent(this,MapActivity.class);
			mapActivity.putExtra("id", 2);
			mapActivity.putExtra("rta_nombre", rta_nombre);
			mapActivity.putExtra("rta_promedio", rta_promedio);
			mapActivity.putExtra("rta_distancia", rta_distancia);
			mapActivity.putExtra("rta_duracion", rta_duracion);
			mapActivity.putExtra("rta_coordenadas", rta_coordenadas);
			mapActivity.putExtra("rta_mode", mode);
			mapActivity.putExtra("distMaxima", distMaxima);
			mapActivity.putExtra("promedio_itm", promedio_itm);
			mapActivity.putExtra("direcciones", direcciones);
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
