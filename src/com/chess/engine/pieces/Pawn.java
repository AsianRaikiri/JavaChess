package com.chess.engine.pieces;

import com.chess.engine.Alliance;
import com.chess.engine.board.BoardUtils;
import com.chess.engine.board.ChessBoard;
import com.chess.engine.board.Move;
import com.google.common.collect.ImmutableList;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static com.chess.engine.board.Move.*;

public class Pawn extends ChessPiece{

    private final static int[] CANDIDATE_MOVE_COORDINATE = {7,8,9,16};


    public Pawn(final Alliance pieceAlliance, final int piecePosition){
        super(PieceType.PAWN, piecePosition, pieceAlliance, true);
    }

    public Pawn(final Alliance pieceAlliance, final int piecePosition, final boolean isFirstMove){
        super(PieceType.BISHOP, piecePosition, pieceAlliance, isFirstMove);
    }

    @Override
    public Collection<Move> calculateLegalMoves(final ChessBoard board){

        final List<Move> legalMoves = new ArrayList<>();
        for (final int currentCandidateOffset : CANDIDATE_MOVE_COORDINATE){
            final int candidateDestinationCoordinate = this.piecePosition + (this.pieceAlliance.getDirection() * currentCandidateOffset);
// Diese Zeile gibt dem Pawn eine Bewegungsrichtung. So muss man nicht für jeden möglichen Zug jeweils die Richtung neu bestimmen,
// sondern macht dies einmal am Anfang.

            if (!BoardUtils.isValidTileCoordinate(candidateDestinationCoordinate)){
                continue;
            }
            if (currentCandidateOffset == 8 && !board.getTile(candidateDestinationCoordinate).isTileOccupied()){
                if (this.pieceAlliance.isPawnPromotionSquare(candidateDestinationCoordinate)) {
                    legalMoves.add(new PawnPromotion(new PawnMove(board, this, candidateDestinationCoordinate)));
                }else{
                    legalMoves.add(new PawnMove(board, this, candidateDestinationCoordinate));
                }
//Diese if clause bestimmt den normalen Move eines Pawns.
            }else if (currentCandidateOffset == 16 && this.isFirstMove() &&
                    ((BoardUtils.SEVENTH_RANK[this.piecePosition] && this.pieceAlliance.isBlack()) ||
                     (BoardUtils.SECOND_RANK[this.piecePosition] && this.pieceAlliance.isWhite()))){
                final int behindCandidateDestinationCoordinate = this.piecePosition + (this.pieceAlliance.getDirection() * 8);
                if(!board.getTile(behindCandidateDestinationCoordinate).isTileOccupied() &&
                   !board.getTile(candidateDestinationCoordinate).isTileOccupied()){
                    legalMoves.add(new PawnJump(board, this, candidateDestinationCoordinate));
                }
//Diese if clause bestimmt den doppelten Move eines Pawns. der funktioniert nur, wenn sich der Pawn noch nicht bewegt hat.
            }else if(currentCandidateOffset == 7 &&
                    !((BoardUtils.A_FILE[this.piecePosition] && this.getPieceAlliance().isBlack() ||
                     (BoardUtils.H_FILE[this.piecePosition] && this.getPieceAlliance().isWhite())))){
                if (board.getTile(candidateDestinationCoordinate).isTileOccupied()){
                    final ChessPiece pieceCandidate = board.getTile(candidateDestinationCoordinate).getPiece();
                    if (this.pieceAlliance != pieceCandidate.getPieceAlliance()){
                        if (this.pieceAlliance.isPawnPromotionSquare(candidateDestinationCoordinate)) {
                            legalMoves.add(new PawnPromotion(new PawnAttackMove(board, this,
                                    candidateDestinationCoordinate, pieceCandidate)));
                        }else{
                            legalMoves.add(new PawnAttackMove(board, this, candidateDestinationCoordinate, pieceCandidate));
                        }
                    }
                }else if(board.getEnPassantPawn()!=null && board.getEnPassantPawn().getPieceAlliance() != this.getPieceAlliance() &&
                   board.getEnPassantPawn().piecePosition == this.getPiecePosition() - this.pieceAlliance.getDirection()){
                    if (this.pieceAlliance.isPawnPromotionSquare(candidateDestinationCoordinate)) {
                        legalMoves.add(new PawnPromotion(new PawnEnPassantAttack(board, this,
                                candidateDestinationCoordinate, board.getEnPassantPawn())));
                    }else {
                        legalMoves.add(new PawnEnPassantAttack(board, this, candidateDestinationCoordinate, board.getEnPassantPawn()));
                    }
                }
//Diese if clause ist einer der beiden möglichen Attack Moves, die Bedingungen schränken nur die Möglichkeit ein, dass der Bauer
//nicht über den Rand des Brettes geht durch einen Move.
            }else if (currentCandidateOffset == 9 &&
                    !((BoardUtils.A_FILE[this.piecePosition] && this.pieceAlliance.isWhite()) ||
                     (BoardUtils.H_FILE[this.piecePosition] && this.pieceAlliance.isBlack()))){
                if (board.getTile(candidateDestinationCoordinate).isTileOccupied()){
                    final ChessPiece pieceCandidate = board.getTile(candidateDestinationCoordinate).getPiece();
                    if (this.pieceAlliance != pieceCandidate.getPieceAlliance()){
                        if (this.pieceAlliance.isPawnPromotionSquare(candidateDestinationCoordinate)) {
                            legalMoves.add(new PawnPromotion(new PawnAttackMove(board, this,
                                    candidateDestinationCoordinate, pieceCandidate)));
                        }else {
                            legalMoves.add(new PawnAttackMove(board, this, candidateDestinationCoordinate, pieceCandidate));
                        }
                    }
                }else if(board.getEnPassantPawn() != null && board.getEnPassantPawn().getPieceAlliance() != this.getPieceAlliance() &&
                   board.getEnPassantPawn().piecePosition == this.piecePosition + this.pieceAlliance.getDirection()){
                    if (this.pieceAlliance.isPawnPromotionSquare(candidateDestinationCoordinate)) {
                        legalMoves.add(new PawnPromotion(new PawnEnPassantAttack(board, this,
                                candidateDestinationCoordinate, board.getEnPassantPawn())));
                    }else {
                        legalMoves.add(new PawnEnPassantAttack(board, this, candidateDestinationCoordinate, board.getEnPassantPawn()));
                    }
                }
            }
        }
        return ImmutableList.copyOf(legalMoves);
    }

    public ChessPiece getPromotionPiece(){
        return new Queen(this.pieceAlliance, this.piecePosition, false);
    }

    @Override
    public Pawn movePiece(final Move move) {
        return new Pawn(move.getMovedPiece().pieceAlliance, move.getDestinationCoordinate());
    }

    @Override
    public String toString(){
        return PieceType.PAWN.toString();
    }
}