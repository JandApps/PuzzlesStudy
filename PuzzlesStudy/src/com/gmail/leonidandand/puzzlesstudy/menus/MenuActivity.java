package com.gmail.leonidandand.puzzlesstudy.menus;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TextView;

import com.gmail.leonidandand.puzzlesstudy.PaymentChecker;
import com.gmail.leonidandand.puzzlesstudy.R;

public abstract class MenuActivity extends Activity {
	private OnClickListener onMenuItemClickListener;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(layoutResourceId());
		onMenuItemClickListener = createOnMenuItemClickListener();
		
		prepareMenuItems();
		otherActions();
	}

	protected abstract int layoutResourceId();

	protected abstract OnClickListener createOnMenuItemClickListener();

	private void prepareMenuItems() {
		prepareTextViews();
		prepareImageViews();
	}

	private void prepareTextViews() {
		int[] textViewsIds = getTextViewsIds();
		for (int id : textViewsIds) {
			TextView textView = (TextView) findViewById(id);
			textView.setOnClickListener(onMenuItemClickListener);
			correctByPaymentState(id, textView);
		}
	}
	
	private void correctByPaymentState(int id, View view) {
		if ((id == R.id.tvPaid || id == R.id.ivPaid) && PaymentChecker.wasPaid()) {
			view.setVisibility(View.INVISIBLE);
		} else if ((id == R.id.tvGallery || id == R.id.ivGallery) && !PaymentChecker.wasPaid()) {
			view.setEnabled(false);
		}
	}

	protected abstract int[] getTextViewsIds();

	private void prepareImageViews() {
		int[] imageViewsIds = getImageViewsIds();
		for (int id : imageViewsIds) {
			ImageView imageView = (ImageView) findViewById(id);
			imageView.setOnClickListener(onMenuItemClickListener);
			correctByPaymentState(id, imageView);
		}
	}

	protected abstract int[] getImageViewsIds();

	protected void otherActions() {
		// by default - does nothing
	}
}
