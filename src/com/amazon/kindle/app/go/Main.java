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

import org.kwt.ui.KWTProgressBar;
import org.kwt.ui.KWTSelectableLabel;

import com.amazon.kindle.kindlet.AbstractKindlet;
import com.amazon.kindle.kindlet.KindletContext;
import com.amazon.kindle.kindlet.event.KindleKeyCodes;
import com.amazon.kindle.kindlet.ui.KBoxLayout;
import com.amazon.kindle.kindlet.ui.KLabel;
import com.amazon.kindle.kindlet.ui.KLabelMultiline;
import com.amazon.kindle.kindlet.ui.KMenu;
import com.amazon.kindle.kindlet.ui.KMenuItem;
import com.amazon.kindle.kindlet.ui.KPages;
import com.amazon.kindle.kindlet.ui.KPanel;
import com.amazon.kindle.kindlet.ui.pages.PageProviders;

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
    private KLabel descriptionLabel;
    private KGoBoardComponent boardComponent;
    private KWTProgressBar progressBar;
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
                    progressBar.incrementTick();
                    commentComponent.setText(board.getComment());
                    
                    progressBar.repaint();
                    boardComponent.repaint();
                    commentComponent.repaint();
                }
                return true;
            case KindleKeyCodes.VK_FIVE_WAY_LEFT:
                if (controller != null && controller.getCurrentMoveNumber() > 0) {
                    e.consume();
                    controller.previousMove();
                    progressBar.decrementTick();
                    commentComponent.setText(board.getComment());

                    progressBar.repaint();
                    boardComponent.repaint();
                    commentComponent.repaint();
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
        
        descriptionLabel = new KLabel();
        descriptionLabel.setFont(new Font(null, Font.BOLD, 18));
        gc.gridy = 1;
        mainPanel.add(descriptionLabel, gc);
        
        boardComponent = new KGoBoardComponent(board);
        boardComponent.setFocusable(true);
        gc.gridy = 2;
        gc.insets = new Insets(0, GLOBAL_X_OFFSET, 0, 0);
        mainPanel.add(boardComponent, gc);
        
        commentComponent = new KCommentArea(board.getSize() * SQUARE_SIZE + STONE_SIZE, 200);
        commentComponent.setFocusable(false);
        gc.gridy = 3;
        gc.weighty = 1.0;
        gc.fill = GridBagConstraints.BOTH;
        gc.insets = new Insets(0, GLOBAL_X_OFFSET + STONE_SIZE/2, GLOBAL_Y_OFFSET, GLOBAL_X_OFFSET + STONE_SIZE/2);
        mainPanel.add(commentComponent, gc);
        
        progressBar = new KWTProgressBar();
        progressBar.setLabelStyle(KWTProgressBar.STYLE_NONE);
        gc.gridy = 4;
        gc.insets = new Insets(0, GLOBAL_X_OFFSET + STONE_SIZE/2, 20, GLOBAL_X_OFFSET + STONE_SIZE/2);
        gc.fill = GridBagConstraints.HORIZONTAL;
        gc.anchor = GridBagConstraints.SOUTH;
        mainPanel.add(progressBar, gc);
        
        final KMenu menu = new KMenu();
        final KMenuItem chooseSgfMenuItem = new KMenuItem("Choose SGF...");
        chooseSgfMenuItem.addActionListener(new ActionListener() {
            
            public void actionPerformed(ActionEvent arg0) {
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
                final KPages sgfListPages = new KPages(PageProviders.createKBoxLayoutProvider(KBoxLayout.Y_AXIS));
                
                sgfListPages.setFocusable(true);
                sgfListPages.setEnabled(true);
                sgfListPages.setPageKeyPolicy(KPages.PAGE_KEYS_GLOBAL);
                
                for (int i = 0; i < sgfList.length; i++) {
                    final KWTSelectableLabel sgfLabel = new KWTSelectableLabel(sgfList[i]);
                    final File sgfFile = sgfFiles[i];
                    sgfLabel.setFocusable(true);
                    sgfLabel.setEnabled(true);
                    sgfLabel.setUnderlineStyle(KWTSelectableLabel.STYLE_DASHED);
                    sgfLabel.addActionListener(new ActionListener() {
                        
                        public void actionPerformed(ActionEvent e) {
                            sgfListPages.setPageKeyPolicy(KPages.PAGE_KEYS_DISABLED);
                            board = new GoBoard(19);
                            board.init();
                            controller = new GoBoardController(board);
                            try {
                                controller.loadSGF(new BufferedReader(new FileReader(sgfFile)));
                                controller.nextMove();
                            } catch (FileNotFoundException fnf) {
                                fnf.printStackTrace();
                            } catch (IncorrectFormatException ife) {
                                ife.printStackTrace();
                            }
                            mainPanel.remove(boardComponent);
                            boardComponent = new KGoBoardComponent(board);
                            GridBagConstraints gc = new GridBagConstraints();
                            gc.gridx = 0;
                            gc.gridy = 2;
                            gc.insets = new Insets(0, GLOBAL_X_OFFSET, 0, 0);
                            gc.weighty = 0.0;
                            gc.anchor = GridBagConstraints.NORTH;
                            mainPanel.add(boardComponent, gc);
                            
                            progressBar.setCurrentTick(0);
                            progressBar.setTotalTicks(controller.getMainBranchLength());
                            
                            commentComponent.setText(board.getComment());
                            refreshTitleAndDescription();
                            
                            root.remove(sgfListPanel);
                            root.add(mainPanel);
                            boardHasFocus = true;
                            root.repaint();
                        }
                    });
                    sgfListPages.addItem(sgfLabel);
                }
                
                boardHasFocus = false;
                GridBagConstraints gc = new GridBagConstraints();
                gc.gridx = 0;
                gc.gridy = 0;
                gc.insets = new Insets(20, 20, 20, 20);
                gc.anchor = GridBagConstraints.NORTH;
                gc.weightx = 1.0;
                gc.weighty = 0.0;
                gc.fill = GridBagConstraints.HORIZONTAL;
                sgfListPanel.add(new KLabelMultiline("Please select an SGF file from the ones below.\n" +
                        "To add more SGFs to KindleGoban, drop them into the sgf/ directory in this application's " +
                        "folder on your Kindle device."));
                
                gc.gridy = 1;
                gc.weighty = 1.0;
                gc.fill = GridBagConstraints.BOTH;
                sgfListPanel.add(sgfListPages, gc);
                
                root.remove(mainPanel);
                root.add(sgfListPanel);
                sgfListPages.first();
                sgfListPages.requestFocus();
            }
        });
        menu.add(chooseSgfMenuItem);
        context.setMenu(menu);
      
        root.add(mainPanel);
        
        try {
            controller = new GoBoardController(board);
            controller.loadSGF(new BufferedReader(new InputStreamReader(getClass().getResourceAsStream(SGF_DIR + "sgf24.sgf"))));
            controller.nextMove();
            
            progressBar.setTotalTicks(controller.getMainBranchLength());
            refreshTitleAndDescription();
        } catch (IncorrectFormatException e) {
            e.printStackTrace();
        }
    }
    
    private void refreshTitleAndDescription() {
        if (board.getPlayerWhite() != null && board.getPlayerBlack() != null) {
            if (board.getWhiteRank() != null && board.getBlackRank() != null) {
                titleLabel.setText(board.getPlayerWhite() + " (" + board.getWhiteRank() + ")" +
                        " vs. " + board.getPlayerBlack() + " (" + board.getBlackRank() + ")");
            } else {
                titleLabel.setText(board.getPlayerWhite() + " vs. " + board.getPlayerBlack());
            }
        }
        
        String description = "";
        if (board.getDate() != null) {
            description += board.getDate() + " ";
        }
        if (board.getEvent() != null) {
            description += "at " + board.getEvent() + " ";
        }
        if (board.getPlace() != null) {
            description += "in " + board.getPlace() + " ";
        }
        if (Character.isLetter(description.charAt(0))) {
            description = Character.toUpperCase(description.charAt(0)) + description.substring(1);
        }
        descriptionLabel.setText(description.trim());
    }
    
}
