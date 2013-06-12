package com.gmail.leonidandand.puzzlesstudy;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.gmail.leonidandand.puzzlesstudy.utils.Dimension;

public class StarterActivity extends Activity {

	private TextView textView;
	private ImageView imageView;
	private PuzzlesView puzzlesView;
	private ImageButton backButton;
	private ImageButton mixButton;
	private boolean gameStarted;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_starter);
		findViews();
		Bitmap bitmap = (Bitmap) SaverLoader.load("bitmap");
		imageView.setBackgroundDrawable(new BitmapDrawable(bitmap));
		imageView.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				onStartGame();
			}
		});
		backButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				onBack();
			}
		});
		mixButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				puzzlesView.mix();
			}
		});
		puzzlesView.addOnGameFinishedListener(new OnGameFinishedListener() {
			@Override
			public void onGameFinished() {
				Toast.makeText(StarterActivity.this, "Excellent", Toast.LENGTH_SHORT).show();
				puzzlesView.mix();
				onBack();
			}
		});
	}

	private void findViews() {
		puzzlesView = (PuzzlesView) findViewById(R.id.puzzlesView);
		backButton = (ImageButton) findViewById(R.id.backButton);
		mixButton = (ImageButton) findViewById(R.id.mixButton);
		textView = (TextView) findViewById(R.id.textView);
		imageView = (ImageView) findViewById(R.id.imageView);
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
		changeVisibility(puzzlesVisibility, puzzlesView, backButton, mixButton);		
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
	
	private void onBack() {
		resetScreen(View.VISIBLE, View.INVISIBLE);
	}
}
