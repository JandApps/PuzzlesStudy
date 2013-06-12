package com.gmail.leonidandand.puzzlesstudy.utils;

public class Size {
	public final int width;
	public final int height;

	public Size(int width, int height) {
		if (width < 0 || height < 0) {
			throw new IllegalArgumentException();
		}
		this.width = width;
		this.height = height;
	}
}
