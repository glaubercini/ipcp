/*
* http://stackoverflow.com/questions/16586562/reordering-jlist-with-drag-and-drop
*/

package br.ipcp.panels.handlers;

import br.ipcp.panels.IPCPVisualizationPanel;
import javax.swing.*;
import java.awt.datatransfer.*;
import java.io.IOException;
import javax.activation.ActivationDataFlavor;
import javax.activation.DataHandler;

public class ListObjectsTransferHandler extends TransferHandler {

    private static final long serialVersionUID = 328264196558529888L;
    
    private final DataFlavor localObjectFlavor;
    private Object[] transferedObjects = null;
    
    private IPCPVisualizationPanel IPCPPanel;
    
    public ListObjectsTransferHandler(IPCPVisualizationPanel IPCPPanel) {
        localObjectFlavor = new ActivationDataFlavor(
                                Object[].class,
                                    DataFlavor.javaJVMLocalObjectMimeType,
                                        "Array of items");
        this.IPCPPanel = IPCPPanel;
    }
    
    @Override
    protected Transferable createTransferable(JComponent c) {
        JList list = (JList) c;
        indices = list.getSelectedIndices();
        transferedObjects = list.getSelectedValuesList().toArray();
        return new DataHandler(transferedObjects, localObjectFlavor.getMimeType());
    }
    
    @Override
    public boolean canImport(TransferSupport info) {
        return !(!info.isDrop() || !info.isDataFlavorSupported(localObjectFlavor));
    }
    
    @Override
    public int getSourceActions(JComponent c) {
        return TransferHandler.MOVE;
    }
    
    @Override
    public boolean importData(TransferSupport info) {
        if(!canImport(info)) {
            return false;
        }
        
        JList target = (JList)info.getComponent();
        JList.DropLocation dl = (JList.DropLocation)info.getDropLocation();
        DefaultListModel listModel = (DefaultListModel)target.getModel();
        
        int index = dl.getIndex();
        int max = listModel.getSize();
        
        if(index<0 || index>max) {
            index = max;
        }
        
        addIndex = index;
        
        try {
            Object[] values = (Object[])info.getTransferable().getTransferData(localObjectFlavor);
            addCount = values.length;
            for(int i = 0; i < values.length; i++) {
                int idx = index++;
                listModel.add(idx, values[i]);
                target.addSelectionInterval(idx, idx);
            }
            return true;
        } catch(UnsupportedFlavorException | IOException exc) {}
        
        return false;
    }
    @Override
    protected void exportDone(JComponent c, Transferable data, int action) {
        cleanup(c, action == TransferHandler.MOVE);
        this.IPCPPanel.recreateChart();
    }
    
    private void cleanup(JComponent c, boolean remove) {
        if(remove && indices != null) {
            JList source = (JList)c;
            DefaultListModel model = (DefaultListModel)source.getModel();

            if(addCount > 0) {
                //http://java-swing-tips.googlecode.com/svn/trunk/DnDReorderList/src/java/example/MainPanel.java
                for(int i=0; i<indices.length; i++) {
                    if(indices[i] >= addIndex) {
                        indices[i] += addCount;
                    }
                }
            }

            for(int i=indices.length-1; i>=0; i--) {
                model.remove(indices[i]);
            }
        }
        indices  = null;
        addCount = 0;
        addIndex = -1;
    }
    
    private int[] indices = null;
    private int addIndex  = -1; //Location where items were added
    private int addCount  = 0;  //Number of items added.
}