package com.gmail.leonidandand.puzzlesstudy.utils;

public class Dimension {
	public final int rows;
	public final int columns;

	public Dimension(int rows, int columns) {
		if (rows <= 0 || columns <= 0) {
			throw new IllegalArgumentException(
					"Dimension constructor: rows and columns must be positive");
		}
		this.rows = rows;
		this.columns = columns;
	}

}
