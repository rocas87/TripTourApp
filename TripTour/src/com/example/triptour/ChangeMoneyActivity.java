package com.example.triptour;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import android.os.Bundle;
import android.text.Editable;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.app.Activity;
import android.app.ProgressDialog;

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
	String valor, url, resultado, origen, destino;
	double total, cant;
	Editable data;
	String [] tokenResultado, tokenMoneda, tokenOrigen, tokenDestino;
	DecimalFormat decimales = new DecimalFormat("0.00");
	ProgressDialog pDialog;
	private Spinner spOrigen, spDestino;
	private List<String> lista = new ArrayList<String>();
	ArrayAdapter<String> adaptadorOrigen, adaptadorDestino;
	
	protected void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.change_money_activity);
				
		txtCambio = (TextView)findViewById(R.id.txtCambio);
		edtCantidad = (EditText)findViewById(R.id.edtCantidad);
		btnCambio = (Button)findViewById(R.id.btnCambio);
				
		//Valores de cambio
		lista.add("USD-U.S. Dollar");
		lista.add("EUR-Euro");
		lista.add("CLP-Chilean Peso");
		lista.add("AFA-Afghanistan Afghani");
		lista.add("ALL-Albanian Lek");
		lista.add("DZD-Algerian Dinar");
		lista.add("ARS-Argentine Peso");
		lista.add("AWG-Aruba Florin");
		lista.add("AUD-Australian Dollar");
		lista.add("BSD-Bahamian Dollar");
		lista.add("BHD-Bahraini Dinar");
		lista.add("BDT-Bangladesh Taka");
		lista.add("BBD-Barbados Dollar");
		lista.add("BZD-Belize Dollar");
		lista.add("BMD-Bermuda Dollar");
		lista.add("BTN-Bhutan Ngultrum");
		lista.add("BOB-Bolivian Boliviano");
		lista.add("BWP-Botswana Pula");
		lista.add("BRL-Brazilian Real");
		lista.add("GBP-British Pound");
		lista.add("BND-Brunei Dollar");
		lista.add("BIF-Burundi Franc");
		lista.add("XOF-CFA Franc (BCEAO)");
		lista.add("XAF-CFA Franc (BEAC)");
		lista.add("KHR-Cambodia Riel");
		lista.add("CAD-Canadian Dollar");
		lista.add("CVE-Cape Verde Escudo");
		lista.add("KYD-Cayman Islands Dollar");
		lista.add("CNY-Chinese Yuan");
		lista.add("COP-Colombian Peso");
		lista.add("KMF-Comoros Franc");
		lista.add("CRC-Costa Rica Colon");
		lista.add("HRK-Croatian Kuna");
		lista.add("CUP-Cuban Peso");
		lista.add("CYP-Cyprus Pound");
		lista.add("CZK-Czech Koruna");
		lista.add("DKK-Danish Krone");
		lista.add("DJF-Dijibouti Franc");
		lista.add("DOP-Dominican Peso");
		lista.add("XCD-East Caribbean Dollar");
		lista.add("EGP-Egyptian Pound");
		lista.add("SVC-El Salvador Colon");
		lista.add("EEK-Estonian Kroon");
		lista.add("ETB-Ethiopian Birr");
		lista.add("FKP-Falkland Islands Pound");
		lista.add("GMD-Gambian Dalasi");
		lista.add("GHC-Ghanian Cedi");
		lista.add("GIP-Gibraltar Pound");
		lista.add("XAU-Gold Ounces");
		lista.add("GTQ-Guatemala Quetzal");
		lista.add("GNF-Guinea Franc");
		lista.add("GYD-Guyana Dollar");
		lista.add("HTG-Haiti Gourde");
		lista.add("HNL-Honduras Lempira");
		lista.add("HKD-Hong Kong Dollar");
		lista.add("HUF-Hungarian Forint");
		lista.add("ISK-Iceland Krona");
		lista.add("INR-Indian Rupee");
		lista.add("IDR-Indonesian Rupiah");
		lista.add("IQD-Iraqi Dinar");
		lista.add("ILS-Israeli Shekel");
		lista.add("JMD-Jamaican Dollar");
		lista.add("JPY-Japanese Yen");
		lista.add("JOD-Jordanian Dinar");
		lista.add("KZT-Kazakhstan Tenge");
		lista.add("KES-Kenyan Shilling");
		lista.add("KRW-Korean Won");
		lista.add("KWD-Kuwaiti Dinar");
		lista.add("LAK-Lao Kip");
		lista.add("LVL-Latvian Lat");
		lista.add("LBP-Lebanese Pound");
		lista.add("LSL-Lesotho Loti");
		lista.add("LRD-Liberian Dollar");
		lista.add("LYD-Libyan Dinar");
		lista.add("LTL-Lithuanian Lita");
		lista.add("MOP-Macau Pataca");
		lista.add("MKD-Macedonian Denar");
		lista.add("MGF-Malagasy Franc");
		lista.add("MWK-Malawi Kwacha");
		lista.add("MYR-Malaysian Ringgit");
		lista.add("MVR-Maldives Rufiyaa");
		lista.add("MTL-Maltese Lira");
		lista.add("MRO-Mauritania Ougulya");
		lista.add("MUR-Mauritius Rupee");
		lista.add("MXN-Mexican Peso");
		lista.add("MDL-Moldovan Leu");
		lista.add("MNT-Mongolian Tugrik");
		lista.add("MAD-Moroccan Dirham");
		lista.add("MZM-Mozambique Metical");
		lista.add("MMK-Myanmar Kyat");
		lista.add("NAD-Namibian Dollar");
		lista.add("NPR-Nepalese Rupee");
		lista.add("ANG-Neth Antilles Guilder");
		lista.add("NZD-New Zealand Dollar");
		lista.add("NIO-Nicaragua Cordoba");
		lista.add("NGN-Nigerian Naira");
		lista.add("KPW-North Korean Won");
		lista.add("NOK-Norwegian Krone");
		lista.add("OMR-Omani Rial");
		lista.add("XPF-Pacific Franc");
		lista.add("PKR-Pakistani Rupee");
		lista.add("XPD-Palladium Ounces");
		lista.add("PAB-Panama Balboa");
		lista.add("PGK-Papua New Guinea Kina");
		lista.add("PYG-Paraguayan Guarani");
		lista.add("PEN-Peruvian Nuevo Sol");
		lista.add("PHP-Philippine Peso");
		lista.add("XPT-Platinum Ounces");
		lista.add("PLN-Polish Zloty");
		lista.add("QAR-Qatar Rial");
		lista.add("ROL-Romanian Leu");
		lista.add("RUB-Russian Rouble");
		lista.add("WST-Samoa Tala");
		lista.add("STD-Sao Tome Dobra");
		lista.add("SAR-Saudi Arabian Riyal");
		lista.add("SCR-Seychelles Rupee");
		lista.add("SLL-Sierra Leone Leone");
		lista.add("XAG-Silver Ounces");
		lista.add("SGD-Singapore Dollar");
		lista.add("SKK-Slovak Koruna");
		lista.add("SIT-Slovenian Tolar");
		lista.add("SBD-Solomon Islands Dollar");
		lista.add("SOS-Somali Shilling");
		lista.add("ZAR-South African Rand");
		lista.add("LKR-Sri Lanka Rupee");
		lista.add("SHP-St Helena Pound");
		lista.add("SDD-Sudanese Dinar");
		lista.add("SRG-Surinam Guilder");
		lista.add("SZL-Swaziland Lilageni");
		lista.add("SEK-Swedish Krona");
		lista.add("TRY-Turkey Lira");
		lista.add("CHF-Swiss Franc");
		lista.add("SYP-Syrian Pound");
		lista.add("TWD-Taiwan Dollar");
		lista.add("TZS-Tanzanian Shilling");
		lista.add("THB-Thai Baht");
		lista.add("TOP-Tonga Pa'anga");
		lista.add("TTD-Trinidad&Tobago Dollar");
		lista.add("TND-Tunisian Dinar");
		lista.add("TRL-Turkish Lira");
		lista.add("USD-U.S. Dollar");
		lista.add("AED-UAE Dirham");
		lista.add("UGX-Ugandan Shilling");
		lista.add("UAH-Ukraine Hryvnia");
		lista.add("UYU-Uruguayan New Peso");
		lista.add("VUV-Vanuatu Vatu");
		lista.add("VEB-Venezuelan Bolivar");
		lista.add("VND-Vietnam Dong");
		lista.add("YER-Yemen Riyal");
		lista.add("YUM-Yugoslav Dinar");
		lista.add("ZMK-Zambian Kwacha");
		lista.add("ZWD-Zimbabwe Dollar");
		//Fin valores
		
		tipoCambio();
		cambioDestino();
		//
		spOrigen.setOnItemSelectedListener(new OnItemSelectedListener()
		{

			@Override
			public void onItemSelected(AdapterView<?> arg0, View arg1,
					int arg2, long arg3) {
				// TODO Auto-generated method stub
				tokenOrigen = (arg0.getItemAtPosition(arg2).toString()).split("-");
				origen = tokenOrigen[0]; 
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
				// TODO Auto-generated method stub
				
			}
			
		});
		
		spDestino.setOnItemSelectedListener(new OnItemSelectedListener()
		{

			@Override
			public void onItemSelected(AdapterView<?> arg, View arg1,
					int arg2, long arg3) {
				// TODO Auto-generated method stub
				tokenDestino = (arg.getItemAtPosition(arg2).toString()).split("-");
				destino = tokenDestino[0]; 
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
				// TODO Auto-generated method stub
				
			}
			
		});
		
		btnCambio.setOnClickListener(this);
	}
	
	public String cambio(double cantidad, String org, String dest) {
		HttpClient httpClient = new DefaultHttpClient();
		HttpContext localContext = new BasicHttpContext();
		HttpResponse response = null;
		cant = Double.parseDouble((edtCantidad.getText().toString()));
		
		url =  "http://www.webservicex.net/CurrencyConvertor.asmx/ConversionRate?FromCurrency="+org+"&ToCurrency="+dest;
		
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
		return resultado+" "+dest;
	}

	@Override
	public void onClick(View arg0) {
		// TODO Auto-generated method stub
		pDialog = new ProgressDialog(this);
		pDialog.setMessage("Calculando...");
		pDialog.show();
		Thread nt = new Thread()
		{
			public void run()
			{
				try
				{
					valor = cambio(Double.parseDouble((edtCantidad.getText().toString())), origen, destino);
					runOnUiThread(new Runnable()
					{
						public void run()
						{
							txtCambio.setText(valor);
							pDialog.dismiss();
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
	
	private void tipoCambio()
	{
		spOrigen = (Spinner)findViewById(R.id.origen);
		spOrigen = (Spinner)this.findViewById(R.id.origen);
		adaptadorOrigen = new ArrayAdapter<String>
			(this,android.R.layout.simple_spinner_item, lista);
		adaptadorOrigen.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spOrigen.setAdapter(adaptadorOrigen);
	}
	
	private void cambioDestino()
	{
		spDestino = (Spinner)findViewById(R.id.destino);
		spDestino = (Spinner)this.findViewById(R.id.destino);
		adaptadorDestino = new ArrayAdapter<String>
			(this,android.R.layout.simple_spinner_item, lista);
		adaptadorDestino.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spDestino.setAdapter(adaptadorDestino);
	}
}