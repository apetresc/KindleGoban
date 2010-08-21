package com.amazon.kindle.app.go;

import java.awt.Container;
import java.awt.Font;
import java.awt.GridBagLayout;
import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.awt.KeyEventDispatcher;
import java.awt.KeyboardFocusManager;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.InputStreamReader;

import org.kwt.ui.KWTSelectableLabel;

import com.amazon.kindle.kindlet.AbstractKindlet;
import com.amazon.kindle.kindlet.KindletContext;
import com.amazon.kindle.kindlet.event.KindleKeyCodes;
import com.amazon.kindle.kindlet.ui.KButton;
import com.amazon.kindle.kindlet.ui.KLabel;
import com.amazon.kindle.kindlet.ui.KLabelMultiline;
import com.amazon.kindle.kindlet.ui.KMenu;
import com.amazon.kindle.kindlet.ui.KMenuItem;
import com.amazon.kindle.kindlet.ui.KPanel;

import com.amazon.kindle.app.go.model.sgf.IncorrectFormatException;
import com.amazon.kindle.app.go.model.sgf.SGFFilenameFilter;
import com.amazon.kindle.app.go.ui.KCommentArea;
import com.amazon.kindle.app.go.ui.KGoBoardComponent;

public class Main extends AbstractKindlet {

    private static final int SQUARE_SIZE = 40;
    private static final int STONE_SIZE  = SQUARE_SIZE;
    private static final int GLOBAL_X_OFFSET = 30;
    private static final int GLOBAL_Y_OFFSET = 20;

    private static final String SGF_DIR = "/sgf/";

    private Container root;
    private KLabel titleLabel;
    private KGoBoardComponent boardComponent;
    private KLabelMultiline commentComponent;

    private GoBoard board;
    private GoBoardController controller;
    private boolean boardHasFocus = true;

    class GlobalDispatcher implements KeyEventDispatcher {

        public boolean dispatchKeyEvent(final KeyEvent e) {
            if (e.isConsumed() || e.getID() == KeyEvent.KEY_RELEASED) return false;
            if (!boardHasFocus) return false;
            
            switch (e.getKeyCode()) {
            case KindleKeyCodes.VK_FIVE_WAY_RIGHT:
                if (controller != null) {
                    e.consume();
                    controller.nextMove();
                    commentComponent.setText(board.getComment());
                    commentComponent.repaint();
                    boardComponent.repaint();
                }
                return true;
            case KindleKeyCodes.VK_FIVE_WAY_LEFT:
                if (controller != null) {
                    e.consume();
                    controller.previousMove();
                    commentComponent.setText(board.getComment());
                    commentComponent.repaint();
                    boardComponent.repaint();
                }
                return true;
            default:
                return false;
            }
        }
        
    }
    
