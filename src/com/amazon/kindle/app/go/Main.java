package com.amazon.kindle.app.go;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.GridBagLayout;
import java.awt.GridBagConstraints;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.KeyEventDispatcher;
import java.awt.KeyboardFocusManager;
import java.awt.Transparency;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.InputStreamReader;

import org.apache.log4j.Logger;

import com.amazon.kindle.kindlet.AbstractKindlet;
import com.amazon.kindle.kindlet.KindletContext;
import com.amazon.kindle.kindlet.event.KindleKeyCodes;
import com.amazon.kindle.kindlet.ui.KBoxLayout;
import com.amazon.kindle.kindlet.ui.KButton;
import com.amazon.kindle.kindlet.ui.KImage;
import com.amazon.kindle.kindlet.ui.KLabel;
import com.amazon.kindle.kindlet.ui.KLabelMultiline;
import com.amazon.kindle.kindlet.ui.KMenu;
import com.amazon.kindle.kindlet.ui.KMenuItem;
import com.amazon.kindle.kindlet.ui.KPanel;
import com.amazon.kindle.kindlet.ui.KTextArea;
import com.amazon.kindle.kindlet.ui.KindletUIResources;
import com.amazon.kindle.kindlet.ui.KindletUIResources.KColorName;
import com.amazon.kindle.kindlet.ui.border.KEmptyBorder;
import com.amazon.kindle.kindlet.ui.border.KLineBorder;
import com.amazon.kindle.kindlet.ui.image.ImageUtil;

import com.amazon.kindle.app.go.model.sgf.IncorrectFormatException;
import com.amazon.kindle.app.go.model.sgf.SGFFilenameFilter;
import com.amazon.kindle.app.go.ui.KCommentArea;
import com.amazon.kindle.app.go.ui.KSelectableLabel;

public class Main extends AbstractKindlet {

    private static final int SQUARE_SIZE = 40;
    private static final int STONE_SIZE  = SQUARE_SIZE;
    private static final int STAR_SIZE = STONE_SIZE/5;
    private static final int GLOBAL_X_OFFSET = 30;
    private static final int GLOBAL_Y_OFFSET = 20;
    private static final int BORDER_WIDTH = 4;

    private static final String SGF_DIR = "/sgf/";

    private KindletContext context;
    private Container root;
    /** An image of the current position on the GoBoard */
    private BufferedImage boardImage;
    /** The KImage component containing <code>boardImage</code> */
    private KImage boardComponent;
    private KLabelMultiline commentComponent;

    private GoBoard board;
    private GoBoardController controller;
    private boolean boardHasFocus = true;

    private final Logger log = Logger.getLogger(Main.class);

    class GlobalDispatcher implements KeyEventDispatcher {

        public boolean dispatchKeyEvent(final KeyEvent e) {
            if (e.isConsumed() || e.getID() == KeyEvent.KEY_RELEASED) return false;
            if (!boardHasFocus) return false;
            
            switch (e.getKeyCode()) {
            case KindleKeyCodes.VK_FIVE_WAY_RIGHT:
                if (controller != null) {
                    e.consume();
                    int[][] affectedStones = controller.nextMove();
                    if (affectedStones.length == 1) {
                        drawBoard(board, affectedStones, true);
                    } else {
                        drawBoard(board);
                    }
                }
                return true;
            case KindleKeyCodes.VK_FIVE_WAY_LEFT:
                if (controller != null) {
                    e.consume();
                    controller.previousMove();
                    drawBoard(board);
                }
                return true;
            default:
                return false;
            }
        }
        
    }
    
