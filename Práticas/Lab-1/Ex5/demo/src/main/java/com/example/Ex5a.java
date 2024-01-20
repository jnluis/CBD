package com.example;

import redis.clients.jedis.Jedis;
import java.util.List;
import java.util.Scanner;

public class Ex5a {

    public Jedis jedis;

    private static int limit = 2;
    private static int timeslot = 30000;

    public int NProducts = 0;
    // USER : {[pedido, TS ],[]}

    public Ex5a() {
        this.jedis = new Jedis();
    }

    public void addRequest(String pedido, int t0) {
        int time = (int) System.currentTimeMillis();
        String[] words = pedido.split(";");

        List<String> requests = jedis.zrangeByScore(words[0], time - timeslot, time);

        if(requests.size() < limit) {
            jedis.zadd(words[0], t0, words[1]);
            System.out.println("Pedido do utilizador: " + words[0] + " [produto: " + words[1] + "] aceite com sucesso");
            System.out.println();
        } else 
            System.err.println("ERROR: Já passou o numero de pedidos nesta janela temporal;"); 
    }

    // Se for para pedir ao user
    // public void printOptions() {
    // System.out.println("Options available:");
    // System.out.println("1 - Add a request;");
    // System.out.println("2 - Exit");
    // }

    public static void main(String[] args) {
        Ex5a board = new Ex5a();
        try (Scanner sc = new Scanner(System.in)) {
            String pedido = "";

            while (true) {
                System.out.println("Insira o seu nome e o seu pedido, seguido ambos de um ';', ou prima 'q' para sair");
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
            // Isto era para uma versão inicial do código, em que não se pedia info's ao user mas era logo tudo passado aqui
            // long currentTime1 = System.currentTimeMillis() / 1000; // In seconds
            // Lista.add("Joao;bolacha;" + Long.toString(currentTime1));
        }

        // long currentTime2 = System.currentTimeMillis() / 1000; // In seconds
        // Lista.add("Maria;Ketchup;" + Long.toString(currentTime2));

        // long currentTime3 = System.currentTimeMillis() / 1000; // In seconds
        // Lista.add("Joao;bacalhau;" + Long.toString(currentTime3));

        // long currentTime4 = System.currentTimeMillis() / 1000; // In seconds
        // Lista.add("joao;agua;" + Long.toString(currentTime4));

        // for(String str: Lista){
        // String[] tmp = str.split(";");
        // board.addRequest(tmp[0],tmp[1] + "-" + tmp[2], t0);
        // }

    }
}