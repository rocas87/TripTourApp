package com.example.triptour;

import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import com.google.android.gms.maps.GoogleMap.InfoWindowAdapter;
import com.google.android.gms.maps.model.Marker;

public class PopupAdapter implements InfoWindowAdapter {
  LayoutInflater inflater=null;

  TextView title, promedio, distancia;
  ImageView est1, est2, est3, est4, est5;
  String[] tokenRes;
 public PopupAdapter(LayoutInflater inflater) {
	 this.inflater=inflater;
  }
  @Override
  public View getInfoWindow(Marker marker) {
    return(null);
  }

  @Override
  public View getInfoContents(Marker marker) {
    View popup=inflater.inflate(R.layout.popup, null);

    title = (TextView)popup.findViewById(R.id.title);
    distancia = (TextView)popup.findViewById(R.id.distancia);
    est1 = (ImageView)popup.findViewById(R.id.est1);
	est2 = (ImageView)popup.findViewById(R.id.est2);
	est3 = (ImageView)popup.findViewById(R.id.est3);
	est4 = (ImageView)popup.findViewById(R.id.est4);
	est5 = (ImageView)popup.findViewById(R.id.est5);
	
    tokenRes = marker.getSnippet().split("/");
    
    title.setText(marker.getTitle());
    distancia.setText(tokenRes[1]);
    	
	if(tokenRes[0].equals("4"))
	{
		est5.setImageURI(Uri.parse("drawable/preview.jpg"));
	}
	else if(tokenRes[0].equals("3"))
	{
		est4.setImageURI(Uri.parse("drawable/preview.jpg"));
		est5.setImageURI(Uri.parse("drawable/preview.jpg"));
	}
	else if(tokenRes[0].equals("2"))
	{
		est3.setImageURI(Uri.parse("drawable/preview.jpg"));
		est4.setImageURI(Uri.parse("drawable/preview.jpg"));
		est5.setImageURI(Uri.parse("drawable/preview.jpg"));
	}
	else if(tokenRes[0].equals("1"))
	{
		est2.setImageURI(Uri.parse("drawable/preview.jpg"));
		est3.setImageURI(Uri.parse("drawable/preview.jpg"));
		est4.setImageURI(Uri.parse("drawable/preview.jpg"));
		est5.setImageURI(Uri.parse("drawable/preview.jpg"));
	}
	
    return(popup);
  }
}