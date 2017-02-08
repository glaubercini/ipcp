package br.ipcp.frames;

import br.ipcp.main.MainWrapper;
import br.ipcp.panels.IPCPVisualizationPanel;
import br.ipcp.weka.JTableWekaArffContextMenu;
import java.awt.GridLayout;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import javax.swing.JFrame;

public class ChartFrame extends JFrame implements ComponentListener {
    
    private static final long serialVersionUID = -3926309082808703629L;
    
    private IPCPVisualizationPanel ipcpPanel;
    
    public ChartFrame(MainWrapper mainWrapper) {
        this.setLayout(new GridLayout(1, 1));
        
        this.ipcpPanel = new IPCPVisualizationPanel(mainWrapper.getDataIntegration().getDataSheet());
        
        JTableWekaArffContextMenu e1 = new JTableWekaArffContextMenu(IPCPVisualizationPanel.CONTEXT_FILTERED, this.ipcpPanel, mainWrapper.getDataIntegration());
        JTableWekaArffContextMenu e2 = new JTableWekaArffContextMenu(IPCPVisualizationPanel.CONTEXT_NOT_FILTERED, this.ipcpPanel, mainWrapper.getDataIntegration());
        JTableWekaArffContextMenu e3 = new JTableWekaArffContextMenu(IPCPVisualizationPanel.CONTEXT_SELECTED, this.ipcpPanel, mainWrapper.getDataIntegration());
        this.ipcpPanel.getSampleJTable(IPCPVisualizationPanel.CONTEXT_FILTERED).addMouseListener(e1);
        this.ipcpPanel.getSampleJTable(IPCPVisualizationPanel.CONTEXT_NOT_FILTERED).addMouseListener(e2);
        this.ipcpPanel.getSampleJTable(IPCPVisualizationPanel.CONTEXT_SELECTED).addMouseListener(e3);
        
        this.add(this.ipcpPanel);
        
        
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setLocation(this.ipcpPanel.getLocation());
	this.getContentPane().setPreferredSize(this.ipcpPanel.getFrameSize());
	this.pack();
	this.addComponentListener(this);
        this.setVisible(true);
    }

    @Override
    public void componentResized(ComponentEvent e) {
        this.ipcpPanel.setFrameSize(this.getContentPane().getSize());
    }

    @Override
    public void componentMoved(ComponentEvent e) {
        this.ipcpPanel.setLocation(this.getLocation());
    }

    @Override
    public void componentShown(ComponentEvent e) { }

    @Override
    public void componentHidden(ComponentEvent e) { }
}
