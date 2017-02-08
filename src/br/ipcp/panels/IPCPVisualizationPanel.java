package br.ipcp.panels;

import br.ipcp.chart.InteractiveParallelCoordinatesChart;
import br.ipcp.data.Attribute;
import br.ipcp.data.DataSheet;
import br.ipcp.data.Sample;
import br.ipcp.panels.handlers.CheckBoxHandler;
import br.ipcp.panels.handlers.ListObjectsTransferHandler;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.beans.PropertyChangeEvent;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import javax.swing.BoxLayout;
import javax.swing.DefaultListModel;
import javax.swing.DropMode;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableModel;

public class IPCPVisualizationPanel extends JPanel implements MouseListener {
    
    private static final long serialVersionUID = 6269106746186163414L;
    
    private final InteractiveParallelCoordinatesChart chart;
    private final DataSheet dataSheet;
    
    private InteractiveParallelCoordinatesPanel chartPanel;
    private JScrollPane scrollChartPanel;
    
    public HashMap<JCheckBox, Attribute> attributeJbxList;
    private JList attrOrderList;
    
    private JCheckBox cbxFilteredOption;
    private JCheckBox cbxNotFilteredOption;
    private JCheckBox cbxSelectedOption;
    
    private JTabbedPane sampleTabbedPane;
    private JPanel panelFilteredOption;
    private JPanel panelNotFilteredOption;
    private JPanel panelSelectedOption;
    
    private JTable filteredTable;
    private JTable notFilteredTable;
    private JTable selectedTable;
    
    public final static int CONTEXT_FILTERED = 0;
    public final static int CONTEXT_NOT_FILTERED = 1;
    public final static int CONTEXT_SELECTED = 2;
    
    public IPCPVisualizationPanel(DataSheet dataSheet) {
        super();
        
        this.dataSheet = dataSheet;        
        this.chart = new InteractiveParallelCoordinatesChart(this.dataSheet, 1);
        
        this.buildPanels();
        this.fillSampleTables();
    }
    
    public JTable getSampleJTable(int context) throws IllegalArgumentException {
        JTable jtable;
        switch(context){
            case CONTEXT_FILTERED: jtable = this.filteredTable; break;
            case CONTEXT_NOT_FILTERED: jtable = this.notFilteredTable; break;
            case CONTEXT_SELECTED: jtable = this.selectedTable; break;
            default: throw new IllegalArgumentException("Wrong argument, see CONTEXT static variables.");
        }
        
        return jtable;
    }
    
    private JScrollPane buildAttributeCheckBoxPanel() {
        this.attributeJbxList = new HashMap<>();
        
        TitledBorder border = new TitledBorder("Display Axes");
        border.setTitleJustification(TitledBorder.CENTER);
        border.setTitlePosition(TitledBorder.TOP);
        
        JPanel cbxPanel = new JPanel();
        cbxPanel.setBorder(border);
        cbxPanel.setLayout(new BoxLayout(cbxPanel, BoxLayout.Y_AXIS));
        
        CheckBoxHandler cbxHandler = new CheckBoxHandler(this);
        
        for (int i = 0; i < this.dataSheet.getAttributes().size(); i++) {
            Attribute attr = this.dataSheet.getAttribute(i);
            
            JCheckBox cbx = new JCheckBox(attr.getName());
            cbx.setSelected(attr.isVisible());
            cbx.addItemListener(cbxHandler);
            cbxPanel.add(cbx);
            
            this.attributeJbxList.put(cbx, attr);
        }
        
        return new JScrollPane(cbxPanel);
    }
    
    private JScrollPane buildAttributeReorderListPanel() {
        this.attrOrderList.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        this.attrOrderList.setDragEnabled(true);
        this.attrOrderList.setDropMode(DropMode.INSERT);
        this.attrOrderList.setTransferHandler(new ListObjectsTransferHandler(this));
        
        this.redrawReorderList();
        
        TitledBorder border = new TitledBorder("Sort Axes");
        border.setTitleJustification(TitledBorder.CENTER);
        border.setTitlePosition(TitledBorder.TOP);
    
        JScrollPane attributesScrollPane = new JScrollPane(this.attrOrderList);
        attributesScrollPane.setBorder(border);
        
        return attributesScrollPane;
    }
    
