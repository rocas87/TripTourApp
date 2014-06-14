package com.example.triptour;

import java.util.ArrayList;

import android.app.Activity;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class AdaptadorComentarios extends ArrayAdapter
{
	Activity context;
	ArrayList<String> usr_nick = new ArrayList<String>();
	ArrayList<String> fechaPost = new ArrayList<String>();
	ArrayList<String> comentario = new ArrayList<String>();
	ArrayList<String> itm_rating = new ArrayList<String>();
	TextView nick, fecha, coment;
	ImageView est1, est2, est3, est4, est5;
	
		//@SuppressWarnings("unchecked")
		public AdaptadorComentarios(Activity context, ArrayList<String> usr_nick, 
				ArrayList<String> fechaPost, ArrayList<String> comentario, ArrayList<String> itm_rating) 
		{
			super(context, R.layout.adaptador_post, usr_nick);
			// TODO Auto-generated constructor stub
			this.usr_nick = usr_nick;
			this.fechaPost = fechaPost;
			this.comentario = comentario;
			this.itm_rating = itm_rating;
			this.context = context;
		}


		public View getView(int position, View convertView, ViewGroup paretn)
		{
			LayoutInflater inflater = context.getLayoutInflater();
			final View post = inflater.inflate(R.layout.adaptador_post, null);
			
			nick = (TextView)post.findViewById(R.id.txtUsrNick);
			fecha = (TextView)post.findViewById(R.id.txtFecha);
			coment = (TextView)post.findViewById(R.id.txtPost);
			
			nick.setText(usr_nick.get(position).toString());
			fecha.setText(fechaPost.get(position).toString());
			coment.setText(comentario.get(position).toString());
			
			est1 = (ImageView)post.findViewById(R.id.est1);
			est2 = (ImageView)post.findViewById(R.id.est2);
			est3 = (ImageView)post.findViewById(R.id.est3);
			est4 = (ImageView)post.findViewById(R.id.est4);
			est5 = (ImageView)post.findViewById(R.id.est5);
			
			if(itm_rating.get(position).toString().equals("4"))
			{
				est5.setImageURI(Uri.parse("drawable/preview.jpg"));
			}
			else if(itm_rating.get(position).toString().equals("3"))
			{
				est4.setImageURI(Uri.parse("drawable/preview.jpg"));
				est5.setImageURI(Uri.parse("drawable/preview.jpg"));
			}
			else if(itm_rating.get(position).toString().equals("2"))
			{
				est3.setImageURI(Uri.parse("drawable/preview.jpg"));
				est4.setImageURI(Uri.parse("drawable/preview.jpg"));
				est5.setImageURI(Uri.parse("drawable/preview.jpg"));
			}
			else if(itm_rating.get(position).toString().equals("1"))
			{
				est2.setImageURI(Uri.parse("drawable/preview.jpg"));
				est3.setImageURI(Uri.parse("drawable/preview.jpg"));
				est4.setImageURI(Uri.parse("drawable/preview.jpg"));
				est5.setImageURI(Uri.parse("drawable/preview.jpg"));
			}
			return post;
		}

}
