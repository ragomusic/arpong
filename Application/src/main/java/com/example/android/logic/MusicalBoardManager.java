package com.example.android.logic;

import android.graphics.Rect;
import android.graphics.RectF;

/**
 * Created by hlindh on 10/29/16.
 */

public class MusicalBoardManager {

    int boardWidth;
    int boardHeigh;
    int nrOfBoardRows;
    int nrOfBoardColumns;
    RectF tileSize = new RectF();

    public void setBoardProperties(int width, int height, int nrOfRows, int nrOfColumns) {
        boardWidth = width;
        boardHeigh = height;
        nrOfBoardRows = nrOfRows;
        nrOfBoardColumns = nrOfColumns;
        tileSize.set(0, 0, width / nrOfColumns, height / nrOfRows);
    }

    public void placeNote(NoteSequence note, int row, int column) {
        note.setPosition(getBoardTile(row, column));
    }

    private RectF getBoardTile(int row, int column) {
        RectF tile = new RectF(tileSize);
        tile.offsetTo(column * tileSize.width(), row * tileSize.height());
        return tile;
    }

}
