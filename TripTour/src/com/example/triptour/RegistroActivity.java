package com.example.triptour;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONObject;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Vibrator;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Toast;

public class RegistroActivity extends Activity implements OnClickListener {

	EditText edtMail, edtNick, edtNombre, edtApellido, edtFecha, edtPass, edtConfPass;
	Button enviar;
	RadioButton rdbtnFemenino, rdbtnMasculino;
	String usr_mail, usr_nick, usr_nombre, usr_apellido, usr_sexo, usr_fecha, usr_pass, confPass, 
		   php, res, valido, masculino, femenino;
	List<NameValuePair> params;
	ProgressDialog pDialog;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.registroactivity);
		
		edtMail = (EditText)findViewById(R.id.edtMail);
		edtNick = (EditText)findViewById(R.id.edtNick);
		edtNombre = (EditText)findViewById(R.id.edtNombre);
		edtApellido = (EditText)findViewById(R.id.edtApellido);
		rdbtnMasculino =(RadioButton)findViewById(R.id.rdtbtnMasculino);
		rdbtnFemenino = (RadioButton)findViewById(R.id.rdbtnFemenino);
		edtFecha = (EditText)findViewById(R.id.edtFecha);
		edtPass = (EditText)findViewById(R.id.edtPass);
		edtConfPass = (EditText)findViewById(R.id.edtConfPass);
		        
		enviar = (Button)findViewById(R.id.btnEnviar);
		enviar.setOnClickListener(this);
	}

	@Override
	public void onClick(View arg0) {
		// TODO Auto-generated method stub
		usr_pass = edtPass.getText().toString();
		confPass = edtConfPass.getText().toString();
		if(usr_pass.equals(confPass))
		{
			usr_mail = edtMail.getText().toString();
			usr_nick = edtNick.getText().toString();
			usr_fecha = edtFecha.getText().toString();
			
			if(usr_mail.equals("") || usr_nick.equals("") || usr_fecha.equals(""))
			{
				Vibrator vibrator =(Vibrator)getSystemService(Context.VIBRATOR_SERVICE);
			    vibrator.vibrate(500);
				Toast.makeText(this,"Campos Mail, Nick y Fecha deben ser completados",
						Toast.LENGTH_LONG).show();
			}
			else
			{	
				usr_nombre = edtNombre.getText().toString();
				usr_apellido = edtApellido.getText().toString();
				usr_fecha = edtFecha.getText().toString();
								
				if(rdbtnMasculino.isChecked() == true)
				{
					usr_sexo = "M";
				}
				else if(rdbtnFemenino.isChecked() == true)
				{
					usr_sexo = "F";
				}
				else
				{
					Vibrator vibrator =(Vibrator)getSystemService(Context.VIBRATOR_SERVICE);
				    vibrator.vibrate(200);
					Toast.makeText(RegistroActivity.this,"Seleccionar opcion sexo",
							Toast.LENGTH_LONG).show();
				}
				
				params = new ArrayList<NameValuePair>();
				params.add(new BasicNameValuePair("usr_mail", usr_mail));
				params.add(new BasicNameValuePair("usr_nick", usr_nick));
				params.add(new BasicNameValuePair("usr_nombre", usr_nombre));
				params.add(new BasicNameValuePair("usr_apellido", usr_apellido));
				params.add(new BasicNameValuePair("usr_sexo", usr_sexo));
				params.add(new BasicNameValuePair("usr_fecha_nacimiento", usr_fecha));
				params.add(new BasicNameValuePair("usr_pass", usr_pass));
				params.add(new BasicNameValuePair("pass", usr_pass));
				
				php = "/servtriptour/registro.php";
				pDialog = new ProgressDialog(this);
				
				Thread nt = new Thread() {
					@Override
					public void run() {
						
						try {
							pDialog.setMessage("Sending...");
							pDialog.show();
							EnviarPost enviar = new EnviarPost();
							res = enviar.enviarPost(params, php);
							JSONArray jsonArray = new JSONArray(res);
							for (int i = 0; i < jsonArray.length(); i++)
							{
								JSONObject jsonObject = jsonArray.getJSONObject(i);
							    valido = jsonObject.getString("valido");
							}         
							runOnUiThread(new Runnable() {
								@Override
								public void run() {
									if(valido.equals("mail"))
									{
										Vibrator vibrator =(Vibrator)getSystemService(Context.VIBRATOR_SERVICE);
									    vibrator.vibrate(200);
										Toast.makeText(RegistroActivity.this,"Mail ya registrado",
												Toast.LENGTH_LONG).show();
										pDialog.dismiss();
									}
									else if(valido.equals("nick"))
									{
										Vibrator vibrator =(Vibrator)getSystemService(Context.VIBRATOR_SERVICE);
									    vibrator.vibrate(200);
										Toast.makeText(RegistroActivity.this,"Nick ya fue ocupado",
												Toast.LENGTH_LONG).show();
										pDialog.dismiss();
									}
									else
									{
										registrado();
									}
								}
							});
						    }
						catch (Exception e) {
							// TODO: handle exception
						}
					}
				};
				nt.start();
			}
		}
	}
	
	private void registrado() 
	{
		// TODO Auto-generated method stub
		Intent Main = new Intent(this,MainActivity.class);
		startActivity(Main);
	}
}
