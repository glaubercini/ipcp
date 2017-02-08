package br.ipcp.weka;

import br.ipcp.data.DataSheet;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;
import weka.core.DenseInstance;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.Utils;

public class DataIntegration {
    private DataSheet dataSheet;
    private TranslateMap translateMap;
    
    public void setDataSheet(DataSheet dataSheet) {
        this.dataSheet = dataSheet;
    }
    
    public DataSheet getDataSheet() {
        return this.dataSheet;
    }
    
    public void setTranslateMap(TranslateMap translateMap) {
        this.translateMap = translateMap;
    }
    
    public TranslateMap getTranslateMap() {
        return this.translateMap;
    }
    
    public String convertDataSheetToArff(DataSheet dataSheet) throws UnsupportedOperationException, ParseException {
        ArrayList<weka.core.Attribute> wekaAttr = new ArrayList<>();
        HashMap<weka.core.Attribute, weka.core.Attribute> indexMap = new HashMap<>();
        
        for (int i = 0; i < dataSheet.getAttributeCount(); i++) {
            br.ipcp.data.Attribute localAttr = dataSheet.getAttribute(i);
            weka.core.Attribute wa = this.translateMap.translateAttribute(localAttr);
            
            weka.core.Attribute nwa;
            if(wa.isDate()) {
                nwa = new weka.core.Attribute(wa.name(), wa.getDateFormat());
            } else if(wa.isNominal()) {
                ArrayList<Object> attrValues = Collections.list(wa.enumerateValues());
                nwa = new weka.core.Attribute(wa.name(), attrValues.stream().map(s -> String.valueOf(s)).collect(Collectors.toList()));
            } else if(wa.isString()) {
                nwa = new weka.core.Attribute(wa.name(), (List) null);
            } else if(wa.isRelationValued()) {
                throw new UnsupportedOperationException("Relationed values are still not supported.");
            } else {
                nwa = new weka.core.Attribute(wa.name());
            }
            
            indexMap.put(nwa, wa);            
            
            wekaAttr.add(nwa);
        }
        
        Instances instances = new Instances(java.util.UUID.randomUUID().toString(), wekaAttr, 0);
        
        for (int i = 0; i < dataSheet.getSampleCount(); i++) {
            Instance iw = this.translateMap.translateSample(dataSheet.getSample(i));
            int attrSize = instances.numAttributes();
            double[] attrValues = new double[attrSize];
            
            for (int j = 0; j < attrSize; j++) {                
                weka.core.Attribute nwa = instances.attribute(j);
                weka.core.Attribute waOld = indexMap.get(nwa);

                ArrayList<String> stringEnumerateValues = null;
                
                if(nwa.isDate()) {
                    attrValues[j] = nwa.parseDate(iw.stringValue(waOld.index()));
                } else if(nwa.isNominal()) {
                    ArrayList<Object> nwaValues = Collections.list(nwa.enumerateValues());
                    stringEnumerateValues = new ArrayList(nwaValues.stream().map(s -> String.valueOf(s)).collect(Collectors.toList()));
                    attrValues[j] = stringEnumerateValues.indexOf(iw.stringValue(waOld.index()));
                    if(attrValues[j] < 0) {
                        attrValues[j] = Utils.missingValue();
                    }
                } else if(nwa.isString()) {
                    attrValues[j] = nwa.addStringValue(iw.stringValue(waOld.index()));
                } else if(nwa.isRelationValued()) {
                    throw new UnsupportedOperationException("Relationed values are still not supported.");
                } else {
                    attrValues[j] = iw.value(waOld.index());
                }
            }
            
            DenseInstance di = new DenseInstance(1.0, attrValues);
            instances.add(di);
        }
        
        return instances.toString();
    }
}
