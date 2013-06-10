package com.gmail.leonidandand.puzzlesstudy;

import java.util.Random;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.gmail.leonidandand.puzzlesstudy.difficulty_level.DifficultyLevel;
import com.gmail.leonidandand.puzzlesstudy.difficulty_level.DimensionLoader;

public class MainActivity extends Activity {

	private final static int[] imageIds = new int[] {
		R.drawable.cut, R.drawable.google, R.drawable.kote,
		R.drawable.nature1, R.drawable.nature2, R.drawable.nature3,
		R.drawable.nature4, R.drawable.nature5, R.drawable.nature6,
		R.drawable.woman, R.drawable.woman2, R.drawable.woman5,
		R.drawable.pesik, R.drawable.pesik_2,
	};

	private int curImagePos = 0;
	private Random random = new Random();
	private PuzzlesView puzzlesView;
	private DimensionLoader dimensionLoader;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		puzzlesView = new PuzzlesView(this);
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
			showImage();
			break;
		}
		return true;
	}

	private void showImage() {
		curImagePos = getRandomPositionOfImage();
		Bitmap bitmap = BitmapFactory.decodeResource(getResources(), imageIds[curImagePos]);
		puzzlesView.setBitmap(bitmap, dimensionLoader.dimension(DifficultyLevel.HARD));
	}

	public int getRandomPositionOfImage() {
		int randomPosition = random.nextInt(imageIds.length);
		return ((imageIds.length > 1 && randomPosition != curImagePos)
					? randomPosition
					: getRandomPositionOfImage());
	}

}
