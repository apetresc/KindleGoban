package com.amazon.kindle.app.go.ui;

import java.awt.AWTEvent;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.apache.log4j.Logger;

import com.amazon.kindle.kindlet.ui.KLabel;

public class KSelectableLabel extends KLabel {
    private static final long serialVersionUID = 8118660222383683366L;
    private static Logger logger = Logger.getLogger(KSelectableLabel.class);
    
    private static final int BUTTON_DOWN_EVENT = 401;
    
    private List actionListeners;
    
    public KSelectableLabel() {
        super();
        this.enableEvents(AWTEvent.KEY_EVENT_MASK);
        this.actionListeners = new LinkedList();
    }
    
    public KSelectableLabel(String text) {
        super(text);
        this.enableEvents(AWTEvent.KEY_EVENT_MASK);
        this.actionListeners = new LinkedList();
    }
    
    public Dimension getPreferredSize() {
        return this.getMinimumSize();
    }
    
    public Dimension getMinimumSize() {
        Dimension d = super.getPreferredSize();
        return new Dimension(d.width, d.height + 4);
    }

    public void paint(Graphics g) {
        super.paint(g);
        
        if (this.isFocusOwner()) {
            int y = super.getPreferredSize().height;
            g.setColor(Color.BLACK);
            g.fillRect(0, y + 1, this.getWidth(), y + 3);
        }
    }

    public void addActionListener(ActionListener listener) {
        this.actionListeners.add(listener);
    }
    
    public void processEvent(AWTEvent e) {
        logger.info("Processing event");
        switch(e.getID()) {
        case BUTTON_DOWN_EVENT:
            Iterator it = actionListeners.iterator();
            while (it.hasNext()) {
                logger.info("Dispatching event");
                ActionListener listener = (ActionListener) it.next();
                listener.actionPerformed(new ActionEvent(this, BUTTON_DOWN_EVENT, null));
            }
            break;
        default:
            break;
        }
    }    
}
