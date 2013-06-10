package com.gmail.leonidandand.puzzlesstudy;

import java.util.ArrayList;
import java.util.Collection;

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

import com.gmail.leonidandand.puzzlesstudy.utils.Dimension;
import com.gmail.leonidandand.puzzlesstudy.utils.Matrix;
import com.gmail.leonidandand.puzzlesstudy.utils.Size;

public class PuzzlesView extends View {
	private static final Dimension DEFAULT_DIMENSION = new Dimension(6, 4);
	private static final int LATTICE_WIDTH = 1;
	
	private Dimension dim = DEFAULT_DIMENSION;
	private Matrix<Bitmap> puzzles = new Matrix<Bitmap>(dim);
	private Mixer mixer = new Mixer();
	private Bitmap fullImage = null;
	private Collection<OnGameFinishedListener> onGameFinishedListeners =
								new ArrayList<OnGameFinishedListener>();
	private GameArbitrator arbitrator;
	private Size puzzleSize;
	private Point lastTouchedPoint;
	private Point draggedLeftUpper;
	private Matrix.Position draggedPosition;

	public PuzzlesView(Context context) {
		super(context);
	}

	public PuzzlesView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public PuzzlesView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}
	
	public void addOnGameFinishedListener(OnGameFinishedListener listener) {
		onGameFinishedListeners.add(listener);
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
		draggingStopped();
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

	private void draggingStopped() {
		draggedPosition = null;
	}

	private boolean existDraggedPuzzle() {
		return draggedPosition != null;
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
				if (!existDraggedPuzzle() || !draggedPuzzlePosition(row, column)) {
					Point leftUpper = leftUpperOfPuzzle(row, column);
					canvas.drawBitmap(puzzles.get(row, column), leftUpper.x, leftUpper.y, null);
				}
			}
		}
		if (existDraggedPuzzle()) {
			Bitmap draggedPuzzle = puzzles.get(draggedPosition);
			canvas.drawBitmap(draggedPuzzle, draggedLeftUpper.x, draggedLeftUpper.y, null);
		}
	}

	private boolean draggedPuzzlePosition(int row, int column) {
		return (row == draggedPosition.row) && (column == draggedPosition.column);
	}
	
	private Point leftUpperOfPuzzle(int row, int column) {
		int x = (puzzleSize.width + LATTICE_WIDTH) * column;
		int y = (puzzleSize.height + LATTICE_WIDTH) * row;
		return new Point(x, y);
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		if (fullImage == null) {
			return false;
		}
		
		switch (event.getAction()) {
		case MotionEvent.ACTION_DOWN:
			onDownTouch(eventPoint(event));
			break;
			
		case MotionEvent.ACTION_MOVE:
			onMoveTouch(eventPoint(event));
			break;
			
		case MotionEvent.ACTION_UP:
			onUpTouch(eventPoint(event));
			break;
		}
		return true;
	}
	
	private Point eventPoint(MotionEvent event) {
		int x = (int) event.getX();
		int y = (int) event.getY();
		return new Point(x, y);
	}

	private void onDownTouch(Point pt) {
		Matrix.Position pos = positionByPoint(pt);
		if (insideGameBoard(pos) && insidePuzzle(pt)) {
			lastTouchedPoint = pt;
			draggedLeftUpper = leftUpperOfPuzzle(pos);
			draggedPosition = pos;
		}
	}
	
	private Point leftUpperOfPuzzle(Matrix.Position pos) {
		return leftUpperOfPuzzle(pos.row, pos.column);
	}
	
	private Matrix.Position positionByPoint(Point pt) {
		int column = pt.x / (puzzleSize.width + LATTICE_WIDTH);
		int row = pt.y / (puzzleSize.height + LATTICE_WIDTH);
		return new Matrix.Position(row, column);
	}
	
	private boolean insideGameBoard(Matrix.Position pos) {
		return (pos.row < dim.rows) && (pos.column < dim.columns);
	}

	private boolean insidePuzzle(Point pt) {
		return ((pt.x % (puzzleSize.width + LATTICE_WIDTH)) < puzzleSize.width) &&
			   ((pt.y % (puzzleSize.height + LATTICE_WIDTH)) < puzzleSize.height);
	}

	private void onMoveTouch(Point pt) {
		if (existDraggedPuzzle()) {
			int dx = pt.x - lastTouchedPoint.x;
			int dy = pt.y - lastTouchedPoint.y;
			draggedLeftUpper = new Point(draggedLeftUpper.x + dx, draggedLeftUpper.y + dy);
			lastTouchedPoint = pt;
			invalidate();
		}
	}

	private void onUpTouch(Point pt) {
		if (existDraggedPuzzle()) {
			Matrix.Position pos = positionByPoint(pt);
			if (insideGameBoard(pos) && insidePuzzle(pt)) {
				puzzles.swap(pos, draggedPosition);
				if (arbitrator.gameFinished(puzzles)) {
					notifyGameFinished();
				}
			}
			draggingStopped();
			invalidate();
		}
	}
		
	private void notifyGameFinished() {
		for (OnGameFinishedListener each : onGameFinishedListeners) {
			each.onGameFinished();
		}
	}

	@SuppressWarnings("unused")
	private Paint colorFilterPaint() {
		Paint p = new Paint(Color.BLUE);
		ColorFilter filter = new LightingColorFilter(Color.argb(120, 20, 60, 250), 1);
		p.setColorFilter(filter);
		return p;
	}
}
