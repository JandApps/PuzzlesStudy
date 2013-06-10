package com.gmail.leonidandand.puzzlesstudy;

import java.io.File;
import java.util.Random;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.gmail.leonidandand.puzzlesstudy.difficulty_level.DifficultyLevel;
import com.gmail.leonidandand.puzzlesstudy.difficulty_level.DimensionLoader;

public class MainActivity extends Activity {

	private static final int[] imageIds = new int[] {
		R.drawable.cut, R.drawable.google, R.drawable.kote,
		R.drawable.nature1, R.drawable.nature2, R.drawable.nature3,
		R.drawable.nature4, R.drawable.nature5, R.drawable.nature6,
		R.drawable.woman, R.drawable.woman2, R.drawable.woman5,
		R.drawable.pesik, R.drawable.pesik_2,
	};
	
	private static final int PICK_IMAGE = 42345;

	private int curImagePos = 0;
	private Random random = new Random();
	private PuzzlesView puzzlesView;
	private DimensionLoader dimensionLoader;
	private DifficultyLevel difficultyLevel = DifficultyLevel.EASY;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		puzzlesView = new PuzzlesView(this);
		puzzlesView.setBackgroundColor(Color.BLACK);
		puzzlesView.addOnGameFinishedListener(new OnGameFinishedListener() {
			@Override
			public void onGameFinished() {
				Toast.makeText(MainActivity.this, "Excellent!", Toast.LENGTH_SHORT).show();
			}
		});
		setContentView(puzzlesView);
		dimensionLoader = new DimensionLoader(getResources());
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
			onShowImage();
			break;
			
		case R.id.loadAndShowImage:
			onLoadAndShowImage();
			break;
		}
		return true;
	}

	private void onShowImage() {
		curImagePos = getRandomPositionOfImage();
		Bitmap bitmap = BitmapFactory.decodeResource(getResources(), imageIds[curImagePos]);
		puzzlesView.setBitmap(bitmap, dimensionLoader.dimension(difficultyLevel));
	}

	public int getRandomPositionOfImage() {
		int randomPosition = random.nextInt(imageIds.length);
		return ((imageIds.length > 1 && randomPosition != curImagePos)
					? randomPosition
					: getRandomPositionOfImage());
	}

	private void onLoadAndShowImage() {
		Intent intent = new Intent();
		intent.setType("image/*");
		intent.setAction(Intent.ACTION_GET_CONTENT);
		startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode == RESULT_OK && requestCode == PICK_IMAGE && data != null) {
			Uri uri = data.getData();
			if (uri != null) {
				onImagePicked(uri);
			}
		}
	}

	private void onImagePicked(Uri uri) {
		String imageFilePath = imageFilePathByUri(uri);
		try {
			Bitmap bitmap = getBitmapByPath(imageFilePath);
			puzzlesView.setBitmap(bitmap, dimensionLoader.dimension(difficultyLevel));
		} catch (Exception e) {
			String message = "Cannot load image. Please, choose other image.\n" + e.getMessage();
			Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
		}
	}

	private String imageFilePathByUri(Uri uri) {
		String[] projection = new String[] { android.provider.MediaStore.Images.ImageColumns.DATA };
        Cursor cursor = getContentResolver().query(uri, projection, null, null, null);
        cursor.moveToFirst();
        String imageFilePath = cursor.getString(0);
        cursor.close();
        return imageFilePath;
	}
	
	private Bitmap getBitmapByPath(String imageFilePath) {
		File imgFile = new File(imageFilePath);
		return BitmapFactory.decodeFile(imgFile.getAbsolutePath());
	}

}
