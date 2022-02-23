package com.chess.engine.player;

import com.chess.engine.Alliance;
import com.chess.engine.board.ChessBoard;
import com.chess.engine.board.Move;
import com.chess.engine.pieces.ChessPiece;
import com.chess.engine.pieces.King;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public abstract class Player {

    protected final ChessBoard board;
    protected final King playerKing;
    protected final Collection<Move> legalMoves;
    private final boolean isInCheck;
    private final Collection<Move> kingCastleMoves;

    Player(final ChessBoard board,
           final Collection<Move> legalMoves,
           final Collection<Move> opponentMoves){
        this.board = board;
        this.playerKing = establishKing();
        this.kingCastleMoves = calculateKingCastles(legalMoves, opponentMoves);
        this.legalMoves = ImmutableList.copyOf(Iterables.concat(legalMoves, kingCastleMoves));
        this.isInCheck = !Player.calculateAttacksOnTile(this.playerKing.getPiecePosition(), opponentMoves).isEmpty();
    }
/*
Es gibt zwei Player, jeder hat einen König und eine Anzahl von möglichen Moves die der Spieler machen kann. Wenn der
Spieler im Schach steht, wird die Anzahl der legalMoves eingeschränkt.
*/
    public King getPlayerKing(){
        return this.playerKing;
    }

    public Collection<Move> getLegalMoves(){
        return this.legalMoves;
    }

    public Collection<Move> getKingCastleMoves(){return kingCastleMoves;}
/*
Diese Methoden ermöglichen es anderen Methoden auf die Moves des Spielers einzugehen.
*/
    protected static Collection<Move> calculateAttacksOnTile(int piecePosition, Collection<Move> moves) {
        final List<Move> attackMoves = new ArrayList<>();
        for (final Move move : moves){
            if (piecePosition == move.getDestinationCoordinate()){
                attackMoves.add(move);
            }
        }
        return ImmutableList.copyOf(attackMoves);
    }

    private King establishKing() {
        for (final ChessPiece piece : getActivePieces()){
            if(piece.getPieceType().isKing()){
                return (King) piece;
            }
        }
        throw new RuntimeException("This is not a valid Chessboard!");
    }
/*
Diese beiden Methoden ermöglichen i m späteren Verlauf das überprüfen auf Checks oder an sich die Sondermoves mit
dem König.
*/
    public boolean isMoveLegal(final Move move){
        return this.legalMoves.contains(move);
    }

    public boolean isInCheck(){
        return this.isInCheck;
    }

    public boolean isInCheckMate(){
        return this.isInCheck && !hasEscapeMoves();
    }

    public boolean isInStaleMate(){
        return !this.isInCheck && !hasEscapeMoves();
    }

    protected boolean hasEscapeMoves(){
        for (final Move move : this.legalMoves){
            final MoveTransition transition = makeMove(move);
            if (transition.getMoveStatus().isDone()) {
                return true;
            }
        }
        return false;
    }
    public boolean isCastled(){
        return false;
    }
/*
Das sind die verschiedenen speziellen Moves die mit dem König theoretisch möglich sind. Diese werden den legalMoves
hinzugefügt oder ersetzen die legalMoves, im Falle eines Checks.
*/
    public MoveTransition makeMove(final Move move){

        if(!isMoveLegal(move)){
            return new MoveTransition(this.board, move, MoveStatus.ILLEGAL_MOVE);
        }

        final ChessBoard transitionBoard = move.execute();

        final Collection<Move> kingAttacks = Player.calculateAttacksOnTile(transitionBoard.currentPlayer().getOpponent().getPlayerKing().getPiecePosition(),
                transitionBoard.currentPlayer().getLegalMoves());

        if(!kingAttacks.isEmpty()){
            return new MoveTransition(this.board, move, MoveStatus.LEAVES_PLAYER_IN_CHECK);
        }

        return new MoveTransition(transitionBoard, move, MoveStatus.DONE);
    }
/*
Move Transition ist mit Abstand eine der wichtigsten Methoden der Klasse. Im Grunde erzeugt man ein virtuelles Schachbrett mit allen möglichen
Zügen des Gegners. Sollte der Gegner einen Angriff auf den König im nächsten Zug machen können, so wird der Zug der zu der Möglichkeit führt
eliminiert. Ansonsten sollte der König innerhalb der möglichen Züge des Gegners nicht in Gefahr sein, dann werden die legalMoves normal erzeugt.
*/
    public abstract Collection<ChessPiece> getActivePieces();
    public abstract Alliance getAlliance();
    public abstract Player getOpponent();
    protected abstract Collection<Move> calculateKingCastles(Collection<Move> playerLegals, Collection<Move> opponentsLegals);
}
/*
Aus der Klasse Player kommen die beiden Klassen des schwarzen und des weißen Spielers. Diese haben jeweils eine andere Alliance und active Spielfiguren.
*/