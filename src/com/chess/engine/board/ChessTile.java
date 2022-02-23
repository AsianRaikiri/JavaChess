package com.chess.engine.board;

import com.chess.engine.pieces.ChessPiece;
import com.google.common.collect.ImmutableMap;

import java.util.HashMap;
import java.util.Map;


public abstract class ChessTile {

    protected final int tileCoordinate;
    private static final Map<Integer, EmptyTile> EMPTY_TILES_CACHE = createAllPossibleEmptyTiles();

    private static Map<Integer, EmptyTile> createAllPossibleEmptyTiles(){
        final Map<Integer, EmptyTile> emptyTileMap = new HashMap<>();
        for (int i = 0; i<BoardUtils.NUM_TILES;i++){
            emptyTileMap.put(i, new EmptyTile(i));
        }
        //return Collections.unmodifiableMap(emptyTileMap); Hätte man auch nutzen können, aber die Guava Map soll besser sein.
        return ImmutableMap.copyOf(emptyTileMap);
        /**
        *Man erstellt im Code zuerst eine Map mit Integer und leeren Tiles, welche auf die Methode "createAllPossibleEmptyTiles"
        *zugreift. Diese deklariert man direkt darunter. Das ist im Grunde genommen einfach nur eine Schleife die eine Map mit 64
        *leeren Feldern erstellt, die von 0 bis 63 nummeriert sind. Zurück gibt man dies dann als eine ImmutableMap, damit im weiteren
        *Verlauf diese Map nicht geklärt werden kann, sondern so weiterhin im Cache existiert.
        */
    }
/**
*Immutability ist wichtig und wie man die nutzt, ImmutableMap ist eine Library von Google die, diese Map Immutable macht.
*Das sorgt dafür, dass das Schachfeld selber nicht jedes Mal neu berechnet werden muss, sondern dass man die leeren Tiles
*einmal am Anfang erstellt und diese danach die gesamte Zeit nutzt. Die OccupiedTiles hingegen werden jedes Mal neu berechnet.
*Dafür muss man erstmal dafür sorgen, dass alle Teile des Codes immutable sind, das heißt, dass sie von außen nicht geändert
*werden können.
*/

    public static ChessTile createTile(final int tileCoordinate, final ChessPiece piece){
        return piece != null ? new OccupiedTile(tileCoordinate, piece) : EMPTY_TILES_CACHE.get(tileCoordinate);
    }

    private ChessTile(final int tileCoordinate){
        this.tileCoordinate = tileCoordinate;
    }
    public abstract boolean isTileOccupied();
    public abstract ChessPiece getPiece();
    public int getTileCoordinate(){
        return this.tileCoordinate;
    }

/**
*Diese Klasse hat an sich eine Koordinate, die bestimmt, wo auf dem Feld dieser Tile ist. Außerdem gibt es zwei Methoden
*einmal um herauszufinden, ob etwas auf dem Tile ist und dann um herauszufinden welches Schachelement auf dem Tile ist.
*Dafür gibt es zwei Fälle, wofür jeweils eine eigene Subclass erstellt wird.
*Die Methoden werden mit abstract definiert, da diese in den Subclasses nochmal überschrieben werden.
*/

    public static final class EmptyTile extends ChessTile{

        private EmptyTile(final int tileCoordinate){
            super(tileCoordinate);
        }

        @Override
        public String toString(){
            return "-";
        }

        @Override
        public boolean isTileOccupied(){
            return false;
        }

        @Override
        public ChessPiece getPiece(){
            return null;
        }
    }

/**
*Die erste Subclass ist für den Fall, dass der Tile nicht besetzt ist. Im Konstruktor nutzt man das Super, um die Variable der
*Parent Class zu bekommen, und mit dem @Override kann man die Methode der ParentClass überschreiben.
*Das Extend sorgt dafür, dass man weiß welche Class die ParentClass ist.
*/

    public static final class OccupiedTile extends ChessTile{

        private final ChessPiece pieceOnTile;

        private OccupiedTile(final int tileCoordinate, ChessPiece pieceOnTile) {
            super(tileCoordinate);
            this.pieceOnTile = pieceOnTile;
        }

        @Override
        public String toString(){
            return getPiece().getPieceAlliance().isBlack() ? getPiece().toString().toLowerCase() :
                   getPiece().toString();
        }

        @Override
        public boolean isTileOccupied(){
            return true;
        }

        @Override
        public ChessPiece getPiece(){
            return this.pieceOnTile;
        }
    }

/**
*Die zweite Subclass ist genau so aufgebaut wie die erste, bloß dass beim Constructor noch mitgegeben wird, welche Figur
*auf dem Feld ist. Eine Information die nötig ist, um der Methode die richtigen Informationen zu geben.
*/

}
