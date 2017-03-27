package br.ipcp.panels;

import br.ipcp.chart.Axis;
import br.ipcp.chart.Filter;
import br.ipcp.chart.InteractiveParallelCoordinatesChart;
import br.ipcp.data.Attribute;
import br.ipcp.data.DataSheet;
import br.ipcp.data.Sample;
import java.awt.Cursor;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Polygon;
import java.awt.RenderingHints;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.image.BufferedImage;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;
import javax.swing.SwingUtilities;

public class InteractiveParallelCoordinatesPanel extends ChartPanel implements MouseMotionListener, MouseListener {
    
    private static final long serialVersionUID = 3171108338004501522L;
    
    private final InteractiveParallelCoordinatesChart chart;
    
    private final HashSet<Sample> samplesHoverList;
    
    private final Map<int[], HashSet<Sample>> samplesLineMap;
    
    private int dragStartX;
    
    private int dragStartY;
    
    private int dragOffsetY;
    
    private boolean dragSelecting = false;
    
    private Filter draggedFilter;
    
    private BufferedImage bufferedImage;
    
    private boolean enableChartPaint = true;
    
    public InteractiveParallelCoordinatesPanel(DataSheet dataSheet, InteractiveParallelCoordinatesChart chart) {
        super(dataSheet, chart);
        this.chart = chart;
        
        this.samplesHoverList = new HashSet<>();
        this.samplesLineMap = new LinkedHashMap<>();
        
        this.addListeners();
    }
    
    private void addListeners() {
        this.addMouseListener(this);
        this.addMouseMotionListener(this);        
    }
    
    public void setEnableChartPaint(boolean enableChartPaint) {
        this.enableChartPaint = enableChartPaint;
    }
    
    public boolean isEnableChartPaint() {
        return this.enableChartPaint;
    }
    
    @Override
    public void paintComponent(Graphics g) {
        Graphics canvasGraphics;
        BufferedImage canvas;
        
        if(this.enableChartPaint) {
            if(this.chart.isAntiAliasing() || this.chart.isUseAlpha()) {
                canvas = new BufferedImage(this.getWidth(), this.getHeight(), BufferedImage.TYPE_INT_ARGB);
                canvasGraphics = canvas.getGraphics();
            } else {
                canvas = null;
                canvasGraphics = g;
            }

            if(this.chart.isAntiAliasing() ){
                Graphics2D graphics2D = (Graphics2D) canvasGraphics;
                graphics2D.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            }
            super.paintComponent(canvasGraphics);

            this.drawSamples(canvasGraphics);
            this.drawAxes(canvasGraphics);

            if (canvas != null) {
                g.drawImage(canvas, 0, 0, null);
            }
        } else {
            super.paintComponent(g);
        }
    }
    
    public void drawSamples(Graphics g) {
        int axisTopPos = this.chart.getAxisTopPos();
        int axisCount = this.chart.getAxisCount();
        
        double[] axisRanges = new double[axisCount];
        int[] axisHeights = new int[axisCount];
        int[] axisWidths = new int[axisCount];
        double[] axisMaxValues = new double[axisCount];
        double[] axisMinValues = new double[axisCount];
        boolean[] axisActiveFlags = new boolean[axisCount];
        
        for (int i = 0; i < axisCount; i++) {
            Attribute attribute = this.chart.getAxis(i).getAttribute();
            
            axisRanges[i] = attribute.getMax() - attribute.getMin();
            axisHeights[i] = this.chart.getAxisHeight();
            axisWidths[i] = this.chart.getAxis(i).getWidth();
            axisMaxValues[i] = attribute.getMax();
            axisMinValues[i] = attribute.getMin();
            axisActiveFlags[i] = this.chart.getAxis(i).isActive();
        }
        
        this.samplesLineMap.clear();
        
        for (int s = 0; s < this.chart.getDataSheet().getSampleCount(); s++) {
            Sample currentSample = this.chart.getDataSheet().getSample(s);
            
            boolean firstAxisDrawn = false;
            
            int lineThickness = this.chart.getLineThickness();
            
            if (this.samplesHoverList.contains(currentSample) || currentSample.isSelected()) {
                g.setColor(this.chart.getSampleLineColorSelected());
            } else if(currentSample.isActive(this.chart)) {
                g.setColor(this.chart.getSampleLineColorFiltered());
            } else {
                g.setColor(this.chart.getSampleLineColor());
            }
            
            int xPositionCurrent = getMarginLeft();
            int yPositionCurrent = axisTopPos;
            int xPositionLast = xPositionCurrent;
            int yPositionLast;
            
            for (int i = 0; i < this.chart.getDataSheet().getAttributeCount(); i++) {
                int yPosition = axisTopPos;
                if (axisActiveFlags[i]) {
                    double value = currentSample.getValue(this.chart.getDataSheet().getAttribute(i));
                    int yPositionRelToBottom;
                    if (axisRanges[i] == 0) {
                        yPositionRelToBottom = 0;
                    } else {
                        double ratio = (value - axisMinValues[i]) / axisRanges[i];
                        yPositionRelToBottom = (int) (axisHeights[i] * ratio);
                    }
                    
                    yPositionLast = yPositionCurrent;
                    yPositionCurrent = yPosition + (axisHeights[i]) - yPositionRelToBottom;
                    
                    if (firstAxisDrawn) {
                        xPositionCurrent = xPositionCurrent + (int) (axisWidths[i] * 0.5);
                        for (int t = 1; t <= lineThickness; t++) {
                            int deltaY = -((int) (t / 2)) * (2 * (t % 2) - 1);
                            g.drawLine(xPositionLast, yPositionLast + deltaY, xPositionCurrent, yPositionCurrent + deltaY);

                            int[] key = {xPositionLast, yPositionLast + deltaY, xPositionCurrent, yPositionCurrent + deltaY};
                            if (!this.samplesLineMap.containsKey(key)) {
                                this.samplesLineMap.put(key, new HashSet<>());
                            }
                            this.samplesLineMap.get(key).add(currentSample);
                        }
                        
                    } else {
                        firstAxisDrawn = true;
                    }
                    xPositionLast = xPositionCurrent;
                    xPositionCurrent = xPositionCurrent + (int) (axisWidths[i] * 0.5);
                }
            }
        }
    }
    
