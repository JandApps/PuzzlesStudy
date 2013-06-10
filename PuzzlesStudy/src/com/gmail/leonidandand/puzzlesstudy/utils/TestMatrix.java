package com.gmail.leonidandand.puzzlesstudy.utils;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import com.gmail.leonidandand.puzzlesstudy.utils.Matrix.Position;

import android.annotation.SuppressLint;

public class TestMatrix {

	@SuppressLint("UseValueOf")
	@Test
	// TODO: refactoring
	public void testMatrix() {
		Matrix<Integer> matrix = new Matrix<Integer>(10, 20);
		for (int row = 0; row < matrix.rows; ++row) {
			for (int column = 0; column < matrix.columns; ++column) {
				matrix.set(row, column, elementForPosition(row, column));
			}
		}
		for (int row = 0; row < matrix.rows; ++row) {
			for (int column = 0; column < matrix.columns; ++column) {
				assertEquals(new Integer(elementForPosition(row, column)), matrix.get(row, column));
			}
		}
		Matrix<Integer> copy = new Matrix<Integer>(matrix);
		assertEquals(matrix.rows, copy.rows);
		assertEquals(matrix.columns, copy.columns);
		for (int row = 0; row < copy.rows; ++row) {
			for (int column = 0; column < copy.columns; ++column) {
				assertEquals(matrix.get(row, column), copy.get(row, column));
			}
		}
	}
	
	private int elementForPosition(int row, int column) {
		return row * column;
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
	public void testSwap() {
		Matrix<Integer> matrix = new Matrix<Integer>(2, 2);
		Matrix.Position pos1 = new Matrix.Position(0, 0);
		Matrix.Position pos2 = new Matrix.Position(1, 1);
		matrix.set(pos1, new Integer(2));
		matrix.set(pos2, new Integer(1));
		matrix.swap(pos1, pos2);
		assertEquals(new Integer(2), matrix.get(pos2));
		assertEquals(new Integer(1), matrix.get(pos1));
	}

	@Test(expected=IllegalArgumentException.class)
	public void testSwapIllegalArguments() {
		Matrix.Position pos1 = new Matrix.Position(0, 0);
		Matrix.Position pos2 = new Matrix.Position(0, -1);
		new Matrix<Integer>(2, 2).swap(pos1, pos2);
	}

	@Test(expected=IndexOutOfBoundsException.class)
	public void testSwapPositionOutOfBounds() {
		Matrix.Position pos1 = new Matrix.Position(0, 3);
		Matrix.Position pos2 = new Matrix.Position(0, 0);
		new Matrix<Integer>(2, 2).swap(pos1, pos2);
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
