package com.chess.engine.board;

import com.chess.engine.Alliance;
import com.chess.engine.pieces.*;
import com.chess.engine.player.BlackPlayer;
import com.chess.engine.player.Player;
import com.chess.engine.player.WhitePlayer;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;

import java.util.*;

public class ChessBoard {

    private final List<ChessTile> gameBoard;
    private final Collection<ChessPiece> whitePieces;
    private final Collection<ChessPiece> blackPieces;
    private final WhitePlayer whitePlayer;
    private final BlackPlayer blackPlayer;
    private final Player currentPlayer;
    private final Pawn enPassantPawn;


    private ChessBoard(final Builder builder){
        this.gameBoard = createGameBoard(builder);
        this.whitePieces = calculateActivePieces(this.gameBoard, Alliance.WHITE);
        this.blackPieces = calculateActivePieces(this.gameBoard, Alliance.BLACK);
        this.enPassantPawn = builder.enPassantPawn;
        final Collection<Move> whiteStandardLegalMoves = calculateLegalMoves(this.whitePieces);
        final Collection<Move> blackStandardLegalMoves = calculateLegalMoves(this.blackPieces);
        this.whitePlayer = new WhitePlayer(this, whiteStandardLegalMoves, blackStandardLegalMoves);
        this.blackPlayer = new BlackPlayer(this, whiteStandardLegalMoves, blackStandardLegalMoves);
        this.currentPlayer = builder.nextMoveMaker.choosePlayer(this.whitePlayer, this.blackPlayer);
    }
/**
 * Das ist der Constructor. Er nimmt einen Builder als Eingabe und erstellt mit der createGameBoard Methode ein GameBoard.
 * Außerdem nutzt er die calculateActivePieces Methode um herauszufinden, welche Figuren von welcher Farbe noch im Spiel sind.
 * Außerdem werden damit auch noch alle legalen Moves für eine Farbe in einer Collection von Moves gespeichert.
 */
@Override
    public String toString(){
        final StringBuilder builder = new StringBuilder();

        for(int i = 0; i < BoardUtils.NUM_TILES; i++) {
            final String tileText = this.gameBoard.get(i).toString();
            builder.append(String.format("%3s", tileText));
            if((i + 1) % BoardUtils.NUM_TILES_PER_ROW == 0){
                builder.append("\n");
            }
        }
        return builder.toString();
    }
/**
*Diese Methode ermöglicht es die toString Methode zu überschreiben, damit im Terminal ein ChessBoard dargestellt werden kann.
*Dabei nutzt die Methode einen generischen StringBuilder, welcher durch die Tiles geht und jeweils die toString Methoden
*von jedem String mitnimmt und zum Endergebnis appended. Nach jeder Zeile wird auch noch ein Umbruch dazugefügt.
*/

    public Player whitePlayer(){
        return this.whitePlayer;
    }
    public Player blackPlayer(){
        return this.blackPlayer;
    }
    public Player currentPlayer(){
        return this.currentPlayer;
    }
    public Collection<ChessPiece> getBlackPieces(){
        return this.blackPieces;
    }
    public Collection<ChessPiece> getWhitePieces(){
        return this.whitePieces;
    }
    public Pawn getEnPassantPawn(){
        return this.enPassantPawn;
    }