    public void redrawReorderList() {
        ArrayList<Attribute> attrVisible = new ArrayList<>();
        ArrayList<Attribute> attrList = new ArrayList<>();
        
        DefaultListModel<Attribute> listModel = new DefaultListModel<>();
             
        for (int i = 0; i < this.dataSheet.getAttributes().size(); i++) {
            Attribute attr = this.dataSheet.getAttribute(i);
            if (attr.isVisible()) {
                attrVisible.add(attr);
            }
        }
        
        for(int i = 0; i < this.attrOrderList.getModel().getSize(); i++) {
            attrList.add((Attribute)this.attrOrderList.getModel().getElementAt(i));
        }
        
        for (int i = 0; i < attrList.size(); i++) {
            Attribute a = attrList.get(i);
            if(attrVisible.contains(a)) {
                listModel.addElement(a);
                attrVisible.remove(a);
            }
        }
        
        for (int i = 0; i < attrVisible.size(); i++) {
            Attribute a = attrVisible.get(i);
            listModel.addElement(a);
        }
        
        this.attrOrderList.setModel(listModel);
        
        this.recreateChart();
    }
    
    public void recreateChart() {        
        ArrayList<Attribute> attrs = new ArrayList<>();
        for (int i = 0; i < this.attrOrderList.getModel().getSize(); i++) {
            attrs.add((Attribute)this.attrOrderList.getModel().getElementAt(i));
        }
        
        DataSheet newDataSheet = this.dataSheet.createSubDataSheet(attrs);
        this.chart.setDataSheet(newDataSheet);
        
        if (this.chartPanel instanceof InteractiveParallelCoordinatesPanel) {
            this.chartPanel.repaint();
        }
        
        this.fillSampleTables();
    }
    
    private JPanel buildSampleOptionsPanel() {
        TitledBorder border = new TitledBorder("Sample Options");
        border.setTitleJustification(TitledBorder.CENTER);
        border.setTitlePosition(TitledBorder.TOP);
        
        JPanel cbxPanel = new JPanel();
        cbxPanel.setBorder(border);
        cbxPanel.setLayout(new BoxLayout(cbxPanel, BoxLayout.Y_AXIS));
        
        ItemListener il = (ItemEvent e) -> {
            this.fillSampleTables();
            this.chartPanel.setEnableChartPaint(true);
        };
        
        this.cbxFilteredOption = new JCheckBox("Show filtered");
        this.cbxFilteredOption.addItemListener(il);
        cbxPanel.add(this.cbxFilteredOption);
        
        this.cbxNotFilteredOption = new JCheckBox("Show not filtered");
        this.cbxNotFilteredOption.addItemListener(il);
        cbxPanel.add(this.cbxNotFilteredOption);
        
        this.cbxSelectedOption = new JCheckBox("Show selected");
        this.cbxSelectedOption.addItemListener(il);
        cbxPanel.add(this.cbxSelectedOption);
        
        JButton btClear = new JButton("Clear distortion");
        btClear.addActionListener((ActionEvent e) -> {
            IPCPVisualizationPanel.this.clearDistortion();
        });
        cbxPanel.add(btClear);
        
        return cbxPanel;
    }
    
