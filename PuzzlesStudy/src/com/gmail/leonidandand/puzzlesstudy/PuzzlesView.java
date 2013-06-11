package com.gmail.leonidandand.puzzlesstudy;

import java.util.ArrayList;
import java.util.Collection;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
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
	private static final int LATTICE_COLOR = Color.WHITE;
	
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
	
	public void setBitmap(Bitmap bitmap, Dimension dim) {
		setDimension(dim);
		setBitmap(bitmap);
	}
	
	private void setDimension(Dimension dim) {
		this.dim = dim;
		puzzles = new Matrix<Bitmap>(dim);
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

	@Override
	protected void onDraw(Canvas canvas) {
		if (fullImage != null) {
			this.canvas = canvas;
			drawLattice();
			drawPuzzles();
		}
	}
	
	private void drawLattice() {
		int maxX = (puzzleSize.width + LATTICE_WIDTH) * dim.columns - LATTICE_WIDTH;
		int maxY = (puzzleSize.height + LATTICE_WIDTH) * dim.rows - LATTICE_WIDTH;
		Paint paint = preparePaintForLattice();
		for (int column = 1; column < dim.columns; ++column) {
			int startX = (puzzleSize.width + LATTICE_WIDTH) * column - 1;
			canvas.drawLine(startX, 0, startX, maxY, paint);
		}
		for (int row = 1; row < dim.rows; ++row) {
			int startY = (puzzleSize.height + LATTICE_WIDTH) * row - 1;
			canvas.drawLine(0, startY, maxX, startY, paint);
		}
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
					canvas.drawBitmap(puzzles.get(pos), leftUpper.x, leftUpper.y, null);
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

	private void draggingStopped() {
		draggedPosition = null;
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
			invalidate();
		}
	}

	private void onUpTouch(Point pt) {
		if (existDraggedPuzzle()) {
			Matrix.Position pos = positionByPoint(pt);
			if (validPuzzlePosition(pos) && insidePuzzle(pt)) {
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
}
