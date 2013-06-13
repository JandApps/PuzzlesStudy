package com.gmail.leonidandand.puzzlesstudy.menus;

import android.content.Context;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import com.gmail.leonidandand.puzzlesstudy.R;

public class OnNewGameMenuItemClickListener implements OnClickListener {

	private Context context;

	public OnNewGameMenuItemClickListener(Context context) {
		this.context = context;
	}

	@Override
	public void onClick(View v) {
		Animation anim = AnimationUtils.loadAnimation(context, R.anim.click_anim);
		v.startAnimation(anim);

		switch (v.getId()) {
		case R.id.tvGallery:
		case R.id.ivGallery:
			// Toast.makeText(context, "You didn't pay for it...", Toast.LENGTH_SHORT).show();
			// кнопка неактивна, поэтому ничего не делает
			break;
		}
	}

}
