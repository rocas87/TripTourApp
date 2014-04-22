package com.example.triptour;

import java.util.ArrayList;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class FindActivity extends Activity{

	TextView txtUsuario, Seleccionado;
	ListView lista;
	ArrayList<String> titulares = new ArrayList<String>();
	ArrayList<String> descripcion = new ArrayList<String>();
	String tit,desc,titaux;
	String usuario;

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
			
			for (int i=0;i<21;i++)
			{
				tit = "Titulo"+i;
				desc = "Descripcion"+i;
				titulares.add(tit);
				descripcion.add(desc);
			}
				
			
			AdaptadorTitulares adap = new AdaptadorTitulares(this,titulares,descripcion);
			lista.setAdapter(adap);
			lista.setOnItemClickListener(new OnItemClickListener()
			{
				@Override
				public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
					// TODO Auto-generated method stub
					Toast.makeText(FindActivity.this,"Seleccionaste: "+titulares.get(position),
							Toast.LENGTH_LONG).show();
				}
			});
		}
}