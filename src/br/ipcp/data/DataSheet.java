package br.ipcp.data;

import java.util.ArrayList;

public class DataSheet {
    
    private ArrayList<Attribute> attributes = new ArrayList<>();
    
    private ArrayList<Sample> samples = new ArrayList<>();
    
    public void setTestData() {
        String[] rawAttributes = new String[] {
          "Cor", "Idade", "Símbolo Preferido"
        };
        
        String[][] rawSamples = new String[][] {
            new String[] { "Vermelho", "10", "14" },
            new String[] { "Verde", "5", "666" },
            new String[] { "Verde", "26", "X" },
            new String[] { "Vermelho", "0", "123" },
            new String[] { "Vermelho", "100", "Ç" },
            new String[] { "Preto", "23", "C" },
            new String[] { "Branco", "23", "14" }
        };
        
        int id = 0;
        
        for (int i = 0; i < rawAttributes.length; i++) {
            Attribute a = new Attribute(rawAttributes[i], this);
            this.attributes.add(i, a);
        }
        
        for (int i = 0; i < rawSamples.length; i++) {
            Sample s = new Sample(++id);
            for (int j = 0; j < rawAttributes.length; j++) {                
                s.setValue(this.attributes.get(j), rawSamples[i][j]);
            }
            this.samples.add(i, s);
        }
    }
    
    public int getAttributeCount() {
        return this.attributes.size();
    }
    
    public int getSampleCount() {
        return this.samples.size();
    }
    
    public Attribute getAttribute(int index) {
        return this.attributes.get(index);
    }
    
    public void setAttributes(ArrayList<Attribute> attributes) {
        this.attributes = attributes;
    }
    
    public ArrayList<Attribute> getAttributes() {
        return this.attributes;
    }
    
    public void setSamples(ArrayList<Sample> samples) {
        this.samples = samples;
    }
    
    public ArrayList<Sample> getSamples() {
        return this.samples;
    }
    
    public Sample getSample(int index) {
        return this.samples.get(index);
    }
    
    public DataSheet createSubDataSheet(ArrayList<Attribute> attrList) {
        DataSheet dataSheet = new DataSheet();
        dataSheet.setAttributes(attrList);
        dataSheet.setSamples(this.getSamples());
        
        return dataSheet;
    }
}
