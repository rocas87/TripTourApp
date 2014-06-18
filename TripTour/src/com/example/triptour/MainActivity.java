package com.example.triptour;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONObject;

import com.google.android.gms.maps.model.LatLng;

import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Vibrator;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity implements OnClickListener, android.location.LocationListener
{
	TextView txtUsuario, txtPass;
	EditText edtUsuario, edtPass;
	Button btnLogin, btnRegister, btnLogonSkip;
	String res,valido,usr_nombre,usr_nick,user,password, php;
	InputStream is = null;
	JSONObject json = null;
	ProgressDialog pDialog;
	Location loc;
	LatLng MiUbicacion;
	LocationManager handle;
	private String provider;
	List<NameValuePair> params;
	EnviarPost enviar = new EnviarPost();
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
				
		txtUsuario = (TextView)findViewById(R.id.txtUsuario);
		txtPass = (TextView)findViewById(R.id.txtPass);
				
		edtUsuario = (EditText)findViewById(R.id.edtUsuario);
		edtPass = (EditText)findViewById(R.id.edtPass);
		
		btnLogin = (Button)findViewById(R.id.btnLogin);
		btnRegister = (Button)findViewById(R.id.btnRegister);
		btnLogonSkip = (Button)findViewById(R.id.btnLogonSkip);
		
		btnLogin.setOnClickListener(this);
		btnRegister.setOnClickListener(this);
		btnLogonSkip.setOnClickListener(this);
		
		loc = getMiUbicacion();
		
		php = "/servtriptour/login.php";
	}

	@Override
	public void onClick(View arg0) {
		// TODO Auto-generated method stub
		switch(arg0.getId())
		{
			case R.id.btnLogin:
				login();
				break;
				
			case R.id.btnRegister:
				Intent registro = new Intent(this,RegistroActivity.class);
				startActivity(registro);
				break;
			
			case R.id.btnLogonSkip:
				Intent home = new Intent(this,HomeActivity.class);
				home.putExtra("usr_nombre", "SR");
				home.putExtra("usr_nick", "SR");
				startActivity(home);
				break;
		}
	}

		
	private void login() 
	{
		// TODO Auto-generated method stub
		pDialog = new ProgressDialog(this);
		pDialog.setMessage("Conencting...");
		pDialog.show();
		user = edtUsuario.getText().toString();
		password = edtPass.getText().toString();
		
		if(user.equals("") || password.equals("")){
			pDialog.dismiss();
			Vibrator vibrator =(Vibrator)getSystemService(Context.VIBRATOR_SERVICE);
		    vibrator.vibrate(500);
			Toast.makeText(MainActivity.this,"Complete todos los campos",
					Toast.LENGTH_LONG).show();
		}else{
			Thread nt = new Thread() {
				@Override
				public void run() {
					
					try {
						params = new ArrayList<NameValuePair>();
						params.add(new BasicNameValuePair("usuario",user));
						params.add(new BasicNameValuePair("pass",password));
						
						res = enviar.enviarPost(params, php);
						Log.e("Response", res);
						JSONArray jsonArray = new JSONArray(res);
						for (int i = 0; i < jsonArray.length(); i++) {
					        JSONObject jsonObject = jsonArray.getJSONObject(i);
					        valido = jsonObject.getString("valido");
					        if(valido.equals("1")){
					        	usr_nick = jsonObject.getString("usr_nick");
					        	pDialog.dismiss();
					        	runOnUiThread(new Runnable() {
									@Override
									public void run() {
										valida(usr_nombre,usr_nick);
									}
								});
					        }else{
					        	runOnUiThread(new Runnable() {
									@Override
									public void run() {
										pDialog.dismiss();
										Vibrator vibrator =(Vibrator)getSystemService(Context.VIBRATOR_SERVICE);
									    vibrator.vibrate(200);
										Toast.makeText(MainActivity.this,"Usuario invalido",
												Toast.LENGTH_LONG).show();
									}
								});
					        }
					        
					}
					} catch (Exception e) {
						// TODO: handle exception
					}
				}
			};
			nt.start();
		}		
	}

	public void valida (String usr_nombre, String usr_nick){
		Intent home = new Intent(this,HomeActivity.class);
		home.putExtra("usr_nombre", usr_nombre);
		home.putExtra("usr_nick", usr_nick);
		startActivity(home);
	}
	
	public Location getMiUbicacion()
	{
		//Llamo al servico de localizacion	        
	    handle = (LocationManager)getSystemService(Context.LOCATION_SERVICE);
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
	    handle.requestLocationUpdates(provider, 60000, 5,(LocationListener) this);
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
