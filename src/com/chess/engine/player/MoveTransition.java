package com.chess.engine.player;

import com.chess.engine.board.ChessBoard;
import com.chess.engine.board.Move;

public class MoveTransition {

    private final ChessBoard transitionBoard;
    private final Move move;
    private final MoveStatus moveStatus;

    public MoveTransition(final ChessBoard transitionBoard, final Move move,final MoveStatus moveStatus) {
        this.transitionBoard = transitionBoard;
        this.move = move;
        this.moveStatus = moveStatus;
    }

    public MoveStatus getMoveStatus(){
        return this.moveStatus;
    }
    public ChessBoard getTransitionBoard(){
        return this.transitionBoard;
    }
}
/*
In MoveTransition wird ein virtuelles Board erstellt mit dem Zug der ausgewählt wurde. Sollte das Board den anderen Konventionen
entsprechen, vor allem der inCheck Regeln, dann wird dieses Board realisiert und als ein mögliches Board hinzugefügt. Sollte der
Move illegal sein, dann wird der jeweilige MoveStatus weitergegeben.
*/