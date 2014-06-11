package com.example.triptour;

import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class AdaptadorTitulares extends ArrayAdapter {

	Activity context;
	ArrayList<String> nombre = new ArrayList<String>();
	ArrayList<String> direccion = new ArrayList<String>();
	ArrayList<String> promedio = new ArrayList<String>();
	ArrayList<String> distancia = new ArrayList<String>();
	String nom, dir, prom, dist, lat;
	TextView name, address, average, distance;
	
	
	//@SuppressWarnings("unchecked")
	public AdaptadorTitulares(Activity context, ArrayList<String> nombre, 
			ArrayList<String> direccion, ArrayList<String> promedio, ArrayList<String> distancia) {
		super(context, R.layout.adaptador_lista, nombre);
		// TODO Auto-generated constructor stub
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
		
		nom = nombre.get(position).toString();
		dir = direccion.get(position).toString();
		prom = promedio.get(position).toString();
		dist = distancia.get(position).toString();
		
		name = (TextView)item.findViewById(R.id.nombre);
		name.setText(nom);
		
		address= (TextView)item.findViewById(R.id.direccion);
		address.setText(dir);
		
		average = (TextView)item.findViewById(R.id.average);
		average.setText(prom);
		
		distance = (TextView)item.findViewById(R.id.distance);
		distance.setText(dist);

		Thread nt = new Thread() {
			@Override
			public void run() {
				
				try {
					ImageView imagen = (ImageView)item.findViewById(R.id.imgItem);
					URL imageUrl = new URL("http://200.14.84.19/servtriptour/imagenes/ic_launcher.png");
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
		return item;
	}
}
