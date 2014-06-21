package com.example.triptour;

import java.net.HttpURLConnection;
import java.net.URL;
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
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
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
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RatingBar;
import android.widget.Spinner;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.RatingBar.OnRatingBarChangeListener;
import android.widget.TextView;
import android.widget.Toast;

public class ItemActivity extends Activity implements android.location.LocationListener{

	String usuario, itm_id, itm_nombre, itm_direccion, itm_promedio, res, php, nota, hora, minuto, latLong, distMax;
	TextView txtNombre, txtUsuario, txtAddress, txtUsr;
	EditText edtComentario, edtHora, edtMinuto, edtDist, edtRadioBusqueda;
	ImageView imagenItem, est1, est2, est3, est4, est5, est1p, est2p, est3p, est4p, est5p;
	RatingBar ratingBar;
	ListView post;
	ProgressDialog pDialog;
	JSONArray jsonArray;
	JSONObject jsonObject;
	ArrayList<String> usr_nick = new ArrayList<String>();
	ArrayList<String> fechaPost = new ArrayList<String>();
	ArrayList<String> comentario = new ArrayList<String>();
	ArrayList<String> itm_rating = new ArrayList<String>();
	List<NameValuePair> params, coment;
	EnviarPost enviar = new EnviarPost();
	LayoutInflater liComentario;
	View promptComentario;
	ArrayAdapter<String> adaptadorCategoria, adaptadorTransporte;
	private Spinner spCategoria, spTransporte;
	private List<String> categorias = new ArrayList<String>();
	private List<String> transporte = new ArrayList<String>();
	LayoutInflater liFind, liRecomendation, liRecomendationRoute;
	View promptFind, promptRecomendation, promptRecomendationRoute;
	String [] tokenDuracion;
	int categoriaFind, transporteFind, categoriaRecomendation, transporteRecomendation, transporteRoute;
	Location loc;
	LocationClient mLocationClient;
	LocationManager handle;
	private String provider;
	
