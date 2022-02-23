package com.chess.engine.pieces;

import com.chess.engine.Alliance;
import com.chess.engine.board.BoardUtils;
import com.chess.engine.board.ChessBoard;
import com.chess.engine.board.ChessTile;
import com.chess.engine.board.Move;
import com.chess.engine.board.Move.MajorAttackMove;
import com.google.common.collect.ImmutableList;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class Queen extends ChessPiece{

    private final static int[] CANDIDATE_MOVE_VECTOR_COORDINATE = {-9,-8,-7,-1,1,7,8,9};

    public Queen(final Alliance pieceAlliance,final int piecePosition){
        super(PieceType.QUEEN, piecePosition, pieceAlliance, true);
    }

    public Queen(final Alliance pieceAlliance, final int piecePosition, final boolean isFirstMove){
        super(PieceType.BISHOP, piecePosition, pieceAlliance, isFirstMove);
    }

    @Override
    public Collection<Move> calculateLegalMoves(final ChessBoard board){

        final List<Move> legalMoves = new ArrayList<>();

        for (final int candidateCoordinateOffset:CANDIDATE_MOVE_VECTOR_COORDINATE){
            int candidateDestinationCoordinate = this.piecePosition;
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
                    legalMoves.add(new Move.MajorMove(board, this, candidateDestinationCoordinate));
                } else{
                    final ChessPiece pieceAtDestination = candidateDestinationTile.getPiece();
                    final Alliance pieceAlliance = pieceAtDestination.getPieceAlliance();
                    if (this.pieceAlliance != pieceAlliance){
                        legalMoves.add(new MajorAttackMove(board, this, candidateDestinationCoordinate, pieceAtDestination));
                    }
                    break;
                }
            }
//Siehe Bishop -->
        }
        return ImmutableList.copyOf(legalMoves);
    }

    @Override
    public Queen movePiece(final Move move) {
        return new Queen(move.getMovedPiece().pieceAlliance, move.getDestinationCoordinate());
    }

    @Override
    public String toString(){
        return PieceType.QUEEN.toString();
    }

    private static boolean isFirstColumnExclusion(final int currentPosition, final int candidateOffset){
        return BoardUtils.A_FILE[currentPosition] && (candidateOffset == -9 || candidateOffset == -1 || candidateOffset == 7);
    }
    private static boolean isEighthColumnExclusion(final int currentPosition, final int candidateOffset){
        return BoardUtils.H_FILE[currentPosition] && (candidateOffset == -7 || candidateOffset == 1 || candidateOffset == 9);
    }
}