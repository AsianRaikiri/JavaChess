package com.chess.engine.board;

import com.chess.engine.pieces.ChessPiece;
import com.chess.engine.pieces.Pawn;
import com.chess.engine.pieces.Rook;

import static com.chess.engine.board.BoardUtils.getPositionAtCoordinate;
import static com.chess.engine.board.ChessBoard.*;


public abstract class Move {

    protected final ChessBoard board;
    protected final ChessPiece movedPiece;
    protected final int destinationCoordinate;
    protected final boolean isFirstMove;
    public static final Move NULL_MOVE = new NullMove();

    private Move(final ChessBoard board, final ChessPiece movedPiece, final int destinationCoordinate){
        this.board = board;
        this.movedPiece = movedPiece;
        this.destinationCoordinate = destinationCoordinate;
        this.isFirstMove = movedPiece.isFirstMove();
    }

    private Move(final ChessBoard board,
                 final int destinationCoordinate){
        this.board = board;
        this.destinationCoordinate = destinationCoordinate;
        this.movedPiece = null;
        this.isFirstMove = false;
    }

    @Override
    public int hashCode(){
        final int prime = 31;
        int result = 1;
        result = prime * result + this.destinationCoordinate;
        result = prime * result + this.movedPiece.hashCode();
        result = prime * result + this.movedPiece.getPiecePosition();

        return result;
    }

    @Override
    public boolean equals(final Object other){
        if(this == other){
            return true;
        }
        if(!(other instanceof Move)){
            return false;
        }
        final Move otherMove = (Move) other;
        return  getMovedPiece().equals(otherMove.getMovedPiece()) &&
                getDestinationCoordinate() == otherMove.getDestinationCoordinate() &&
                getCurrentCoordinate() == ((Move) other).getCurrentCoordinate();
    }

    public ChessBoard getBoard(){
        return this.board;
    }
    public int getCurrentCoordinate(){
        return this.getMovedPiece().getPiecePosition();
    }

    public int getDestinationCoordinate(){
        return this.destinationCoordinate;
    }

    public ChessPiece getMovedPiece(){
        return this.movedPiece;
    }

    public boolean isAttack(){
        return false;
    }

    public boolean isCastlingMove(){
        return false;
    }

    public ChessPiece getAttackedPiece(){
        return null;
    }

    public ChessBoard execute() {
        final Builder builder = new Builder();
        for(final ChessPiece piece : this.board.currentPlayer().getActivePieces()){
            if(!this.movedPiece.equals(piece)){
                builder.setPiece(piece);
            }
        }
        for(final ChessPiece piece : this.board.currentPlayer().getOpponent().getActivePieces()){
            builder.setPiece(piece);
        }
        builder.setPiece(this.movedPiece.movePiece(this));
        builder.setMoveMaker(this.board.currentPlayer().getOpponent().getAlliance());
        return builder.build();

    }

    public static final class MajorMove extends Move{
        public MajorMove(final ChessBoard board,
                         final ChessPiece movedPiece,
                         final int destinationCoordinate){
            super(board, movedPiece, destinationCoordinate);
        }

        @Override
        public boolean equals(final Object other){
            return this == other || other instanceof MajorMove && super.equals(other);
        }

        @Override
        public String toString(){
            return movedPiece.getPieceType().toString() + getPositionAtCoordinate(this.destinationCoordinate);
        }
    }

    public static class AttackMove extends Move{

        final ChessPiece attackedPiece;

        public AttackMove(final ChessBoard board,
                          final ChessPiece movedPiece,
                          final int destinationCoordinate,
                          final ChessPiece attackedPiece){
            super(board, movedPiece, destinationCoordinate);
            this.attackedPiece = attackedPiece;
        }

        @Override
        public int hashCode(){
            return this.attackedPiece.hashCode() + super.hashCode();
        }

        @Override
        public boolean equals(final Object other){
            if(this == other){
                return true;
            }
            if(!(other instanceof AttackMove)){
                return false;
            }
            final AttackMove otherAttackMove = (AttackMove) other;
            return super.equals(otherAttackMove) && getAttackedPiece().equals(otherAttackMove.getAttackedPiece());
        }

        @Override
        public boolean isAttack(){
            return true;
        }

        @Override
        public ChessPiece getAttackedPiece(){
            return this.attackedPiece;
        }

    }

    public static class MajorAttackMove extends AttackMove {
        public MajorAttackMove(final ChessBoard board,
                               final ChessPiece pieceMoved,
                               final int destinationCoordinate,
                               final ChessPiece pieceAttacked){
            super(board, pieceMoved, destinationCoordinate, pieceAttacked);
        }

