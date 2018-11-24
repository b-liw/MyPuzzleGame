package com.tutorial.puzzle.mypuzzlegame;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.view.Display;
import android.view.ViewGroup;
import android.widget.Chronometer;
import android.widget.ImageView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class PuzzleActivity extends AppCompatActivity {

    private ImageView puzzleImage;
    private ConstraintLayout rootLayout;
    private Chronometer chronometer;
    private List<PuzzlePiece> pieces = new ArrayList<>();
    private int rows = 4;
    private int columns = 4;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_puzzle);
        initializeViews();
        final byte[] photoBytes = getPhotoBytesFromIntent();
        if (photoBytes == null) {
            finish();
        }

        puzzleImage.post(new Runnable() {
            @Override
            public void run() {
                Point screenSize = getAvailableScreenSize();
                int topMargin = getPuzzleImageLayoutParams().topMargin;
                int puzzleImageHeight = puzzleImage.getHeight();
                int puzzleImageWidth = puzzleImage.getWidth();
                int imageBottomBorderPosY = puzzleImageHeight + topMargin;
                Bitmap scaledBitmap = convertBytesToBitmap(photoBytes, puzzleImageHeight, puzzleImageWidth);
                pieces = splitImageToPuzzlePieces(scaledBitmap);
                for (PuzzlePiece piece : pieces) {
                    piece.setOnTouchListener(new PuzzlePieceTouchListener(imageBottomBorderPosY));
                }
                Collections.shuffle(pieces);
                int xw = screenSize.x / columns;
                int yh = (screenSize.y - imageBottomBorderPosY - getActionBarHeight()) / (rows + 1);
                putPuzzlePiecesOnLayout(imageBottomBorderPosY, xw, yh);
                initializeChronometer();
            }
        });
    }

    private void initializeViews() {
        puzzleImage = findViewById(R.id.puzzle_image);
        rootLayout = findViewById(R.id.root_layout);
        chronometer = findViewById(R.id.chronometer);
    }

    private byte[] getPhotoBytesFromIntent() {
        return getIntent().getByteArrayExtra("photo_camera_id");
    }

    private Bitmap convertBytesToBitmap(byte[] photoBytes, int height, int width) {
        Bitmap bm = BitmapFactory.decodeByteArray(photoBytes, 0, photoBytes.length);
        return Bitmap.createScaledBitmap(bm, width, height, false);
    }

    private Point getScreenSize() {
        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        return size;
    }

    private int getActionBarHeight() {
        int actionBarHeight = 0;
        if (getActionBar() != null) {
            actionBarHeight = getActionBar().getHeight();
        }
        return actionBarHeight;
    }

    public Point getAvailableScreenSize() {
        int actionBarHeight = getActionBarHeight();
        Point screenSize = getScreenSize();
        return new Point(screenSize.x, screenSize.y - actionBarHeight);
    }

    private ViewGroup.MarginLayoutParams getPuzzleImageLayoutParams() {
        return (ViewGroup.MarginLayoutParams) puzzleImage.getLayoutParams();
    }

    private boolean isGameCompleted() {
        for (PuzzlePiece piece : pieces) {
            if (!piece.isCompleted()) {
                return true;
            }
        }
        return false;
    }

    private void initializeChronometer() {
        chronometer.start();
        chronometer.setOnChronometerTickListener(new Chronometer.OnChronometerTickListener() {
            @Override
            public void onChronometerTick(Chronometer chronometer) {
                if (isGameCompleted()) {
                    return;
                }
                chronometer.stop();
                Toast.makeText(getApplicationContext(), "KONIEC GRY. Powróć do poprzedniego ekranu aby wybrać inny obrazek.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private Path createPuzzlePath(int pieceHeight, int row, int col, int offsetX, int offsetY, Bitmap puzzlePieceBitmap) {
        int cubicShapeSize = pieceHeight / 4;
        Path path = new Path();
        path.moveTo(offsetX, offsetY);

        if (row == 0) {
            path.lineTo(puzzlePieceBitmap.getWidth(), offsetY);
        } else {
            path.lineTo(offsetX + (puzzlePieceBitmap.getWidth() - offsetX) / 3, offsetY);
            int x1 = offsetX + (puzzlePieceBitmap.getWidth() - offsetX) / 6;
            int y1 = offsetY - cubicShapeSize;
            int x2 = offsetX + (puzzlePieceBitmap.getWidth() - offsetX) / 6 * 5;
            int y2 = offsetY - cubicShapeSize;
            int x3 = offsetX + (puzzlePieceBitmap.getWidth() - offsetX) / 3 * 2;
            int y3 = offsetY;
            path.cubicTo(x1, y1, x2, y2, x3, y3);
            path.lineTo(puzzlePieceBitmap.getWidth(), offsetY);
        }

        if (col == columns - 1) {
            path.lineTo(puzzlePieceBitmap.getWidth(), puzzlePieceBitmap.getHeight());
        } else {
            path.lineTo(puzzlePieceBitmap.getWidth(), offsetY + (puzzlePieceBitmap.getHeight() - offsetY) / 3);
            int x1 = puzzlePieceBitmap.getWidth() - cubicShapeSize;
            int y1 = offsetY + (puzzlePieceBitmap.getHeight() - offsetY) / 6;
            int x2 = puzzlePieceBitmap.getWidth() - cubicShapeSize;
            int y2 = offsetY + (puzzlePieceBitmap.getHeight() - offsetY) / 6 * 5;
            int x3 = puzzlePieceBitmap.getWidth();
            int y3 = offsetY + (puzzlePieceBitmap.getHeight() - offsetY) / 3 * 2;
            path.cubicTo(x1, y1, x2, y2, x3, y3);
            path.lineTo(puzzlePieceBitmap.getWidth(), puzzlePieceBitmap.getHeight());
        }

        if (row == rows - 1) {
            path.lineTo(offsetX, puzzlePieceBitmap.getHeight());
        } else {
            path.lineTo(offsetX + (puzzlePieceBitmap.getWidth() - offsetX) / 3 * 2, puzzlePieceBitmap.getHeight());
            int x1 = offsetX + (puzzlePieceBitmap.getWidth() - offsetX) / 6 * 5;
            int y1 = puzzlePieceBitmap.getHeight() - cubicShapeSize;
            int x2 = offsetX + (puzzlePieceBitmap.getWidth() - offsetX) / 6;
            int y2 = puzzlePieceBitmap.getHeight() - cubicShapeSize;
            int x3 = offsetX + (puzzlePieceBitmap.getWidth() - offsetX) / 3;
            int y3 = puzzlePieceBitmap.getHeight();
            path.cubicTo(x1, y1, x2, y2, x3, y3);
            path.lineTo(offsetX, puzzlePieceBitmap.getHeight());
        }

        if (col == 0) {
            path.close();
        } else {
            path.lineTo(offsetX, offsetY + (puzzlePieceBitmap.getHeight() - offsetY) / 3 * 2);
            int x1 = offsetX - cubicShapeSize;
            int y1 = offsetY + (puzzlePieceBitmap.getHeight() - offsetY) / 6 * 5;
            int x2 = offsetX - cubicShapeSize;
            int y2 = offsetY + (puzzlePieceBitmap.getHeight() - offsetY) / 6;
            int x3 = offsetX;
            int y3 = offsetY + (puzzlePieceBitmap.getHeight() - offsetY) / 3;
            path.cubicTo(x1, y1, x2, y2, x3, y3);
            path.close();
        }
        return path;
    }

    private Bitmap createBitmapWithPuzzleShape(int pieceWidth, int pieceHeight, int row, int col,
                                               int offsetX, int offsetY, Bitmap puzzlePieceBitmap) {
        Bitmap bitmapForCanvas = Bitmap.createBitmap(
                pieceWidth + offsetX,
                pieceHeight + offsetY,
                Bitmap.Config.ARGB_8888
        );
        Canvas canvas = new Canvas(bitmapForCanvas);
        Path path = createPuzzlePath(pieceHeight, row, col, offsetX, offsetY, puzzlePieceBitmap);
        Paint paint = new Paint();
        paint.setColor(0xFF000000);
        paint.setStyle(Paint.Style.FILL);
        canvas.drawPath(path, paint);
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(puzzlePieceBitmap, 0, 0, paint);
        return bitmapForCanvas;
    }

    private PuzzlePiece createPuzzlePieceWithRandomRotation(int pieceWidth, int pieceHeight,
                                                            int leftMargin, int topMargin,
                                                            int x, int y, int offsetX,
                                                            int offsetY, Bitmap puzzlePiece) {
        int randomPos = new Random().nextInt(PuzzlePiece.rotationAngle.size());
        int rotationAngle = PuzzlePiece.rotationAngle.get(randomPos);
        PuzzlePiece piece = new PuzzlePiece(
                getApplicationContext(),
                x - offsetX + leftMargin,
                y - offsetY + topMargin,
                pieceWidth + offsetX,
                pieceHeight + offsetY,
                rotationAngle
        );
        piece.setImageBitmap(puzzlePiece);
        return piece;
    }

    private void putPuzzlePiecesOnLayout(int startY, int xw, int yh) {
        for (int i = 0; i < pieces.size(); i++) {
            PuzzlePiece piece = pieces.get(i);
            int col = i % rows;
            int row = i / rows;
            rootLayout.addView(piece);
            int posX = xw * col;
            int posY = startY + yh * row;
            piece.animate()
                    .x(posX)
                    .y(posY)
                    .setDuration(0)
                    .start();
        }
    }

    private List<PuzzlePiece> splitImageToPuzzlePieces(Bitmap scaledBitmap) {
        List<PuzzlePiece> pieces = new ArrayList<>();
        int pieceWidth = scaledBitmap.getWidth() / columns;
        int pieceHeight = scaledBitmap.getHeight() / rows;
        int leftMargin = (getPuzzleImageLayoutParams()).leftMargin;
        int topMargin = (getPuzzleImageLayoutParams()).topMargin;
        for (int row = 0; row < rows; row++) {
            for (int col = 0; col < columns; col++) {
                int x = col * pieceWidth;
                int y = row * pieceHeight;
                int offsetX = 0;
                int offsetY = 0;
                if (col > 0) {
                    offsetX = pieceWidth / 3;
                }
                if (row > 0) {
                    offsetY = pieceHeight / 3;
                }

                Bitmap puzzlePieceBitmap = Bitmap.createBitmap(
                        scaledBitmap,
                        x - offsetX,
                        y - offsetY,
                        pieceWidth + offsetX,
                        pieceHeight + offsetY
                );
                Bitmap puzzleShapeBitmap = createBitmapWithPuzzleShape(
                        pieceWidth,
                        pieceHeight,
                        row,
                        col,
                        offsetX,
                        offsetY,
                        puzzlePieceBitmap
                );
                PuzzlePiece puzzlePiece = createPuzzlePieceWithRandomRotation(
                        pieceWidth,
                        pieceHeight,
                        leftMargin,
                        topMargin,
                        x,
                        y,
                        offsetX,
                        offsetY,
                        puzzleShapeBitmap
                );

                pieces.add(puzzlePiece);
            }
        }
        return pieces;
    }


}
