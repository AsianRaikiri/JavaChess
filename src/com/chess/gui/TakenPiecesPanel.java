package com.chess.gui;

import com.chess.engine.board.Move;
import com.chess.engine.pieces.ChessPiece;
import com.google.common.primitives.Ints;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.EtchedBorder;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.List;

import static com.chess.gui.Table.*;

public class TakenPiecesPanel extends JPanel {

    private final JPanel northPanel;
    private final JPanel southPanel;
    private static final Dimension TAKEN_PIECES_DIMENSION = new Dimension(40,80);
    private static final Color PANEL_COLOR = Color.decode("0xFDF5E6");
    private static final EtchedBorder PANEL_BORDER = new EtchedBorder(EtchedBorder.RAISED);

    public TakenPiecesPanel(){
        super(new BorderLayout());
        setBackground(PANEL_COLOR);
        setBorder(PANEL_BORDER);
        this.northPanel = new JPanel(new GridLayout(8,2));
        this.southPanel = new JPanel(new GridLayout(8,2));
        this.northPanel.setBackground(PANEL_COLOR);
        this.southPanel.setBackground(PANEL_COLOR);
            add(this.northPanel, BorderLayout.NORTH);
            add(this.southPanel, BorderLayout.SOUTH);
        setPreferredSize(TAKEN_PIECES_DIMENSION);
    }

    public void redo(final MoveLog moveLog, BoardDirection boardDirection){
        southPanel.removeAll();
        northPanel.removeAll();
        final List<ChessPiece> whiteTakenPieces = new ArrayList<>();
        final List<ChessPiece> blackTakenPieces = new ArrayList<>();

        for(final Move move : moveLog.getMoves()){
            if(move.isAttack()){
                final ChessPiece takenPiece = move.getAttackedPiece();
                if(takenPiece.getPieceAlliance().isWhite()){
                    whiteTakenPieces.add(takenPiece);
                }else if(takenPiece.getPieceAlliance().isBlack()){
                    blackTakenPieces.add(takenPiece);
                }else{
                    throw new RuntimeException("This should not be possible!");
                }
            }
        }
        Collections.sort(whiteTakenPieces, new Comparator<ChessPiece>() {
            @Override
            public int compare(final ChessPiece o1, final ChessPiece o2) {
                return Ints.compare(o1.getPieceValue(), o2.getPieceValue());
            }
        });
        Collections.sort(blackTakenPieces, new Comparator<ChessPiece>() {
            @Override
            public int compare(final ChessPiece o1, final ChessPiece o2) {
                return Ints.compare(o1.getPieceValue(), o2.getPieceValue());
            }
        });

        for(final ChessPiece takenPiece : whiteTakenPieces){
            try{
                final BufferedImage image = ImageIO.read(new File("art/stockfish/" +
                        takenPiece.getPieceAlliance().toString().charAt(0) + "" +
                        takenPiece + ".gif"));
                final ImageIcon icon = new ImageIcon(image);
                final JLabel imageLabel = new JLabel(new ImageIcon(icon.getImage().getScaledInstance(
                        icon.getIconWidth() - 15, icon.getIconWidth() - 15, Image.SCALE_SMOOTH)));
                if(boardDirection.isNormal()) {
                    this.northPanel.add(imageLabel);
                }else{
                    this.southPanel.add(imageLabel);
                }
            }catch(final IOException e){
                e.printStackTrace();
            }
        }
        for(final ChessPiece takenPiece : blackTakenPieces){
            try{
                final BufferedImage image = ImageIO.read(new File("art/stockfish/" +
                        takenPiece.getPieceAlliance().toString().charAt(0) + "" +
                        takenPiece + ".gif"));
                final ImageIcon icon = new ImageIcon(image);
                final JLabel imageLabel = new JLabel(new ImageIcon(icon.getImage().getScaledInstance(
                        icon.getIconWidth() - 15, icon.getIconWidth() - 15, Image.SCALE_SMOOTH)));
                if(boardDirection.isNormal()) {
                    this.southPanel.add(imageLabel);
                }else{
                    this.northPanel.add(imageLabel);
                }
            }catch(final IOException e){
                e.printStackTrace();
            }
        }
        validate();
    }
}