        @Override
        public boolean equals(final Object other){
            return this == other || other instanceof MajorAttackMove && super.equals(other);
        }

        @Override
        public String toString(){
            return movedPiece.getPieceType() + "x" + getPositionAtCoordinate(this.destinationCoordinate);
        }
    }

    public static class PawnMove extends Move{

        public PawnMove(final ChessBoard board,
                        final ChessPiece movedPiece,
                        final int destinationCoordinate){
            super(board, movedPiece, destinationCoordinate);
        }

        @Override
        public boolean equals(final Object other){
            return this == other || other instanceof PawnMove && super.equals(other);
        }

        @Override
        public String toString(){
            return getPositionAtCoordinate(this.destinationCoordinate);
        }
    }

    public static class PawnAttackMove extends AttackMove{

        public PawnAttackMove(final ChessBoard board,
                              final ChessPiece movedPiece,
                              final int destinationCoordinate,
                              final ChessPiece attackedPiece){
            super(board, movedPiece, destinationCoordinate, attackedPiece);
        }

        @Override
        public boolean equals(final Object other){
            return this == other || other instanceof PawnAttackMove && super.equals(other);
        }

        @Override
        public String toString(){
            return getPositionAtCoordinate(this.movedPiece.getPiecePosition()).charAt(0) + "x" +
                   getPositionAtCoordinate(this.destinationCoordinate);
        }
    }

    public static final class PawnEnPassantAttack extends PawnAttackMove{

        public PawnEnPassantAttack(final ChessBoard board,
                              final ChessPiece movedPiece,
                              final int destinationCoordinate,
                              final ChessPiece attackedPiece){
            super(board, movedPiece, destinationCoordinate, attackedPiece);
        }

        @Override
        public boolean equals(final Object other){
            return this == other || other instanceof PawnEnPassantAttack && super.equals(other);
        }

        @Override
        public ChessBoard execute(){
            final Builder builder = new Builder();
            for(final ChessPiece piece : this.board.currentPlayer().getActivePieces()){
                if(!this.movedPiece.equals(piece)){
                    builder.setPiece(piece);
                }
            }
            for(final ChessPiece piece : this.board.currentPlayer().getOpponent().getActivePieces()) {
                if (!piece.equals(attackedPiece)){
                    builder.setPiece(piece);
                }
            }
            builder.setPiece(this.movedPiece.movePiece(this));
            builder.setMoveMaker(this.board.currentPlayer().getOpponent().getAlliance());
            return builder.build();
        }

        @Override
        public String toString(){
            return getPositionAtCoordinate(this.movedPiece.getPiecePosition()).charAt(0) + "x" +
                    getPositionAtCoordinate(this.destinationCoordinate) + "e.p.";
        }
    }

    public static final class PawnJump extends PawnMove{

        public PawnJump(final ChessBoard board,
                        final ChessPiece movedPiece,
                        final int destinationCoordinate){
            super(board, movedPiece, destinationCoordinate);
        }

        @Override
        public ChessBoard execute(){
            final Builder builder = new Builder();
            for(final ChessPiece piece : this.board.currentPlayer().getActivePieces()){
                if(!this.movedPiece.equals(piece)){
                    builder.setPiece(piece);
                }
            }
            for(final ChessPiece piece : this.board.currentPlayer().getOpponent().getActivePieces()){
                builder.setPiece(piece);
            }
            final Pawn movedPawn = (Pawn) this.movedPiece.movePiece(this);
            builder.setPiece(movedPawn);
            builder.setEnPassantPawn(movedPawn);
            builder.setMoveMaker(this.board.currentPlayer().getOpponent().getAlliance());
            return builder.build();
        }
    }

    public static class PawnPromotion extends Move{
        final public Move decoratedMove;
        final Pawn promotedPawn;

        public PawnPromotion(final Move decoratedMove) {
            super(decoratedMove.getBoard(), decoratedMove.getMovedPiece(), decoratedMove.getDestinationCoordinate());
            this.decoratedMove = decoratedMove;
            this.promotedPawn = (Pawn) decoratedMove.getMovedPiece();
        }

        @Override
        public int hashCode(){
            return decoratedMove.hashCode() + (31 *promotedPawn.hashCode());
        }

        @Override
        public boolean equals(final Object other){
            return this == other || other instanceof PawnPromotion && super.equals(other);
        }

