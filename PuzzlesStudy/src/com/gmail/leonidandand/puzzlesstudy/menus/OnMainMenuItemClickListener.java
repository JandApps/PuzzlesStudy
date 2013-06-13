package com.gmail.leonidandand.puzzlesstudy.menus;

import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Toast;

import com.gmail.leonidandand.puzzlesstudy.R;

public class OnMainMenuItemClickListener implements OnClickListener {

	private final Context context;

	public OnMainMenuItemClickListener(Context context) {
		this.context = context;
	}
	
	@Override
	public void onClick(View v) {
		Animation anim = AnimationUtils.loadAnimation(context, R.anim.click_anim);
		v.startAnimation(anim);
		
		switch (v.getId()) {
		case R.id.tvPlay:
		case R.id.ivPlay:
			Intent intent = new Intent(context, NewGameMenuActivity.class);
			context.startActivity(intent);
			break;
		
		case R.id.tvLoad:
		case R.id.ivLoad:
			Toast.makeText(context, "Loading game...", Toast.LENGTH_SHORT).show();
			//Intent intent = new Intent(context, ContinueActivity.class);
			//context.startActivity(intent);
			break;
		
		case R.id.tvRating:
		case R.id.ivRating:
			Toast.makeText(context, "Showing rating...", Toast.LENGTH_SHORT).show();
			//Intent intent = new Intent(context, RatingActivity.class);
			//context.startActivity(intent);
			break;
		
		case R.id.tvPaid:
		case R.id.ivPaid:
			Toast.makeText(context, "Showing paid features...", Toast.LENGTH_SHORT).show();
			//Intent intent = new Intent(context, PaidFeaturesActivity.class);
			//context.startActivity(intent);
			break;

		}

	}
}; 
