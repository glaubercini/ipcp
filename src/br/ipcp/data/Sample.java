package br.ipcp.data;

import br.ipcp.chart.Filter;
import br.ipcp.chart.InteractiveParallelCoordinatesChart;
import java.util.HashMap;

public class Sample {
    private final HashMap<Attribute, String> stringAttributeValues = new HashMap<>();
    
    private final HashMap<Attribute, Float> numericAttributeValues = new HashMap<>();
    
    private final HashMap<Filter, Boolean> activationMap = new HashMap<>();
    
    private int id;
    
    private boolean insideBounds;
    
    private boolean selected = false;
    
    public Sample(int id) {
        this.id = id;
    }
    
    public void setValue(Attribute attribute, String attributeValue) {
        attribute.addDiscreteLevel(attributeValue);
        this.stringAttributeValues.put(attribute, attributeValue);
        
        if (attribute.isNumeric()) {
            this.numericAttributeValues.put(attribute, Float.parseFloat(attributeValue));
        }
    }
    
    public double getValue(Attribute attribute) {
        if (attribute.isNumeric()) {
            return this.numericAttributeValues.get(attribute);
        } else {
            return attribute.getDiscreteLevelIndex(this.stringAttributeValues.get(attribute));
        }
    }
    
    public String getValueName(Attribute attribute) {
        return this.stringAttributeValues.get(attribute);
    }
    
    public void setActive(Filter filter, boolean active) {
        this.activationMap.put(filter, active);
    }
    
    public boolean isActive(InteractiveParallelCoordinatesChart chart) {
        for (int i = 0; i < chart.getAxisCount(); i++) {
            
            Filter uf = chart.getAxis(i).getUpperFilter();
            Filter lf = chart.getAxis(i).getLowerFilter();
            
            if (!this.activationMap.containsKey(uf)) {
                this.activationMap.put(uf, true);
            }
            
            if (!this.activationMap.containsKey(lf)) {
                this.activationMap.put(lf, true);
            }
            
            if (!this.activationMap.get(uf))
                return false;
            
            if (!this.activationMap.get(lf))
                return false;
        }
        return true;
    }
    
    public void setSelected(boolean selected) {
        this.selected = selected;
    }
    
    public boolean isSelected() {
        return this.selected;
    }
}
