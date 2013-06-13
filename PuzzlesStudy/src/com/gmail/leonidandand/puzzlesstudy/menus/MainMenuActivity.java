package com.gmail.leonidandand.puzzlesstudy.menus;

import com.gmail.leonidandand.puzzlesstudy.R;

import android.view.View.OnClickListener;

public class MainMenuActivity extends MenuActivity {
	private static final int[] textViewsIds = {
		R.id.tvPlay, R.id.tvLoad, R.id.tvRating, R.id.tvPaid
	};
	private static final int[] imageViewIds = {
		R.id.ivPlay, R.id.ivLoad, R.id.ivRating, R.id.ivPaid
	};

	@Override
	protected int layoutResourceId() {
		return R.layout.activity_main_menu;
	}

	@Override
	protected OnClickListener createOnMenuItemClickListener() {
		return new OnMainMenuItemClickListener(this);
	}

	@Override
	protected int[] getTextViewsIds() {
		return textViewsIds;
	}

	@Override
	protected int[] getImageViewsIds() {
		return imageViewIds;
	}
}

