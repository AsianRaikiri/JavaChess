package com.chess.engine;

import com.chess.engine.board.BoardUtils;
import com.chess.engine.player.BlackPlayer;
import com.chess.engine.player.Player;
import com.chess.engine.player.WhitePlayer;

import static com.chess.engine.board.BoardUtils.*;

public enum Alliance {
    WHITE {
        @Override
        public int getDirection(){
            return -1;
        }
        @Override
        public boolean isWhite(){
            return true;
        }
        @Override
        public boolean isBlack(){
            return false;
        }
        @Override
        public boolean isPawnPromotionSquare(int position){
            return EIGHTH_RANK[position];
        }
        @Override
        public Player choosePlayer(final WhitePlayer whitePlayer,
                                   final BlackPlayer blackPlayer) {
            return whitePlayer;
        }
    },
    BLACK{
        @Override
        public int getDirection(){
            return 1;
        }
        @Override
        public boolean isWhite(){
            return false;
        }
        @Override
        public boolean isBlack(){
            return true;
        }

        @Override
        public boolean isPawnPromotionSquare(int position){
            return FIRST_RANK[position];
        }
        @Override
        public Player choosePlayer(final WhitePlayer whitePlayer,
                                   final BlackPlayer blackPlayer) {
            return blackPlayer;
        }
    };

    public abstract int getDirection();
    public abstract boolean isWhite();
    public abstract boolean isBlack();
    public abstract boolean isPawnPromotionSquare(int position);

    public abstract Player choosePlayer(WhitePlayer whitePlayer, BlackPlayer blackPlayer);
}