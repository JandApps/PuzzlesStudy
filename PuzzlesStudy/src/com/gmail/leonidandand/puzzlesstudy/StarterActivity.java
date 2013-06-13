package com.gmail.leonidandand.puzzlesstudy;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.gmail.leonidandand.puzzlesstudy.utils.Dimension;

public class StarterActivity extends Activity {
	private TextView textView;
	private ImageView imageView;
	private PuzzlesView puzzlesView;
	private boolean gameStarted;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_starter);
		ResourceReader.init(getResources());
		findViews();
		setPreview();
		setListeners();
	}

	private void findViews() {
		puzzlesView = (PuzzlesView) findViewById(R.id.puzzlesView);
		textView = (TextView) findViewById(R.id.textView);
		imageView = (ImageView) findViewById(R.id.imageView);
	}
	
	private void setPreview() {
		Bitmap bitmap = (Bitmap) SaverLoader.load("bitmap");
		imageView.setBackgroundDrawable(new BitmapDrawable(bitmap));
	}

	private void setListeners() {
		imageView.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				onStartGame();
			}
		});
		puzzlesView.addOnPuzzleAssembledListener(new OnPuzzleAssembledListener() {
			@Override
			public void onGameFinished() {
				onPuzzleAssembled();
			}
		});
	}

	private void onStartGame() {
		resetScreen(View.INVISIBLE, View.VISIBLE);
		if (!gameStarted) {
			setPuzzles();
		}
		gameStarted = true;
	}

	private void resetScreen(int previewVisibility, int puzzlesVisibility) {
		changeVisibility(previewVisibility, textView, imageView);
		changeVisibility(puzzlesVisibility, puzzlesView);		
	}
	
	private void setPuzzles() {
		Bitmap bitmap = (Bitmap) SaverLoader.load("bitmap");
		Dimension dim = (Dimension) SaverLoader.load("dim");
		puzzlesView.set(bitmap, dim);
	}

	private void changeVisibility(int visibility, View...views) {
		for (View each : views) {
			each.setVisibility(visibility);
		}
	}
	
	private void onPreview() {
		resetScreen(View.VISIBLE, View.INVISIBLE);
	}

	private void onPuzzleAssembled() {
		Toast.makeText(StarterActivity.this, "Excellent", Toast.LENGTH_SHORT).show();
		puzzlesView.mix();
		onPreview();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		puzzlesView.releaseImageResources();
	}
	
}
