package br.ipcp.chart;

import br.ipcp.data.Attribute;
import br.ipcp.data.DataSheet;

public class Filter {
    
    public static final int UPPER_FILTER = 0;
    
    public static final int LOWER_FILTER = 1;
    
    public static final double FILTER_TOLERANCE = 0.00001;
    
    private final DataSheet dataSheet;
    
    private final int filterType;
    
    private final Axis axis;
    
    private int xPos;
    
    private double value;
    
    public Filter(DataSheet dataSheet, Axis axis, int filterType) {
        this.dataSheet = dataSheet;
        this.axis = axis;
        this.filterType = filterType;
        
        this.reset();
    }
    
    public void setValue(double value) {
        this.value = value;
        this.apply();
    }
    
    public double getValue() {
        return this.value;
    }
    
    public Axis getAxis() {
        return this.axis;
    }
    
    public void reset() {
        if (this.filterType == Filter.UPPER_FILTER) {
            this.setValue(this.axis.getAttribute().getMax());
        } else {
            this.setValue(this.axis.getAttribute().getMin());
        }
    }
    
    public void apply() {
        Attribute attr = this.axis.getAttribute();
        double tolerance = this.getAxis().getRange() * Filter.FILTER_TOLERANCE;
        
        if (tolerance <= 0) {
            tolerance = Filter.FILTER_TOLERANCE;
        }
        
        if (this.filterType == UPPER_FILTER) {
            for (int i = 0; i < this.dataSheet.getSampleCount(); i++) {
                if (this.dataSheet.getSample(i).getValue(attr) - tolerance > this.getValue()) {
                    this.dataSheet.getSample(i).setActive(this, false);
                } else {
                    this.dataSheet.getSample(i).setActive(this, true);
                }
            }
        } else if (this.filterType == LOWER_FILTER) {
            for (int i = 0; i < this.dataSheet.getSampleCount(); i++) {
                if (this.dataSheet.getSample(i).getValue(attr) + tolerance < this.getValue()) {
                    this.dataSheet.getSample(i).setActive(this, false);
                } else {
                    this.dataSheet.getSample(i).setActive(this, true);
                }
            }
        }
    }
    
    public int getXPos() {
        return this.xPos;
    }
    
    public void setXPos(int xPos) {
        this.xPos = xPos;
    }
    
    public int getYPos() {
        //return this.getAxis().getChart().getAxisTopPos() + (int) (this.getAxis().getChart().getAxisHeight() * 0.5);
        double upperLimit = this.axis.getAttribute().getMax();
        
        double lowerLimit = this.axis.getAttribute().getMin();
        
        if (this.value > upperLimit) {
            this.value = upperLimit;
        } else if (this.value < lowerLimit) {
            this.value = lowerLimit;
        }
        
        double valueRange = upperLimit - lowerLimit;
        
        int topPos = this.axis.getChart().getAxisTopPos() - 1;
        int bottomPos = topPos + this.getAxis().getChart().getAxisHeight() + 1;
        double posRange = bottomPos - topPos;
        
        double ratio = (this.value - lowerLimit) / valueRange;
        
        int yPos = bottomPos - (int) (posRange * ratio);
        
        return yPos;
    }
    
    public void setYPos(int pos) {
        double upperLimit = this.axis.getAttribute().getMax();
        
        double lowerLimit = this.axis.getAttribute().getMin();
        
        double valueRange = upperLimit - lowerLimit;
        
        int topPos = this.axis.getChart().getAxisTopPos() - 1;
        
        int bottomPos = topPos + this.getAxis().getChart().getAxisHeight() + 1;
        
        double posRange = bottomPos - topPos;
        
        double ratio = (bottomPos - pos) / posRange;
        
        this.value = lowerLimit + valueRange * ratio;
        
        this.apply();
    }
    
    public int getHighestPos() {
        int pos;
        switch (this.filterType) {
            case Filter.UPPER_FILTER:
                pos = this.getAxis().getChart().getAxisTopPos();
                break;
            case Filter.LOWER_FILTER:
                pos = this.getAxis().getUpperFilter().getYPos();
                break;
            default:
                pos = this.getAxis().getChart().getAxisTopPos();
                break;
        }
        return pos - 1;
    }
    
    public int getLowestPos() {
        int pos;
        switch (this.filterType) {
            case Filter.UPPER_FILTER:
                pos = this.getAxis().getLowerFilter().getYPos();
                break;
            case Filter.LOWER_FILTER:
                pos = this.getAxis().getChart().getAxisTopPos() + this.getAxis().getChart().getAxisHeight();
                break;
            default:
                pos = this.getAxis().getChart().getAxisTopPos() + this.getAxis().getChart().getAxisHeight();
                break;
        }
        return pos + 1;
    }
}
