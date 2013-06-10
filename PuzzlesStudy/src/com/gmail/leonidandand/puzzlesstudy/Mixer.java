package com.gmail.leonidandand.puzzlesstudy;

import java.util.Random;

import android.graphics.Bitmap;

import com.gmail.leonidandand.puzzlesstudy.utils.Matrix;

public class Mixer {
	
	public void mix(Matrix<Bitmap> puzzles) {
		Random r = new Random(System.nanoTime());

		for (int i = 0; i < puzzles.rows; ++i) {
			for (int j = 0; j < puzzles.columns; ++j) {
				
				int rowOne = r.nextInt(puzzles.rows);
				int columnOne = r.nextInt(puzzles.columns);

				int rowTwo = r.nextInt(puzzles.rows);
				int columnTwo = r.nextInt(puzzles.columns);

				Bitmap puzzleOne = puzzles.get(rowOne, columnOne);
				Bitmap puzzleTwo = puzzles.get(rowTwo, columnTwo);
				
				puzzles.set(rowOne, columnOne, puzzleTwo);
				puzzles.set(rowTwo, columnTwo, puzzleOne);
			}
		}
	}
}
