package com.example;

import redis.clients.jedis.Jedis;
import java.util.List;
import java.util.Scanner;

public class Ex5b {

    public Jedis jedis;

    private static int Unit_limit = 6; // 2 pedidos por unidade
    private static int timeslot = 60000;
    public int NProducts = 0;
    
    public Ex5b() {
        this.jedis = new Jedis();
    }

    public void addRequest(String pedido, int t0) {
        int time = (int) System.currentTimeMillis();
        String[] words = pedido.split(";");
        
        List<String> requests = jedis.zrangeByScore(words[0], time - timeslot, time);
        
        NProducts += Integer.parseInt(words[2]);
        if(NProducts < Unit_limit) {
            jedis.zadd(words[0], t0, words[1]);
            System.out.println("Pedido do utilizador: " + words[0] + " [produto: " + words[1] + " ,quantidade " + words[2] + "] aceite com sucesso");

            System.out.println();
        } else 
            System.err.println("ERROR: Já passou a quantidade de produto permitida nesta janela temporal;"); 
        }

    public static void main(String[] args) {
        Ex5b board = new Ex5b();
        try (Scanner sc = new Scanner(System.in)) {
            String pedido = "";

            while (true) {
                System.out.println("Insira o seu nome, o seu pedido e a respetiva quantidade, seguido ambos de um ';', ou prima 'q' para sair");
                String op = sc.nextLine();
                switch (op) {
                    case "q":
                        System.out.println("Saindo...");
                        System.exit(0);
                    default:
                        pedido = op.trim();
                        if (pedido == ""){
                            System.out.println("Não inseriu nenhum valor no seu pedido");
                            System.exit(0);
                        }
                        int currentTime1 = (int)System.currentTimeMillis(); // In seconds
                        board.addRequest(pedido, currentTime1);
                }
                       
            }
        }
    }
}
