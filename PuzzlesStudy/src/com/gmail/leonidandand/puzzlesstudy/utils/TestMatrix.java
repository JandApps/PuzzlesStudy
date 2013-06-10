package com.gmail.leonidandand.puzzlesstudy.utils;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;

public class TestMatrix {
	
	private interface OnEachElementHandler {
		void handle(Matrix<Integer> each, int row, int column);
	}
	
	private void forEachElementOfMatrix(Matrix<Integer> matrix, OnEachElementHandler handler) {
		for (int row = 0; row < matrix.rows; ++row) {
			for (int column = 0; column < matrix.columns; ++column) {
				handler.handle(matrix, row, column);
			}
		}
	}

	private static final Integer VALUE = 5;
	private static final int ROWS = 10;
	private static final int COLUMNS = 20;
	private Matrix<Integer> matrix;

	@Before
	public void setUp() {
		this.matrix = new Matrix<Integer>(ROWS, COLUMNS);
	}

	@Test
	public void testMatrix() {
		fillMatrix(matrix);
		forEachElementOfMatrix(matrix, new OnEachElementHandler() {
			@Override
			public void handle(Matrix<Integer> each, int row, int column) {
				assertEquals(elementForPosition(row, column), each.get(row, column));
			}
		});
	}
	
	private void fillMatrix(Matrix<Integer> matrix) {
		forEachElementOfMatrix(matrix, new OnEachElementHandler() {
			@Override
			public void handle(Matrix<Integer> each, int row, int column) {
				each.set(row, column, elementForPosition(row, column));
			}
		});
	}
	
	@Test
	public void testMatrixCopyConstructor() {
		fillMatrix(matrix);
		Matrix<Integer> copy = new Matrix<Integer>(matrix);
		assertEquals(matrix.rows, copy.rows);
		assertEquals(matrix.columns, copy.columns);
		forEachElementOfMatrix(copy, new OnEachElementHandler() {
			@Override
			public void handle(Matrix<Integer> each, int row, int column) {
				Integer expected = TestMatrix.this.matrix.get(row, column);
				assertEquals(expected, each.get(row, column));
			}
		});
	}
	
	private Integer elementForPosition(int row, int column) {
		return row * column;
	}

	@Test
	public void testInitValueIsNull() {
		assertNull(matrix.get(0, 0));
	}

	@Test
	public void testDimensionOfMatrix() {
		assertEquals(ROWS, matrix.rows);
		assertEquals(COLUMNS, matrix.columns);
	}

	@Test(expected=IllegalArgumentException.class)
	public void testConstructorIllegalArguments() {
		new Matrix<Integer>(1, -1);
	}

	@Test(expected=IllegalArgumentException.class)
	public void testSetIllegalArguments() {
		matrix.set(1, -1, VALUE);
	}

	@Test(expected=IndexOutOfBoundsException.class)
	public void testSetOutOfBoundsArguments() {
		matrix.set(0, COLUMNS + 1, VALUE);
	}

	@Test(expected=IllegalArgumentException.class)
	public void testGetIllegalArguments() {
		matrix.get(1, -1);
	}

	@Test(expected=IndexOutOfBoundsException.class)
	public void testGetOutOfBoundsArguments() {
		matrix.get(ROWS + 1, 3);
	}

	@Test
	public void testSwap() {
		Matrix<Integer> matrix = new Matrix<Integer>(2, 2);
		Matrix.Position pos1 = new Matrix.Position(0, 0);
		Matrix.Position pos2 = new Matrix.Position(1, 1);
		Integer val1 = 1;
		Integer val2 = 2;
		matrix.set(pos1, val2);
		matrix.set(pos2, val1);
		matrix.swap(pos1, pos2);
		assertEquals(val2, matrix.get(pos2));
		assertEquals(val1, matrix.get(pos1));
	}

	@Test(expected=IllegalArgumentException.class)
	public void testSwapIllegalArguments() {
		Matrix.Position pos1 = new Matrix.Position(0, 0);
		Matrix.Position pos2 = new Matrix.Position(0, -1);
		matrix.swap(pos1, pos2);
	}

	@Test(expected=IndexOutOfBoundsException.class)
	public void testSwapPositionOutOfBounds() {
		Matrix.Position pos1 = new Matrix.Position(0, COLUMNS + 1);
		Matrix.Position pos2 = new Matrix.Position(0, 0);
		matrix.swap(pos1, pos2);
	}

	@Test
	public void testEquals() {
		Matrix<Integer> matrix1 = new Matrix<Integer>(1, 2);
		Matrix<Integer> matrix2 = new Matrix<Integer>(1, 2);
		matrix1.set(0, 0, VALUE);
		matrix1.set(0, 1, VALUE);
		matrix2.set(0, 0, VALUE);
		matrix2.set(0, 1, VALUE);
		assertTrue(matrix1.equals(matrix2));
		matrix2.set(0, 0, VALUE + 1);
		assertFalse(matrix1.equals(matrix2));
	}
}
