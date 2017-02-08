package br.ipcp.weka;

import br.ipcp.data.Attribute;
import br.ipcp.data.DataSheet;
import br.ipcp.data.Sample;
import java.io.File;
import java.io.FileReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Enumeration;
import weka.core.Instances;

public class DatasetBuilder {
        
    public DataIntegration createDatasetFromFile(final File file) throws Exception {
        String filename = file.getAbsolutePath();
        
        Reader r = new java.io.BufferedReader(new FileReader(filename));
        
        Instances instances = new Instances(r);
        instances.setClassIndex(instances.numAttributes() - 1);
        
        weka.core.Attribute attr = instances.attribute(2);
        Enumeration<Object> x = attr.enumerateValues();
        
        final DataIntegration dataIntegration = this.createDatasetFromInstances(instances);
        
        return dataIntegration;
    }
    
    public DataIntegration createDatasetFromInstances(Instances instances) {
        final DataSheet result = new DataSheet();
        final TranslateMap tMap = new TranslateMap();
        final DataIntegration dataIntegration = new DataIntegration();

        int attrs = instances.numAttributes();

        int id = 0;

        ArrayList<Attribute> attributes = result.getAttributes();
        ArrayList<Sample> samples = result.getSamples();

        for (int i = 0; i < attrs; i++) {
            Attribute a = new Attribute(instances.attribute(i).name(), result);
            attributes.add(i, a);
            
            tMap.putAttribute(a, instances.attribute(i));
        }

        for (int i = 0; i < instances.numInstances(); i++) {
            Sample s = new Sample(++id);
            for (int j = 0; j < attrs; j++) {    
                String sValue;
                try
                {
                    sValue = instances.instance(i).stringValue(j);
                } catch(IllegalArgumentException e) {
                    sValue = String.valueOf(instances.instance(i).value(j));
                }

                s.setValue(attributes.get(j), sValue);
            }
            samples.add(i, s);
            tMap.putSample(s, instances.instance(i));
        }
        
        dataIntegration.setDataSheet(result);
        dataIntegration.setTranslateMap(tMap);
        
        return dataIntegration;
    }
}
