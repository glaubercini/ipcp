package br.ipcp.weka;

import br.ipcp.data.Attribute;
import br.ipcp.data.Sample;
import java.util.HashMap;
import weka.core.Instance;

public class TranslateMap {
    public HashMap<Attribute, weka.core.Attribute> attrLocalWeka;
    public HashMap<weka.core.Attribute, Attribute> attrWekaLocal;
    
    public HashMap<Sample, Instance> sampleLocalWeka;
    public HashMap<Instance, Sample> sampleWekaLocal;
     
    public TranslateMap() {
        this.attrLocalWeka = new HashMap<>();
        this.attrWekaLocal = new HashMap<>();
        
        this.sampleLocalWeka = new HashMap<>();
        this.sampleWekaLocal = new HashMap<>();
    }
    
    public void putAttribute(Attribute attrLocal, weka.core.Attribute attrWeka) {
        this.attrLocalWeka.put(attrLocal, attrWeka);
        this.attrWekaLocal.put(attrWeka, attrLocal);
    }
    
    public void putSample(Sample sampleLocal, Instance sampleWeka) {
        this.sampleLocalWeka.put(sampleLocal, sampleWeka);
        this.sampleWekaLocal.put(sampleWeka, sampleLocal);
    }
    
    public Attribute translateAttribute(weka.core.Attribute attrWeka) {
        return this.attrWekaLocal.get(attrWeka);
    }
    
    public weka.core.Attribute translateAttribute(Attribute attrLocal) {
        return this.attrLocalWeka.get(attrLocal);
    }
    
    public Sample translateSample(Instance sampleWeka) {
        return this.sampleWekaLocal.get(sampleWeka);
    }
    
    public Instance translateSample(Sample sampleLocal) {
        return this.sampleLocalWeka.get(sampleLocal);
    }
}
