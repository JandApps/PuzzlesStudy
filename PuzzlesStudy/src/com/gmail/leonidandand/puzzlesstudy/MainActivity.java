package com.gmail.leonidandand.puzzlesstudy;

import java.io.File;
import java.util.Random;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import com.gmail.leonidandand.puzzlesstudy.difficulty.DimensionLoader;
import com.gmail.leonidandand.puzzlesstudy.utils.Dimension;

public class MainActivity extends Activity {

	private static final int[] imageIds = new int[] {
		R.drawable.cut, R.drawable.kote, R.drawable.pesik,
		R.drawable.woman, R.drawable.leopard1
	};
	private static final String[] difficultyNames = new String[] { "Easy", "Medium", "Hard" };
	
	private static final int PICK_IMAGE = 42345;

	private int curImagePos = 0;
	private Bitmap currentBitmap;
	private Random random = new Random();
	private PuzzlesView puzzlesView;
	private Spinner difficultyPicker;
	private Button mixButton;
	private DimensionLoader dimensionLoader;


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		ResourceReader.init(getResources());
		setContentView(R.layout.activity_main);
		puzzlesView = (PuzzlesView) findViewById(R.id.puzzlesView);
		puzzlesView.addOnGameFinishedListener(new OnGameFinishedListener() {
			@Override
			public void onGameFinished() {
				Toast.makeText(MainActivity.this, "Excellent!", Toast.LENGTH_SHORT).show();
			}
		});
		dimensionLoader = new DimensionLoader(getResources());
		
		initSpinner();
		
		findViewById(R.id.pickImageButton).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				onPickAndShowImage();
			}
		});
		
		findViewById(R.id.randomImageButton).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				onShowRandomImage();
			}
		});
		
		mixButton = (Button) findViewById(R.id.mixButton);
		mixButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				puzzlesView.mix();
			}
		});
		mixButton.setEnabled(false);
	}

	private void initSpinner() {
		difficultyPicker = (Spinner) findViewById(R.id.difficultyPicker);
        ArrayAdapter<String> adapter= new ArrayAdapter<String>(
        		this, android.R.layout.simple_spinner_item, difficultyNames
        );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        difficultyPicker.setAdapter(adapter);
        difficultyPicker.setSelection(0);
	}
	
	private void onShowRandomImage() {
		curImagePos = getRandomPositionOfImage();
		try {
			Bitmap bitmap = BitmapFactory.decodeResource(getResources(), imageIds[curImagePos]);
			setBitmap(bitmap);
		} catch (OutOfMemoryError e) {
			Toast.makeText(this, "Sorry. Image too big. Please, pick other image.", Toast.LENGTH_SHORT)
				 .show();
		}
	}
	
	private void setBitmap(Bitmap bitmap) {
		setCurrentBitmap(bitmap);
		int position = difficultyPicker.getSelectedItemPosition();
		if (position == AdapterView.INVALID_POSITION) {
			Toast.makeText(this, "Please, pick difficulty", Toast.LENGTH_SHORT).show();
		} else {
			Dimension dim = dimensionLoader.dimension(difficultyNames[position]);
			puzzlesView.set(bitmap, dim);
			mixButton.setEnabled(true);
		}
	}

	private void setCurrentBitmap(Bitmap bitmap) {
		if (currentBitmap != null && !currentBitmap.equals(bitmap)) {
			currentBitmap.recycle();
		}
		currentBitmap = bitmap;
	}

	public int getRandomPositionOfImage() {
		int randomPosition = random.nextInt(imageIds.length);
		return ((imageIds.length > 1 && randomPosition != curImagePos)
					? randomPosition
					: getRandomPositionOfImage());
	}

	private void onPickAndShowImage() {
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
			setBitmap(bitmap);
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
