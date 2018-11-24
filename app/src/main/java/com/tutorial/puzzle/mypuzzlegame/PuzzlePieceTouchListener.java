package com.tutorial.puzzle.mypuzzlegame;

import android.view.MotionEvent;
import android.view.View;

import java.util.Calendar;

class PuzzlePieceTouchListener implements View.OnTouchListener {

    private static final int MAX_CLICK_DURATION = 100;
    private long startClickTime;
    private float deltaX;
    private float deltaY;
    private float lastPositionX;
    private float lastPositionY;
    private int imageBottomBorder;

    public PuzzlePieceTouchListener(int imageBottomBorder) {
        this.imageBottomBorder = imageBottomBorder;
    }

    private void animatePuzzleMovementToCorrectPosition(PuzzlePiece puzzlePiece) {
        puzzlePiece.animate()
                .x(puzzlePiece.getCorrectPositionX())
                .y(puzzlePiece.getCorrectPositionY())
                .setDuration(500)
                .alpha(0.7f)
                .start();
    }

    private void animatePuzzleMovement(PuzzlePiece puzzlePiece, float x, float y, int duration) {
        puzzlePiece.animate()
                .x(x)
                .y(y)
                .setDuration(duration)
                .start();
    }

    private boolean isPuzzleCloseToCorrectPosition(PuzzlePiece puzzlePiece, float rawX, float rawY) {
        double xDiff = Math.abs(puzzlePiece.getCorrectPositionX() - (rawX + deltaX));
        double yDiff = Math.abs(puzzlePiece.getCorrectPositionY() - (rawY + deltaY));
        double marginX = 0.50 * puzzlePiece.getPuzzleWidth();
        double marginY = 0.50 * puzzlePiece.getPuzzleHeight();
        return xDiff <= marginX && yDiff <= marginY && puzzlePiece.hasValidRotation();
    }

    private boolean isShortClickPerformed() {
        long clickDuration = Calendar.getInstance().getTimeInMillis() - startClickTime;
        return clickDuration < MAX_CLICK_DURATION;
    }

    private void calculatePositions(PuzzlePiece puzzlePiece, float rawX, float rawY) {
        float relativePuzzleX = puzzlePiece.getX();
        float relativePuzzleY = puzzlePiece.getY();
        this.deltaX = relativePuzzleX - rawX;
        this.deltaY = relativePuzzleY - rawY;
        if (rawY > imageBottomBorder) {
            this.lastPositionX = rawX + deltaX;
            this.lastPositionY = rawY + deltaY;
        }
    }

    @Override
    public boolean onTouch(View view, MotionEvent event) {
        PuzzlePiece puzzlePiece = (PuzzlePiece) view;

        if (!puzzlePiece.canMove()) {
            return false;
        }

        float rawX = event.getRawX();
        float rawY = event.getRawY();

        switch (event.getActionMasked()) {
            case MotionEvent.ACTION_DOWN: {
                calculatePositions(puzzlePiece, rawX, rawY);
                puzzlePiece.bringToFront();
                startClickTime = Calendar.getInstance().getTimeInMillis();
                return true;
            }
            case MotionEvent.ACTION_MOVE: {
                animatePuzzleMovement(puzzlePiece, rawX + deltaX, rawY + deltaY, 0);
                return true;
            }
            case MotionEvent.ACTION_UP: {
                if (isShortClickPerformed()) {
                    puzzlePiece.rotate90Clockwise();
                }
                if (isPuzzleCloseToCorrectPosition(puzzlePiece, rawX, rawY)) {
                    puzzlePiece.disableMoving();
                    animatePuzzleMovementToCorrectPosition(puzzlePiece);
                    puzzlePiece.setCompleted(true);
                } else if (rawY + deltaY < imageBottomBorder) {
                    animatePuzzleMovement(puzzlePiece, this.lastPositionX, this.lastPositionY, 500);
                }
                return true;
            }
            default:
                return false;
        }
    }

}
