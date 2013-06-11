package com.gmail.leonidandand.puzzlesstudy.difficulty;

import java.util.HashMap;
import java.util.Map;

import android.content.res.Resources;

import com.gmail.leonidandand.puzzlesstudy.R;
import com.gmail.leonidandand.puzzlesstudy.utils.Dimension;

public class DimensionLoader {
	
	private Map<Difficulty, Dimension> dimensions;
	private Resources resources;
	
	public DimensionLoader(Resources resources) {
		this.resources = resources;
		dimensions = new HashMap<Difficulty, Dimension>();
		loadDimensions();
	}
	
	private void loadDimensions() {
		Dimension easy = dimensionByIds(R.integer.easy_rows, R.integer.easy_columns);
		dimensions.put(Difficulty.EASY, easy);
		Dimension medium = dimensionByIds(R.integer.medium_rows, R.integer.medium_columns);
		dimensions.put(Difficulty.MEDIUM, medium);
		Dimension hard = dimensionByIds(R.integer.hard_rows, R.integer.hard_columns);
		dimensions.put(Difficulty.HARD, hard);
	}
	
	private Dimension dimensionByIds(int rowsId, int columnsId) {
		int rows = resources.getInteger(rowsId);
		int columns = resources.getInteger(columnsId);
		return new Dimension(rows, columns);
	}
	
	public Dimension dimension(String difficultyName) {
		return dimension(Difficulty.valueOf(difficultyName.toUpperCase()));
	}

	public Dimension dimension(Difficulty difficulty) {
		switch (difficulty) {
		case EASY:
		case MEDIUM:
		case HARD:
			return dimensions.get(difficulty);
			
		default:
			throw new IllegalArgumentException();
		}
	}
}
