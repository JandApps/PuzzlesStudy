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
import com.gmail.leonidandand.puzzlesstudy.utils.Matrix.Position;
import com.gmail.leonidandand.puzzlesstudy.utils.Size;

public class PuzzlesView extends View {
	private static final Dimension DEFAULT_DIMENSION = new Dimension(6, 4);
	private static final int LATTICE_WIDTH = 2;
	private static final int LATTICE_COLOR = Color.LTGRAY;
	private static final float ALLOWABLE_ERROR = 0.2f;
	
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
	private Matrix.Position nearestPosition;
	private Canvas canvas;

	
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
	
	public void set(Bitmap bitmap, Dimension dim) {
		setDimension(dim);
		setBitmap(bitmap);
	}
	
	private void setDimension(Dimension dim) {
		this.dim = dim;
		puzzles = new Matrix<Bitmap>(dim);
	}

	private void setBitmap(Bitmap bitmap) {
		draggingStopped();
		calculateSizes();
		this.fullImage = scaleBitmap(bitmap);
		cutIntoPuzzles();
		arbitrator = new GameArbitrator(puzzles);
		mix();
	}

	private void draggingStopped() {
		draggedPosition = null;
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
		puzzles.forEach(new Matrix.OnEachHandler<Bitmap>() {
			@Override
			public void handle(Matrix<Bitmap> matrix, Position pos) {
				matrix.set(pos, puzzleByPosition(pos));
			}
		});
	}
	
	private Bitmap puzzleByPosition(Matrix.Position pos) {
		int x = pos.column * puzzleSize.width;
		int y = pos.row * puzzleSize.height;
		return Bitmap.createBitmap(fullImage, x, y, puzzleSize.width, puzzleSize.height);
	}
	
	public void mix() {
		mixer.mix(puzzles);
		invalidate();
	}

	@Override
	protected void onDraw(Canvas canvas) {
		if (fullImage != null) {
			this.canvas = canvas;
			drawLattice();
			drawPuzzles();
		}
	}
	
	private void drawLattice() {
		Point rightDownPoint = puzzlesRightDownPoint();
		Paint paint = preparePaintForLattice();
		for (int column = 1; column < dim.columns; ++column) {
			Point startPoint = latticeLineStartPoint(0, column);
			canvas.drawLine(startPoint.x, 0, startPoint.x, rightDownPoint.y, paint);
		}
		for (int row = 1; row < dim.rows; ++row) {
			Point startPoint = latticeLineStartPoint(row, 0);
			canvas.drawLine(0, startPoint.y, rightDownPoint.x, startPoint.y, paint);
		}
	}
	
	private Point latticeLineStartPoint(int row, int column) {
		Point point = leftUpperOfPuzzle(new Position(row, column));
		return new Point(point.x - 1, point.y - 1);
	}
	
	private Point puzzlesRightDownPoint() {
		Point point = leftUpperOfPuzzle(new Matrix.Position(dim.rows, dim.columns));
		return new Point(point.x - LATTICE_WIDTH, point.y - LATTICE_WIDTH);
	}

	private Paint preparePaintForLattice() {
		Paint paint = new Paint();
		paint.setColor(LATTICE_COLOR);
		paint.setStrokeWidth(LATTICE_WIDTH);
		return paint;
	}

	private void drawPuzzles() {
		puzzles.forEach(new Matrix.OnEachHandler<Bitmap>() {
			@Override
			public void handle(Matrix<Bitmap> matrix, Position pos) {
				if (!existDraggedPuzzle() || !pos.equals(draggedPosition)) {
					Point leftUpper = leftUpperOfPuzzle(pos);
					Paint paint = paintForPosition(pos);
					canvas.drawBitmap(puzzles.get(pos), leftUpper.x, leftUpper.y, paint);
				}
			}
		});
		if (existDraggedPuzzle()) {
			Bitmap draggedPuzzle = puzzles.get(draggedPosition);
			canvas.drawBitmap(draggedPuzzle, draggedLeftUpper.x, draggedLeftUpper.y, null);
		}
	}

	private boolean existDraggedPuzzle() {
		return draggedPosition != null;
	}

	private Paint paintForPosition(Position pos) {
		return (replaceablePosition(pos) ? replaceablePaint() : null);
	}

	private boolean replaceablePosition(Position pos) {
		return (nearestPosition != null) && pos.equals(nearestPosition)
			&& draggedAndNearestCanBeSwapped();
	}

