package br.ipcp.data;

import br.ipcp.chart.Axis;
import br.ipcp.chart.Filter;
import java.util.ArrayList;
import java.util.HashMap;

public class Attribute {
    
    private final DataSheet dataSheet;
    
    private final String name;
    
    private boolean numeric;
    
    private double max;
    
    private double min;
    
    private boolean visible;
    
    private final ArrayList<String> discreteLevels = new ArrayList<>();
    
    private final HashMap<Filter, Boolean> activationMap = new HashMap<>();
    
    private Axis axis;
    
    public Attribute(String name, DataSheet dataSheet){
        this.name = name;
        this.dataSheet = dataSheet;
        
        this.max = Double.NEGATIVE_INFINITY;
        this.min = Double.POSITIVE_INFINITY;
        
        this.numeric = true;
        this.visible = true;
    }
    
    public String getName(){
        return this.name;
    }
    
    public void setNumeric(boolean isNumeric){
        this.numeric = isNumeric;
    }
    
    public boolean isNumeric(){
        return this.numeric;
    }
    
    public void addDiscreteLevel(String level) {
        if (!this.discreteLevels.contains(level)) {
            try
            {
                if (!this.isNumeric()) {
                    throw new NumberFormatException();
                }
                float fLevel = Float.parseFloat(level);
                this.addDiscreteLevel(fLevel);
            }catch(NumberFormatException e) {
                this.discreteLevels.add(level);
                this.min = 0;
                this.max = this.discreteLevels.size() - 1;
                this.numeric = false;
            }
        }
    }
    
    private void addDiscreteLevel(float level) {
        String sLevel = String.valueOf(level);
        if (!this.discreteLevels.contains(sLevel)) {
            this.discreteLevels.add(sLevel);
            if (level < this.min) {
                this.min = level;
            }
            
            if (level > this.max) {
                this.max = level;
            }
        }
    }
    
    public int getDiscreteLevelIndex(String level) {
        return this.discreteLevels.indexOf(level);
    }
    
    public double getMin() {
        return this.min;
    }
    
    public double getMax() {
        return this.max;
    }
    
    public void setVisible(boolean visible) {
        this.visible = visible;
    }
    
    public boolean isVisible() {
        return this.visible;
    }
    
    public void setActive(Filter filter, boolean active) {
        this.activationMap.put(filter, active);
    }
    
    public void setAxis(Axis axis) {
        this.axis = axis;
    }
    
    public Axis getAxis() {
        return this.axis;
    }
    
    @Override
    public String toString() {
        return this.name;
    }
}
