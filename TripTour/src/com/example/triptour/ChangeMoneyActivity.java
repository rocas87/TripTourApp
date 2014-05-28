package com.example.triptour;

import java.text.DecimalFormat;

import android.R.integer;
import android.os.Bundle;
import android.text.Editable;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.app.Activity;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;
import org.apache.http.util.EntityUtils;

public class ChangeMoneyActivity extends Activity implements OnClickListener{
	
	TextView txtCambio;
	EditText edtCantidad;
	Button btnCambio;
	String valor, url, resultado;
	double total, cant;
	Editable data;
	String [] tokenResultado, tokenMoneda;
	DecimalFormat decimales = new DecimalFormat("0.00"); 
	
	protected void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.change_money_activity);
				
		txtCambio = (TextView)findViewById(R.id.txtCambio);
		edtCantidad = (EditText)findViewById(R.id.edtCantidad);
		btnCambio = (Button)findViewById(R.id.btnCambio);
		
		//
		
		btnCambio.setOnClickListener(this);
	}
	
	public String cambio(double cantidad, String origen, String destino) {
		HttpClient httpClient = new DefaultHttpClient();
		HttpContext localContext = new BasicHttpContext();
		HttpResponse response = null;
		cant = Double.parseDouble((edtCantidad.getText().toString()));
		url =  "http://www.webservicex.net/CurrencyConvertor.asmx/ConversionRate?FromCurrency="+origen+"&ToCurrency="+destino;
		
		HttpGet httpget = new HttpGet(url);
		try {
			response = httpClient.execute(httpget, localContext);
			HttpEntity entity = response.getEntity();
			resultado = EntityUtils.toString(entity, "UTF-8");
			tokenResultado = resultado.split(">");
			tokenMoneda = tokenResultado[2].split("<");
			total = Double.parseDouble(tokenMoneda[0])*cant; 
		    resultado = decimales.format(total); 
			
		} catch (Exception e) {

		}
		return resultado;
	}

	@Override
	public void onClick(View arg0) {
		// TODO Auto-generated method stub
		Thread nt = new Thread()
		{
			public void run()
			{
				try
				{
					valor = cambio(100, "USD", "CLP");
					runOnUiThread(new Runnable()
					{
						public void run()
						{
							txtCambio.setText(valor);
						}
					});
				}
				catch(Exception e)
				{
					//nada
				}
			}
		};
		nt.start();
	}
}