package gezgin.zeplin;
import java.util.ArrayList;


public class LinkedList {

    class Node{      //Graph olusturulmasi icin node yapisi ve bu nodelerin kenar degerleri tanimlanir.
        Node next;
        int plaka;
        double cost;
        public Node(int plaka, double cost){
            this.plaka = plaka;
            this.cost = cost;
        }
    }
    
    public Node head = null;
    public Node tail = null;
    private int komsuSayisi = 0;
    
    public void insert(int plaka, double cost){
        Node newNode = new Node(plaka, cost);
        if(this.head == null){
            this.head = newNode;
            this.head.next = this.tail;
        }
        else{                             //Istenen sehre yeni bir komsu eklenir.
            if(this.head.next == null){
                this.head.next = newNode;
                this.tail = newNode;
            }
            else{
                this.tail.next = newNode;
                this.tail = newNode;
            }
        }
        this.komsuSayisi++;
    }
    
    public sehirMinCost enYakinNode(ArrayList<Integer> visited){
        double minCost = Double.MAX_VALUE;
        int minPlaka = -1;
        Node aktif = this.head;
        while(aktif != null){
            if(!visited.contains(aktif.plaka) && minCost > aktif.cost){
                minCost = aktif.cost;
                minPlaka = aktif.plaka;       //Istenen sehrin komsularindan en yakin olan belirlenir.
            }                              //Eger komsusu yoksa null dondurulur, var ise en yakin olan dondurulur.
            aktif = aktif.next;
        }
        if(minPlaka != -1){
            sehirMinCost TEMP = new sehirMinCost(minPlaka, minCost);
            return TEMP;
        }
        return null;
    }
    
    public ArrayList<Integer> butunKomsular(){
        ArrayList<Integer> komsular = new ArrayList<>();
        Node aktif = this.head;
        while (aktif != null){           //Istenen sehrin butun komsulari dondurulur.
            komsular.add(aktif.plaka);
            aktif = aktif.next;
        }
        return komsular;
    }
    
    public ArrayList<Double> butunCostlar(){
        ArrayList<Double> costlar = new ArrayList<>();
        Node aktif = this.head;
        while(aktif != null){          //Istenen sehrin komsulariyla olan mesafeler dondurulur.
            costlar.add(aktif.cost);
            aktif = aktif.next;
        }
        return costlar;
    }
    
    public int getKomsuSayisi(){
        return this.komsuSayisi;
    }

}

