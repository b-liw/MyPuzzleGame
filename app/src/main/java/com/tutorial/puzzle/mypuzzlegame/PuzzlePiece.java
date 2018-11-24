package com.tutorial.puzzle.mypuzzlegame;

import android.content.Context;

import java.util.Arrays;
import java.util.List;

public class PuzzlePiece extends android.support.v7.widget.AppCompatImageView {

    public static final List<Integer> rotationAngle = Arrays.asList(0, 90, 180, 270);
    private int correctPositionX;
    private int correctPositionY;
    private int width;
    private int height;
    private int currentRotation = 0;
    private boolean canMove = true;
    private boolean completed = false;

    public PuzzlePiece(Context context, int correctPositionX, int correctPositionY, int width, int height, int rotation) {
        super(context);
        this.correctPositionX = correctPositionX;
        this.correctPositionY = correctPositionY;
        this.width = width;
        this.height = height;
        this.setRotation(rotation);
    }

    public boolean isCompleted() {
        return completed;
    }

    public void setCompleted(boolean completed) {
        this.completed = completed;
    }

    public void rotate90Clockwise() {
        if (this.canMove()) {
            this.currentRotation = (currentRotation + 90);
            this.animate().rotation(currentRotation).setDuration(500).start();
        }
    }

    private void setRotation(int newRotation) {
        if (this.canMove()) {
            this.currentRotation = newRotation;
            this.animate().rotation(newRotation).setDuration(0).start();
        }
    }

    public void disableMoving() {
        this.canMove = false;
    }

    public boolean canMove() {
        return canMove;
    }

    public int getCorrectPositionX() {
        return correctPositionX;
    }

    public int getCorrectPositionY() {
        return correctPositionY;
    }

    public int getPuzzleWidth() {
        return width;
    }

    public int getPuzzleHeight() {
        return height;
    }

    public boolean hasValidRotation() {
        return this.currentRotation % 360 == 0;
    }

}

