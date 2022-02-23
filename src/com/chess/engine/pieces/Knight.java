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

public class Knight extends ChessPiece{

    private final static int[] CANDIDATE_MOVE_COORDINATES = {-17,-15,-10,-6,6,10,15,17};
    //Das sind die möglichen Bewegungen eines Pferds als Addition oder Subtraktion auf dem Feld.
    public Knight(final Alliance pieceAlliance, final int piecePosition){
        super(PieceType.KNIGHT, piecePosition, pieceAlliance, true);
    }

    public Knight(final Alliance pieceAlliance, final int piecePosition, final boolean isFirstMove){
        super(PieceType.BISHOP, piecePosition, pieceAlliance, isFirstMove);
    }

    @Override
    public Collection<Move> calculateLegalMoves(final ChessBoard board){

        final List<Move> legalMoves = new ArrayList<>();

        for (final int currentCandidateOffset : CANDIDATE_MOVE_COORDINATES){
            final int candidateDestinationCoordinate = this.piecePosition + currentCandidateOffset;

            if (BoardUtils.isValidTileCoordinate(candidateDestinationCoordinate)){
                if(isFirstColumnExclusion(this.piecePosition, currentCandidateOffset) ||
                   isSecoundColumnExclusion(this.piecePosition, currentCandidateOffset) ||
                   isSeventhColumnExclusion(this.piecePosition, currentCandidateOffset) ||
                   isEighthColumnExclusion(this.piecePosition, currentCandidateOffset)){
                    continue;
                }
                final ChessTile candidateDestinationTile = board.getTile(candidateDestinationCoordinate);
                if (!candidateDestinationTile.isTileOccupied()){
                   legalMoves.add(new MajorMove(board, this, candidateDestinationCoordinate));
                } else{
                    final ChessPiece pieceAtDestination = candidateDestinationTile.getPiece();
                    final Alliance pieceAlliance = pieceAtDestination.getPieceAlliance();
                    if (this.pieceAlliance != pieceAlliance){
                        legalMoves.add(new MajorAttackMove(board, this, candidateDestinationCoordinate, pieceAtDestination));
                    }
                }
            }
        }
        return ImmutableList.copyOf(legalMoves);
    }
/*
Zuerst wird eine Liste an legalen Moves erstellt, welche erstmal noch leer ist. Der Ort wo das Pferd sich hinbewegen will,
wird durch einen For Loop bestimmt der die momentane Position nimmt und dann den Offset eines möglichen Moves dazu addiert.
Wenn dieser eine Valide Zahl des Feldes ist, und den Exception Regeln folgt, dann wird erst überprüft ob auf dem Feld eine
andere Figur steht oder nicht. Ist keine andere Figur da, kann er einen normalen Zug dahin machen, ist eine feindliche Figur da,
darf er einen schlagenden Zug dahin machen. Dieser Zug wird dann in die legalMoves Liste hinzugefügt und am Ende wird diese Liste
als Immutable Liste zurückgegeben.
*/

    @Override
    public Knight movePiece(final Move move) {
        return new Knight(move.getMovedPiece().pieceAlliance, move.getDestinationCoordinate());
    }

    @Override
    public String toString(){
        return PieceType.KNIGHT.toString();
    }

    private static boolean isFirstColumnExclusion(final int currentPosition, final int candidateOffset){
        return BoardUtils.A_FILE[currentPosition] && (candidateOffset == -17 || candidateOffset == -10
               || candidateOffset == 6 || candidateOffset == 15);
    }
    private static boolean isSecoundColumnExclusion(final int currentPosition, final int candidateOffset){
        return BoardUtils.B_FILE[currentPosition] && (candidateOffset == -10 || candidateOffset == 6);
    }
    private static boolean isSeventhColumnExclusion(final int currentPosition, final int candidateOffset){
        return BoardUtils.G_FILE[currentPosition] && (candidateOffset == -6 || candidateOffset == 10);
    }
    private static boolean isEighthColumnExclusion(final int currentPosition, final int candidateOffset) {
        return BoardUtils.H_FILE[currentPosition] && (candidateOffset == -15 || candidateOffset == -6
               || candidateOffset == 10 || candidateOffset == 17);
    }
/*
Die ganzen Exclusions kommen daher, da wenn ein Pferd in einem der Randgebiete sich befindet, sich die Anzahl der Bewegungen
die er machen darf verringert, dies jedoch nicht durch die ValidCoordinate abgedeckt wird.
*/
}
