package com.chess.gui;

import com.chess.engine.board.BoardUtils;
import com.chess.engine.board.ChessBoard;
import com.chess.engine.board.ChessTile;
import com.chess.engine.board.Move;
import com.chess.engine.pieces.ChessPiece;
import com.chess.engine.player.MoveTransition;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import static com.chess.engine.board.Move.*;
import static com.chess.engine.board.Move.MoveFactory.*;
import static javax.swing.SwingUtilities.isLeftMouseButton;
import static javax.swing.SwingUtilities.isRightMouseButton;

public class Table {

    private final JFrame gameFrame;
    private final GameHistoryPanel gameHistoryPanel;
    private final TakenPiecesPanel takenPiecesPanel;
    private final BoardPanel boardPanel;
    private final MoveLog moveLog;
    private ChessBoard chessBoard;
    private ChessTile sourceTile;
    private ChessTile destinationTile;
    private ChessPiece humanMovedPiece;
    private BoardDirection boardDirection;
    private boolean highlightLegalMoves;
    private final static Dimension OUTER_FRAME_DIMENSION = new Dimension(600,600);
    private final static Dimension BOARD_PANEL_DIMENSION = new Dimension(400,350);
    private final static Dimension TILE_PANEL_DIMENSION = new Dimension(10,10);
    private static String defaultPieceImagePath = "art/stockfish/";
    private final Color lightTileColor = Color.decode("#FFFACD");
    private final Color darkTileColor = Color.decode("#593E1A");

    public Table() {
        this.gameFrame = new JFrame("JavaChess");
        this.gameFrame.setLayout(new BorderLayout());
        final JMenuBar tableMenuBar = createTableMenuBar();
        this.gameFrame.setJMenuBar(tableMenuBar);
        this.gameFrame.setSize(OUTER_FRAME_DIMENSION);
        this.chessBoard = ChessBoard.createStandardBoard();
        this.gameHistoryPanel = new GameHistoryPanel();
        this.takenPiecesPanel = new TakenPiecesPanel();
        this.boardPanel = new BoardPanel();
        this.moveLog = new MoveLog();
        this.boardDirection = BoardDirection.NORMAL;
        this.highlightLegalMoves = true;
        this.gameFrame.add(this.takenPiecesPanel, BorderLayout.WEST);
        this.gameFrame.add(this.boardPanel, BorderLayout.CENTER);
        this.gameFrame.add(this.gameHistoryPanel, BorderLayout.EAST);
        this.gameFrame.setVisible(true);
    }

    private JMenuBar createTableMenuBar() {
        final JMenuBar tableMenuBar = new JMenuBar();
        tableMenuBar.add(createFileMenu());
        tableMenuBar.add(createPreferencesMenu());
        return tableMenuBar;
    }

