package redis;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.params.ScanParams;
import redis.clients.jedis.resps.ScanResult;
import redis.clients.jedis.resps.Tuple;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.Scanner;
import java.util.List;

public class Ex4a {
    private Jedis jedis;
    public static String KEY = "Key"; 

    public Ex4a() {
        this.jedis = new Jedis();
    }

    public void saveName(String name) {
        jedis.zadd(KEY, 1, name);     
    }

    public void getAutocomplete(String answer) {
        ScanParams scanParams = new ScanParams();
        scanParams.match(answer + "*");
        String cur = ScanParams.SCAN_POINTER_START;

        boolean cycleIsFinished = false;
        while (!cycleIsFinished) {
            ScanResult<Tuple> scanResult = jedis.zscan(KEY, cur, scanParams);
            List<Tuple> listResults = scanResult.getResult();

            // do whatever with the key-value pairs in result
            if (listResults.size()!=0){
                System.out.println(listResults.get(0).getElement());
            }
            cur = scanResult.getCursor();
            if (cur.equals("0")) {
                cycleIsFinished = true;
            }
        }
    }

    public static void main(String[] args) {
        Ex4a board = new Ex4a();
        String answer;

        try {
            FileInputStream ficheiro = new FileInputStream("./Ex4/demo/names.txt"); // para encontrar o caminho para o ficheiro
            Scanner sc = new Scanner(ficheiro);
        
            while (sc.hasNextLine()) {
                board.saveName(sc.nextLine());
            }
            sc.close();
        } catch (FileNotFoundException e) {
            System.err.println("O ficheiro não foi encontrado.");
            e.printStackTrace(); 
            System.exit(1);
        }


        Scanner sc2 = new Scanner(System.in);
        do{
            System.out.print("Search for ('Enter' for quit): ");
            answer = sc2.nextLine().toLowerCase();
            // chamar função
            if(answer.equals("")){
                break;
            }    
            board.getAutocomplete(answer);
        }
        while(true);

        sc2.close();
    }
}