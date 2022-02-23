package com.chess;

import com.chess.engine.board.ChessBoard;
import com.chess.gui.Table;

public class JavaChess {
    public static void main(String[] args){
        ChessBoard board = ChessBoard.createStandardBoard();

        System.out.println(board);

        Table table = new Table();
    }
}

