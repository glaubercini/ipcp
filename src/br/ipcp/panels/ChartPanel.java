package br.ipcp.panels;

import java.awt.Dimension;
import java.awt.Graphics;

import javax.swing.JPanel;

import br.ipcp.chart.InteractiveParallelCoordinatesChart;
import br.ipcp.data.DataSheet;

public abstract class ChartPanel extends JPanel {

    private static final long serialVersionUID = 8399899741480908760L;
    
    private final InteractiveParallelCoordinatesChart chart;

    private final DataSheet dataSheet;

    private final int marginTop = 20;

    private final int marginBottom = 80;

    private final int marginLeft = 80;

    private final int marginRight = 20;
    
    public ChartPanel(DataSheet dataSheet, InteractiveParallelCoordinatesChart chart){
        this.dataSheet = dataSheet;
        this.chart = chart;
    }
    
    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        this.setBackground(this.chart.getBackgroundColor());
        this.drawPlotFieldBackground(g);
    }
    
    public void drawPlotFieldBackground(Graphics g) {
        g.setColor(this.chart.getBackgroundColor());
        g.fillRect(0, 0, this.getWidth(), this.getHeight());
    }
    
    @Override
    public Dimension getPreferredSize() {
        int width = marginLeft + marginRight + chart.getWidth();
        int height = marginTop + marginBottom + chart.getHeight();
        Dimension preferredSize = new Dimension(width, height);

        return preferredSize;
    }
    
    public abstract void reset();
    
    public int getMarginTop() {
        return marginTop;
    }
    
    public int getMarginBottom() {
        return marginBottom;
    }
    
    public int getMarginLeft() {
        return marginLeft;
    }
    
    public int getMarginRight() {
        return marginRight;
    }
    
    public InteractiveParallelCoordinatesChart getChart() {
        return this.chart;
    }
    
    public DataSheet getDataSheet() {
        return dataSheet;
    }
}
