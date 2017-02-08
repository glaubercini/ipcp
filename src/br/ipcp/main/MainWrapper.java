package br.ipcp.main;

import br.ipcp.frames.ChartFrame;
import br.ipcp.weka.DataIntegration;
import br.ipcp.weka.DatasetBuilder;
import java.io.File;

public class MainWrapper {
    private DataIntegration dataIntegration;
    
    public MainWrapper() {
        this.startArffChart();
    }
    
    private void startArffChart() {
        try {
            DatasetBuilder builder = new DatasetBuilder();
            //String pathname = "C:\\Program Files\\Weka-3-7\\data\\iris.2D.arff";
            String pathname = "C:\\Users\\Glauber\\OneDrive\\Mestrado\\Dissertação\\Produção Artigo\\datasets\\mushroom.arff";
            this.dataIntegration = builder.createDatasetFromFile(new File(pathname));
                    
            ChartFrame chartFrame = new ChartFrame(this);
        } catch (Exception ex) {
            System.out.println(ex);
            ex.printStackTrace();
        }
    }
    
    public DataIntegration getDataIntegration() {
        return this.dataIntegration;
    }
}
