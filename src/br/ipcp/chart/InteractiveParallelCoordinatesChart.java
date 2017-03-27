package br.ipcp.chart;

import br.ipcp.data.Attribute;
import br.ipcp.data.DataSheet;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Point;
import java.io.Serializable;
import java.util.ArrayList;


public class InteractiveParallelCoordinatesChart implements Serializable {

    private static final long serialVersionUID = 1473375760071118757L;
    
    private Point location;

    private Dimension frameSize;

    private DataSheet dataSheet;
    
    private boolean antiAliasing;

    private boolean useAlpha;
    
    private static final int BOTTOM_PADDING = 60;
    
    private Color backgroundColor = new Color(255, 255, 255);
    
    private final int topMargin = 10;    
    
    private final int axisLabelFontSize = 16;
    
    private int axisLabelVerticalDistance = 10;
    
    private boolean verticallyOffsetAxisLabels = true;
    
    private final Color axisLabelFontColor = new Color(15, 49, 178);
    
    private final Color axisColor = new Color(15, 49, 178);
    
    private final ArrayList<Axis> axes = new ArrayList<>();
    
    private final int lineThickness = 1;
    
    private final Color sampleLineColor = new Color(188, 0, 2);
    
    private final Color sampleLineColorFiltered = new Color(32, 255, 0);
    
    private final Color sampleLineColorSelected = new Color(0, 0, 0);
    
    private final Color filterColor  = new Color(15, 49, 178);
    
    private final int filterHeight = 20;
    
    private final int filterWidth = 7;

    public InteractiveParallelCoordinatesChart(DataSheet dataSheet, int id) {
        this.dataSheet = dataSheet;
        this.location = new Point(100, 100);
        this.frameSize = new Dimension(800, 600);
        this.antiAliasing = true;
        this.useAlpha = false;
        this.setLocation(new Point(100, 100));
        this.setFrameSize(new Dimension(800, 640));
        
        this.buildAxis();
    }
    
    private void buildAxis() {
        this.axes.clear();
        for (int i = 0; i < dataSheet.getAttributeCount(); i++) {
            Attribute attr = this.dataSheet.getAttribute(i);
            if (attr.getAxis() instanceof Axis) {
                this.axes.add(attr.getAxis());
            } else {
                Axis axis = new Axis(dataSheet, this, dataSheet.getAttribute(i));                
                this.axes.add(axis);
            }
        }
    }
    
    public DataSheet getDataSheet() {
        return this.dataSheet;
    }

    public void setDataSheet(DataSheet dataSheet) {
        this.dataSheet = dataSheet;
        this.buildAxis();
    }
    
    public Dimension getFrameSize() {
        return this.frameSize;
    }
    
    public void setFrameSize(Dimension size) {
        this.frameSize = size;
    }
    
    public Point getLocation() {
        return this.location;
    }
    
    public void setLocation(Point location) {
        this.location = location;
    }
    
    public boolean isAntiAliasing() {
        return this.antiAliasing;
    }
    
    public void setAntiAliasing(boolean antiAliasing) {
        this.antiAliasing = antiAliasing;
    }
    
    public boolean isUseAlpha() {
        return this.useAlpha;
    }
    
    public void setUseAlpha(boolean useAlpha) {
        this.useAlpha = useAlpha;
    }
    
    public int getAxisCount() {
        return this.axes.size();
    }
    
    public Axis getAxis(int index) {
        return this.axes.get(index);
    }
    
    public int getLineThickness() {
        return this.lineThickness;
    }
    
    public Color getSampleLineColor() {
        return this.sampleLineColor;
    }
    
    public Color getSampleLineColorFiltered() {
        return this.sampleLineColorFiltered;
    }
    
    public Color getSampleLineColorSelected() {
        return this.sampleLineColorSelected;
    }
    
    public boolean isVerticallyOffsetAxisLabels() {
        return this.verticallyOffsetAxisLabels;
    }

    public void setVerticallyOffsetAxisLabels(boolean verticallyOffsetAxisLabels) {
        this.verticallyOffsetAxisLabels = verticallyOffsetAxisLabels;
    }
    
    public int getAxisLabelVerticalDistance() {
        return this.axisLabelVerticalDistance;
    }
    
    public void setAxisLabelVerticalDistance(int axisLabelVerticalDistance) {
        this.axisLabelVerticalDistance = axisLabelVerticalDistance;
    }
    
    public Color getFilterColor() {
        return this.filterColor;
    }

    public String getTitle() {
        return "Interactive Parallel Coordinates Plot";
    }

    public int getWidth() {
        int width = 0;
        if (this.getAxis(0).isActive()) {
            width = width + (int) (0.5 * this.getAxis(0).getWidth());
        }
        for (int i = 1; i < this.getAxisCount(); i++) {
            if (this.getAxis(i).isActive()) {
                width = width + (int) (this.getAxis(i).getWidth());
            }
        }
        return width;
    }

    public int getHeight() {
        int height = this.getAxisTopPos() + this.getAxisHeight();
        return height;
    }

    public Color getBackgroundColor() {
        return this.backgroundColor;
    }

    public void setBackgroundColor(Color backGroundColor) {
        this.backgroundColor = backGroundColor;
    }
    
    public int getAxisLabelFontSize() {
        return this.axisLabelFontSize;
    }
    
    public Color getAxisLabelFontColor() {
        return this.axisLabelFontColor;
    }
    
    public Color getAxisColor() {
        return this.axisColor;
    }
    
    public int getTopMargin() {
        return this.topMargin;
    }
    
    public int getFilterHeight() {
        return this.filterHeight;
    }
    
    public int getFilterWidth() {
        return this.filterWidth;
    }
    
    public int getAxisTopPos() {
        int topPos;
        if (this.verticallyOffsetAxisLabels) {
            topPos = 2 * this.getAxisLabelFontSize() + this.axisLabelVerticalDistance + this.getTopMargin() * 2 + this.getFilterHeight();
        } else {
            topPos = this.getAxisLabelFontSize() + this.getTopMargin() * 2 + this.getFilterHeight();
        }
        
        return topPos;
    }
    
    public int getAxisHeight() {
        return this.getFrameSize().height - this.getAxisTopPos() - InteractiveParallelCoordinatesChart.BOTTOM_PADDING;
    }
}
