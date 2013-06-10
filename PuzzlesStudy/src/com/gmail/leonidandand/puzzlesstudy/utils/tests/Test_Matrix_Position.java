package com.gmail.leonidandand.puzzlesstudy.utils.tests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import com.gmail.leonidandand.puzzlesstudy.utils.Matrix;


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

	@Test
	public void testEquals() {
		assertTrue(new Matrix.Position(1, 2).equals(new Matrix.Position(1, 2)));
		assertFalse(new Matrix.Position(1, 2).equals(new Matrix.Position(0, 0)));
		assertFalse(new Matrix.Position(1, 2).equals(null));
	}

}
