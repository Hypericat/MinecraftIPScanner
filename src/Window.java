import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.RoundRectangle2D;
import java.util.concurrent.atomic.AtomicBoolean;

public class Window extends JFrame {

    ButtonGroupManager buttonGroupManager;
    private String iconPath = "image\\stonksViewerIcon.png";
    Color themeColor = new Color(44, 27, 71);
    int cornerRadius = 25;
    private Dimension unMaximizedSize;
    public static int yWindowMoveThreshold = 40;
    private Window instance;
    int mousePressX = 0, mousePressY = 0;
    public Window(String name) {
        buttonGroupManager = new ButtonGroupManager();
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setLayout(null);
        this.setUndecorated(true);
        this.setTitle(name);
        //this.setIconImage(Toolkit.getDefaultToolkit().getImage(getClass().getResource(iconPath)));
        setStyling(this.getContentPane());
        this.setOpacity(0.90f);
        instance = this;
    }
    public void initDecorations() {
        addCloseButton("X", new Bound(this.getWidth() - 50, 10, 40, 29));
        addMaximizeButton("O", new Bound(this.getWidth() - 95, 10, 40, 29));
        addMinimizeButton("-", new Bound(this.getWidth() - 140, 10, 40, 29));

        //Logic to allow dragging and moving of the window
        this.addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent me) {
                mousePressX = me.getX();
                mousePressY = me.getY();
            }
        });
        addMouseMotionListener(new MouseMotionAdapter() {
            public void mouseDragged(MouseEvent me) {
                //return if the mouse is lower than the move threshold or if the screen is maximized or else
                if (!(mousePressY <= yWindowMoveThreshold) || getExtendedState() != JFrame.NORMAL) return;
                instance.setLocation(instance.getLocation().x + (me.getX() - mousePressX), instance.getLocation().y + (me.getY() - mousePressY));
            }
        });

    }
    public JTextField addTextBox(Bound bound) {
        return addTextBox("", bound);
    }
    public void setBackground(Component component) {
        component.setBackground(themeColor);
    }
    public boolean shouldFocus(Component component) {
        return (component instanceof JTextField);
    }
    public void setStyling(Component component) {
        setBackground(component);
        component.setForeground(Color.WHITE);
        component.setFont(Font.getFont(Font.MONOSPACED));
        component.setFocusable(shouldFocus(component));
    }
    public JTextField addTextBox(String text, Bound bound) {
        JTextField box = new JTextField(text);
        box.setBounds(bound.toRect());
        setStyling(box);
        this.add(box);
        return box;
    }
    //Overrides superclass in order to log the size for resetting after unMaximizing
    @Override
    public void setSize(int width, int height) {
        this.setMaximumSize(this.getToolkit().getScreenSize());
        this.unMaximizedSize = new Dimension(width, height);
        super.setSize(width, height);
    }
    @Override
    public void setSize(Dimension dimension) {
        this.setMaximumSize(this.getToolkit().getScreenSize());
        this.unMaximizedSize = dimension;
        super.setSize(dimension);
    }
    public JButton addButton(String text, Bound bound, ActionListener actionListener) {
        JButton button = new JButton(text);
        button.setBounds(bound.toRect());
        setStyling(button);
        if (actionListener != null) {
            button.addActionListener(actionListener);
        }
        this.add(button);
        return button;
    }
    public JButton getButton(String text, Bound bound, ActionListener actionListener) {
        JButton button = new JButton(text);
        button.setBounds(bound.toRect());
        setStyling(button);
        if (actionListener != null) {
            button.addActionListener(actionListener);
        }
        return button;
    }
    public void init() {
        Dimension screen = this.getToolkit().getScreenSize();
        this.setSize(10000, 10000);
        this.setLayout(null);
        this.setShape(new RoundRectangle2D.Float(0, 0, this.getWidth(), this.getHeight(), cornerRadius, cornerRadius));
        unMaximizedSize = new Dimension(1200, 900);
        maximize();
        unMaximize();
        this.setLocation(screen.width / 2 - this.getWidth() / 2, screen.height / 2 - this.getHeight() / 2);
        initDecorations();
        this.addLabelCentered("MC Json Searcher", new Bound(this.getWidth() / 2, 15, 0, 0), 30);
        this.setVisible(true);
    }
    public ButtonGroupID addRadio(String optionName, Bound bound, ButtonGroupID group) {
        JRadioButton radio = new JRadioButton(optionName);
        radio.setBounds(bound.toRect());
        setStyling(radio);
        group.add(radio);
        this.add(radio);
        return group;
    }
    public void addMinimizeButton(String text, Bound bound) {
        JButton button = new JButton();
        button.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {setState(Frame.ICONIFIED);
            }
        });
        addSpecialButton(button, text, bound);
    }
    public void addMaximizeButton(String text, Bound bound) {
        JButton button = new JButton();
        button.addActionListener(e -> {
            //Look at the current state of the screen and either call maximize or unMaximize
            if (instance.getExtendedState() == JFrame.NORMAL)
                maximize();
            else
                unMaximize();
        });
        addSpecialButton(button, text, bound);

    }
    public void maximize() {
        setExtendedState(JFrame.MAXIMIZED_BOTH);
    }
    public void unMaximize() {
        Dimension screen = this.getToolkit().getScreenSize();
        super.setSize(unMaximizedSize);
        this.setLocation(screen.width / 2 - this.getWidth() / 2, screen.height / 2 - this.getHeight() / 2);
        setExtendedState(JFrame.NORMAL);
    }
    public void addSpecialButton(JButton button, String text, Bound bound) {
        button.setBounds(bound.toRect());
        button.setContentAreaFilled(false);
        button.setBorderPainted(false);
        setBackground(button);
        JLabel label = new JLabel(text);
        label.setForeground(Color.WHITE);
        label.setFont(new Font("Default", Font.BOLD, 20));
        JPanel panel = new JPanel();
        panel.add(label);
        panel.add(button);
        panel.setBounds(bound.toRect());
        panel.setComponentZOrder(label, panel.getComponentCount() - 1);
        setBackground(panel);
        this.add(panel);
        this.add(button);
    }
    public void addCloseButton(String text, Bound bound) {
        JButton button = new JButton();
        button.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                System.exit(0);
            }
        });
        addSpecialButton(button, text, bound);
    }

    /**
     *
     * @param optionName
     * Name that displays next to the radio option
     * @param bound
     * Self-explanatory check Bound class for more details
     * @param id
     * Group identifier check ButtonGroupManager and ButtonGroupID for more details
     * @return return the ButtonGroupID to add multiple radios to the same group easily.
     * Can also be found using an instance of the radio through ButtonGroupManager.getGroup(AbstractButton)
     *
     * May return null if the specified id is invalid
     */
    public ButtonGroupID addRadio(String optionName, Bound bound, int id) {
        ButtonGroupID group = getButtonGroupManager().getGroup(id);
        if (group == null) return null;
        return addRadio(optionName, bound, group);
    }
    public ButtonGroupID addRadio(String optionName, Bound bound) {
        ButtonGroupID group = buttonGroupManager.addGroup();
        return addRadio(optionName, bound, group);
    }
    public ButtonGroupManager getButtonGroupManager() {
        return this.buttonGroupManager;
    }
    public JLabel addLabel(String text, Bound bound) {
        JLabel label = new JLabel(text);
        label.setBounds(bound.toRect());
        setStyling(label);
        this.add(label);
        return label;
    }
    public JLabel getLabel(String text, Bound bound) {
        JLabel label = new JLabel(text);
        label.setBounds(bound.toRect());
        setStyling(label);
        return label;
    }
    public void addCheckBox(String text, Bound bound, AtomicBoolean bool) {
        JCheckBox box = new JCheckBox(text, bool.get());
        box.setBounds(bound.toRect());
        setStyling(box);
        box.addItemListener(e -> bool.set(!bool.get()));
        this.add(box);
    }
    public void addLabelCentered(String text, Bound bound, int size) {
        JLabel label = new JLabel(text);
        setStyling(label);
        label.setFont(new Font("Serif", Font.BOLD, size));
        label.setBounds(bound.getX() - label.getPreferredSize().width / 2, bound.getY() - label.getPreferredSize().height / 2, label.getPreferredSize().width, label.getPreferredSize().height);

        this.add(label);
    }
}