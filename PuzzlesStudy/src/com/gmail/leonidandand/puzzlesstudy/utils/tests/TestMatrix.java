package com.gmail.leonidandand.puzzlesstudy.utils.tests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;

import com.gmail.leonidandand.puzzlesstudy.utils.Matrix;
import com.gmail.leonidandand.puzzlesstudy.utils.Matrix.Position;

public class TestMatrix {
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
		matrix.forEach(new Matrix.OnEachHandler<Integer>() {
			@Override
			public void handle(Matrix<Integer> matrix, Matrix.Position pos) {
				assertEquals(elementForPosition(pos), matrix.get(pos));
			}
		});
	}
	
	private void fillMatrix(Matrix<Integer> matrix) {
		matrix.forEach(new Matrix.OnEachHandler<Integer>() {
			@Override
			public void handle(Matrix<Integer> matrix, Matrix.Position pos) {
				matrix.set(pos, elementForPosition(pos));
			}
		});
	}
	
	private Integer elementForPosition(Matrix.Position pos) {
		return pos.row * pos.column;
	}
	
	@Test
	public void testMatrixCopyConstructor() {
		fillMatrix(matrix);
		Matrix<Integer> copy = new Matrix<Integer>(matrix);
		assertEquals(matrix.rows, copy.rows);
		assertEquals(matrix.columns, copy.columns);
		copy.forEach(new Matrix.OnEachHandler<Integer>() {
			@Override
			public void handle(Matrix<Integer> matrix, Matrix.Position pos) {
				Integer expected = TestMatrix.this.matrix.get(pos);
				assertEquals(expected, matrix.get(pos));
			}
		});
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
		matrix1.set(0, 0, VALUE);
		matrix1.set(0, 1, VALUE);
		Matrix<Integer> matrix2 = new Matrix<Integer>(1, 2);
		matrix2.set(0, 0, VALUE);
		matrix2.set(0, 1, VALUE);
		assertTrue(matrix1.equals(matrix2));
		matrix2.set(0, 0, VALUE + 1);
		assertFalse(matrix1.equals(matrix2));
	}

	@Test
	public void testForEach() {
		Matrix<Integer> counts = new Matrix<Integer>(matrix.rows, matrix.columns);
		for (int row = 0; row < counts.rows; ++row) {
			for (int column = 0; column < counts.columns; ++column) {
				counts.set(row, column, 0);
			}
		}
		counts.forEach(new Matrix.OnEachHandler<Integer>() {
			@Override
			public void handle(Matrix<Integer> matrix, Matrix.Position pos) {
				matrix.set(pos, matrix.get(pos) + 1);
			}
		});
		for (int row = 0; row < counts.rows; ++row) {
			for (int column = 0; column < counts.columns; ++column) {
				assertTrue(counts.get(row, column) == 1);
			}
		}
	}

	@Test
	public void testForEachOrder_LeftToRight_UpToDown() {
		Matrix<Boolean> flags = new Matrix<Boolean>(5, 7);
		flags.forEach(new Matrix.OnEachHandler<Boolean>() {
			@Override
			public void handle(Matrix<Boolean> matrix, Position pos) {
				matrix.set(pos, false);
			}
		});
		flags.forEach(new Matrix.OnEachHandler<Boolean>() {
			@Override
			public void handle(Matrix<Boolean> matrix, Position pos) {
				assertPreviousElementByPassed(matrix, pos);
				assertNextElementNotByPassed(matrix, pos);
				matrix.set(pos, true);
			}
		});
	}

	private void assertPreviousElementByPassed(Matrix<Boolean> matrix, Position pos) {
		if (!pos.equals(new Matrix.Position(0, 0))) {
			Matrix.Position positionBefore = positionBefore(matrix.rows, matrix.columns, pos);
			assertTrue(matrix.get(positionBefore));
		}
	}

	private void assertNextElementNotByPassed(Matrix<Boolean> matrix, Position pos) {
		Matrix.Position lastPos = new Matrix.Position(matrix.rows - 1, matrix.columns - 1);
		if (!pos.equals(lastPos)) {
			Matrix.Position positionAfter = positionAfter(matrix.rows, matrix.columns, pos);
			assertFalse(matrix.get(positionAfter));
		}
	}

	private Matrix.Position positionBefore(int rows, int columns, Position pos) {
		if (pos.column - 1 < 0) {
			return new Matrix.Position(pos.row - 1, columns - 1);
		} else {
			return new Matrix.Position(pos.row, pos.column - 1);
		}
	}

	private Position positionAfter(int rows, int columns, Position pos) {
		if (pos.column + 1 >= columns) {
			return new Matrix.Position(pos.row + 1, 0);
		} else {
			return new Matrix.Position(pos.row, pos.column + 1);
		}
	}
}
