package com.gmail.leonidandand.puzzlesstudy.menus;

import java.util.ArrayList;
import java.util.Collection;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;

import com.gmail.leonidandand.puzzlesstudy.R;
import com.gmail.leonidandand.puzzlesstudy.SaverLoader;
import com.gmail.leonidandand.puzzlesstudy.StarterActivity;
import com.gmail.leonidandand.puzzlesstudy.difficulty.DimensionLoader;
import com.gmail.leonidandand.puzzlesstudy.utils.Dimension;

public class NewGameMenuActivity extends MenuActivity {
	private static final int[] textViewsIds = { R.id.tvGallery };
	private static final int[] imageViewsIds = { R.id.ivGallery };
	private static final int[] radioButtonsIds = { R.id.rbEasy, R.id.rbMedium, R.id.rbHard };

	@Override
	protected int layoutResourceId() {
		return R.layout.activity_new_game_menu;
	}

	@Override
	protected OnClickListener createOnMenuItemClickListener() {
		return new OnNewGameMenuItemClickListener(getApplicationContext());
	}

	@Override
	protected int[] getTextViewsIds() {
		return textViewsIds;
	}

	@Override
	protected int[] getImageViewsIds() {
		return imageViewsIds;
	}

	@Override
	protected void otherActions() {
		initImagesForGame();
	}
	
	private void initImagesForGame() {
		LinearLayout imagesContainer = (LinearLayout) findViewById(R.id.imagesContainer);
		Collection<ImageView> imageViewsForGame = imageViewsFromContainer(imagesContainer);
		for (ImageView each : imageViewsForGame) {
			each.setOnClickListener(new OnImagePickListener(this));
		}
	}
	
	private Collection<ImageView> imageViewsFromContainer(LinearLayout container) {
		Collection<ImageView> imageViews = new ArrayList<ImageView>();
		int childCount = container.getChildCount();
		for (int i = 0; i < childCount; ++i) {
			View child = container.getChildAt(i);
			if (child instanceof ImageView) {
				imageViews.add((ImageView) child);
			}
		}
		return imageViews;
	}
	
	void startGame(int imageId) {
		Bitmap bitmap = BitmapFactory.decodeResource(getResources(), imageId);
		DimensionLoader dimLoader = new DimensionLoader(getResources());
		String difficultyName = checkedRadioButtonText();
		Dimension dim = dimLoader.dimension(difficultyName);
		startGame(bitmap, dim);
	}
	
	private String checkedRadioButtonText() {
		for (int id : radioButtonsIds) {
			RadioButton radioButton = (RadioButton) findViewById(id);
			if (radioButton.isChecked()) {
				return radioButton.getText().toString();
			}
		}
		return "NOT CHECKED RADIO BUTTON";
	}

	private void startGame(Bitmap bitmap, Dimension dim) {
		SaverLoader.save("bitmap", bitmap);
		SaverLoader.save("dim", dim);
		Intent intent = new Intent(this, StarterActivity.class);
		startActivity(intent);
	}
}