    public void create(final KindletContext context) {
        this.context = context;
        root = context.getRootContainer();
        final KPanel mainPanel = new KPanel(new GridBagLayout());
        
        KeyboardFocusManager.getCurrentKeyboardFocusManager().addKeyEventDispatcher(new GlobalDispatcher());

        board = new GoBoard(19);
        board.init();

        boardImage = ImageUtil.createCompatibleImage(board.getSize() * SQUARE_SIZE + STONE_SIZE + GLOBAL_X_OFFSET, board.getSize() * SQUARE_SIZE + STONE_SIZE + GLOBAL_Y_OFFSET, Transparency.OPAQUE);		
        
        boardComponent = new KImage(boardImage);
        boardComponent.setFocusable(true);
        GridBagConstraints gc = new GridBagConstraints();
        gc.gridx = 0;
        gc.gridy = 0;
        gc.insets = new Insets(0, 0, 0, 0);
        gc.weighty = 0.0;
        gc.anchor = GridBagConstraints.NORTH;
        mainPanel.add(boardComponent, gc);
        
        commentComponent = new KCommentArea(board.getSize() * SQUARE_SIZE + STONE_SIZE, 200);
        commentComponent.setFocusable(false);
        gc.gridy = 1;
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
                    final KLabel sgfLabel = new KSelectableLabel(sgfList[i]);
                    final File sgfFile = sgfFiles[i];
                    sgfLabel.setFocusable(true);
                    sgfLabel.setEnabled(true);
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
                            
                            root.remove(sgfListPanel);
                            root.add(mainPanel);
                            boardHasFocus = true;
                            root.repaint();
                            drawBoard(board);
                        }
                        
                    });
                    gc.gridy = i;
                    sgfListPanel.add(sgfLabel, gc);
                    
                }
                

                okButton.addActionListener(new ActionListener() {
                    public void actionPerformed(ActionEvent arg0) {
                        log.info("OkButton!");
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
        } catch (IncorrectFormatException e) {
            e.printStackTrace();
        }

        drawBoard(board);
    }

    public void drawBoard(GoBoard board) {
        drawBoard(board, true);
    }
    
    public void drawBoard(GoBoard board, int[][] affectedPoints, boolean repaint) {
        if (board.getComment() != null) {
            commentComponent.setText(board.getComment());
            commentComponent.repaint();
        }
        
        final int X_OFFSET = STONE_SIZE/2 + GLOBAL_X_OFFSET;
        final int Y_OFFSET = STONE_SIZE/2 + GLOBAL_Y_OFFSET;
        
        Graphics2D g = boardImage.createGraphics();
        
        for (int i = 0; i < affectedPoints.length; i++) {
            int x = affectedPoints[i][0];
            int y = affectedPoints[i][1];
            
            switch (board.getPoint(x, y)) {
            case GoBoard.BLACK:
                g.setColor(context.getUIResources().getColor(KColorName.BLACK));
                g.fillOval(X_OFFSET + x * SQUARE_SIZE - STONE_SIZE/2 - 1,
                        Y_OFFSET + y * SQUARE_SIZE - STONE_SIZE/2 - 1, 
                        STONE_SIZE+2,
                        STONE_SIZE+2);
                break;
            case GoBoard.WHITE:
                g.setColor(context.getUIResources().getColor(KColorName.BLACK));
                g.fillOval(X_OFFSET + x * SQUARE_SIZE - STONE_SIZE/2,
                        Y_OFFSET + y * SQUARE_SIZE - STONE_SIZE/2,
                        STONE_SIZE+1,
                        STONE_SIZE+1);
                g.setColor(context.getUIResources().getColor(KColorName.WHITE));
                g.fillOval(X_OFFSET + x * SQUARE_SIZE - STONE_SIZE/2 + BORDER_WIDTH/2,
                        Y_OFFSET + y * SQUARE_SIZE - STONE_SIZE/2 + BORDER_WIDTH/2,
                        STONE_SIZE - BORDER_WIDTH,
                        STONE_SIZE - BORDER_WIDTH);
                break;
            case GoBoard.BLANK:
                break;
            }
        }
        
        boardComponent.setImage(boardImage);
        boardComponent.repaint();
    }
    
    public void drawBoard(GoBoard board, boolean repaint) {
        log.info("Drawing board!\n\n");
        if (board.getComment() != null) {
            commentComponent.setText(board.getComment());
            commentComponent.repaint();
        }
        Graphics2D g = boardImage.createGraphics();
        g.setColor(context.getUIResources().getBackgroundColor(KindletUIResources.KColorName.WHITE));
        g.fillRect(0, 
                0, 
                board.getSize() * SQUARE_SIZE + STONE_SIZE + GLOBAL_X_OFFSET, 
                board.getSize() * SQUARE_SIZE + STONE_SIZE + GLOBAL_Y_OFFSET);

        final int X_OFFSET = STONE_SIZE/2 + GLOBAL_X_OFFSET;
        final int Y_OFFSET = STONE_SIZE/2 + GLOBAL_Y_OFFSET;

        g.setColor(context.getUIResources().getColor(KColorName.BLACK));
        // Draw border
        g.fillRect(0 + X_OFFSET, 
                0 + Y_OFFSET,
                BORDER_WIDTH, 
                (board.getSize() - 1) * SQUARE_SIZE);

        g.fillRect(0 + X_OFFSET,
                0 + Y_OFFSET,
                (board.getSize() - 1) * SQUARE_SIZE,
                BORDER_WIDTH);

        g.fillRect(0 + X_OFFSET,
                (board.getSize() - 1) * SQUARE_SIZE + Y_OFFSET,
                (board.getSize() - 1) * SQUARE_SIZE,
                BORDER_WIDTH);

        g.fillRect((board.getSize() - 1) * SQUARE_SIZE + X_OFFSET,
                0 + Y_OFFSET, 
                BORDER_WIDTH, 
                (board.getSize() - 1) * SQUARE_SIZE + BORDER_WIDTH);

        // Draw grid
        for (int x = 0; x < board.getSize(); x++) {
            g.drawLine(0 + X_OFFSET,
                    x * SQUARE_SIZE + Y_OFFSET,
                    (board.getSize() - 1) * SQUARE_SIZE + X_OFFSET,
                    x * SQUARE_SIZE + Y_OFFSET);
            g.drawLine(x * SQUARE_SIZE + X_OFFSET,
                    0 + Y_OFFSET,
                    x * SQUARE_SIZE + X_OFFSET,
                    (board.getSize() - 1) * SQUARE_SIZE + Y_OFFSET);
        }

        // Draw star points
        int[] starPoints = new int[3];
        switch (board.getSize()) {
        case 19:
            starPoints = new int[] {3, 9, 15};
            break;
        default:
            starPoints = new int[0];
        }

        for (int x = 0; x < starPoints.length; x++) {
            for (int y = 0; y < starPoints.length; y++) {
                if (board.getPoint(starPoints[x], starPoints[y]) == GoBoard.BLANK) {
                    g.fillOval(X_OFFSET + starPoints[x] * SQUARE_SIZE - STAR_SIZE/2,
                            Y_OFFSET + starPoints[y] * SQUARE_SIZE - STAR_SIZE/2,
                            STAR_SIZE,
                            STAR_SIZE);
                }
            }
        }

        // Draw the actual stones
        for (int x = 0; x < board.getSize(); x++) {
            for (int y = 0; y < board.getSize(); y++) {
                switch (board.getPoint(x, y)) {
                case GoBoard.BLACK:
                    g.setColor(context.getUIResources().getColor(KColorName.BLACK));
                    g.fillOval(X_OFFSET + x * SQUARE_SIZE - STONE_SIZE/2 - 1,
                            Y_OFFSET + y * SQUARE_SIZE - STONE_SIZE/2 - 1, 
                            STONE_SIZE+2,
                            STONE_SIZE+2);
                    break;
                case GoBoard.WHITE:
                    g.setColor(context.getUIResources().getColor(KColorName.BLACK));
                    g.fillOval(X_OFFSET + x * SQUARE_SIZE - STONE_SIZE/2,
                            Y_OFFSET + y * SQUARE_SIZE - STONE_SIZE/2,
                            STONE_SIZE+1,
                            STONE_SIZE+1);
                    g.setColor(context.getUIResources().getColor(KColorName.WHITE));
                    g.fillOval(X_OFFSET + x * SQUARE_SIZE - STONE_SIZE/2 + BORDER_WIDTH/2,
                            Y_OFFSET + y * SQUARE_SIZE - STONE_SIZE/2 + BORDER_WIDTH/2,
                            STONE_SIZE - BORDER_WIDTH,
                            STONE_SIZE - BORDER_WIDTH);
                    break;
                case GoBoard.BLANK:
                    break;
                }
            }
        }
        
        boardComponent.setImage(boardImage);
        boardComponent.repaint();
    }
}