    private JPanel buildSampleTabbedGridsPanel() {        
        JPanel panel = new JPanel(new GridLayout(1, 1));
        
        this.sampleTabbedPane = new JTabbedPane();

        //Filtered Tab
        this.panelFilteredOption = new JPanel(new GridLayout(1, 1));
        this.sampleTabbedPane.addTab("Filtered", null, this.panelFilteredOption, "Filtered Samples");
        this.sampleTabbedPane.setMnemonicAt(0, KeyEvent.VK_1);
        
        this.filteredTable = new JTable() {
            private static final long serialVersionUID = 2793957061432039023L;
            @Override
            public boolean isCellEditable(int row, int column) {                
                return false;               
            };
        };
        this.filteredTable.setPreferredScrollableViewportSize(new Dimension(500, 70));
        this.filteredTable.setFillsViewportHeight(true);

        JPanel jt = new JPanel(new GridLayout(1, 0));
        JScrollPane scrollPane = new JScrollPane(this.filteredTable);
        jt.add(scrollPane);
        this.panelFilteredOption.add(jt);
        
        //Not Filtered Tab
        this.panelNotFilteredOption = new JPanel(new GridLayout(1, 1));
        this.sampleTabbedPane.addTab("Not Filtered", null, this.panelNotFilteredOption, "Not Filtered Samples");
        this.sampleTabbedPane.setMnemonicAt(1, KeyEvent.VK_2);
        
        this.notFilteredTable = new JTable() {
            private static final long serialVersionUID = 2793957061432039023L;
            @Override
            public boolean isCellEditable(int row, int column) {                
                return false;               
            };
        };
        this.notFilteredTable.setPreferredScrollableViewportSize(new Dimension(500, 70));
        this.notFilteredTable.setFillsViewportHeight(true);

        JPanel jt2 = new JPanel(new GridLayout(1, 0));
        JScrollPane scrollPane2 = new JScrollPane(this.notFilteredTable);
        jt2.add(scrollPane2);
        this.panelNotFilteredOption.add(jt2);
        
        //Selected Tab
        this.panelSelectedOption = new JPanel(new GridLayout(1, 1));
        this.sampleTabbedPane.addTab("Selected", null, this.panelSelectedOption, "Selected Samples");
        this.sampleTabbedPane.setMnemonicAt(2, KeyEvent.VK_3);
        
        this.selectedTable = new JTable() {
            private static final long serialVersionUID = 2793957061432039023L;
            @Override
            public boolean isCellEditable(int row, int column) {                
                return false;               
            };
        };
        this.selectedTable.setPreferredScrollableViewportSize(new Dimension(500, 70));
        this.selectedTable.setFillsViewportHeight(true);

        JPanel jt3 = new JPanel(new GridLayout(1, 0));
        JScrollPane scrollPane3 = new JScrollPane(this.selectedTable);
        jt3.add(scrollPane3);
        this.panelSelectedOption.add(jt3);
        
        this.sampleTabbedPane.setTabLayoutPolicy(JTabbedPane.SCROLL_TAB_LAYOUT);
        panel.add(this.sampleTabbedPane);
        
        return panel;
    }
    
    private void buildPanels() {
        //Atributos - Lista
        JScrollPane attrCbxPanel = this.buildAttributeCheckBoxPanel();

        //Atributos - Permutação
        DefaultListModel<Attribute> listModel = new DefaultListModel<>();
        for (int i = 0; i < this.dataSheet.getAttributes().size(); i++) {
            listModel.addElement(this.dataSheet.getAttribute(i));
        }
        this.attrOrderList = new JList(listModel);
        JScrollPane attrListReorderListPanel = this.buildAttributeReorderListPanel();
        
        //Painel Esquerdo Superior Completo
        JSplitPane leftSplitPanel = new JSplitPane(JSplitPane.VERTICAL_SPLIT,
                                                attrCbxPanel, attrListReorderListPanel);
        leftSplitPanel.setOneTouchExpandable(true);
        leftSplitPanel.setDividerLocation(150);
        
        //Painel Esquero Inferior
        JPanel leftBottomPanel = this.buildSampleOptionsPanel();
        
        //Left Panel
        JPanel leftPanel = new JPanel(new BorderLayout());
        leftPanel.add(leftSplitPanel, BorderLayout.CENTER);
        leftPanel.add(leftBottomPanel, BorderLayout.SOUTH);        
        
        //Gráfico
        this.chartPanel = new InteractiveParallelCoordinatesPanel(this.dataSheet, this.chart);
        this.scrollChartPanel = new JScrollPane(this.chartPanel,
                                                    JScrollPane.VERTICAL_SCROLLBAR_NEVER,
                                                        JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        this.chartPanel.addMouseListener(this);

        //Right Panel        
        JPanel sampleTabbedGridsPanel = this.buildSampleTabbedGridsPanel();
        
        JSplitPane rightPanel = new JSplitPane(JSplitPane.VERTICAL_SPLIT,
                                                this.scrollChartPanel, sampleTabbedGridsPanel);
        rightPanel.setOneTouchExpandable(true);
        rightPanel.setDividerLocation(500);
        
        rightPanel.addPropertyChangeListener((PropertyChangeEvent evt) -> {
            chart.setFrameSize(scrollChartPanel.getSize());
        });
        
        rightPanel.addComponentListener(new ComponentListener() {
            @Override
            public void componentResized(ComponentEvent e) {
                chart.setFrameSize(scrollChartPanel.getSize());
            }

            @Override
            public void componentMoved(ComponentEvent e) {
                chart.setFrameSize(scrollChartPanel.getSize());
            }

            @Override
            public void componentShown(ComponentEvent e) {                
                chart.setFrameSize(scrollChartPanel.getSize());
            }

            @Override
            public void componentHidden(ComponentEvent e) {                
                chart.setFrameSize(scrollChartPanel.getSize());
            }
        });
        //rightPanel.setMinimumSize(new Dimension(400, 200));
        
        //Split
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT,
                                                leftPanel, rightPanel);
        splitPane.setOneTouchExpandable(true);
        splitPane.setDividerLocation(150);

        Dimension minimumSize = new Dimension(100, 50);
        attrListReorderListPanel.setMinimumSize(minimumSize);
        this.scrollChartPanel.setMinimumSize(minimumSize);

        splitPane.setPreferredSize(new Dimension(400, 200));

        this.setLayout(new BorderLayout());
        
        this.add(splitPane, BorderLayout.CENTER);
    }
    
