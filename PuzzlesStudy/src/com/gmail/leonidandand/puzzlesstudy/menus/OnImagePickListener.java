package com.gmail.leonidandand.puzzlesstudy.menus;

import java.util.HashMap;
import java.util.Map;

import android.view.View;
import android.view.View.OnClickListener;

import com.gmail.leonidandand.puzzlesstudy.R;

public class OnImagePickListener implements OnClickListener {
	
	private static final Map<Integer, Integer> imageIds = new HashMap<Integer, Integer>();
	static {
		imageIds.put(R.id.imageView1, R.drawable.animal_1);
		imageIds.put(R.id.imageView2, R.drawable.animal_2);
		imageIds.put(R.id.imageView3, R.drawable.nature_1);
		imageIds.put(R.id.imageView4, R.drawable.nature_2);
		imageIds.put(R.id.imageView5, R.drawable.nature_3);
	}
	
	private NewGameMenuActivity newGameMenu;

	public OnImagePickListener(NewGameMenuActivity newGameMenu) {
		this.newGameMenu = newGameMenu;
	}

	@Override
	public void onClick(View v) {
		int imageViewId = getImageIdByViewId(v.getId());
		newGameMenu.startGame(imageViewId);
	}

	private int getImageIdByViewId(Integer imageViewId) {
		return imageIds.get(imageViewId);
	}

}
