package com.example.triptour;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;

import android.app.Activity;
import android.util.Log;

public class Climate extends Activity {
	
	String url, resultado, region, direccion, data, separa, respuesta;
	String[] tokens, tokensRegion;
	
	public String climaURL(final String latitud, final String longitud)
	{
		Thread tr = new Thread()
		{
			@Override
			public void run()
			{
				try
				{
					data = ubicacion(latitud,longitud);
					JSONObject jsonObject = new JSONObject(data).getJSONArray("results").getJSONObject(0);
					direccion = jsonObject.getString("formatted_address");
					tokens = direccion.split(",");
					separa = tokens[2];
					tokensRegion = separa.split(" ");
					region = tokensRegion[2];
					Log.e("token", region);
					respuesta = meteoChile(region);
				}
				catch (Exception e)
				{

				}
			}	
		};
			tr.start();
			while (respuesta==null)
			{
				
			}
		return respuesta;
	}
	
	public String ubicacion(String latitud, String longitud) {
		HttpClient httpClient = new DefaultHttpClient();
		HttpContext localContext = new BasicHttpContext();
		HttpResponse response = null;
		url =  "http://maps.googleapis.com/maps/api/geocode/json?latlng="+latitud+","+longitud+"&sensor=false";
		Log.e("token",url);
		HttpGet httpget = new HttpGet(url);
		try {
			response = httpClient.execute(httpget, localContext);
			HttpEntity entity = response.getEntity();
			resultado = EntityUtils.toString(entity, "UTF-8");
			
		} catch (Exception e) {

		}
		return resultado;
	}
	
	public String meteoChile(String region)
	{
		Log.e("token","meteo");
		if(region.equals("Metropolitan"))
		{
			Log.e("token","meteo1");
			return "http://www.meteochile.cl/reg05m.php";
		}
		else
		{
			Log.e("token","meteo2");
			return "http://www.meteochile.cl";
		}
		
	}
}
