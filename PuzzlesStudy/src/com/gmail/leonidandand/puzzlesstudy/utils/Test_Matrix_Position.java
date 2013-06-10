package com.gmail.leonidandand.puzzlesstudy.utils;

import static org.junit.Assert.*;

import org.junit.Test;


public class Test_Matrix_Position {

	@Test
	public void testCreation() {
		Matrix.Position pos = new Matrix.Position(1, 2);
		assertEquals(1, pos.row);
		assertEquals(2, pos.column);
	}

	@Test(expected=IllegalArgumentException.class)
	public void testIllegalArguments() {
		new Matrix.Position(1, -2);
	}

}
