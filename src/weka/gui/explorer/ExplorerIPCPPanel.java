package weka.gui.explorer;

import br.ipcp.data.DataSheet;
import br.ipcp.panels.IPCPVisualizationPanel;
import br.ipcp.weka.DataIntegration;
import br.ipcp.weka.DatasetBuilder;
import br.ipcp.weka.JTableWekaArffContextMenu;
import java.awt.BorderLayout;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;

import javax.swing.JPanel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import weka.core.Instances;
import weka.gui.explorer.Explorer.ExplorerPanel;

public class ExplorerIPCPPanel extends JPanel implements ExplorerPanel, ComponentListener, ChangeListener {

    private static final long serialVersionUID = 2463683344570457162L;

    private final static String TITLE = "Interactive Parallel Coordinates Plot";

    private Explorer explorer;
    private Instances instances;
    private Instances cachedIntances;
    
    private IPCPVisualizationPanel ipcpPanel;

    public ExplorerIPCPPanel() {
        super(new BorderLayout());
    }

    @Override
    public Explorer getExplorer() {
        return this.explorer;
    }

    @Override
    public String getTabTitle() {
        return TITLE;
    }

    @Override
    public String getTabTitleToolTip() {
        return TITLE;
    }

    @Override
    public void setExplorer(Explorer parent) {
        explorer = parent;
        explorer.getTabbedPane().addChangeListener(this);
        explorer.getTabbedPane().addComponentListener(this);
    }

    @Override
    public void stateChanged(ChangeEvent e) {
        if(explorer.getTabbedPane().getSelectedComponent() == ExplorerIPCPPanel.this && this.cachedIntances != this.instances){
            DatasetBuilder builder = new DatasetBuilder();
            DataIntegration dataIntegration = builder.createDatasetFromInstances(instances);
            DataSheet dataSheet = dataIntegration.getDataSheet();

            this.ipcpPanel = new IPCPVisualizationPanel(dataSheet);
        
            JTableWekaArffContextMenu e1 = new JTableWekaArffContextMenu(IPCPVisualizationPanel.CONTEXT_FILTERED, this.ipcpPanel, dataIntegration);
            JTableWekaArffContextMenu e2 = new JTableWekaArffContextMenu(IPCPVisualizationPanel.CONTEXT_NOT_FILTERED, this.ipcpPanel, dataIntegration);
            JTableWekaArffContextMenu e3 = new JTableWekaArffContextMenu(IPCPVisualizationPanel.CONTEXT_SELECTED, this.ipcpPanel, dataIntegration);
            this.ipcpPanel.getSampleJTable(IPCPVisualizationPanel.CONTEXT_FILTERED).addMouseListener(e1);
            this.ipcpPanel.getSampleJTable(IPCPVisualizationPanel.CONTEXT_NOT_FILTERED).addMouseListener(e2);
            this.ipcpPanel.getSampleJTable(IPCPVisualizationPanel.CONTEXT_SELECTED).addMouseListener(e3);

            this.removeAll();
            this.add(this.ipcpPanel, BorderLayout.CENTER);
            
            this.cachedIntances = this.instances;
        }
    }
    
    @Override
    public void setInstances(Instances inst) {
        this.instances = inst;
    }

    @Override
    public void componentResized(ComponentEvent e) {
        if(this.ipcpPanel instanceof IPCPVisualizationPanel) {
            this.ipcpPanel.setFrameSize(this.getSize());
        }
    }

    @Override
    public void componentMoved(ComponentEvent e) {
        if(this.ipcpPanel instanceof IPCPVisualizationPanel) {
            this.ipcpPanel.setLocation(this.getLocation());
        }
    }

    @Override
    public void componentShown(ComponentEvent e) {}

    @Override
    public void componentHidden(ComponentEvent e) {}
}
