// GlassPanel.java
package com.guardian.ui;

import javax.swing.*;
import java.awt.*;

public class GlassPanel extends JPanel {

    public GlassPanel(LayoutManager layout) {
        super(layout);
        setOpaque(false);
    }

    public GlassPanel() {
        this(new BorderLayout());
    }
}
