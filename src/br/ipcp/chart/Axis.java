package br.ipcp.chart;

import br.ipcp.data.Attribute;
import br.ipcp.data.DataSheet;

public class Axis {
    private InteractiveParallelCoordinatesChart chart;
    
    private double max;
    
    private double min;    
    
    private DataSheet dataSheet;
    
    private Attribute attribute;
    
    private int width;
    
    private boolean active = true;
    
    private Filter upperFilter;
    
    private Filter lowerFilter;
    
    public Axis(DataSheet dataSheet, InteractiveParallelCoordinatesChart chart, Attribute attribute) {
        this.dataSheet = dataSheet;
        this.chart = chart;
        this.attribute = attribute;
        
        this.upperFilter = new Filter(this.dataSheet, this, Filter.UPPER_FILTER);
        this.lowerFilter = new Filter(this.dataSheet, this, Filter.LOWER_FILTER);
        
        // Referência circular
        // Utilizado pra manter filtros pré selecionados
        // evitando que o usuário tenha que os fazer novamente
        this.attribute.setAxis(this);
        
        initialiseSettings();
    }
    
    public InteractiveParallelCoordinatesChart getChart() {
        return this.chart;
    }
    
    private void initialiseSettings() {
        this.width = 200;
        //this.dataSheet.evaluateBoundsForAllDesigns(this.chart);
    }
    
    public void setWidth(int width){
        this.width = width;
    }
    
    public int getWidth(){
        return this.width;
    }
    
    public void setActive(boolean active){
        this.active = active;
    }
    
    public boolean isActive(){
        return this.active;
    }
    
    public double getRange() {
        return ( this.max - this.min );
    }
    
    public Attribute getAttribute(){
        return this.attribute;
    }
    
    public Filter getUpperFilter() {
        return this.upperFilter;
    }
    
    public Filter getLowerFilter() {
        return this.lowerFilter;
    }
}
