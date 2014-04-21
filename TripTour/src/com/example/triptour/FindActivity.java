package com.example.triptour;

import java.util.ArrayList;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class FindActivity extends Activity{

	TextView txtUsuario, Seleccionado;
	ListView resultado;
	ArrayList<String> datos = new ArrayList<String>();
	String usuario;
	
	// TODO Auto-generated method stub
		@Override
		protected void onCreate(Bundle savedInstanceState) {
			super.onCreate(savedInstanceState);
			setContentView(R.layout.find_activity);
			
			txtUsuario = (TextView)findViewById(R.id.txtUsuario);
			resultado = (ListView)findViewById(R.id.resultado);
			
			Bundle find = getIntent().getExtras();
			usuario = find.getString("user");
			txtUsuario.setText(usuario);
			
			datos.add("Lugar1");
			datos.add("Lugar2");
			datos.add("Lugar3");
			datos.add("Lugar4");
			datos.add("Lugar5");
			datos.add("Lugar6");
			datos.add("Lugar7");
			datos.add("Lugar8");
			datos.add("Lugar9");
			datos.add("Lugar10");
			ArrayAdapter<String> adaptador = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,datos);
			resultado.setAdapter(adaptador);
			resultado.setOnItemClickListener(new OnItemClickListener()
			{
				@Override
				public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
					// TODO Auto-generated method stub
					Toast.makeText(FindActivity.this,"Seleccionaste: "+datos.get(position),
							Toast.LENGTH_LONG).show();
				}
			});
		}
			
}
