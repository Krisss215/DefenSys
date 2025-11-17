// GlassRow.java
package com.guardian.ui;
import javax.swing.*;
import java.awt.*;

public class GlassRow extends JPanel {

    public GlassRow() {
        setOpaque(false);
        setLayout(new GridLayout(1, 0, 16, 0));
    }
}
