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

public class Ex5b {

    private static int Unit_limit = 4;
    private static int timeslot = 15000;
    public static int NProducts = 0;

    public static void addRequest(MongoCollection<Document> col, String pedido, int t0) {
        int time = (int) System.currentTimeMillis();
        String[] words = pedido.split(";");
        

        List<Document> pipeline = Arrays.asList(
                new Document("$match",
                        new Document(words[0] + ".products.TS",
                                new Document("$gte", time - timeslot).append("$lte", time))),
                new Document("$project",
                        new Document("nproducts", new Document("$sum", "$" + words[0] + ".products.qty"))));

        AggregateIterable<Document> result = col.aggregate(pipeline);
        Document d = result.first();

        Integer count = Integer.parseInt(words[2]);
    
        if (d != null){
            count += d.getInteger("nproducts"); 
        } 

        if (count < Unit_limit) {

            Document ExistUser = col.find(new Document(words[0], new Document("$exists", true))).first();

            if (ExistUser != null) {
                col.updateOne(ExistUser,
                        new Document("$push", new Document(words[0] + ".products",
                                new Document("product", words[1]).append("qty", Integer.parseInt(words[2])).append("TS", t0))));
            } else {
                col.insertOne(new Document(words[0],
                        new Document("products", Arrays.asList(new Document("product", words[1])
                        .append("qty", Integer.parseInt(words[2]))
                        .append("TS", t0)))));
            }
            System.out.println("Pedido do utilizador: " + words[0] + " [produto: " + words[1] + " ,quantidade "
                    + words[2] + "] aceite com sucesso");

            System.out.println();
        } else
            System.err.println("ERROR: Já passou a quantidade de produto permitida nesta janela temporal;");
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
                            "Insira o seu nome, o seu pedido e a respetiva quantidade, seguido ambos de um ';', ou prima 'q' para sair");
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
