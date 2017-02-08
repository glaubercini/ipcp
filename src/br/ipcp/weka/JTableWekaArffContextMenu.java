package br.ipcp.weka;

import br.ipcp.panels.IPCPVisualizationPanel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.text.ParseException;
import javax.swing.JFileChooser;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;
import javax.swing.filechooser.FileNameExtensionFilter;

public class JTableWekaArffContextMenu extends MouseAdapter {
    
    private final int context;
    private final IPCPVisualizationPanel panel;
    private final DataIntegration dataIntegration;
    private final JPopupMenu contextMenu;
    
    public static String LAST_DIRECTORY = "";
    
    public JTableWekaArffContextMenu(int context, IPCPVisualizationPanel panel, DataIntegration dataIntegration) {
        this.context = context;
        this.panel = panel;
        this.dataIntegration = dataIntegration;
        this.contextMenu = new JPopupMenu();
        
        ActionListener menuListener = (ActionEvent e) -> {
            try {
                JFileChooser fc = new JFileChooser(LAST_DIRECTORY);
                FileNameExtensionFilter filter = new FileNameExtensionFilter("Weka file format", "arff", "arff");
                fc.setFileFilter(filter);
                if (fc.showSaveDialog(JTableWekaArffContextMenu.this.panel) == JFileChooser.APPROVE_OPTION) {                    
                    String arffContent = this.dataIntegration.convertDataSheetToArff(this.panel.getSampleTableDataSheet(this.context));
                    PrintWriter out;
                    try {
                        String filePath = fc.getSelectedFile().getAbsolutePath();
                        if (!filePath.toLowerCase().endsWith(".arff")) {
                            filePath += ".arff";
                        }                        
                        out = new PrintWriter(filePath);
                        out.println(arffContent);
                        out.flush();
                        out.close();
                        File directory = fc.getSelectedFile().getParentFile();
                        if (directory.isDirectory()) {
                            LAST_DIRECTORY = directory.getAbsolutePath();
                        }
                    } catch (FileNotFoundException ex) {
                        JOptionPane.showMessageDialog(JTableWekaArffContextMenu.this.panel, ex.getMessage());
                    }
                }
            } catch (UnsupportedOperationException | ParseException ex) {
                JOptionPane.showMessageDialog(JTableWekaArffContextMenu.this.panel, ex.getMessage());
            }
        };
        
        String menuItemName;
        switch(context) {
            case IPCPVisualizationPanel.CONTEXT_FILTERED: menuItemName = "Export Filtered Sample Arff"; break;
            case IPCPVisualizationPanel.CONTEXT_NOT_FILTERED: menuItemName = "Export Not Filtered Sample Arff"; break;
            case IPCPVisualizationPanel.CONTEXT_SELECTED: menuItemName = "Export Selected Sample Arff"; break;
            default: menuItemName = ""; break;
        }
        
        JMenuItem menuArff = new JMenuItem(menuItemName);
        menuArff.addActionListener(menuListener);
        
        this.contextMenu.add(menuArff);
    }
    
    @Override
    public void mousePressed(MouseEvent e) {}
    
    @Override
    public void mouseReleased(MouseEvent e) {
        if(e.isPopupTrigger()) {
            this.contextMenu.setVisible(true);
            this.contextMenu.show(e.getComponent(), e.getX(), e.getY());
        }
    }
}
