package gezgin.zeplin;
import java.awt.image.BufferedImage;
import java.util.Scanner;
import java.util.InputMismatchException;
import java.util.ArrayList;
import java.io.*;
import java.net.URL;
import java.util.Collections;
import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;


public class GezginZeplin {

    public static void main(String[] args) throws IOException{
        long startTime, endTime;
        LinkedList[] Komsuluklar = new LinkedList[81];
        int rota[] = rotaAl();
        int baslangicPlaka = rota[0];
        int bitisPlaka = rota[1];
        startTime = System.currentTimeMillis();
        dosyalariDuzenle();
        endTime = System.currentTimeMillis();
        System.out.println("Dosyalarin duzenlenmesi " + (double)(endTime - startTime)/1000 + " saniye surdu.");
        if(problemiAl() == 1){
            startTime = System.currentTimeMillis();
            sabitUcretleMaxKar(Komsuluklar, baslangicPlaka, bitisPlaka);
            endTime = System.currentTimeMillis();
            System.out.println("Sabit Ucretle Max. Kar probleminin cozumu toplam " + (double)(endTime - startTime)/1000 + " saniye surdu.");

        }
        else{
            startTime = System.currentTimeMillis();
            yuzdeElliKar(Komsuluklar, baslangicPlaka, bitisPlaka);
            endTime = System.currentTimeMillis();
            System.out.println("%50 Kar probleminin cozumu toplam " + (double)(endTime - startTime)/1000 + " saniye surdu.");
        }
    }
    
    public static void dosyalariDuzenle() throws IOException{
        String fileNameLL = "lat long.txt";
        String fileNameNBH = "Komşuluklar.txt";
        String fileNameSB = "Sehir Bilgileri.txt";
        
        FileReader fileReaderLL = new FileReader(fileNameLL);
        BufferedReader bufferedReaderLL = new BufferedReader(fileReaderLL);
        FileReader fileReaderNBH = new FileReader(fileNameNBH);
        BufferedReader bufferedReaderNBH = new BufferedReader(fileReaderNBH);
        FileWriter fileWriter = new FileWriter(fileNameSB);
        BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);
        String line;
        