        @Override
        public ChessBoard execute(){
            final ChessBoard pawnMovedBoard = this.decoratedMove.execute();
            final Builder builder = new Builder();
            for(final ChessPiece piece : pawnMovedBoard.currentPlayer().getActivePieces()){
                if(!this.equals(piece)){
                    builder.setPiece(piece);
                }
            }
            for(final ChessPiece piece : pawnMovedBoard.currentPlayer().getOpponent().getActivePieces()){
                builder.setPiece(piece);
            }
            builder.setPiece(this.promotedPawn.getPromotionPiece().movePiece(this));
            builder.setMoveMaker(pawnMovedBoard.currentPlayer().getAlliance());
            return builder.build();
        }

        @Override
        public boolean isAttack(){
            return this.decoratedMove.isAttack();
        }

        @Override
        public ChessPiece getAttackedPiece(){
            return this.decoratedMove.getAttackedPiece();
        }

        @Override
        public String toString(){
            return "";
        }
    }

    static abstract class CastleMove extends Move{

        protected final Rook castleRook;
        protected final int castleRookStart;
        protected final int castleRookDestination;
        public CastleMove(final ChessBoard board,
                          final ChessPiece movedPiece,
                          final int destinationCoordinate,
                          final Rook castleRook,
                          final int castleRookStart,
                          final int castleRookDestination){
            super(board, movedPiece, destinationCoordinate);
            this.castleRook = castleRook;
            this.castleRookStart = castleRookStart;
            this.castleRookDestination = castleRookDestination;
        }

        public Rook getCastleRook(){
            return this.castleRook;
        }

        @Override
        public boolean isCastlingMove(){
            return true;
        }

        @Override
        public ChessBoard execute(){
            final Builder builder = new Builder();
            for(final ChessPiece piece : this.board.currentPlayer().getActivePieces()){
                if(!this.movedPiece.equals(piece) && !this.castleRook.equals(piece)){
                    builder.setPiece(piece);
                }
            }
            for(final ChessPiece piece : this.board.currentPlayer().getOpponent().getActivePieces()){
                builder.setPiece(piece);
            }
            builder.setPiece(this.movedPiece.movePiece(this));
            builder.setPiece(new Rook(this.castleRook.getPieceAlliance(), this.castleRookDestination));
            builder.setMoveMaker(this.board.currentPlayer().getOpponent().getAlliance());


            return builder.build();
        }

        @Override
        public int hashCode(){
            final int prime = 31;
            int result = super.hashCode();
            result = prime * result + this.castleRook.hashCode();
            result = prime * result + this.castleRookDestination;
            return  result;
        }

        @Override
        public boolean equals(final Object other){
            if(this == other){
                return true;
            }
            if(!(other instanceof CastleMove)){
                return false;
            }
            final CastleMove otherCastleMove = (CastleMove) other;
            return super.equals(otherCastleMove) && this.castleRook.equals(otherCastleMove.getCastleRook());
        }
    }

    public static final class KingSideCastleMove extends CastleMove{
        public KingSideCastleMove(final ChessBoard board,
                                  final ChessPiece movedPiece,
                                  final int destinationCoordinate,
                                  final Rook castleRook,
                                  final int castleRookStart,
                                  final int castleRookDestination){
            super(board, movedPiece, destinationCoordinate, castleRook, castleRookStart, castleRookDestination);
        }

        @Override
        public boolean equals(final Object other){
            return  this == other || other instanceof KingSideCastleMove && super.equals(other);
        }

        @Override
        public String toString(){
            return "O-O";
        }
    }

    public static final class QueenSideCastleMove extends CastleMove{
        public QueenSideCastleMove(final ChessBoard board,
                                   final ChessPiece movedPiece,
                                   final int destinationCoordinate,
                                   final Rook castleRook,
                                   final int castleRookStart,
                                   final int castleRookDestination){
            super(board, movedPiece, destinationCoordinate,castleRook, castleRookStart, castleRookDestination);
        }

        @Override
        public boolean equals(final Object other){
            return  this == other || other instanceof QueenSideCastleMove && super.equals(other);
        }

        @Override
        public String toString(){
            return "O-O-O";
        }
    }

    public static final class NullMove extends Move{
        public NullMove(){
            super(null, 65);
        }

        @Override
        public ChessBoard execute(){
            throw new RuntimeException("cannot execute the Null Move");
        }

        @Override
        public int getCurrentCoordinate(){
            return -1;
        }
    }

    public  static class MoveFactory{

        private MoveFactory(){
            throw new RuntimeException("Not instantiable!");
        }

        public static Move createMove(final ChessBoard board,
                                      final int currentCoordinate,
                                      final int destinationCoordinate){

            for(final Move move : board.getAllLegalMoves()){
                if(move.getCurrentCoordinate() == currentCoordinate &&
                        move.getDestinationCoordinate() == destinationCoordinate){
                    return move;
                }
            }
            return NULL_MOVE;
        }

    }
}
