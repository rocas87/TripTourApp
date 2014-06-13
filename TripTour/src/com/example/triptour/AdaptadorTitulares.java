package com.example.triptour;

import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class AdaptadorTitulares extends ArrayAdapter {

	Activity context;
	ArrayList<String> id = new ArrayList<String>();
	ArrayList<String> nombre = new ArrayList<String>();
	ArrayList<String> direccion = new ArrayList<String>();
	ArrayList<String> promedio = new ArrayList<String>();
	ArrayList<String> distancia = new ArrayList<String>();
	String itm_id, nom, dir, prom, dist, lat;
	TextView name, address, average, distance;
	ImageView imagen, est1, est2, est3, est4, est5;
	
	//@SuppressWarnings("unchecked")
	public AdaptadorTitulares(Activity context, ArrayList<String> id, ArrayList<String> nombre, 
			ArrayList<String> direccion, ArrayList<String> promedio, ArrayList<String> distancia) {
		super(context, R.layout.adaptador_lista, nombre);
		// TODO Auto-generated constructor stub
		this.id = id;
		this.nombre = nombre;
		this.direccion = direccion;
		this.promedio = promedio;
		this.distancia = distancia;
		this.context = context;
	}


	public View getView(int position, View convertView, ViewGroup paretn)
	{
		LayoutInflater inflater = context.getLayoutInflater();
		final View item = inflater.inflate(R.layout.adaptador_lista, null);
		
		itm_id = id.get(position).toString(); 
		nom = nombre.get(position).toString();
		dir = direccion.get(position).toString();
		prom = promedio.get(position).toString();
		dist = distancia.get(position).toString();
		
		name = (TextView)item.findViewById(R.id.nombre);
		name.setText(nom);
		
		address= (TextView)item.findViewById(R.id.direccion);
		address.setText(dir);
				
		distance = (TextView)item.findViewById(R.id.distance);
		distance.setText(dist);
		
		
		imagen = (ImageView)item.findViewById(R.id.imgItem);
		Thread nt = new Thread() {
			@Override
			public void run() {
				
				try {
					URL imageUrl = new URL("http://200.14.84.19/servtriptour/imagenes/"+itm_id+".jpg");
					HttpURLConnection conn = (HttpURLConnection)imageUrl.openConnection();
					conn.connect();
					Bitmap bitm = BitmapFactory.decodeStream(conn.getInputStream());
					imagen.setImageBitmap(bitm);
				}
				catch (Exception e) 
				{
					// TODO: handle exception
				}
			}
		};
		nt.start();
		
		est1 = (ImageView)item.findViewById(R.id.est1);
		est2 = (ImageView)item.findViewById(R.id.est2);
		est3 = (ImageView)item.findViewById(R.id.est3);
		est4 = (ImageView)item.findViewById(R.id.est4);
		est5 = (ImageView)item.findViewById(R.id.est5);
		
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
		return item;
	}
}
