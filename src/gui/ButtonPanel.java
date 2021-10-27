package gui;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class ButtonPanel extends JPanel {
    protected List<JButton> buttonList = new ArrayList<>();

    public ButtonPanel() {
        setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS));

    }

    public void updatePanel(JButton button) {
        add(button);
    }
}
