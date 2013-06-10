package com.gmail.leonidandand.puzzlesstudy;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.LightingColorFilter;
import android.graphics.Paint;
import android.graphics.Point;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

import com.gmail.leonidandand.puzzlesstudy.utils.Dimension;
import com.gmail.leonidandand.puzzlesstudy.utils.Matrix;
import com.gmail.leonidandand.puzzlesstudy.utils.Size;

public class PuzzlesView extends View {

	private final int LATTICE_WIDTH = 1;
	private Dimension dim = new Dimension(6, 4);
	private Matrix<Bitmap> puzzles = new Matrix<Bitmap>(dim);
	private GameArbitrator arbitrator;
	private Bitmap fullImage = null;
	private Mixer mixer = new Mixer();
	private Size puzzleSize;
	private Point lastTouchPoint;
	private Point touchedLeftUpper;
	private Matrix.Position touchedPos;

	public PuzzlesView(Context context) {
		super(context);
	}

	public PuzzlesView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}
	

	public PuzzlesView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}
	
	public void setBitmap(Bitmap bitmap, Dimension dim) {
		this.dim = dim;
		setBitmap(bitmap);
	}
	
	public void setBitmap(Bitmap bitmap) {
		calculateSizes();
		this.fullImage = scaleBitmap(bitmap);
		cutIntoPuzzles();
		arbitrator = new GameArbitrator(puzzles);
		mixer.mix(puzzles);
		untouch();
		invalidate();
	}

	private void calculateSizes() {
		int width = getWidth() - LATTICE_WIDTH * (dim.columns - 1);
		int height = getHeight() - LATTICE_WIDTH * (dim.rows - 1);
		puzzleSize = new Size(width / dim.columns, height / dim.rows);
	}
	
	private Bitmap scaleBitmap(Bitmap bitmap) {
		int fullImageWidth = puzzleSize.width * dim.columns;
		int fullImageHeight = puzzleSize.height * dim.rows;
		return Bitmap.createScaledBitmap(bitmap, fullImageWidth, fullImageHeight, true);
	}

	private void cutIntoPuzzles() {
		for (int row = 0; row < dim.rows; ++row) {
			for (int column = 0; column < dim.columns; ++column) {
				Bitmap puzzle = puzzleAtPosition(row, column);
				puzzles.set(row, column, puzzle);
			}
		}
	}
	
	private Bitmap puzzleAtPosition(int row, int column) {
		int x = column * puzzleSize.width;
		int y = row * puzzleSize.height;
		return Bitmap.createBitmap(fullImage, x, y, puzzleSize.width, puzzleSize.height);
	}

	private void untouch() {
		touchedPos = null;
	}

	@Override
	protected void onDraw(Canvas canvas) {
		if (fullImage != null) {
			drawPuzzles(canvas);
		}
	}
	
	private void drawPuzzles(Canvas canvas) {
		for (int row = 0; row < dim.rows; ++row) {
			for (int column = 0; column < dim.columns; ++column) {
				if (!existTouchedPuzzle() || !touchedPuzzlePosition(row, column)) {
					Point leftUpper = leftUpperOfPuzzle(row, column);
					canvas.drawBitmap(puzzles.get(row, column), leftUpper.x, leftUpper.y, null);
				}
			}
		}
		if (existTouchedPuzzle()) {
			Bitmap touchedPuzzle = puzzles.get(touchedPos.row, touchedPos.column);
			canvas.drawBitmap(touchedPuzzle, touchedLeftUpper.x, touchedLeftUpper.y, null);
		}
	}

	private boolean existTouchedPuzzle() {
		return touchedPos != null;
	}

	private boolean touchedPuzzlePosition(int row, int column) {
		return (row == touchedPos.row) && (column == touchedPos.column);
	}
	
	private Point leftUpperOfPuzzle(int row, int column) {
		int x = (puzzleSize.width + LATTICE_WIDTH) * column;
		int y = (puzzleSize.height + LATTICE_WIDTH) * row;
		return new Point(x, y);
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		switch (event.getAction()) {
		case MotionEvent.ACTION_DOWN:
			onDownTouch(event);
			break;
			
		case MotionEvent.ACTION_MOVE:
			onMoveTouch(event);
			break;
			
		case MotionEvent.ACTION_UP:
			onUpTouch(event);
		}
		return true;
	}

	private void onDownTouch(MotionEvent event) {
		int x = (int) event.getX();
		int y = (int) event.getY();
		int column = x / (puzzleSize.width + LATTICE_WIDTH);
		int row = y / (puzzleSize.height + LATTICE_WIDTH);
		if (insideGameBoard(row, column) && insidePuzzle(x, y)) {
			lastTouchPoint = new Point(x, y);
			touchedLeftUpper = leftUpperOfPuzzle(row, column);
			touchedPos = new Matrix.Position(row, column);
		}
	}
	
	private boolean insideGameBoard(int row, int column) {
		return (row < dim.rows) && (column < dim.columns);
	}

	private boolean insidePuzzle(int x, int y) {
		return ((x % (puzzleSize.width + LATTICE_WIDTH)) < puzzleSize.width) &&
			   ((y % (puzzleSize.height + LATTICE_WIDTH)) < puzzleSize.height);
	}

	private void onMoveTouch(MotionEvent event) {
		if (existTouchedPuzzle()) {
			int x = (int) event.getX();
			int y = (int) event.getY();
			int dx = x - lastTouchPoint.x;
			int dy = y - lastTouchPoint.y;
			touchedLeftUpper = new Point(touchedLeftUpper.x + dx, touchedLeftUpper.y + dy);
			lastTouchPoint = new Point(x, y);
			invalidate();
		}
	}

	private void onUpTouch(MotionEvent event) {
		if (existTouchedPuzzle()) {
			int x = (int) event.getX();
			int y = (int) event.getY();
			int column = x / (puzzleSize.width + LATTICE_WIDTH);
			int row = y / (puzzleSize.height + LATTICE_WIDTH);
			if (insideGameBoard(row, column) && insidePuzzle(x, y)) {
				swapWithTouchedPuzzle(row, column);
				if (arbitrator.gameFinished(puzzles)) {
					Toast.makeText(getContext(), "Game finished", Toast.LENGTH_SHORT).show();
				}
			}
			untouch();
			invalidate();
		}
	}

	private void swapWithTouchedPuzzle(int row, int column) {
		Bitmap temp = puzzles.get(row, column);
		Bitmap touchedPuzzle = puzzles.get(touchedPos.row, touchedPos.column);
		puzzles.set(row, column, touchedPuzzle);
		puzzles.set(touchedPos.row, touchedPos.column, temp);
	}
	
	
	
	
	@SuppressWarnings("unused")
	private Paint colorFilterPaint() {
		Paint p = new Paint(Color.BLUE);
		ColorFilter filter = new LightingColorFilter(Color.argb(120, 20, 60, 250), 1);
		p.setColorFilter(filter);
		return p;
	}
}
