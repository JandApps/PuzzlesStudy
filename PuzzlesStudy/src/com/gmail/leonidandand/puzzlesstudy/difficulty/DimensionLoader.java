package com.gmail.leonidandand.puzzlesstudy.difficulty;

import java.util.HashMap;
import java.util.Map;

import android.content.res.Resources;

import com.gmail.leonidandand.puzzlesstudy.R;
import com.gmail.leonidandand.puzzlesstudy.utils.Dimension;

public class DimensionLoader {
	
	private Map<String, Dimension> dimensions;
	private Resources resources;
	
	public DimensionLoader(Resources resources) {
		this.resources = resources;
		dimensions = new HashMap<String, Dimension>();
		loadDimensions();
	}
	
	private void loadDimensions() {
		addByIds(R.string.difficult_easy, R.integer.easy_rows, R.integer.easy_columns);
		addByIds(R.string.difficult_medium, R.integer.medium_rows, R.integer.medium_columns);
		addByIds(R.string.difficult_hard, R.integer.hard_rows, R.integer.hard_columns);
	}
	
	private void addByIds(int nameId, int rowsId, int columnsId) {
		int rows = resources.getInteger(rowsId);
		int columns = resources.getInteger(columnsId);
		Dimension dim = new Dimension(rows, columns);
		String name = resources.getString(nameId);
		dimensions.put(name.toLowerCase(), dim);
	}
	
	public Dimension dimension(String difficultyName) {
		return dimensions.get(difficultyName.toLowerCase());
	}
}
