package com.gmail.leonidandand.puzzlesstudy.difficulty_level;

import java.util.HashMap;
import java.util.Map;

import android.content.res.Resources;

import com.gmail.leonidandand.puzzlesstudy.R;
import com.gmail.leonidandand.puzzlesstudy.utils.Dimension;

public class DimensionLoader {
	
	private Map<DifficultyLevel, Dimension> dimensions;
	private Resources resources;
	
	public DimensionLoader(Resources resources) {
		this.resources = resources;
		dimensions = new HashMap<DifficultyLevel, Dimension>();
		loadDimensions();
	}
	
	private void loadDimensions() {
		Dimension easy = dimensionByIds(R.integer.easy_rows, R.integer.easy_columns);
		Dimension medium = dimensionByIds(R.integer.medium_rows, R.integer.medium_columns);
		Dimension hard = dimensionByIds(R.integer.hard_rows, R.integer.hard_columns);
		dimensions.put(DifficultyLevel.EASY, easy);
		dimensions.put(DifficultyLevel.MEDIUM, medium);
		dimensions.put(DifficultyLevel.HARD, hard);
	}
	
	private Dimension dimensionByIds(int rowsId, int columnsId) {
		int rows = resources.getInteger(rowsId);
		int columns = resources.getInteger(columnsId);
		return new Dimension(rows, columns);
	}

	public Dimension dimension(DifficultyLevel level) {
		switch (level) {
		case EASY:
		case MEDIUM:
		case HARD:
			return dimensions.get(level);
			
		default:
			throw new IllegalArgumentException();
		}
	}
}
