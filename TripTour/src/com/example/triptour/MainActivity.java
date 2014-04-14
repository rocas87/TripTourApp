package com.example.triptour;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity implements OnClickListener, OnTouchListener{

	TextView txtUsuario, txtPass, txtRegistrar;
	EditText edtUsuario, edtPass;
	Button btnLogin;
	String ip, resultado,res,valido,usr_nombre,usr_nick;
	InputStream is = null;
	JSONObject json = null;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		ip = "192.168.30.75";
		txtUsuario = (TextView)findViewById(R.id.txtUsuario);
		txtPass = (TextView)findViewById(R.id.txtPass);
		txtRegistrar = (TextView)findViewById(R.id.txtRegistrar);
		
		edtUsuario = (EditText)findViewById(R.id.edtUsuario);
		edtPass = (EditText)findViewById(R.id.edtPass);
		
		btnLogin = (Button)findViewById(R.id.btnLogin);
		
		btnLogin.setOnClickListener(this);
		txtRegistrar.setOnTouchListener(this);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public void onClick(View arg0) {
		// TODO Auto-generated method stub
		Thread nt = new Thread() {
			@Override
			public void run() {
				
				try {
					res = enviarPost(edtUsuario.getText().toString(), edtPass.getText().toString());
					JSONArray jsonArray = new JSONArray(resultado);
					for (int i = 0; i < jsonArray.length(); i++) {
				        JSONObject jsonObject = jsonArray.getJSONObject(i);
				        valido = jsonObject.getString("valido");
				        if(valido.equals("1")){
				        	usr_nombre = jsonObject.getString("usr_nombre");
				        	usr_nick = jsonObject.getString("usr_nick");
				        	runOnUiThread(new Runnable() {
								@Override
								public void run() {
									Toast.makeText(MainActivity.this, res+"valido: "+valido+" nombre: "
											+usr_nombre+" nick: "+usr_nick,
											Toast.LENGTH_LONG).show();
								}
							});
				        }else{
				        	runOnUiThread(new Runnable() {
								@Override
								public void run() {
									Toast.makeText(MainActivity.this, res+"Usuario invalido: ",
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

	@Override
	public boolean onTouch(View arg0, MotionEvent arg1) {
		// TODO Auto-generated method stub
		return false;
	}
	
	public String enviarPost(String usuario, String pass) {

		HttpClient httpClient = new DefaultHttpClient();
		HttpContext localContext = new BasicHttpContext();
		HttpPost httpPost = new HttpPost("http://192.168.0.103/servtriptour/login.php");
		HttpResponse response = null;
		try {
			List<NameValuePair> params = new ArrayList<NameValuePair>();
			params.add(new BasicNameValuePair("usuario", usuario));
			params.add(new BasicNameValuePair("pass", pass));
			httpPost.setEntity(new UrlEncodedFormEntity(params));
			response = httpClient.execute(httpPost, localContext);
			HttpEntity entity = response.getEntity();
			resultado = EntityUtils.toString(entity, "UTF-8");
		} catch (Exception e) {
			// TODO: handle exception
		}
		return resultado;
	}
}
