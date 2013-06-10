package com.gmail.leonidandand.puzzlesstudy.utils;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import android.annotation.SuppressLint;

public class TestMatrix {

	@SuppressLint("UseValueOf")
	@Test
	public void testMatrix() {
		Matrix<Integer> matrix = new Matrix<Integer>(2, 1);
		matrix.set(0, 0, new Integer(1));
		matrix.set(1, 0, new Integer(2));
		assertEquals(new Integer(1), matrix.get(0, 0));
		assertEquals(new Integer(2), matrix.get(1, 0));
	}

	@Test
	public void testInitValueIsNull() {
		Matrix<Integer> matrix = new Matrix<Integer>(1, 1);
		assertNull(matrix.get(0, 0));
	}

	@Test
	public void testDimensionOfMatrix() {
		Matrix<Integer> matrix = new Matrix<Integer>(2, 3);
		assertEquals(2, matrix.rows);
		assertEquals(3, matrix.columns);
	}

	@Test(expected=IllegalArgumentException.class)
	public void testConstructorIllegalArguments() {
		new Matrix<Integer>(1, -1);
	}

	@Test(expected=IllegalArgumentException.class)
	public void testSetIllegalArguments() {
		new Matrix<Integer>(5, 3).set(1, -1, 5);
	}

	@Test(expected=IndexOutOfBoundsException.class)
	public void testSetOutOfBoundsArguments() {
		new Matrix<Integer>(4, 1).set(0, 2, 5);
	}

	@Test(expected=IllegalArgumentException.class)
	public void testGetIllegalArguments() {
		new Matrix<Integer>(5, 3).get(1, -1);
	}

	@Test(expected=IndexOutOfBoundsException.class)
	public void testGetOutOfBoundsArguments() {
		new Matrix<Integer>(6, 1).get(0, 3);
	}

	@Test
	public void testEquals() {
		Matrix<Integer> matrix1 = new Matrix<Integer>(1, 2);
		Matrix<Integer> matrix2 = new Matrix<Integer>(1, 2);
		matrix1.set(0, 0, 1);
		matrix2.set(0, 0, 1);
		matrix1.set(0, 1, 1);
		matrix2.set(0, 1, 1);
		assertTrue(matrix1.equals(matrix2));
		matrix2.set(0, 0, 2);
		assertFalse(matrix1.equals(matrix2));
	}
}