	private boolean draggedAndNearestCanBeSwapped() {
		if (nearestPosition == null) {
			return false;
		}
		Point nearestLeftUpper = leftUpperOfPuzzle(nearestPosition);
		int dx = Math.abs(nearestLeftUpper.x - draggedLeftUpper.x);
		int dy = Math.abs(nearestLeftUpper.y - draggedLeftUpper.y);
		boolean dxAllowable = (dx <= ALLOWABLE_ERROR * puzzleSize.width);
		boolean dyAllowable = (dy <= ALLOWABLE_ERROR * puzzleSize.height);
		return (dxAllowable && dyAllowable);
	}

	private Paint replaceablePaint() {
		Paint paint = new Paint(Color.argb(70, 160, 170, 165));
		ColorFilter filter = new LightingColorFilter(Color.argb(100, 60, 180, 140), 1);
		paint.setColorFilter(filter);
		return paint;
	}
	
	private Point leftUpperOfPuzzle(Matrix.Position pos) {
		int x = (puzzleSize.width + LATTICE_WIDTH) * pos.column;
		int y = (puzzleSize.height + LATTICE_WIDTH) * pos.row;
		return new Point(x, y);
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		if (notUploadedImage()) {
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
	
	private boolean notUploadedImage() {
		return fullImage == null;
	}

	private Point eventPoint(MotionEvent event) {
		int x = (int) event.getX();
		int y = (int) event.getY();
		return new Point(x, y);
	}

	private void onDownTouch(Point pt) {
		Matrix.Position pos = positionByPoint(pt);
		if (validPuzzlePosition(pos) && insidePuzzle(pt)) {
			lastTouchedPoint = pt;
			draggedPosition = pos;
			draggedLeftUpper = leftUpperOfPuzzle(draggedPosition);
		}
	}
	
	private Matrix.Position positionByPoint(Point pt) {
		int column = pt.x / (puzzleSize.width + LATTICE_WIDTH);
		int row = pt.y / (puzzleSize.height + LATTICE_WIDTH);
		return new Matrix.Position(row, column);
	}
	
	private boolean validPuzzlePosition(Matrix.Position pos) {
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
			calculateNearestForDragged();
			invalidate();
		}
	}

	private void calculateNearestForDragged() {
		if (draggedLeftUpper.x >= 0 && draggedLeftUpper.y >= 0) {
			Matrix.Position pos1 = positionByPoint(draggedLeftUpper);
			Matrix.Position pos2 = new Matrix.Position(pos1.row, pos1.column + 1);
			Matrix.Position pos3 = new Matrix.Position(pos1.row + 1, pos1.column);
			Matrix.Position pos4 = new Matrix.Position(pos1.row + 1, pos1.column + 1);
			calculateNearestForDragged(new Matrix.Position[] { pos1, pos2, pos3, pos4 });
			
		}
	}

	private void calculateNearestForDragged(Matrix.Position[] positions) {
		for (Matrix.Position each : positions) {
			if (insideGameBoard(each)) {
				checkCloseness(each);
			}
		}
	}

	private void checkCloseness(Position pos) {
		if (nearestPosition == null || !insideGameBoard(pos)) {
			nearestPosition = pos;
		} else if (distanceFromDraggedTo(pos) < distanceFromDraggedTo(nearestPosition)) {
			nearestPosition = pos;
		}
	}

	private int distanceFromDraggedTo(Position pos) {
		Point leftUpper = leftUpperOfPuzzle(pos);
		int dx = leftUpper.x - draggedLeftUpper.x;
		int dy = leftUpper.y - draggedLeftUpper.y;
		return dx * dx + dy * dy;
	}

	private boolean insideGameBoard(Position pos) {
		return pos.row >= 0 && pos.row < dim.rows &&
			   pos.column >= 0 && pos.column < dim.columns;
	}

	private void onUpTouch(Point pt) {
		if (existDraggedPuzzle()) {
			if (draggedAndNearestCanBeSwapped()) {
				puzzles.swap(nearestPosition, draggedPosition);
			}
			nearestPosition = null;
			draggingStopped();
			invalidate();
			if (arbitrator.gameFinished(puzzles)) {
				notifyGameFinished();
			}
		}
	}
		
	private void notifyGameFinished() {
		for (OnGameFinishedListener each : onGameFinishedListeners) {
			each.onGameFinished();
		}
	}
}
