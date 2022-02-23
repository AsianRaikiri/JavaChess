package com.chess.engine.pieces;

import com.chess.engine.Alliance;
import com.chess.engine.board.BoardUtils;
import com.chess.engine.board.ChessBoard;
import com.chess.engine.board.ChessTile;
import com.chess.engine.board.Move;
import com.google.common.collect.ImmutableList;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static com.chess.engine.board.Move.*;

public class Bishop extends ChessPiece{

    private final static int[] CANDIDATE_MOVE_VECTOR_COORDINATE = {-9,-7,7,9};

    public Bishop(final Alliance pieceAlliance, final int piecePosition){
        super(PieceType.BISHOP, piecePosition, pieceAlliance, true);
    }

    public Bishop(final Alliance pieceAlliance, final int piecePosition, final boolean isFirstMove){
        super(PieceType.BISHOP, piecePosition, pieceAlliance, isFirstMove);
    }

    @Override
    public Collection<Move> calculateLegalMoves(final ChessBoard board){

        final List<Move> legalMoves = new ArrayList<>();

        for (final int candidateCoordinateOffset:CANDIDATE_MOVE_VECTOR_COORDINATE){
            int candidateDestinationCoordinate = this.piecePosition;
/*
Wie auch schon beim Knight wird die Liste legalMoves als eine ArrayList definiert, welche im Folgenden erweitert wird.
Danach wird eine Schleife genutzt, die durch jede der 4 Richtungen geht, die der Bishop gehen kann und resettet das Ziel der Figur.
*/
            while(BoardUtils.isValidTileCoordinate(candidateDestinationCoordinate)){
                if (isFirstColumnExclusion(candidateDestinationCoordinate, candidateCoordinateOffset) ||
                    isEighthColumnExclusion(candidateDestinationCoordinate, candidateCoordinateOffset)){
                    break;
                }
                candidateDestinationCoordinate += candidateCoordinateOffset;
                if (!BoardUtils.isValidTileCoordinate(candidateDestinationCoordinate)){
                    break;
                }
                final ChessTile candidateDestinationTile = board.getTile(candidateDestinationCoordinate);
                if (!candidateDestinationTile.isTileOccupied()){
                    legalMoves.add(new MajorMove(board, this, candidateDestinationCoordinate));
                } else {
                    final ChessPiece pieceAtDestination = candidateDestinationTile.getPiece();
                    final Alliance pieceAlliance = pieceAtDestination.getPieceAlliance();
                    if (this.pieceAlliance != pieceAlliance){
                        legalMoves.add(new MajorAttackMove(board, this, candidateDestinationCoordinate, pieceAtDestination));
                    }
                    break;
                }
            }
/**
*Die While Schleife überprüft, ob der nächste Schritt in die Richtung möglich ist. Dafür wird zuerst überprüft, ob die
*Column Grenzfälle eintreten. Sollte dies nicht der Fall sein und der anzustrebende Wert noch innerhalb des Feldes sein,
*dann wird überprüft, ob das Feld besetzt ist. Sollte das Feld nicht besetzt sein, wird das zu legalMoves hinzugefügt und
*die Schleife geht zum nächsten Wert in die nächste Richtung. Ist es wiederum ein Feld der von einer anderen Figur besetzt ist,
*dann wird der definitiv nicht mehr weiter gehen. Durch eine simple if clause, wird nur geschaut, ob die Figur von der anderen
*Allianz ist und sollte dies der Fall sein, dann wird noch der Attack Move zu den legalen Moves geaddet.
*/
        }
        return ImmutableList.copyOf(legalMoves);
    }

    @Override
    public Bishop movePiece(final Move move) {
        return new Bishop(move.getMovedPiece().pieceAlliance, move.getDestinationCoordinate());
    }

    @Override
    public String toString(){
        return PieceType.BISHOP.toString();
    }

    private static boolean isFirstColumnExclusion(final int currentPosition, final int candidateOffset){
        return BoardUtils.A_FILE[currentPosition] && (candidateOffset == -9 || candidateOffset == 7);
    }
    private static boolean isEighthColumnExclusion(final int currentPosition, final int candidateOffset){
        return BoardUtils.H_FILE[currentPosition] && (candidateOffset == -7 || candidateOffset == 9);
    }
}
