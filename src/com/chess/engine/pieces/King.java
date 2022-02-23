package com.chess.engine.pieces;

import com.chess.engine.Alliance;
import com.chess.engine.board.BoardUtils;
import com.chess.engine.board.ChessBoard;
import com.chess.engine.board.ChessTile;
import com.chess.engine.board.Move;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static com.chess.engine.board.Move.*;

public class King extends ChessPiece{

    private final static int[] CANDIDATE_MOVE_COORDINATE = {-9,-8,-7,-1,1,7,8,9};

    public King(final Alliance pieceAlliance, final int piecePosition){
        super(PieceType.KING, piecePosition, pieceAlliance, true);
    }

    public King(final Alliance pieceAlliance, final int piecePosition, final boolean isFirstMove){
        super(PieceType.BISHOP, piecePosition, pieceAlliance, isFirstMove);
    }

    @Override
    public Collection<Move> calculateLegalMoves(ChessBoard board) {

        final List<Move> legalMoves = new ArrayList<>();

        for(final int currentCandidateOffset : CANDIDATE_MOVE_COORDINATE){
            final int candidateDestinationCoordinate = this.piecePosition + currentCandidateOffset;
            if(isFirstColumnExclusion(this.piecePosition, currentCandidateOffset) ||
               isEighthColumnExclusion(this.piecePosition, currentCandidateOffset)){
                continue;
            }
            if (BoardUtils.isValidTileCoordinate(candidateDestinationCoordinate)){
                final ChessTile candidateDestinationTile = board.getTile(candidateDestinationCoordinate);
                if(!candidateDestinationTile.isTileOccupied()) {
                    legalMoves.add(new MajorMove(board, this, candidateDestinationCoordinate));
                }else {
                    final ChessPiece pieceAtDestination = candidateDestinationTile.getPiece();
                    final Alliance pieceAlliance = pieceAtDestination.getPieceAlliance();
                    if(this.pieceAlliance != pieceAlliance){
                       legalMoves.add(new MajorAttackMove(board, this, candidateDestinationCoordinate, pieceAtDestination));
                    }
                }
            }
//Siehe Queen --> bloß in eine Richtung jeweils.
        }
        if(board.currentPlayer() != null) {
            legalMoves.addAll(board.currentPlayer().getKingCastleMoves());
        }
        return ImmutableList.copyOf(legalMoves);
    }

    @Override
    public King movePiece(final Move move) {
        return new King(move.getMovedPiece().pieceAlliance, move.getDestinationCoordinate());
    }

    @Override
    public String toString(){
        return PieceType.KING.toString();
    }

    private static boolean isFirstColumnExclusion(final int currentPosition, final int candidateOffset){
        return BoardUtils.A_FILE[currentPosition] && (candidateOffset == -9 || candidateOffset == -1 || candidateOffset == 7);
    }
    private static boolean isEighthColumnExclusion(final int currentPosition, final int candidateOffset){
        return BoardUtils.H_FILE[currentPosition] && (candidateOffset == -7 || candidateOffset == 1 || candidateOffset == 9);
    }
}
