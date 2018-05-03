package co.appguardian.peerfy.services.util;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;

import java.io.ByteArrayOutputStream;

public class BitmapToBase64 {
	
	/**
	 * 
	 * @param image
	 * @return
	 */
	public static String encodeTobase64(Bitmap image)
	{
	    Bitmap immagex=image;
	    ByteArrayOutputStream baos = new ByteArrayOutputStream();  
	    immagex.compress(Bitmap.CompressFormat.JPEG, 0, baos);
	    byte[] b = baos.toByteArray();
	    String imageEncoded = Base64.encodeToString(b,Base64.DEFAULT);
	    return imageEncoded;
	}
	
	/**
	 * 
	 * @param input
	 * @return
	 */
	public Bitmap decodeBase64(String input) 
	{
	    byte[] decodedByte = Base64.decode(input, 0);
	    return BitmapFactory.decodeByteArray(decodedByte, 0, decodedByte.length); 
	}
}
