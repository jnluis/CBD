package com.example;

import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

import org.bson.Document;

import com.mongodb.*;
import com.mongodb.client.AggregateIterable;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;

public class Ex5a {

    private static int limit = 2;
    private static int timeslot = 15000;

    public static void addRequest(MongoCollection<Document> col, String pedido, int t0) {
        int time = (int) System.currentTimeMillis();
        String[] words = pedido.split(";");
        int count = 0;
  
        List<Document> pipeline = Arrays.asList(
            new Document("$match", new Document(words[0] + ".products.TS", new Document("$gte", time - timeslot).append("$lte", time))),
            new Document("$project", new Document("NProducts", new Document("$size", "$" + words[0] + ".products"))));
            
        AggregateIterable<Document> result = col.aggregate(pipeline);
        Document d = result.first();
    
        if (d != null) 
            count = d.getInteger("NProducts");
    
        if (count < limit) {
            Document ExistUser = col.find(new Document(words[0], new Document("$exists", true))).first();

            if (ExistUser != null) {
                col.updateOne(ExistUser,
                        new Document("$push", new Document(words[0] + ".products",
                                new Document("product", words[1]).append("TS", t0))));
            } else {
                col.insertOne(new Document(words[0],
                        new Document("products", Arrays.asList(new Document("product", words[1]).append("TS", t0)))));
            }
            System.out.println("Pedido do utilizador: " + words[0] + " [produto: " + words[1] + "] aceite com sucesso");
            System.out.println();
        } else
            System.err.println("ERROR: Já passou o numero de pedidos nesta janela temporal;");
    }

    public static void main(String[] args) {
        String uri = "mongodb://127.0.0.1:27017/?directConnection=true&serverSelectionTimeoutMS=2000&appName=mongosh+1.10.6";

        try (MongoClient mongoClient = MongoClients.create(uri)) {
            MongoDatabase database = mongoClient.getDatabase("Att");
            MongoCollection<Document> collection = database.getCollection("orders");

            try (Scanner sc = new Scanner(System.in)) {
                String pedido = "";

                while (true) {
                    System.out.println(
                            "Insira o seu nome e o seu pedido, seguido ambos de um ';', ou prima 'q' para sair");
                    String op = sc.nextLine();
                    switch (op) {
                        case "q":
                            System.out.println("Saindo...");
                            System.exit(0);
                        default:
                            pedido = op.trim();
                            if (pedido == "") {
                                System.out.println("Não inseriu nenhum valor no seu pedido");
                                System.exit(0);
                            }
                            int currentTime1 = (int) System.currentTimeMillis(); // In seconds
                            addRequest(collection, pedido, currentTime1);
                    }
                }
            }
        } catch (MongoException me) {
            System.err.println(me);
            System.exit(1);
        }
    }
}