    public void drawAxes(Graphics g) {
        int xPosition = this.getMarginLeft();
        int yPosition = this.chart.getAxisTopPos();
        
        FontMetrics fm = g.getFontMetrics();
        Axis lastAxis = null;
        Axis currentAxis;
        int drawnAxisCount = 0;
        
        for (int i = 0; i < this.chart.getAxisCount(); i++) {
            currentAxis = this.chart.getAxis(i);
            
            if (lastAxis != null) {
                xPosition = xPosition + (int) (lastAxis.getWidth() * 0.5) + (int) (currentAxis.getWidth() * 0.5);
            }
            
            String axisLabel = currentAxis.getAttribute().getName();
            int slenX = fm.stringWidth(axisLabel);
            g.setFont(new Font("SansSerif", Font.PLAIN, this.chart.getAxisLabelFontSize()));

            int yLabelOffset = 0;
            if (this.chart.isVerticallyOffsetAxisLabels()) {
                yLabelOffset = ((drawnAxisCount++) % 2) * (this.chart.getAxisLabelFontSize() + chart.getAxisLabelVerticalDistance());
            }
            
            // Axis Label
            g.setColor(this.chart.getAxisLabelFontColor());
            g.drawString(axisLabel, xPosition - (int) (0.5 * slenX), this.chart.getAxisLabelFontSize() + chart.getTopMargin() + yLabelOffset);

            // Axis Line
            g.setColor(this.chart.getAxisColor());
            g.drawLine(xPosition, yPosition, xPosition, yPosition + (this.chart.getAxisHeight()));
            
            
            // Filters
            Filter uf = currentAxis.getUpperFilter();
            Filter lf = currentAxis.getLowerFilter();
            
            uf.setXPos(xPosition);
            lf.setXPos(xPosition);

            // Filters - Lower
            g.setColor(this.chart.getFilterColor());
            g.drawLine(uf.getXPos(), uf.getYPos(), uf.getXPos() - this.chart.getFilterWidth(), uf.getYPos() - this.chart.getFilterHeight());
            g.drawLine(uf.getXPos(), uf.getYPos(), uf.getXPos() + this.chart.getFilterWidth(), uf.getYPos() - this.chart.getFilterHeight());
            g.drawLine(uf.getXPos() - this.chart.getFilterWidth(), uf.getYPos() - this.chart.getFilterHeight(), uf.getXPos() + this.chart.getFilterWidth(), uf.getYPos() - this.chart.getFilterHeight());
            
            //Filter - Upper
            g.drawLine(lf.getXPos(), lf.getYPos(), lf.getXPos() - this.chart.getFilterWidth(), lf.getYPos() + this.chart.getFilterHeight());
            g.drawLine(lf.getXPos(), lf.getYPos(), lf.getXPos() + this.chart.getFilterWidth(), lf.getYPos() + this.chart.getFilterHeight());
            g.drawLine(lf.getXPos() - this.chart.getFilterWidth(), lf.getYPos() + this.chart.getFilterHeight(), lf.getXPos() + this.chart.getFilterWidth(), lf.getYPos() + this.chart.getFilterHeight());
            
            //Filter - Lines
            if (null != lastAxis) {
                g.drawLine(lastAxis.getUpperFilter().getXPos(), lastAxis.getUpperFilter().getYPos(), uf.getXPos(), uf.getYPos());
                g.drawLine(lastAxis.getLowerFilter().getXPos(), lastAxis.getLowerFilter().getYPos(), lf.getXPos(), lf.getYPos());
            }
            
            lastAxis = currentAxis;
        }
    }
    