    public void create(final KindletContext context) {
        root = context.getRootContainer();
        final KPanel mainPanel = new KPanel(new GridBagLayout());
        
        KeyboardFocusManager.getCurrentKeyboardFocusManager().addKeyEventDispatcher(new GlobalDispatcher());

        board = new GoBoard(19);
        board.init();
        
        GridBagConstraints gc = new GridBagConstraints();
        
        titleLabel = new KLabel();
        titleLabel.setFont(new Font(null, Font.BOLD, 25));
        gc.gridx = 0;
        gc.gridy = 0;
        gc.weighty = 0.0;
        gc.anchor = GridBagConstraints.NORTH;
        mainPanel.add(titleLabel, gc);
        
        boardComponent = new KGoBoardComponent(board);
        boardComponent.setFocusable(true);
        gc.gridy = 1;
        gc.insets = new Insets(0, GLOBAL_X_OFFSET, 0, 0);
        mainPanel.add(boardComponent, gc);
        
        commentComponent = new KCommentArea(board.getSize() * SQUARE_SIZE + STONE_SIZE, 200);
        commentComponent.setFocusable(false);
        gc.gridy = 2;
        gc.weighty = 1.0;
        gc.fill = GridBagConstraints.BOTH;
        gc.insets = new Insets(0, GLOBAL_X_OFFSET + STONE_SIZE/2, GLOBAL_Y_OFFSET, GLOBAL_X_OFFSET + STONE_SIZE/2);
        mainPanel.add(commentComponent, gc);
        
        final KMenu menu = new KMenu();
        final KMenuItem chooseSgfMenuItem = new KMenuItem("Choose SGF...");
        chooseSgfMenuItem.addActionListener(new ActionListener() {
            
            public void actionPerformed(ActionEvent arg0) {
                KButton okButton = new KButton("OK");
                File sgfDir = new File(context.getHomeDirectory(), "sgf");
                if (!sgfDir.exists()) {
                    sgfDir.mkdir();
                }
                final File[] sgfFiles = sgfDir.listFiles(new SGFFilenameFilter());
                String[] sgfList = new String[sgfFiles.length];
                
                for (int i = 0; i < sgfFiles.length; i++) {
                    sgfList[i] = sgfFiles[i].getName();
                }

                final KPanel sgfListPanel = new KPanel(new GridBagLayout());
                GridBagConstraints gc = new GridBagConstraints();
                gc.gridx = 0;
                gc.insets = new Insets(10, 10, 10, 10);
                gc.weighty = 0.0;
                gc.anchor = GridBagConstraints.WEST;
                
                for (int i = 0; i < sgfList.length; i++) {
                    final KWTSelectableLabel sgfLabel = new KWTSelectableLabel(sgfList[i]);
                    final File sgfFile = sgfFiles[i];
                    sgfLabel.setFocusable(true);
                    sgfLabel.setEnabled(true);
                    sgfLabel.setUnderlineStyle(KWTSelectableLabel.STYLE_DASHED);
                    sgfLabel.addActionListener(new ActionListener() {
                        
                        public void actionPerformed(ActionEvent arg0) {
                            board = new GoBoard(19);
                            board.init();
                            controller = new GoBoardController(board);
                            try {
                                controller.loadSGF(new BufferedReader(new FileReader(sgfFile)));
                                controller.nextMove();
                            } catch (FileNotFoundException e) {
                                e.printStackTrace();
                            } catch (IncorrectFormatException e) {
                                e.printStackTrace();
                            }
                            mainPanel.remove(boardComponent);
                            boardComponent = new KGoBoardComponent(board);
                            GridBagConstraints gc = new GridBagConstraints();
                            gc.gridx = 0;
                            gc.gridy = 1;
                            gc.insets = new Insets(0, GLOBAL_X_OFFSET, 0, 0);
                            gc.weighty = 0.0;
                            gc.anchor = GridBagConstraints.NORTH;
                            mainPanel.add(boardComponent, gc);
                            
                            commentComponent.setText(board.getComment());
                            refreshTitle();
                            
                            root.remove(sgfListPanel);
                            root.add(mainPanel);
                            boardHasFocus = true;
                            root.repaint();
                        }
                    });
                    gc.gridy = i;
                    sgfListPanel.add(sgfLabel, gc);
                    
                }
                

                okButton.addActionListener(new ActionListener() {
                    public void actionPerformed(ActionEvent e) {
                        root.remove(sgfListPanel);
                        root.add(mainPanel);
                        boardHasFocus = true;
                        root.repaint();
                    }
                });
                gc.gridy = gc.gridy + 1;
                sgfListPanel.add(okButton, gc);
                
                boardHasFocus = false;
                root.remove(mainPanel);
                root.add(sgfListPanel);
                okButton.requestFocus();
            }
        });
        menu.add(chooseSgfMenuItem);
        context.setMenu(menu);
      
        root.add(mainPanel);
        
        try {
            controller = new GoBoardController(board);
            controller.loadSGF(new BufferedReader(new InputStreamReader(getClass().getResourceAsStream(SGF_DIR + "sgf24.sgf"))));
            controller.nextMove();
            refreshTitle();
        } catch (IncorrectFormatException e) {
            e.printStackTrace();
        }
    }
    
    private void refreshTitle() {
        if (board.getPlayerWhite() != null && board.getPlayerBlack() != null) {
            if (board.getWhiteRank() != null && board.getBlackRank() != null) {
                titleLabel.setText(board.getPlayerWhite() + " (" + board.getWhiteRank() + ")" +
                        " vs. " + board.getPlayerBlack() + " (" + board.getBlackRank() + ")");
            } else {
                titleLabel.setText(board.getPlayerWhite() + " vs. " + board.getPlayerBlack());
            }
        }
    }
    
}
