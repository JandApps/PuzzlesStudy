package com.gmail.leonidandand.puzzlesstudy;

import java.util.Random;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

public class MainActivity extends Activity {

	private final static int[] imageIds = new int[] { R.drawable.cut,
			R.drawable.google, R.drawable.kote, R.drawable.nature1,
			R.drawable.nature2, R.drawable.nature3, R.drawable.nature4,
			R.drawable.nature5, R.drawable.nature6, R.drawable.pesik,
			R.drawable.pesik_2, R.drawable.woman, R.drawable.woman2,
			R.drawable.woman5 };

	private int currentImageId = 0;
	private Random random = new Random();

	private PuzzlesView drawer;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		drawer = new PuzzlesView(this);
		setContentView(drawer);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.showImage:
			currentImageId = getRandomImageId();
			Bitmap bitmap = BitmapFactory.decodeResource(getResources(), imageIds[currentImageId]);
			drawer.setBitmap(bitmap);
			break;
		}
		return true;
	}

	public int getRandomImageId() {
		int nextId = random.nextInt(imageIds.length);
		return (nextId != currentImageId ? nextId : getRandomImageId());
	}

}