    private boolean updateHoverList(int x, int y) {
        boolean designsFound = false;
        this.samplesHoverList.clear();
        for (Map.Entry<int[], HashSet<Sample>> mapEntry : this.samplesLineMap.entrySet()) {
            int[] coords = mapEntry.getKey();
            HashSet<Sample> sampleEntry = mapEntry.getValue();

            int xStart = coords[0];
            int yStart = coords[1];
            int xEnd = coords[2];
            int yEnd = coords[3];

            if (x > xStart && x < xEnd && Math.min(yStart, yEnd) < y && Math.max(yStart, yEnd) > y) {
                int xDelta = xEnd - xStart;
                int xMiddle = xStart + (xDelta / 2);

                int yDelta = Math.abs(yEnd - yStart) / 2;
                int yMiddle = yEnd > yStart ? yStart + yDelta : yEnd + yDelta;

                int designCount = sampleEntry.size();
                int yOffset = (int) ((100. * (double) designCount) / (double) this.chart.getDataSheet().getSampleCount());

                int[] xPoints = new int[4];
                int[] yPoints = new int[4];

                // start
                xPoints[0] = xStart;
                yPoints[0] = yStart;

                // top
                xPoints[1] = xMiddle;
                yPoints[1] = yMiddle - yOffset - 2;

                // end
                xPoints[2] = xEnd;
                yPoints[2] = yEnd;

                // low
                xPoints[3] = xMiddle;
                yPoints[3] = yMiddle + yOffset + 2;

                Polygon poly = new Polygon(xPoints, yPoints, xPoints.length);

                if (poly.contains(x, y)) {
                    this.samplesHoverList.addAll(sampleEntry);
                    designsFound = true;
                }
            }
        }
        return designsFound;
    }

    @Override
    public void mouseDragged(MouseEvent e) {
        if (this.draggedFilter != null) {
            this.draggedFilter.setYPos(Math.max(Math.min(e.getY() + this.dragOffsetY, this.draggedFilter.getLowestPos()), this.draggedFilter.getHighestPos()));
            this.repaint();
        } else if (this.dragSelecting) {
            this.dragOffsetY = e.getY();
            this.repaint();
        }
    }

    @Override
    public void mouseMoved(MouseEvent e) {
        boolean needRepaint = false;
        
        this.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
        

        if (this.samplesHoverList.size() > 0) {
            needRepaint = true;
        }

        this.samplesHoverList.clear();

        needRepaint = needRepaint || this.updateHoverList(e.getX(), e.getY());
        
        if (needRepaint) {
            this.repaint();
        }
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        if (SwingUtilities.isLeftMouseButton(e) && this.updateHoverList(e.getX(), e.getY())) {
            this.samplesHoverList.stream().forEach((s) -> {
                s.setSelected(!s.isSelected());
            });
        }
    }

    @Override
    public void mousePressed(MouseEvent e) {
        this.dragStartX = e.getX();
        this.dragStartY = e.getY();
        
        for (int i = 0; i < this.chart.getAxisCount(); i++) {
            Filter uf = this.chart.getAxis(i).getUpperFilter();
            Filter lf = this.chart.getAxis(i).getLowerFilter();
            if (this.chart.getAxis(i).isActive()
                    && this.dragStartY >= ( uf.getYPos() - this.chart.getFilterHeight() )
                    && this.dragStartY <= uf.getYPos()
                    && this.dragStartX >= ( uf.getXPos() - this.chart.getFilterWidth() )
                    && this.dragStartX <= ( uf.getXPos() + this.chart.getFilterWidth() )) {
                
                this.draggedFilter = uf;
                this.dragOffsetY = uf.getYPos() - this.dragStartY;
                this.setCursor(new Cursor(Cursor.MOVE_CURSOR));
                
            } else if (this.chart.getAxis(i).isActive()
                    && this.dragStartY >= lf.getYPos()
                    && this.dragStartY <= lf.getYPos() + this.chart.getFilterHeight()
                    && this.dragStartX >= lf.getXPos() - this.chart.getFilterWidth()
                    && this.dragStartX <= lf.getXPos() + this.chart.getFilterWidth()) {
                
                this.draggedFilter = lf;
                this.dragOffsetY = lf.getYPos() - this.dragStartY;
                this.setCursor(new Cursor(Cursor.MOVE_CURSOR));
            }
        }
        
        if (this.draggedFilter == null){
            if (SwingUtilities.isLeftMouseButton(e)) {
                this.samplesHoverList.clear();
                this.dragSelecting = true;        
            }
        }
    }
    
    @Override
    public void reset() {
        this.samplesHoverList.clear();
        for (int i = 0; i < this.chart.getAxisCount(); i++) {
            Axis a = this.chart.getAxis(i);
            a.getUpperFilter().reset();
            a.getLowerFilter().reset();
        }
        
        for (int i = 0; i < this.chart.getDataSheet().getSampleCount(); i++) {
            Sample s = this.chart.getDataSheet().getSample(i);
            s.setSelected(false);
        }
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        this.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
        this.draggedFilter = null;
    }

    @Override
    public void mouseEntered(MouseEvent e) {}

    @Override
    public void mouseExited(MouseEvent e) {}
}
