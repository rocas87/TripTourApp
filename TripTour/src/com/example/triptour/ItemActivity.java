package com.example.triptour;

import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONObject;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Vibrator;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

public class ItemActivity extends Activity{

	String usuario, itm_id, itm_nombre, itm_direccion, itm_promedio, res, php;
	TextView txtNombre, txtUsuario, txtAddress;
	ImageView imagenItem, est1, est2, est3, est4, est5;
	ListView post;
	ProgressDialog pDialog;
	JSONArray jsonArray;
	JSONObject jsonObject;
	ArrayList<String> usr_nick = new ArrayList<String>();
	ArrayList<String> fechaPost = new ArrayList<String>();
	ArrayList<String> comentario = new ArrayList<String>();
	ArrayList<String> itm_rating = new ArrayList<String>();
	List<NameValuePair> params;
	EnviarPost enviar = new EnviarPost();
	
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
		post = (ListView)findViewById(R.id.post);
		
		txtNombre.setText(itm_nombre);
		downloadImagen(itm_id);
		promedioCalif(itm_promedio);
		txtAddress.setText(itm_direccion);
		comentarios(itm_id);
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
}