    private ArrayList<Attribute> getAttributesFromList() {
        ArrayList<Attribute> aa = new ArrayList<>();
        for(int i = 0; i < this.attrOrderList.getModel().getSize(); i++) {
            aa.add((Attribute)this.attrOrderList.getModel().getElementAt(i));
        }
        
        return aa;
    }
    
    public DataSheet getSampleTableDataSheet(int context) {
        DataSheet subDS = new DataSheet();
        ArrayList<Attribute> subAttr = new ArrayList<>();
        ArrayList<Sample> subSamp = new ArrayList<>();
        
        ArrayList<Attribute> aa = this.getAttributesFromList();
        for (int i = 0; i < aa.size(); i++) {
            Attribute attr = aa.get(i);
            if(attr.isVisible()) {
                subAttr.add(attr);
            }
        }
        
        if (context == IPCPVisualizationPanel.CONTEXT_FILTERED) {
            for (int i = 0; i < this.dataSheet.getSampleCount(); i++) {
                Sample sample = this.dataSheet.getSample(i);
                if(sample.isActive(this.chart)) {
                    subSamp.add(sample);
                }
            }
        }
        
        if (context == IPCPVisualizationPanel.CONTEXT_NOT_FILTERED) {
            for (int i = 0; i < this.dataSheet.getSampleCount(); i++) {
                Sample sample = this.dataSheet.getSample(i);
                if(!sample.isActive(this.chart)) {
                    subSamp.add(sample);
                }
            }
        }
        
        if (context == IPCPVisualizationPanel.CONTEXT_SELECTED) {
            for (int i = 0; i < this.dataSheet.getSampleCount(); i++) {
                Sample sample = this.dataSheet.getSample(i);
                if(sample.isSelected()) {
                    subSamp.add(sample);
                }
            }
        }
        
        subDS.setAttributes(subAttr);
        subDS.setSamples(subSamp);
        
        return subDS;
    }
    