	protected void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.item_activity);
		
		Bundle item = getIntent().getExtras();
		usuario = item.getString("user");
		itm_id = item.getString("itm_id");
		itm_nombre = item.getString("itm_nombre");
		itm_direccion = item.getString("itm_direccion");
		itm_promedio = item.getString("itm_promedio");
		
		txtNombre = (TextView)findViewById(R.id.txtNombre);
		imagenItem = (ImageView)findViewById(R.id.imgItem);
		est1 = (ImageView)findViewById(R.id.est1);
		est2 = (ImageView)findViewById(R.id.est2);
		est3 = (ImageView)findViewById(R.id.est3);
		est4 = (ImageView)findViewById(R.id.est4);
		est5 = (ImageView)findViewById(R.id.est5);
		txtAddress = (TextView)findViewById(R.id.txtAddress);
		ratingBar = (RatingBar)findViewById(R.id.ratingBar);
		post = (ListView)findViewById(R.id.post);
		
		txtNombre.setText(itm_nombre);
		downloadImagen(itm_id);
		promedioCalif(itm_promedio);
		txtAddress.setText(itm_direccion);
		ratingBar.setStepSize((float) 1.0);
		addListenerOnRatingBar();
		comentarios(itm_id);
		
		// Action Bar
		
		ActionBar actionBar = getActionBar();
		actionBar.setDisplayHomeAsUpEnabled(true);
		
		//Categorias disponibles
		categorias.add("Bar");
		categorias.add("Zoologico/Zoo");
		categorias.add("Museo/Museum");
		categorias.add("Parques/Park");
		categorias.add("Parque de Diversiones/Amusement Park");
		categorias.add("Deportes/Sports");
		categorias.add("Restaurant");
		categorias.add("Senderismo/Hiking");
		categorias.add("Artesania/Crafts");
		categorias.add("Patrimonios Nacionales/National Treasures");
		//Tipos de transporte
		transporte.add("Caminando/By walking");
		transporte.add("Conduciendo/By driving");

	}
	
	void downloadImagen(final String id)
	{
		Thread nt = new Thread() {
			@Override
			public void run() {
				
				try {
					URL imageUrl = new URL("http://200.14.84.19/servtriptour/imagenes/"+id+".jpg");
					Log.e("token","http://200.14.84.19/servtriptour/imagenes/"+id+".jpg" );
					HttpURLConnection conn = (HttpURLConnection)imageUrl.openConnection();
					conn.connect();
					Bitmap bitm = BitmapFactory.decodeStream(conn.getInputStream());
					imagenItem.setImageBitmap(bitm);
				}
				catch (Exception e) 
				{
					// TODO: handle exception
				}
			}
		};
		nt.start();
	}
	
	void promedioCalif(String prom)
	{
		if(prom.equals("4"))
		{
			est5.setImageURI(Uri.parse("drawable/preview.jpg"));
		}
		else if(prom.equals("3"))
		{
			est4.setImageURI(Uri.parse("drawable/preview.jpg"));
			est5.setImageURI(Uri.parse("drawable/preview.jpg"));
		}
		else if(prom.equals("2"))
		{
			est3.setImageURI(Uri.parse("drawable/preview.jpg"));
			est4.setImageURI(Uri.parse("drawable/preview.jpg"));
			est5.setImageURI(Uri.parse("drawable/preview.jpg"));
		}
		else if(prom.equals("1"))
		{
			est2.setImageURI(Uri.parse("drawable/preview.jpg"));
			est3.setImageURI(Uri.parse("drawable/preview.jpg"));
			est4.setImageURI(Uri.parse("drawable/preview.jpg"));
			est5.setImageURI(Uri.parse("drawable/preview.jpg"));
		}
	}
	
	public void addListenerOnRatingBar()
	{
		ratingBar.setOnRatingBarChangeListener(new OnRatingBarChangeListener() {
			public void onRatingChanged(RatingBar ratingBar, float rating,
				boolean fromUser) {
				if(usuario.equals("SR"))
				{
					Vibrator vibrator =(Vibrator)getSystemService(Context.VIBRATOR_SERVICE);
	        		vibrator.vibrate(200);
	        		Toast.makeText(ItemActivity.this, "Debe estar registrado para evaluar", Toast.LENGTH_LONG).show();
				}
				else
				{
					promptComentario(rating);
				}
				
			}
		});
	}
	
	private void promptComentario(final float rating) 
	{
		// TODO Auto-generated method stub
		liComentario = LayoutInflater.from(this);
		promptComentario= liComentario.inflate(R.layout.prompt_comentario_activity, null);
		
		txtUsr = (TextView)promptComentario.findViewById(R.id.txtUsr);
		est1p = (ImageView)promptComentario.findViewById(R.id.est1);
		est2p = (ImageView)promptComentario.findViewById(R.id.est2);
		est3p = (ImageView)promptComentario.findViewById(R.id.est3);
		est4p = (ImageView)promptComentario.findViewById(R.id.est4);
		est5p = (ImageView)promptComentario.findViewById(R.id.est5);
		edtComentario = (EditText)promptComentario.findViewById(R.id.edtComentario);
		
		txtUsr.setText(usuario);
		
		nota = "5";
		if(String.valueOf(rating).equals("4.0"))
		{
			est5p.setImageURI(Uri.parse("drawable/preview.jpg"));
			nota = "4";
		}
		else if(String.valueOf(rating).equals("3.0"))
		{
			est4p.setImageURI(Uri.parse("drawable/preview.jpg"));
			est5p.setImageURI(Uri.parse("drawable/preview.jpg"));
			nota = "3";
		}
		else if(String.valueOf(rating).equals("2.0"))
		{
			est3p.setImageURI(Uri.parse("drawable/preview.jpg"));
			est4p.setImageURI(Uri.parse("drawable/preview.jpg"));
			est5p.setImageURI(Uri.parse("drawable/preview.jpg"));
			nota = "2";
		}
		else if(String.valueOf(rating).equals("1.0"))
		{
			est2p.setImageURI(Uri.parse("drawable/preview.jpg"));
			est3p.setImageURI(Uri.parse("drawable/preview.jpg"));
			est4p.setImageURI(Uri.parse("drawable/preview.jpg"));
			est5p.setImageURI(Uri.parse("drawable/preview.jpg"));
			nota = "1";
		}
		AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
		alertDialogBuilder.setView(promptComentario);
		
		// Mostramos el mensaje del cuadro de dialogo
		alertDialogBuilder
		.setCancelable(false)
		.setPositiveButton("OK", new DialogInterface.OnClickListener() {
		public void onClick(DialogInterface dialog,int id) {
		// Rescatamos el nombre del EditText y lo mostramos por pantalla
			coment = new ArrayList<NameValuePair>();
			coment.add(new BasicNameValuePair("usr", usuario));
			coment.add(new BasicNameValuePair("itm_id", itm_id));
			coment.add(new BasicNameValuePair("post", edtComentario.getText().toString()));
			coment.add(new BasicNameValuePair("rating", nota));
			Log.e("token", usuario);
			Log.e("token", "itm_id:"+itm_id);
			Log.e("token", "itm_rating:"+nota);
			Log.e("token", "itm_post:"+edtComentario.getText().toString());
			
			php = "/servtriptour/comentario.php";

			pDialog.setMessage("Guardando...");
			pDialog.show();
			Thread tr = new Thread()
			{

				@Override
				public void run()
				{
					try
					{
						res = enviar.enviarPost(coment, php);
						Log.e("token", "res:"+res);
						runOnUiThread
						 (
							 new Runnable()
							 {
								 @Override
								 public void run() 
								 {
									 if(res.equals("3"))
									 {
										 pDialog.dismiss();
										 Toast.makeText(ItemActivity.this, "Comentario guardado exitosamente", Toast.LENGTH_LONG).show(); 
									 }
									 else
									 {
										Vibrator vibrator =(Vibrator)getSystemService(Context.VIBRATOR_SERVICE);
							        	vibrator.vibrate(200);
							        	pDialog.dismiss();
							        	Toast.makeText(ItemActivity.this, "Error, vuelva a intentarlo", Toast.LENGTH_LONG).show();
									 }
									 
								 }
									 }
							 );
						 }
					catch (Exception e)
					{

					}
				}			
			};
			tr.start();
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
	
	public void comentarios(String id)
	{
		php = "/servtriptour/infItem.php";
		
		params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("itm_id",id));

		
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
					Log.e("token", res);
					
					jsonArray = new JSONArray(res);
					Log.e("token", String.valueOf(jsonArray.length()));
					 for (int i = 0; i < jsonArray.length(); i++) 
					 {
						jsonObject = jsonArray.getJSONObject(i);
						usr_nick.add(jsonObject.getString("usr_nick"));
						fechaPost.add(jsonObject.getString("fechaPost"));
						comentario.add(jsonObject.getString("itm_post"));
						itm_rating.add(jsonObject.getString("itm_rating"));
					 }
						 runOnUiThread
						 (
								 new Runnable()
								 {
									 @Override
									 public void run() 
									 {
										 Log.e("token", "pd");
										 llenaLista(usr_nick,fechaPost,comentario,itm_rating);
										 pDialog.dismiss();
									 }
								 }
						 );
				}
				catch (Exception e)
				{

				}
			}			
		};
		tr.start();
	}
	
	public void llenaLista(final ArrayList<String> usr_nick, final ArrayList<String> fechaPost,	final ArrayList<String> comentario, 
			final ArrayList<String> itm_rating)
		{
			AdaptadorComentarios adap = new AdaptadorComentarios(this,usr_nick,fechaPost,comentario,itm_rating);
			post.setAdapter(adap);
		}
	
	//ActionBar
	
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
	        		Toast.makeText(ItemActivity.this, "Debe estar registrado", Toast.LENGTH_LONG).show();
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
	        		Toast.makeText(ItemActivity.this, "Debe estar registrado", Toast.LENGTH_LONG).show();
	        	}
	        	else
	        	{
	        		promptRecomendationRoute();
	        	}
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
		
		edtRadioBusqueda = (EditText)promptFind.findViewById(R.id.edtRadioBusqueda);
		
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
			if(edtRadioBusqueda.getText().toString().equals(""))
			{
				Vibrator vibrator =(Vibrator)getSystemService
						 (Context.VIBRATOR_SERVICE);
				 vibrator.vibrate(200);
				Toast.makeText(ItemActivity.this,"Debe llenar el radio",Toast.LENGTH_LONG).show();
			}
			else
			{
				find(edtRadioBusqueda.getText().toString());
			}
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

	public void find(String radio)
	{
		Intent find = new Intent(this,FindActivity.class);
		find.putExtra("user", usuario);
		find.putExtra("categoria", String.valueOf(categoriaFind));
		find.putExtra("transporte", String.valueOf(transporteFind));
		find.putExtra("radioBusqueda", radio);
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
		
		edtRadioBusqueda = (EditText)promptRecomendation.findViewById(R.id.edtRadioBusqueda);
		
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
				if(edtRadioBusqueda.getText().toString().equals(""))
				{
					Vibrator vibrator =(Vibrator)getSystemService
							 (Context.VIBRATOR_SERVICE);
					 vibrator.vibrate(200);
					Toast.makeText(ItemActivity.this,"Debe llenar el radio",Toast.LENGTH_LONG).show();
				}
				else
				{
					recomendation(edtRadioBusqueda.getText().toString());
				}
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

	public void recomendation(String radio)
	{
		Intent recomendation = new Intent(this,RecomendationActivity.class);
		recomendation.putExtra("user", usuario);
		recomendation.putExtra("categoria", String.valueOf(categoriaRecomendation));
		recomendation.putExtra("transporte", String.valueOf(transporteRecomendation));
		recomendation.putExtra("radioBusqueda", radio);
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
		
		edtHora = (EditText)promptRecomendationRoute.findViewById(R.id.edtHora);
		edtMinuto = (EditText)promptRecomendationRoute.findViewById(R.id.edtMinuto);
		edtDist = (EditText)promptRecomendationRoute.findViewById(R.id.edtDisMax);
		
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
				if(edtHora.getText().toString().equals(""))
				{
					Vibrator vibrator =(Vibrator)getSystemService
							 (Context.VIBRATOR_SERVICE);
					 vibrator.vibrate(200);
					Toast.makeText(ItemActivity.this,"Debe llenar hora",Toast.LENGTH_LONG).show();
				}
				else if(edtMinuto.getText().toString().equals(""))
				{
					Vibrator vibrator =(Vibrator)getSystemService
							 (Context.VIBRATOR_SERVICE);
					 vibrator.vibrate(200);
					Toast.makeText(ItemActivity.this,"Debe llenar minutos",Toast.LENGTH_LONG).show();
				}
				else if(edtDist.getText().toString().equals(""))
				{
					Vibrator vibrator =(Vibrator)getSystemService
							 (Context.VIBRATOR_SERVICE);
					 vibrator.vibrate(200);
					Toast.makeText(ItemActivity.this,"Debe llenar distancia",Toast.LENGTH_LONG).show();
				}
				else
				{
					hora = edtHora.getText().toString();
					minuto = edtMinuto.getText().toString();
					distMax = edtDist.getText().toString();
					recomendationRoute();
				}
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
		recomendationRoute.putExtra("distMaxima", distMax);
		startActivity(recomendationRoute);
	}
	
	//Localizacion
	public Location getMiUbicacion()
	{
		//Llamo al servico de localizacion	        
	    handle = (LocationManager)getSystemService(LOCATION_SERVICE);
	    //Clase criteria permite decidir mejor poveedor de posicion
	    Criteria c = new Criteria();
	    //obtiene el mejor proveedor en funci�n del criterio asignado
	    //ACCURACY_FINE(La mejor presicion)--ACCURACY_COARSE(PRESISION MEDIA)
	    c.setAccuracy(Criteria.ACCURACY_COARSE);
	    //Indica si es necesaria la altura por parte del proveedor
	    c.setAltitudeRequired(false);
	    provider = handle.getBestProvider(c, true);
	    //Se activan las notificaciones de localizaci�n con los par�metros: 
	    //proveedor, tiempo m�nimo de actualizaci�n, distancia m�nima, Locationlistener
	    handle.requestLocationUpdates(provider, 60000, 5,this);
	    //Obtiene la ultima posicion conocida por el proveedor
	    loc = handle.getLastKnownLocation(provider);
	  
	    return loc;
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
}