    private JMenu createFileMenu() {
        final JMenu fileMenu = new JMenu("File");
        final JMenuItem openPGN = new JMenuItem("Load PGN File");
        openPGN.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent e) {
                System.out.println("open up that PGN File!");
            }
        });
        fileMenu.add(openPGN);

        final JMenuItem exitMenuItem = new JMenuItem("Exit");
        exitMenuItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent e) {
                System.exit(0);
            }
        });
        fileMenu.add(exitMenuItem);

        return fileMenu;
    }

    private JMenu createPreferencesMenu(){
        final JMenu preferencesMenu = new JMenu("Preferences");
        final JMenuItem flipBoardMenuItem = new JMenuItem("Flip Board");
        flipBoardMenuItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent e) {
                boardDirection = boardDirection.opposite();
                boardPanel.drawBoard(chessBoard);
                takenPiecesPanel.redo(moveLog, boardDirection);
            }
        });
        preferencesMenu.add(flipBoardMenuItem);
        preferencesMenu.addSeparator();

        final JCheckBoxMenuItem legalMoveHighlighterCheckBox= new JCheckBoxMenuItem("Highlight Legal Moves", true);
        legalMoveHighlighterCheckBox.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                highlightLegalMoves = legalMoveHighlighterCheckBox.isSelected();
            }
        });
        preferencesMenu.add(legalMoveHighlighterCheckBox);
        return preferencesMenu;
    }

    public enum BoardDirection {
        NORMAL {
            @Override
            List<TilePanel> traverse(final List<TilePanel> boardTiles){
                return boardTiles;
            }

            @Override
            BoardDirection opposite(){
                return FLIPPED;
            }

            @Override
            public boolean isNormal(){
                return true;
            }
        },
        FLIPPED {
            @Override
            List<TilePanel> traverse(final List<TilePanel> boardTiles){
                return Lists.reverse(boardTiles);
            }

            @Override
            BoardDirection opposite(){
                return NORMAL;
            }

            @Override
            public boolean isNormal(){
                return false;
            }
        };
        abstract List<TilePanel> traverse(final List<TilePanel> boardTiles);
        abstract BoardDirection opposite();
        public abstract boolean isNormal();
    }

    //TODO boardPanel in ChessBoardPanel und TilePanel in ChessBoardPanel umbenennen!
    private class BoardPanel extends JPanel {
        final List<TilePanel> boardTiles;

        BoardPanel(){
            super(new GridLayout(8,8));
            this.boardTiles = new ArrayList<>();
            for(int i = 0; i< BoardUtils.NUM_TILES; i++){
                final TilePanel tilePanel = new TilePanel(this, i);
                this.boardTiles.add(tilePanel);
                add(tilePanel);
            }
            setPreferredSize(BOARD_PANEL_DIMENSION);
            validate();
        }

        public void drawBoard(final ChessBoard board){
            removeAll();
            for(final TilePanel tilePanel : boardDirection.traverse(boardTiles)){
                tilePanel.drawTile(board);
                add(tilePanel);
            }
            validate();
            repaint();
        }
    }

    public static class MoveLog{

        private final List<Move> moves;

        MoveLog(){
            this.moves = new ArrayList<>();
        }

        public List<Move> getMoves() {
            return this.moves;
        }

        public void addMove(final Move move){
            this.moves.add(move);
        }

        public int size(){
            return this.moves.size();
        }

        public void clear(){
            this.moves.clear();
        }

        public boolean removeMove(final Move move){
            return this.moves.remove(move);
        }

        public Move removeMove(int index){
            return this.moves.remove(index);
        }


    }

    private class TilePanel extends JPanel {

        private final int tileId;

        TilePanel(final BoardPanel boardPanel, final int tileId){
            super(new GridBagLayout());
            this.tileId = tileId;
            setPreferredSize(TILE_PANEL_DIMENSION);
            assignTileColor();
            assignTilePieceIcon(chessBoard);

            addMouseListener(new MouseListener() {
                @Override
                public void mouseClicked(final MouseEvent e) {
                    if(isRightMouseButton(e)){
                        sourceTile = null;
                        destinationTile = null;
                        humanMovedPiece = null;
                    }else if( isLeftMouseButton(e)){
                        if(sourceTile == null){
                            sourceTile = chessBoard.getTile(tileId);
                            humanMovedPiece = sourceTile.getPiece();
                            if(humanMovedPiece == null){
                                sourceTile = null;
                            }
                        }else{
                            destinationTile = chessBoard.getTile(tileId);
                            final Move move = createMove(chessBoard,
                                    sourceTile.getTileCoordinate(), destinationTile.getTileCoordinate());
                            final MoveTransition transition = chessBoard.currentPlayer().makeMove(move);
                            if(transition.getMoveStatus().isDone()){
                                chessBoard = transition.getTransitionBoard();
                                moveLog.addMove(move);
                            }
                            sourceTile = null;
                            destinationTile = null;
                            humanMovedPiece = null;
                        }
                        SwingUtilities.invokeLater(new Runnable() {
                            @Override
                            public void run() {
                                gameHistoryPanel.redo(chessBoard, moveLog);
                                takenPiecesPanel.redo(moveLog, boardDirection);
                                boardPanel.drawBoard(chessBoard);
                            }
                        });
                    }
                }

                @Override
                public void mousePressed(final MouseEvent e) {
                }

                @Override
                public void mouseReleased(final MouseEvent e) {
                }

                @Override
                public void mouseEntered(final MouseEvent e) {
                }

                @Override
                public void mouseExited(final MouseEvent e) {
                }
            });

            validate();
        }

        public void drawTile(final ChessBoard board){
            assignTileColor();
            assignTilePieceIcon(board);
            highlightLegals(board);
            validate();
            repaint();
        }

        private void assignTilePieceIcon(final ChessBoard board){
            this.removeAll();
            if(board.getTile(this.tileId).isTileOccupied()){
                try {
                    final BufferedImage image = ImageIO.read(new File(defaultPieceImagePath +
                            board.getTile(this.tileId).getPiece().getPieceAlliance().toString().charAt(0) +
                            board.getTile(this.tileId).getPiece() + ".gif"));
                    add(new JLabel(new ImageIcon(image)));
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        private void highlightLegals(final ChessBoard board){
            if(highlightLegalMoves){
                for (final Move move: pieceLegalMoves(board)){
                    if(move.getDestinationCoordinate() == this.tileId) {
                        try {
                            add(new JLabel(new ImageIcon(ImageIO.read(new File("art/misc/green_dot.png")))));
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        }

        private List<Move> pieceLegalMoves(final ChessBoard board){
            List<Move> realLegalMoves = new ArrayList<>();
            if(humanMovedPiece!= null && humanMovedPiece.getPieceAlliance() == board.currentPlayer().getAlliance()){
                for(Move move : humanMovedPiece.calculateLegalMoves(board)) {
                    final MoveTransition transition = board.currentPlayer().makeMove(move);
                    if (transition.getMoveStatus().isDone()) {
                        realLegalMoves.add(move);
                    }
                }
            }
            return ImmutableList.copyOf(realLegalMoves);
        }

        private void assignTileColor() {
            if(BoardUtils.EIGHTH_RANK[this.tileId] ||
               BoardUtils.SIXTH_RANK[this.tileId] ||
               BoardUtils.FOURTH_RANK[this.tileId] ||
               BoardUtils.SECOND_RANK[this.tileId]){
                setBackground(this.tileId % 2 == 0 ? lightTileColor : darkTileColor);
            }else if(BoardUtils.SEVENTH_RANK[this.tileId] ||
                     BoardUtils.FIFTH_RANK[this.tileId] ||
                     BoardUtils.THIRD_RANK[this.tileId] ||
                     BoardUtils.FIRST_RANK[this.tileId]){
                setBackground(this.tileId % 2 != 0 ? lightTileColor : darkTileColor);
            }
        }
    }
}