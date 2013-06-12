package com.gmail.leonidandand.puzzlesstudy;

import java.util.ArrayList;
import java.util.Collection;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
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
	private final int LATTICE_WIDTH = 2;
	private final float ALLOWABLE_ERROR = 0.4f;
	
	private Dimension dim;
	private Bitmap fullImage;
	private Matrix<Bitmap> puzzles;
	private Mixer mixer = new Mixer();
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
		if (imageWasSet()) {
			releasePreviousImageResources();
		}
		setDimension(dim);
		setBitmap(bitmap);
	}
	
	private boolean imageWasSet() {
		return (fullImage != null);
	}
	
	private void releasePreviousImageResources() {
		fullImage.recycle();
		puzzles.forEach(new Matrix.OnEachHandler<Bitmap>() {
			@Override
			public void handle(Matrix<Bitmap> matrix, Position pos) {
				Bitmap bitmap = matrix.get(pos);
				bitmap.recycle();
				matrix.set(pos, null);
			}
		});
	}
	
	private void setDimension(Dimension dim) {
		this.dim = dim;
		puzzles = new Matrix<Bitmap>(dim);
	}

	private void setBitmap(Bitmap bitmap) {
		draggingStopped();
		calculatePuzzleSize();
		fullImage = scaledToFullSize(bitmap);
		cutIntoPuzzles();
		arbitrator = new GameArbitrator(puzzles);
		mix();
	}

	private void draggingStopped() {
		draggedPosition = null;
	}

	private void calculatePuzzleSize() {
		int width = getWidth() - LATTICE_WIDTH * (dim.columns - 1);
		int height = getHeight() - LATTICE_WIDTH * (dim.rows - 1);
		puzzleSize = new Size(width / dim.columns, height / dim.rows);
	}
	
	private Bitmap scaledToFullSize(Bitmap bitmap) {
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
		if (imageWasSet()) {
			this.canvas = canvas;
			drawLattice();
			drawPuzzles();
		}
	}
	
	private void drawLattice() {
		Point rightDownPoint = puzzlesRightDownPoint();
		Paint latticePaint = preparePaintForLattice();
		drawVerticalLatticeLines(rightDownPoint, latticePaint);
		drawHorizontalLatticeLines(rightDownPoint, latticePaint);
	}
	
	private Point puzzlesRightDownPoint() {
		Point point = leftUpperOfPuzzle(new Matrix.Position(dim.rows, dim.columns));
		return new Point(point.x - LATTICE_WIDTH, point.y - LATTICE_WIDTH);
	}
	
	private Point leftUpperOfPuzzle(Matrix.Position pos) {
		int x = (puzzleSize.width + LATTICE_WIDTH) * pos.column;
		int y = (puzzleSize.height + LATTICE_WIDTH) * pos.row;
		return new Point(x, y);
	}

	private Paint preparePaintForLattice() {
		Paint paint = new Paint();
		paint.setColor(ResourceReader.colorById(R.color.lattice_color));
		paint.setStrokeWidth(LATTICE_WIDTH);
		return paint;
	}

	private void drawVerticalLatticeLines(Point rightDownPoint, Paint latticePaint) {
		for (int column = 1; column < dim.columns; ++column) {
			Point startPoint = latticeLineStartPoint(0, column);
			canvas.drawLine(startPoint.x, 0, startPoint.x, rightDownPoint.y, latticePaint);
		}
	}
	
	private void drawHorizontalLatticeLines(Point rightDownPoint, Paint latticePaint) {
		for (int row = 1; row < dim.rows; ++row) {
			Point startPoint = latticeLineStartPoint(row, 0);
			canvas.drawLine(0, startPoint.y, rightDownPoint.x, startPoint.y, latticePaint);
		}
	}

	private Point latticeLineStartPoint(int row, int column) {
		Point point = leftUpperOfPuzzle(new Position(row, column));
		return new Point(point.x - 1, point.y - 1);
	}

	private void drawPuzzles() {
		puzzles.forEach(new Matrix.OnEachHandler<Bitmap>() {
			@Override
			public void handle(Matrix<Bitmap> matrix, Position pos) {
				if (!existDraggedPuzzle() || !pos.equals(draggedPosition)) {
					Point leftUpper = leftUpperOfPuzzle(pos);
					Paint paint = paintForPosition(pos);
					Bitmap puzzle = puzzles.get(pos);
					canvas.drawBitmap(puzzle, leftUpper.x, leftUpper.y, paint);
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
		return positionOfCommutablePuzzle(pos)
					? commutablePuzzlePaint()
					: null;
	}

	private boolean positionOfCommutablePuzzle(Position pos) {
		return draggedAndNearestCanBeSwapped() && pos.equals(nearestPosition);
	}

	private boolean draggedAndNearestCanBeSwapped() {
		if (nearestPosition == null) {
			return false;
		}
		Point nearestLeftUpper = leftUpperOfPuzzle(nearestPosition);
		int dx = Math.abs(nearestLeftUpper.x - draggedLeftUpper.x);
		int dy = Math.abs(nearestLeftUpper.y - draggedLeftUpper.y);
		return allowableOffset(dx, dy);
	}

	private boolean allowableOffset(int dx, int dy) {
		boolean dxAllowable = (dx <= ALLOWABLE_ERROR * puzzleSize.width);
		boolean dyAllowable = (dy <= ALLOWABLE_ERROR * puzzleSize.height);
		return dxAllowable && dyAllowable;
	}

	private Paint commutablePuzzlePaint() {
		Paint paint = new Paint();
		int color = ResourceReader.colorById(R.color.commutable_puzzle_color);
		ColorFilter filter = new LightingColorFilter(color, 1);
		paint.setColorFilter(filter);
		return paint;
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		if (!imageWasSet()) {
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
			draggedPosition = pos;
			draggedLeftUpper = leftUpperOfPuzzle(pos);
		}
	}
	
	private Matrix.Position positionByPoint(Point pt) {
		int column = pt.x / (puzzleSize.width + LATTICE_WIDTH);
		int row = pt.y / (puzzleSize.height + LATTICE_WIDTH);
		return new Matrix.Position(row, column);
	}

	private boolean insideGameBoard(Position pos) {
		return pos.row >= 0 && pos.row < dim.rows &&
			   pos.column >= 0 && pos.column < dim.columns;
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
			calculateNearestForDragged(pos1, pos2, pos3, pos4);
		}
	}

	private void calculateNearestForDragged(Matrix.Position... positions) {
		for (Matrix.Position each : positions) {
			if (insideGameBoard(each)) {
				checkCloseness(each);
			}
		}
	}

	private void checkCloseness(Position pos) {
		if (nearestPosition == null || !insideGameBoard(nearestPosition)) {
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

	private void onUpTouch(Point pt) {
		if (!existDraggedPuzzle()) {
			return;
		}
		if (draggedAndNearestCanBeSwapped()) {
			puzzles.swap(nearestPosition, draggedPosition);
		}
		nearestPosition = null;
		draggingStopped();
		invalidate();
		if (arbitrator.gameFinished(puzzles)) {
			notifyThatGameFinished();
		}
	}
		
	private void notifyThatGameFinished() {
		for (OnGameFinishedListener each : onGameFinishedListeners) {
			each.onGameFinished();
		}
	}
}
