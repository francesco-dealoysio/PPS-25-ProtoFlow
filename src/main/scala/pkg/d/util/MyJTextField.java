package pkg.d.util;

import java.awt.BorderLayout;
import java.awt.Dimension;
import javax.swing.BorderFactory;
import javax.swing.JFrame;
import javax.swing.JTextField;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;

/**
 * Imposta le dimensioni di un TextField ed il numero massimo di caratteri che può contenere
 */
public class MyJTextField extends JTextField {

    public static void main(String[] args) {
        TestMyJTextField tmjtf = new TestMyJTextField();
        tmjtf.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        tmjtf.setVisible(true);
    }

    public MyJTextField(int width, int height, int _limit) {
        super();
        setPreferredSize(new Dimension(width, height));
        EmptyBorder eBorder = new EmptyBorder(0, 3, 0, 0);
        // LineBorder lBorder = new LineBorder(new Color(100, 100, 100)); 
        Border lBorder = getBorder();
        setBorder(BorderFactory.createCompoundBorder(lBorder, eBorder));
        setDocument(new JTextFieldLimit(_limit));
    }
}

/**
 * Classe di Test
 * @author Francesco de Aloysio
 */
class TestMyJTextField extends JFrame {
    public TestMyJTextField() {

        setTitle(getClass().getName());
        setSize(new Dimension(400,200));
        setLocationRelativeTo(null);
        setAlwaysOnTop(true);

        MyJTextField mtf = new MyJTextField(40,25,10);
        getContentPane().add(mtf, BorderLayout.NORTH);
    }
}
