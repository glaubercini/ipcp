package br.ipcp.panels.handlers;

import br.ipcp.panels.IPCPVisualizationPanel;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import javax.swing.JCheckBox;

public class CheckBoxHandler implements ItemListener {
    
    private final IPCPVisualizationPanel ipcpPanel;
    
    public CheckBoxHandler(IPCPVisualizationPanel ipcpPanel) {
        this.ipcpPanel = ipcpPanel;
    }

    @Override
    public void itemStateChanged(ItemEvent e) {
        JCheckBox cbx = (JCheckBox)e.getItem();
        this.ipcpPanel.attributeJbxList.get(cbx).setVisible(cbx.isSelected());
        this.ipcpPanel.redrawReorderList();
        this.ipcpPanel.fillSampleTables();
    }
}
