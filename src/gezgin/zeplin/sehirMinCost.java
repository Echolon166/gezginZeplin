package gezgin.zeplin;

public class sehirMinCost {
    private int plaka;               //Baslangictan belirlenen sehire kadar olan
    private double cost;      //yolu ve yolun mesafesini saklamak icin olusturulan sinif.
    private String path;
    
    sehirMinCost(int plaka, double cost){
        setPlaka(plaka);
        setCost(cost);
    }
    
    sehirMinCost(int plaka, double cost, String path){
        setPlaka(plaka);
        setCost(cost);
        setPath(path);
    }
    
    private void setPlaka(int plaka){
        this.plaka = plaka;
    }
    
    public int getPlaka(){
        return plaka;
    }
    
    private void setCost(double cost){
        this.cost = cost;
    }
    
    public double getCost(){
        return cost;
    }
    
    private void setPath(String path){
        this.path = path;
    }
    
    public String getPath(){
        return path;
    }
}
