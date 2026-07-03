package pkg.d.util;

import javax.swing.*;
import javax.swing.border.BevelBorder;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class MyJLabel extends JLabel {

    public MyJLabel(String s, int w, int h) {
        Color labelColor = (new JButton()).getBackground();
        setText(s);
        setPreferredSize(new Dimension(w,h));
        setMinimumSize(new Dimension(w,h));
        setHorizontalAlignment((int) CENTER_ALIGNMENT);
        setBackground(labelColor.darker());
        setBorder(BorderFactory.createBevelBorder(BevelBorder.RAISED));
        setOpaque(true);
    }

    public MyJLabel(String s, int w, int h, int align) {
        Color labelColor = (new JButton()).getBackground();
        setText(s);
        setPreferredSize(new Dimension(w,h));
        setMinimumSize(new Dimension(w,h));
        setHorizontalAlignment(align);
        setBackground(labelColor.darker());
        // JLabel con padding
        EmptyBorder eBorder = new EmptyBorder(0, 5, 0, 5);
        BevelBorder bBorder = new BevelBorder(BevelBorder.RAISED);
        setBorder(BorderFactory.createCompoundBorder(bBorder, eBorder));
        setOpaque(true);
//        setFocusable(false);
    }
}
