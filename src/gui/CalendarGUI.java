package gui;

import ast.CalendarEvent;

import ast.Date;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;
import java.awt.*;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.lang.reflect.Array;
import java.util.*;
import java.util.List;
import java.util.Calendar;

public class CalendarGUI implements ComponentListener {
    private int year, month;
    private String monthName;
    private JFrame mainFrame;
    private JTable table;
    private String[] months = {"Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug",
    "Sept", "Oct", "Nov", "Dec"};
    private String[] days = {"Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat"};
    private DefaultTableModel tbm;
    private JPanel panel;
    private JScrollPane scrollPane;
    private CalendarEvent[] events;
    private List<Event> eventButtonList = new ArrayList<>();
    private HashMap<String, Color> colorDictionary = makeColorDict();
    // what the calendar takes in will be changed later once we know what data format the back-end is giving us.
    public CalendarGUI(int year, Date date, CalendarEvent[] events) {
        this.year = year;
        this.monthName = date.getMonth();
        this.events = events;
    }


    // makes the frame and other containers of the calendar.
    public void makeCalendar() {
        try {
            for (UIManager.LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (Exception e) {
            System.out.println("UI is not available");
        }
        // set up main frame
        mainFrame = new JFrame("Calendar");
        mainFrame.setLayout(null);
        mainFrame.setSize(1920,1080);
        Container pane = mainFrame.getContentPane();
        pane.setLayout(null);

        // set up calendar components
        JLabel monthLabel = new JLabel(this.monthName);
        tbm = new DefaultTableModel();
        table = new JTable(tbm);
        table.setBackground(new Color(255, 255, 255));

        scrollPane = new JScrollPane(table);
        panel = new JPanel(null);
        panel.setLayout(new BoxLayout(panel, BoxLayout.PAGE_AXIS));
        panel.add(monthLabel);
        panel.add(scrollPane);
        pane.add(panel);
        panel.setBounds(0,0,mainFrame.getWidth() - 10, mainFrame.getHeight() - 10);
        monthLabel.setBounds(0, 0, mainFrame.getWidth(), mainFrame.getHeight() / 10);
        scrollPane.setBounds(0, 0, mainFrame.getWidth() - 30, mainFrame.getHeight() - 30);


        for (int i = 0; i < months.length; i++) {
            if (this.monthName.toLowerCase().equals(months[i].toLowerCase())) {
                month = i + 1;
            }
        }

        // set up the jtable for calendar
        setUpTable(mainFrame.getHeight());
        generateEventButtons();

        // final main frame set up
        mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        mainFrame.addComponentListener(this);
        mainFrame.setLocationRelativeTo(null);
        mainFrame.setVisible(true);

    }

    // populates and sets up the table
    private void setUpTable(int height) {
        for (int i = 0; i < 7; i++) {
            tbm.addColumn(days[i]);
        }
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.setRowHeight(height / 7);
        tbm.setColumnCount(7);
        tbm.setRowCount(6);
        for (int i=0; i<6; i++){
            for (int j=0; j<7; j++){
                tbm.setValueAt(null, i, j);
            }
        }


        // using the GregorianCalendar
        GregorianCalendar gCal = new GregorianCalendar(this.year, this.month, 1);
        TimeZone timezone = TimeZone.getTimeZone("PST");
        gCal.setTimeZone(timezone);
        int daysInMonth = gCal.getActualMaximum(GregorianCalendar.DAY_OF_MONTH);
        int firstDayOfMonth = gCal.get(GregorianCalendar.DAY_OF_WEEK);

        for (int i = 1; i < daysInMonth + 1; i++) {
            int row = (i + firstDayOfMonth - 3) / 7;
            int column = (i + firstDayOfMonth - 3)%7;
            tbm.setValueAt(i, row, column);
        }


        // custom renderer and editor for jtable cells
        for (int i = 0; i < table.getColumnCount(); i++) {
            table.getColumnModel().getColumn(i).setCellRenderer(new ButtonRenderer());
            table.getColumnModel().getColumn(i).setCellEditor(new ButtonEditor());
        }
        table.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int rowSelected = table.rowAtPoint(e.getPoint());
                int columnSelected = table.columnAtPoint(e.getPoint());
                if (rowSelected >= 0 && columnSelected >= 0) {
                    System.out.println(table.getValueAt(rowSelected, columnSelected));
                }
            }
        });
    }


    // helper for converting calendarEvents to event buttons
    private void generateEventButtons() {
        for (CalendarEvent e: events) {
            Event event = new Event(e.getName(), e.getType(), e.getDate(), e.getStart_time(), e.getEnd_time(), e.getPlace(), colorDictionary.get(e.getColor().toLowerCase()), e.isPriority());
            eventButtonList.add(event);
        }
        Collections.sort(eventButtonList);
    }


    // custom renderer for rendering buttons onto each individual cell.
    class ButtonRenderer implements TableCellRenderer {
        ButtonPanel panel = new ButtonPanel();
        JScrollPane scroll = new JScrollPane(panel);
        @Override public Component getTableCellRendererComponent(
                JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            panel = new ButtonPanel();
            scroll = new JScrollPane(panel);
            panel.setBackground(table.getBackground());
            if (table.getValueAt(row, column) != null) {
                panel.add(new JLabel(value.toString()));
                for (Event e: eventButtonList) {
                    if ((int) value == e.getDate().getDay()) {
                        panel.updatePanel(e);
                    }
                }
            }
            return scroll;
        }
    }


    // custom editor for jtable cells
    class ButtonEditor extends AbstractCellEditor implements TableCellEditor {
         ButtonPanel panel = new ButtonPanel();
         JScrollPane scroll = new JScrollPane(panel);
         int value = 0;

        @Override
        public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
            panel = new ButtonPanel();
            scroll = new JScrollPane(panel);
            panel.setBackground(table.getBackground());
            if (table.getValueAt(row, column) != null) {
                panel.add(new JLabel(value.toString()));
                for (Event e: eventButtonList) {
                    if ((int) value == e.getDate().getDay()) {
                        panel.updatePanel(e);
                    }
                }
            }
            if (value == null) {
                this.value = 0;
            }
            else {
                this.value = (int) value;
            }
            return scroll;
        }

        @Override
        public Object getCellEditorValue() {
            if (value != 0) {
                return value;
            }
            else return null;
        }

    }


    // method used to dynamically resize components when frame is resized.
    @Override
    public void componentResized(ComponentEvent e) {
        table.setRowHeight(mainFrame.getHeight() / 7);
        panel.setBounds(0,0,mainFrame.getWidth() - 10, mainFrame.getHeight() - 10);
        scrollPane.setBounds(10, 20, mainFrame.getWidth() - 30, mainFrame.getHeight() - 30);

    }

    @Override
    public void componentMoved(ComponentEvent e) {

    }

    @Override
    public void componentShown(ComponentEvent e) {

    }

    @Override
    public void componentHidden(ComponentEvent e) {

    }

    private HashMap<String, Color> makeColorDict(){
        HashMap<String, Color> colorDict = new HashMap<>();
        colorDict.put("red", Color.RED);
        colorDict.put("blue", Color.BLUE);
        colorDict.put("green", Color.GREEN);
        colorDict.put("yellow", Color.YELLOW);
        colorDict.put("orange", Color.ORANGE);
        colorDict.put("pink", Color.PINK);
        colorDict.put("cyan", Color.CYAN);
        return colorDict;
    }
}