        while((line = bufferedReaderLL.readLine()) != null){               //Veriler okunur, duzenlenir ve 
            String[] saveParcalar = line.split(",");                    //plaka kodu,lat,long,rakim ve komsuluklari
            String NBHline = bufferedReaderNBH.readLine();                 //iceren yeni bir dosya hazirlanir.
            if(Character.isDigit(NBHline.charAt(0))){
                if(Character.isDigit(NBHline.charAt(1))){
                    NBHline = NBHline.substring(3).replaceAll(",","-");
                }
                else{
                    NBHline = NBHline.substring(2).replaceAll(",","-");
                }
            }
            bufferedWriter.write(saveParcalar[2] + "," + saveParcalar[0] + "," + saveParcalar[1] + "," + saveParcalar[3] + "," + NBHline);
            bufferedWriter.newLine();
        }
        bufferedReaderLL.close();
        bufferedReaderNBH.close();
        bufferedWriter.close();
    }
    
    public static void graphOlustur(LinkedList[] Komsuluklar, int yolcuSayisi, int baslangicPlaka, int bitisPlaka) throws IOException{
        String fileName = "Sehir Bilgileri.txt";
        FileReader fileReader = new FileReader(fileName);                                
        BufferedReader bufferedReader = new BufferedReader(fileReader);
        String fileNameW = "Komsuluklar_arasi_mesafeler.txt";
        FileWriter fileWriter = new FileWriter(fileNameW);
        BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);
        String line;                              //yolcuSayisi, baslangicPlaka ve bitisPlaka degerlerine bagimli olarak
        int i = 1;                           //LinkedList class yapisi icerisinde her bir nodenin "Adjacency List"leri olusturulur.
        bufferedReader.readLine();
        bufferedWriter.write("Komsuluklar  Egim  Mesafe");
        while(i<=81){
            line = bufferedReader.readLine();
            String[] parcalar = line.split(",");
            String[] paramParcalar = parcalar[4].split("-");
            for(int j=0; j<paramParcalar.length; j++){
                FileReader fileReaderTEMP = new FileReader(fileName);
                BufferedReader bufferedReaderTEMP = new BufferedReader(fileReaderTEMP);
                String lineTEMP = null;
                for(int k=-1; k<Integer.parseInt(paramParcalar[j]); k++){
                    lineTEMP = bufferedReaderTEMP.readLine();
                }
                String[] parcalarTEMP = lineTEMP.split(",");
                double mesafeKM = haversineFormula(Double.parseDouble(parcalar[1]), Double.parseDouble(parcalar[2]), Double.parseDouble(parcalarTEMP[1]), Double.parseDouble(parcalarTEMP[2]));
                double yukseklikFarkiM;
                if(i == baslangicPlaka){
                    yukseklikFarkiM = Double.parseDouble(parcalar[3]) - (Double.parseDouble(parcalarTEMP[3]) + 50.0);
                }
                else if(Integer.parseInt(parcalarTEMP[0]) == bitisPlaka){
                    yukseklikFarkiM = (Double.parseDouble(parcalar[3]) + 50.0) - Double.parseDouble(parcalarTEMP[3]);
                }
                else{
                    yukseklikFarkiM = Double.parseDouble(parcalar[3]) - Double.parseDouble(parcalarTEMP[3]);
                }
                double egim = Math.toDegrees(Math.atan(yukseklikFarkiM / mesafeKM));  
                if(80-yolcuSayisi >= egim && egim >= yolcuSayisi-80){  //Eger egim (80-yolcuSayisi)'ndan buyuk ise zeplin oraya ucamayacagindan
                    double gidilecekYol = Math.sqrt(Math.pow(yukseklikFarkiM/1000,2) + Math.pow(mesafeKM, 2));      //grapha bu node dahil edilmez.
                    Komsuluklar[i-1].insert(Integer.parseInt(parcalarTEMP[0]), gidilecekYol); 
                }
                bufferedWriter.newLine();
                bufferedWriter.write(i + "->" + parcalarTEMP[0] + "  Egim: " + egim + "   Mesafe: " + mesafeKM);
                bufferedReaderTEMP.close();                    //Her bir komsulugu, aralarindaki egim ve mesafeyi iceren bir dosya olusturulur.
            }
            i++;
        }
       bufferedReader.close(); 
       bufferedWriter.close();
    }
    
    public static int[] rotaAl(){
        Scanner keyboard = new Scanner(System.in);
        int baslangicPlaka = 0, bitisPlaka = 0;       //Baslangic ve bitis sehri kullanicidan alinir.
        while(baslangicPlaka>81 || baslangicPlaka<1){
            System.out.print("Zeplinin kalkis yapacagi ilin plakasini giriniz: ");
            try{
                baslangicPlaka = keyboard.nextInt();
            }
            catch(InputMismatchException ex){
                System.out.println("Lutfen plakayi numerik formatta giriniz.");
                keyboard.next();
            }
        }
        while(bitisPlaka>81 || bitisPlaka<1 || bitisPlaka == baslangicPlaka){
            if(bitisPlaka == baslangicPlaka){
                System.out.println("Zeplinin kalktigi ve indigi iller farkli olmalidir.");
            }
            System.out.print("Zeplinin inis yapacagi ilin plakasini giriniz: ");
            try{
                bitisPlaka = keyboard.nextInt();
            }
            catch(InputMismatchException ex){
                System.out.println("Lutfen plakayi numerik formatta giriniz.");
                keyboard.next();
            }
        }
        return new int[] {baslangicPlaka, bitisPlaka};
    }
    
    public static int problemiAl(){
        int problem = 0;
        Scanner keyboard = new Scanner(System.in);
        while(problem>2 || problem<1){
            System.out.print("Sabit ucretle maksimum problemi icin 1, %50 kar problemi icin 2 giriniz: ");
            try{
                problem = keyboard.nextInt();
            }
            catch(InputMismatchException ex){
                System.out.println("Lutfen kisi sayisini numerik formatta giriniz.");
                keyboard.next();
            }
        }
        return problem;
    }
    
    public static double haversineFormula(double lat1, double long1, double lat2, double long2){
        double dLat  = Math.toRadians((lat2 - lat1));                      //Haversine formulu kullanilarak
        double dLong = Math.toRadians((long2 - long1));   //enlem-boylamlari bilinen iki sehir arasindaki uzaklik hesaplanir.
        lat1 = Math.toRadians(lat1);                           //https://en.wikipedia.org/wiki/Haversine_formula
        lat2   = Math.toRadians(lat2);                         
        double a = Math.pow(Math.sin(dLat/2), 2) + Math.cos(lat1) * Math.cos(lat2) * Math.pow(Math.sin(dLong/2),2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        c = c*6371;
        return c;
    }
     
    public static sehirMinCost dijkstraAlgorithm(LinkedList[] Komsuluklar, int baslangicPlaka, int bitisPlaka){
        ArrayList<Integer> visited = new ArrayList<>();
        visited.add(baslangicPlaka);
        sehirMinCost[] minCost = new sehirMinCost[81];
        minCost[baslangicPlaka-1] = new sehirMinCost(baslangicPlaka, 0.0, Integer.toString(baslangicPlaka));
        while(!visited.contains(bitisPlaka)){
            sehirMinCost tempNode = null;                //Djikstra algoritmasi kullanilarak 2 sehir arasindaki
            double minCostTemp = Double.MAX_VALUE;                      //en kisa yol bulunur.
            int minYolNerede = -1;                       //https://en.wikipedia.org/wiki/Dijkstra%27s_algorithm
            int plakaTemp = -1;
            for(int i=0; i<visited.size(); i++){
                if(Komsuluklar[visited.get(i)-1].getKomsuSayisi() > 0){
                    tempNode = Komsuluklar[visited.get(i)-1].enYakinNode(visited);
                }
                if(tempNode != null && minCostTemp > (tempNode.getCost() + minCost[visited.get(i)-1].getCost())){
                    minCostTemp = tempNode.getCost() + minCost[visited.get(i)-1].getCost();
                    plakaTemp = tempNode.getPlaka();
                    minYolNerede = i;
                }
            }
            if(plakaTemp != -1){
                minCost[plakaTemp-1] = new sehirMinCost(plakaTemp, minCostTemp, minCost[visited.get(minYolNerede)-1].getPath() + "-" + plakaTemp);
                visited.add(plakaTemp);
            }
            else{
                return null;
            }
        }
        return minCost[bitisPlaka-1];
    }
    
    public static void depthFirstSearch(LinkedList[] Komsuluklar, ArrayList<String> paths, ArrayList<Double> pathCosts, int bitisPlaka, double minCost, double currentCost, ArrayList<Integer> visited) throws IOException{
        ArrayList<Integer> nodes = Komsuluklar[visited.get(visited.size()-1)-1].butunKomsular();
        ArrayList<Double> nodeCosts = Komsuluklar[visited.get(visited.size()-1)-1].butunCostlar();
        for(int i=0; i<nodes.size(); i++){
            if(visited.contains(nodes.get(i))){             //Depth-first search algoritmasi kullanilarak
                continue;                                    //iki sehir arasindaki butun yollar bulunur.
            }                                             //https://en.wikipedia.org/wiki/Depth-first_search
            if(bitisPlaka == nodes.get(i)){
               visited.add(nodes.get(i));
               pathCosts.add(currentCost + nodeCosts.get(i));
               String Temp = "";
               for(int j=0; j<visited.size(); j++){
                   if(j == 0){
                       Temp = Integer.toString(visited.get(j));
                   }
                   else{
                       Temp = Temp + "-" + visited.get(j);
                   }
                }
                paths.add(Temp);
                visited.remove(visited.size()-1);
                break;
            }
        }
        for(int i=0; i<nodes.size(); i++){
            if(visited.contains(nodes.get(i)) || bitisPlaka == nodes.get(i)){
                continue;                               //2 Sehir arasindaki butun yollari bulmak cok uzun surdugunden
            }                                     //bulunacak yollarin en kisa yolun 2 katindan daha uzun olamamasi ayarlanir.   
            visited.add(nodes.get(i));
            if(currentCost + nodeCosts.get(i) < minCost*2){  //Yolun aranacagi aralik degistirilebilir, istenirse kaldirilabilir.
                depthFirstSearch(Komsuluklar, paths, pathCosts, bitisPlaka, minCost, currentCost+nodeCosts.get(i), visited);
            }
            visited.remove(visited.size()-1);
        }
    }
    
    public static void haritadaGoster(String path, String title) throws IOException{
        BufferedImage image = null;
        String fileName = "Sehir Bilgileri.txt";       //Bulunan en kisa yol Google Static Maps API araciligi ile haritaya cizilir.
        String[] parcalar = path.split("-");                  //Ekrana cikti verilir ve dosyaya .png uzantisi ile kaydedilir.
        String[] latT = new String[parcalar.length];          //https://developers.google.com/maps/documentation/static-maps/
        String[] longT = new String[parcalar.length];
        String resimUrl = "https://maps.googleapis.com/maps/api/staticmap?&size=640x360&scale=2&style=feature:road|visibility:off&center=39.1702,35.1430&path=color:0xff0000ff|weight:2";
        for(int i=0; i<parcalar.length; i++){
            FileReader fileReader = new FileReader(fileName);
            BufferedReader bufferedReader = new BufferedReader(fileReader);
            String line = null;
            for(int k=-1; k<Integer.parseInt(parcalar[i]); k++){
                line = bufferedReader.readLine();
            }
            String[] parcalarTEMP = line.split(",");   //Haritanin URL'si olusturulur.
            latT[i] = parcalarTEMP[1];
            longT[i] = parcalarTEMP[2];
            resimUrl = (resimUrl + "|" + latT[i] + "," + longT[i]);
        }
        resimUrl = resimUrl + "&markers=color:blue|label:B|" + latT[0] + "," + longT[0];
        resimUrl = resimUrl + "&markers=color:blue|label:S|" + latT[latT.length-1] + "," + longT[longT.length-1];
        resimUrl = resimUrl + "&markers=color:blue|size:tiny";
        for(int i=1; i<latT.length-1; i++){
            resimUrl = resimUrl + "|" + latT[i] + "," + longT[i];
        }
        resimUrl = resimUrl + "&zoom=5&key=AIzaSyBqHdy_X4cPsKncpEDTXszvo03FbkdXHuI";
        URL url = new URL(resimUrl);
        image = ImageIO.read(url);     //URL'den harita resim formatinda cekilir.
        
        String kayitTitle = title + ".png";
        ImageIO.write(image, "png", new File(kayitTitle));  //Cekilen harita .png formatinda kayit edilir.
        
        JFrame frame = new JFrame();
        frame.setSize(1300, 760);      //Harita ekranda gosterilir.
        frame.setTitle(title);
        JLabel label = new JLabel(new ImageIcon(image));
        frame.add(label);
        frame.setVisible(true);
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
    }
    
    public static void yuzdeElliKar(LinkedList[] Komsuluklar, int baslangicPlaka, int bitisPlaka) throws IOException{
        long startTime, endTime;
        startTime = System.currentTimeMillis(); 
        sehirMinCost[] minCost = new sehirMinCost[5];
        for(int i=0; i<minCost.length; i++){             //10-20-30-40-50 yolcu sayilari icin en karli yollar bulunur.
            minCost[i] = null;                                 //Her bir ozgun yol harita uzerinde gosterilir.
        }                                               //Sehirler arasindaki butun yollar bulunur ve min. ucretlere gore
        for(int i=50; i>9; i-=10){                                       //siralanarak dosyaya yazilir.
            for(int j=0; j<Komsuluklar.length; j++){
                Komsuluklar[j] = new LinkedList();
            }
            graphOlustur(Komsuluklar, i, baslangicPlaka, bitisPlaka);   //Her kisi sayisi icin tek tek Graph olusturulur.
            minCost[i/10-1] = dijkstraAlgorithm(Komsuluklar, baslangicPlaka, bitisPlaka);  //Aralarindaki en karli yol bulunur.
        }
        if(minCost[0] != null){
            for(int i=50; i>9; i-=10){
                if(minCost[i/10-1] != null){
                    System.out.println();
                    System.out.println(i + " kisi ile hareket edildiginde " + baslangicPlaka + " ile " + bitisPlaka + " sehirleri arasindaki en karli rota:");
                    System.out.println(minCost[i/10-1].getPath());
                    double ucret = (maaliyetHesapla(minCost[i/10-1].getCost())*1.5)/i;
                    System.out.println("%50 kar elde etmek icin her bir yolcudan " + ucret + " TL alinmalidir.");
                }
                else{
                    System.out.println("Istediginiz sehirler arasinda " + i + " kisilik yol bulunamadi.");
                }
            }
            endTime = System.currentTimeMillis();
            System.out.println("10-20-30-40-50 yolcu sayilari icin en karli yollarin bulunmasi " + (double)(endTime - startTime)/1000 + " saniye surdu.");
            startTime = System.currentTimeMillis(); 
            for(int j=0; j<Komsuluklar.length; j++){
                Komsuluklar[j] = new LinkedList();
            }
            graphOlustur(Komsuluklar, 10, baslangicPlaka, bitisPlaka);
            ArrayList<String> paths = new ArrayList<>();
            ArrayList<Double> pathCosts = new ArrayList<>();
            ArrayList<Integer> visited = new ArrayList();
            visited.add(baslangicPlaka);
            depthFirstSearch(Komsuluklar, paths, pathCosts, bitisPlaka, minCost[0].getCost(), 0.0, visited); //Sehirler arasindaki butun yollar bulunur.
            butunYollariYazdir(paths, pathCosts, baslangicPlaka, bitisPlaka, 2);  //Min. ucretlerine gore siralanarak dosyaya yazdirilir
            endTime = System.currentTimeMillis();
            System.out.println("Sehirler arasindaki butun yollarin bulunup yazdirilmasi " + (double)(endTime - startTime)/1000 + " saniye surdu.");
            startTime = System.currentTimeMillis(); 
            for(int i=50; i>9; i-=10){
                if(minCost[i/10-1] != null && (i == 50 || minCost[i/10] == null || minCost[i/10-1].getPath() != minCost[i/10].getPath())){
                    haritadaGoster(minCost[i/10-1].getPath(), baslangicPlaka + "-" + bitisPlaka + "_arasi_" + i + "_kisi_icin_en_karli_rota");  //En karli yol haritada gosterilir.
                }
            }
            endTime = System.currentTimeMillis();
            System.out.println("En karli yollarin haritada gosterilmesi " + (double)(endTime - startTime)/1000 + " saniye surdu.");
        }
        else{
            System.out.println("Istediginiz sehirler arasinda yol bulunamadi.");
        }
    }
    
    public static void sabitUcretleMaxKar(LinkedList[] Komsuluklar, int baslangicPlaka, int bitisPlaka) throws IOException{
        long startTime, endTime;
        startTime = System.currentTimeMillis();
        sehirMinCost minCost = null;
        int minCostYolcuSayisi = 0;                          //5-50 arasinda yolcu sayisi icin en karli yol bulunur.
        for(int i=50; i>4; i--){                                       //Harita uzerinde gosterilir.
            for(int j=0; j<Komsuluklar.length; j++){    //Sehirler arasindaki butun yollar bulunur ve max. karlarina gore
                Komsuluklar[j] = new LinkedList();                   //siralanarak dosyaya yazdirilir.
            }
            sehirMinCost minCostTemp = null;
            graphOlustur(Komsuluklar, i, baslangicPlaka, bitisPlaka);  //Her kisi sayisi icin tek tek Graph olusturulur.
            minCostTemp = dijkstraAlgorithm(Komsuluklar, baslangicPlaka, bitisPlaka);    //Aralarindaki en karli yol bulunur.
            if(minCostTemp != null){
                if(minCost == null){
                    minCost = minCostTemp;
                    minCostYolcuSayisi = i;
                }
                else{
                    double minCostNet = gelirHesapla(minCostYolcuSayisi) - maaliyetHesapla(minCost.getCost());
                    double minCostTempNet = gelirHesapla(i) - maaliyetHesapla(minCostTemp.getCost());
                    if(minCostTempNet > minCostNet){
                        minCost = minCostTemp;
                        minCostYolcuSayisi = i;
                    }
                }
            }
        }
        if(minCost != null){
            System.out.println();
            System.out.println("Sabit ucret " + gelirHesapla(1) + " alindiginda " + baslangicPlaka + " ile " + bitisPlaka + " sehirleri arasindaki en karli rota: ");
            System.out.println(minCost.getPath());
            double income = gelirHesapla(minCostYolcuSayisi) - maaliyetHesapla(minCost.getCost());
            if(income > 0){
                System.out.println(minCostYolcuSayisi + " kisi ile gidildiginde " + income + " kar yapilir.");
            }
            else{
                System.out.println(minCostYolcuSayisi + " kisi ile gidildiginde " + income + " zarar yapilir.");
            }
            endTime = System.currentTimeMillis();
            System.out.println("5-50 yolcu sayisi icin en karli yolun bulunmasi " + (double)(endTime - startTime)/1000 + " saniye surdu.");
            startTime = System.currentTimeMillis(); 
            for(int j=0; j<Komsuluklar.length; j++){
                Komsuluklar[j] = new LinkedList();
            }
            graphOlustur(Komsuluklar, 5, baslangicPlaka, bitisPlaka);
            ArrayList<String> paths = new ArrayList<>();
            ArrayList<Double> pathCosts = new ArrayList<>();    
            ArrayList<Integer> visited = new ArrayList();   
            visited.add(baslangicPlaka);
            depthFirstSearch(Komsuluklar, paths, pathCosts, bitisPlaka, minCost.getCost(), 0.0, visited); //Sehirler arasindaki butun yollar bulunur.  
            butunYollariYazdir(paths, pathCosts, baslangicPlaka, bitisPlaka, 1);   //Max. karlarina gore siralanarak dosyaya yazdirilir.
            endTime = System.currentTimeMillis();
            System.out.println("Sehirler arasindaki butun yollarin bulunup yazdirilmasi " + (double)(endTime - startTime)/1000 + " saniye surdu.");
            startTime = System.currentTimeMillis(); 
            haritadaGoster(minCost.getPath(), baslangicPlaka + "-" + bitisPlaka + "_arasi_en_karli_rota");  //En karli yol haritada gosterilir.
            endTime = System.currentTimeMillis();
            System.out.println("En karli yolun haritada gosterilmesi " + (double)(endTime - startTime)/1000 + " saniye surdu.");
        }
        else{
            System.out.println("Istediginiz sehirler arasinda yol bulunamadi.");
        }
    }
    
    public static void butunYollariYazdir(ArrayList<String> paths, ArrayList<Double> pathCosts, int baslangicPlaka, int bitisPlaka, int hangiProblem) throws IOException{
        ArrayList<String> komsuluklar = new ArrayList<>();
        ArrayList<Double> komsuluklarEgim = new ArrayList<>();          //Sehirler arasindaki butun yollar karlarina gore siralanir.
        String fileNameR = "Komsuluklar_arasi_mesafeler.txt";
        FileReader fileReader = new FileReader(fileNameR);
        BufferedReader bufferedReader = new BufferedReader(fileReader);
        String line;
        bufferedReader.readLine();
        while((line = bufferedReader.readLine()) != null){
            String[] parcalar = line.split("  ");
            komsuluklar.add(parcalar[0]);
            komsuluklarEgim.add(Double.parseDouble(parcalar[1].split(":")[1]));
        }
        bufferedReader.close();
        
        String fileName = baslangicPlaka + "-" + bitisPlaka + "_arasi_alternatif_rotalar.txt";
        FileWriter fileWriter = new FileWriter(fileName);
        BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);    

        ArrayList<Double> pathCostsSorted = new ArrayList<>(pathCosts);
        ArrayList<String> pathsSorted = new ArrayList<>();
        Collections.sort(pathCostsSorted);
        for(int i=0; i<pathCostsSorted.size(); i++){
            pathsSorted.add(paths.get(pathCosts.indexOf(pathCostsSorted.get(i))));
        }
        if(hangiProblem == 1){      //Siralanan bu yollar dosyaya karlariyla birlikte yazdirilir.
            bufferedWriter.write("KisiSayisi  MaxKar  Rota");
            for(int i=0; i<pathsSorted.size(); i++){
                double maxEgim = 0;
                String[] parcalar = pathsSorted.get(i).split("-");
                for(int j=0; j<parcalar.length-1; j++){
                    String path = parcalar[j] + "->" + parcalar[j+1];
                    double egimTemp = Math.abs(komsuluklarEgim.get(komsuluklar.indexOf(path)));
                    if(egimTemp > maxEgim){
                        maxEgim = egimTemp;
                    }
                }
                double income = gelirHesapla(80-((int)maxEgim+1)) - maaliyetHesapla(pathCostsSorted.get(i));
                bufferedWriter.newLine();
                bufferedWriter.write(80-((int)maxEgim+1) + " " + Double.toString(income) + "  " + pathsSorted.get(i));
            }
        }
        else if(hangiProblem == 2){     //Siralanan bu yollar dosyaya ucretleriyle birlikte yazdirilir.
            bufferedWriter.write("KisiSayisi  AlinacakUcret  Rota");
            for(int i=0; i<pathsSorted.size(); i++){
                double maxEgim = 0;
                String[] parcalar = pathsSorted.get(i).split("-");
                for(int j=0; j<parcalar.length-1; j++){
                    String path = parcalar[j] + "->" + parcalar[j+1];
                    double egimTemp = Math.abs(komsuluklarEgim.get(komsuluklar.indexOf(path)));
                    if(egimTemp > maxEgim){
                        maxEgim = egimTemp;
                    }
                }
                int yolcuSayisi = ((int)Math.floor((80-((int)maxEgim+1))/10))*10;
                double ucret = (maaliyetHesapla(pathCostsSorted.get(i))*1.5)/yolcuSayisi;
                bufferedWriter.newLine();
                bufferedWriter.write(yolcuSayisi + " " + Double.toString(ucret) + " " + pathsSorted.get(i));
            }
        }
        bufferedWriter.close();
    }
    
    public static double maaliyetHesapla(double cost){
        double yuzKmCost = 1000.0;   //Zeplinin yuz km basina harcadigi TL.
        return cost*(yuzKmCost/100);
    }
    
    public static double gelirHesapla(int yolcuSayisi){
        double ucret = 20.0;    //Yolcu basina alinan sabit ucret.
        return ucret*yolcuSayisi;
    }
}