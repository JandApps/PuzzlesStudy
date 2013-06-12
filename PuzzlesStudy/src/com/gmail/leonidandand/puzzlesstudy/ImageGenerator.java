package com.gmail.leonidandand.puzzlesstudy;

import java.util.Random;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

public class ImageGenerator {
	private static final int[] imageIds = new int[] {
		R.drawable.cut, R.drawable.kote, R.drawable.pesik,
		R.drawable.woman, R.drawable.leopard1
	};
	
	private static ImageGenerator singleInstance = null;
	
	public static ImageGenerator getInstance() {
		if (singleInstance == null) {
			singleInstance = new ImageGenerator();
		}
		return singleInstance;
	}
	
	private Random random = new Random();
	private int currentImagePos = -1;
	private Bitmap currentBitmap;
	private Resources resources;
	
	private ImageGenerator() {
		// do nothing
	}
	
	public void setResources(Resources resources) {
		this.resources = resources;
	}

	public Bitmap randomImage() {
		releasePreviousImageResources();
		currentImagePos = getRandomPositionOfImage();
		currentBitmap = BitmapFactory.decodeResource(resources, imageIds[currentImagePos]);
		return currentBitmap;
	}

	private int getRandomPositionOfImage() {
		int randomPosition = random.nextInt(imageIds.length);
		return ((imageIds.length > 1 && randomPosition != currentImagePos)
					? randomPosition
					: getRandomPositionOfImage());
	}

	private void releasePreviousImageResources() {
		if (currentBitmap != null) {
			currentBitmap.recycle();
		}
	}
	
	
}
