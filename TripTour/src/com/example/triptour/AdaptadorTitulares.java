package com.example.triptour;

import java.util.ArrayList;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class AdaptadorTitulares extends ArrayAdapter {

	Activity context;
	ArrayList<String> titulares = new ArrayList<String>();
	ArrayList<String> descripcion = new ArrayList<String>();
	String tit,descrip;
	
	
	//@SuppressWarnings("unchecked")
	public AdaptadorTitulares(Activity context, ArrayList<String> titulares, ArrayList<String> descripcion) {
		super(context, R.layout.adaptador_lista,titulares);
		// TODO Auto-generated constructor stub
		this.titulares = titulares;
		this.descripcion = descripcion;
		this.context = context;
	}


	public View getView(int position, View convertView, ViewGroup paretn)
	{
		LayoutInflater inflater = context.getLayoutInflater();
		View item = inflater.inflate(R.layout.adaptador_lista, null);
		
		tit = titulares.get(position).toString();
		descrip = descripcion.get(position).toString();
		
		TextView titulo = (TextView)item.findViewById(R.id.titulo);
		titulo.setText(tit);
		
		TextView desc = (TextView)item.findViewById(R.id.desc);
		desc.setText(descrip);
		
		return item;
	}
}