    public void fillSampleTables() {
        
        DecimalFormat df = new DecimalFormat("#.####");
        df.setRoundingMode(RoundingMode.HALF_UP);
        
        if(this.filteredTable != null) {
            if(this.cbxFilteredOption.isSelected()) {
                
                ArrayList<Attribute> aa = this.getAttributesFromList();
                
                DefaultTableModel tm = new DefaultTableModel();
                
                for (int i = 0; i < aa.size(); i++) {
                    Attribute attr = aa.get(i);
                    if(attr.isVisible()) {
                        tm.addColumn(attr);
                    }
                }

                for (int i = 0; i < this.dataSheet.getSampleCount(); i++) {
                    Sample sample = this.dataSheet.getSample(i);
                    ArrayList<Object> row = new ArrayList<>();
                    if(sample.isActive(this.chart)) {
                        for (int j = 0; j < aa.size(); j++) {
                            Attribute attr = aa.get(j);
                            if(attr.isVisible()) {
                                String value;
                                if (attr.isNumeric()) {
                                    double vt = sample.getValue(attr);
                                    value = df.format(vt);
                                } else {
                                    value = sample.getValueName(attr);
                                }
                                row.add(value);
                            }
                        }
                        tm.addRow(row.toArray());
                    }
                }
                
                this.filteredTable.setModel(tm);
            } else {
                this.filteredTable.setModel(new DefaultTableModel());
            }
        }
        
        if(this.notFilteredTable != null) {
            if(this.cbxNotFilteredOption.isSelected()) {
                
                ArrayList<Attribute> aa = this.getAttributesFromList();
                
                DefaultTableModel tm = new DefaultTableModel();
                
                for (int i = 0; i < aa.size(); i++) {
                    Attribute attr = aa.get(i);
                    if(attr.isVisible()) {
                        tm.addColumn(attr);
                    }
                }

                for (int i = 0; i < this.dataSheet.getSampleCount(); i++) {
                    Sample sample = this.dataSheet.getSample(i);
                    ArrayList<Object> row = new ArrayList<>();
                    if(!sample.isActive(this.chart)) {
                        for (int j = 0; j < aa.size(); j++) {
                            Attribute attr = aa.get(j);
                            if(attr.isVisible()) {
                                String value;
                                if (attr.isNumeric()) {
                                    double vt = sample.getValue(attr);
                                    value = df.format(vt);
                                } else {
                                    value = sample.getValueName(attr);
                                }
                                row.add(value);
                            }
                        }
                        tm.addRow(row.toArray());
                    }
                }
                
                this.notFilteredTable.setModel(tm);
            } else {
                this.notFilteredTable.setModel(new DefaultTableModel());
            }
        }
        
        if(this.selectedTable != null) {
            if(this.cbxSelectedOption.isSelected()) {
                
                ArrayList<Attribute> aa = this.getAttributesFromList();
                
                DefaultTableModel tm = new DefaultTableModel();
                
                for (int i = 0; i < aa.size(); i++) {
                    Attribute attr = aa.get(i);
                    if(attr.isVisible()) {
                        tm.addColumn(attr);
                    }
                }

                for (int i = 0; i < this.dataSheet.getSampleCount(); i++) {
                    Sample sample = this.dataSheet.getSample(i);
                    ArrayList<Object> row = new ArrayList<>();
                    if(sample.isSelected()) {
                        for (int j = 0; j < aa.size(); j++) {
                            Attribute attr = aa.get(j);
                            if(attr.isVisible()) {
                                String value;
                                if (attr.isNumeric()) {
                                    double vt = sample.getValue(attr);
                                    value = df.format(vt);
                                } else {
                                    value = sample.getValueName(attr);
                                }
                                row.add(value);
                            }
                        }
                        tm.addRow(row.toArray());
                    }
                }
                
                this.selectedTable.setModel(tm);
            } else {
                this.selectedTable.setModel(new DefaultTableModel());
            }
        }
    }
    
    private void clearDistortion() {
        for (int i = 0; i < this.dataSheet.getAttributes().size(); i++) {
            Attribute attr = this.dataSheet.getAttribute(i);
            if (attr.isVisible()) {
                attr.setVisible(true);
            }
        }
        
        this.attributeJbxList.entrySet().stream().forEach(e -> {
            e.getKey().setSelected(true);
        });
        
        this.attrOrderList.setModel(new DefaultListModel<>());
        
        this.chartPanel.reset();
        
        this.cbxFilteredOption.setSelected(false);
        this.cbxNotFilteredOption.setSelected(false);
        this.cbxSelectedOption.setSelected(false);
        
        this.fillSampleTables();
        
        this.redrawReorderList();
    }
    
    public void setFrameSize(Dimension size) {
        this.chart.setFrameSize(size);
    }
    
    public Dimension getFrameSize() {
        return this.chart.getFrameSize();
    }
    
    @Override
    public void setLocation(Point location) {
        this.chart.setLocation(location);
    }
    
    @Override
    public Point getLocation() {
        return this.chart.getLocation();
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        this.fillSampleTables();
    }

    @Override
    public void mousePressed(MouseEvent e) {}

    @Override
    public void mouseReleased(MouseEvent e) {
        this.fillSampleTables();
    }

    @Override
    public void mouseEntered(MouseEvent e) {}

    @Override
    public void mouseExited(MouseEvent e) {}
}
