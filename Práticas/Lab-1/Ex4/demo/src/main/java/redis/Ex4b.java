package redis;

import redis.clients.jedis.Jedis;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Ex4b {
    private Jedis jedis;
    public static String KEY = "Key"; 
    List<String> NomesSort = new ArrayList<>();

    public Ex4b() {
        this.jedis = new Jedis();
    }

    public void getAutocompleteB(String answer) {
    NomesSort = jedis.zrevrange("Ex4b", 0, -1); // para vir já em ordem contrária
    for (String N : NomesSort){
        if (N.startsWith(answer)){
            Double score = jedis.zscore("Ex4b", N);
            System.out.println(N + " - " + score.intValue());
        }
    }
    }

    public static void main(String[] args) {
        Ex4b board = new Ex4b();
        String answer;
        try {
            BufferedReader leitor = new BufferedReader(new FileReader("./Ex4/demo/nomes-pt-2021.csv"));
            
            String line;
            while ((line = leitor.readLine()) != null) {

                String[] parts = line.split(";");

                if (parts.length == 2){
                    String nomes = parts[0].trim(); 
                    String popularidade = parts[1].trim();
                    board.jedis.zadd("Ex4b", Integer.parseInt(popularidade), nomes);
                }
            }
            leitor.close();
        } catch (IOException e) {
            System.err.println("Erro ao ler o ficheiro.");
            e.printStackTrace(); 
            System.exit(1);
        }


        Scanner sc2 = new Scanner(System.in);
        do{
            System.out.print("Search for ('Enter' for quit): ");
            answer = sc2.nextLine().toLowerCase();
            if(answer == "") 
                break;
            // chamar função
            board.getAutocompleteB(answer);
        }
        while(true);

        sc2.close();
    }
}
