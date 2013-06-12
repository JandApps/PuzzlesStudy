package com.gmail.leonidandand.puzzlesstudy;

import android.graphics.Bitmap;

import com.gmail.leonidandand.puzzlesstudy.utils.Matrix;

public class GameArbitrator {
	private Matrix<Bitmap> startPuzzles;

	public GameArbitrator(Matrix<Bitmap> puzzles) {
		startPuzzles = new Matrix<Bitmap>(puzzles);
	}

	public boolean gameFinished(Matrix<Bitmap> puzzles) {
		for (int row = 0; row < puzzles.rows; ++row) {
			for (int column = 0; column < puzzles.columns; ++column) {
				if (startPuzzles.get(row, column) != puzzles.get(row, column)) {
					return false;
				}
			}
		}
		return true;
	}
}