    private Collection<Move> calculateLegalMoves(final Collection<ChessPiece> pieces){
        final List<Move> legalMoves = new ArrayList<>();
        for(final ChessPiece piece : pieces){
            legalMoves.addAll(piece.calculateLegalMoves(this));
        }
        return ImmutableList.copyOf(legalMoves);
    }
//Diese Methode geht durch alle aktiven Figuren einer Farbe durch und berechnet, welche Züge möglich sind.
    private static Collection<ChessPiece> calculateActivePieces(final List<ChessTile> gameBoard,final Alliance alliance){
        final List<ChessPiece> activePieces = new ArrayList<>();
        for(final ChessTile tile : gameBoard){
            if (tile.isTileOccupied()){
                final ChessPiece piece = tile.getPiece();
                if(piece.getPieceAlliance() == alliance){
                    activePieces.add(piece);
                }
            }
        }
        return ImmutableList.copyOf(activePieces);
    }
/**
 * Diese Methode geht durch jedes Tile durch, und wenn auf einem Tile eine Figur der richtigen Farbe ist, wird diese
 * Figur den aktiven Figuren der Farbe hinzugefügt.
 */
public ChessTile getTile(final int tileCoordinate){
        return gameBoard.get(tileCoordinate);
    }
/**
 * Diese Methode gibt die Tile zurück, die dieser ID entspricht. Diese Tile wiederum enthält Informationen darüber, ob
 * auf der Tile selber eine Figur steht oder nicht.
 */
private static List<ChessTile> createGameBoard(final Builder builder){
        final ChessTile[] tiles = new ChessTile[BoardUtils.NUM_TILES];
        for (int i = 0; i < BoardUtils.NUM_TILES; i++){
            tiles[i] = ChessTile.createTile(i, builder.boardConfig.get(i));
        }
        return ImmutableList.copyOf(tiles);
    }
/**
 * Diese Methode erstellt ein GamBoard, nach den Spezifikationen des boardConfig des Builders.
 */
    public static ChessBoard createStandardBoard(){
        final Builder builder = new Builder();

        builder.setPiece(new Rook(Alliance.BLACK, 0));
        builder.setPiece(new Knight(Alliance.BLACK, 1));
        builder.setPiece(new Bishop(Alliance.BLACK, 2));
        builder.setPiece(new Queen(Alliance.BLACK, 3));
        builder.setPiece(new King(Alliance.BLACK, 4));
        builder.setPiece(new Bishop(Alliance.BLACK, 5));
        builder.setPiece(new Knight(Alliance.BLACK, 6));
        builder.setPiece(new Rook(Alliance.BLACK, 7));
        builder.setPiece(new Pawn(Alliance.BLACK, 8));
        builder.setPiece(new Pawn(Alliance.BLACK, 9));
        builder.setPiece(new Pawn(Alliance.BLACK, 10));
        builder.setPiece(new Pawn(Alliance.BLACK, 11));
        builder.setPiece(new Pawn(Alliance.BLACK, 12));
        builder.setPiece(new Pawn(Alliance.BLACK, 13));
        builder.setPiece(new Pawn(Alliance.BLACK, 14));
        builder.setPiece(new Pawn(Alliance.BLACK, 15));
        //Das waren die schwarzen Figuren.
        builder.setPiece(new Pawn(Alliance.WHITE, 48));
        builder.setPiece(new Pawn(Alliance.WHITE, 49));
        builder.setPiece(new Pawn(Alliance.WHITE, 50));
        builder.setPiece(new Pawn(Alliance.WHITE, 51));
        builder.setPiece(new Pawn(Alliance.WHITE, 52));
        builder.setPiece(new Pawn(Alliance.WHITE, 53));
        builder.setPiece(new Pawn(Alliance.WHITE, 54));
        builder.setPiece(new Pawn(Alliance.WHITE, 55));
        builder.setPiece(new Rook(Alliance.WHITE, 56));
        builder.setPiece(new Knight(Alliance.WHITE, 57));
        builder.setPiece(new Bishop(Alliance.WHITE, 58));
        builder.setPiece(new Queen(Alliance.WHITE, 59));
        builder.setPiece(new King(Alliance.WHITE, 60));
        builder.setPiece(new Bishop(Alliance.WHITE, 61));
        builder.setPiece(new Knight(Alliance.WHITE, 62));
        builder.setPiece(new Rook(Alliance.WHITE, 63));
        //Das sind die schwarzen Figuren.
        builder.setMoveMaker(Alliance.WHITE);
        return builder.build();
    }

    public Iterable<Move> getAllLegalMoves() {
        return Iterables.unmodifiableIterable(Iterables.concat(this.whitePlayer.getLegalMoves(), this.blackPlayer.getLegalMoves()));
    }

    /**
 * Diese Methode gibt dem Builder sowohl die Positionen jeder Figur die Standardmäßig existiert, als auch welche Farbe den
 * ersten Zug machen kann.
 */
public static class Builder{

        Map<Integer, ChessPiece> boardConfig;
        Alliance nextMoveMaker;
        Pawn enPassantPawn;

        public Builder(){
            this.boardConfig = new HashMap<>();
        }

        public Builder setPiece(final ChessPiece piece){
            this.boardConfig.put(piece.getPiecePosition(), piece);
            return this;
        }

        public Builder setMoveMaker(final Alliance nextMoveMaker){
            this.nextMoveMaker = nextMoveMaker;
            return this;
        }

        public ChessBoard build(){
            return new ChessBoard(this);
        }

        public void setEnPassantPawn(Pawn enPassantPawn) {
            this.enPassantPawn = enPassantPawn;
        }
    }
/**
*Dies ist die Klassendefinition des Builders. Der hat jeweils eine HashMap als boardConfig und einige Methoden.
*Mit setPiece gibt man dem initialisierten Builder Figuren, mit deren Position.
*Der MoveMaker legt fest, welche Seite als erstes anfängt. und die build Methode erzeugt einfach ein ChessBoard mit den Spezifikationen.
*/
}
