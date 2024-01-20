package com.example;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.bson.Document;

import com.mongodb.*;
import com.mongodb.client.AggregateIterable;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Indexes;
import com.mongodb.client.model.Projections;

public class Ex3 {
    public static void main(String[] args) {
        String uri = "mongodb://127.0.0.1:27017/?directConnection=true&serverSelectionTimeoutMS=2000&appName=mongosh+1.10.6";

        Document doc = new Document("address", new Document("building", "10000")
                .append("coord", Arrays.asList(-77.4293, 42.6841165)))
                .append("rua", "Bold Street")
                .append("zipcode", "55599")
                .append("localidade", "Bold State")
                .append("gastronomia", "Bold Dish")
                .append("grades", Arrays.asList(new Document("date", "1423554800000")
                        .append("grade", "B")
                        .append("score", "10"),
                        new Document("date", "1423554910000")
                                .append("grade", "A+")
                                .append("score", "25")))
                .append("nome", "Restaurante da Pedra")
                .append("restaurant_id", "111222333");

        try (MongoClient mongoClient = MongoClients.create(uri)) {
            MongoDatabase database = mongoClient.getDatabase("cbd");
            MongoCollection<Document> collection = database.getCollection("restaurants");

            System.out.println("<<<<<<<<<<<<<<<<<<<<<<<<< A) >>>>>>>>>>>>>>>>>>>>>>");
            InsertElement(collection, doc);
            EditElement(collection, "rua", "Astoria Boulevard", "Lutton Triangle");
            SearchElem(collection, new Document("localidade", "Bronx"));

            System.out.println();
            System.out.println("<<<<<<<<<<<<<<<<<<<<<<<<< B) >>>>>>>>>>>>>>>>>>>>>>");
            SingleFieldIndex(collection, "localidade");

            System.out.println("Para o índice 'localidade': ");
            SearchElem(collection, new Document("localidade","Bronx")); // é suposto vir
            // as respostas todas tal e qual como na alinea a', mas de forma mais rápida

            SingleFieldIndex(collection, "gastronomia");
            System.out.println();
            System.out.println("Para o índice 'gastronomia': ");
            SearchElem(collection, new Document("gastronomia","Hotdogs"));

            TextIndex(collection, "nome");
            System.out.println();
            System.out.println("Para o índice de texto 'nome': ");
            SearchElem(collection, new Document("nome","Deli")); // só funciona se
            // especificar um nome concreto de um restaurante

            System.out.println();
            System.out.println("<<<<<<<<<<<<<<<<<<<<<<<<< C) >>>>>>>>>>>>>>>>>>>>>>");
            System.out.println(
                    "\n2. Apresente os campos restaurant_id, nome, localidade e gastronomia para todos os documentos da coleção.");
            FindIterable<Document> iterable = collection.find()
                    .projection(Projections.include("restaurant_id", "nome", "localidade", "gastronomia"));
            for (Document document : iterable) {
                System.out.println(document.toJson());
            }

            System.out.println();
            System.out.println("\n4. Indique o total de restaurantes localizados no Bronx.");
            long count = collection.countDocuments(new Document("localidade", "Bronx"));
            System.out.println(">> " + count);

            System.out.println();
            System.out.println(
                    "\n7. Encontre os restaurantes que obtiveram uma ou mais pontuações (score) entre [80 e 100].");

            FindIterable<Document> iterable2 = collection
                    .find(Filters.and(Filters.gte("grades.score", 80), Filters.lte("grades.score", 100)));
            for (Document document : iterable2) {
                System.out.println(document.toJson());
            }

            System.out.println();
            System.out.println(
                    "\n10. Liste o restaurant_id, o nome, a localidade e gastronomia dos restaurantes cujo nome começam por \"Wil\".");
            FindIterable<Document> iterable3 = collection.find(Filters.regex("nome", "Will*"))
                    .projection(Projections.include("restaurant_id", "nome", "localidade", "gastronomia"));
            for (Document document : iterable3) {
                System.out.println(document.toJson());
            }

            System.out.println();
            System.out.println("\n22. Conte o total de restaurante existentes em cada localidade.");

            List<Document> pipeline = Arrays.asList(
                    new Document("$group", new Document("_id", "$localidade")
                            .append("totalRestaurantes", new Document("$sum", 1))),
                    new Document("$project", new Document("_id", 0)
                            .append("localidade", "$_id")
                            .append("totalRestaurantes", 1)));

            AggregateIterable<Document> output = collection.aggregate(pipeline);

            for (Document document : output) {
                System.out.println(document.toJson());
            }

            System.out.println();
            System.out.println("<<<<<<<<<<<<<<<<<<<<<<<<< D) >>>>>>>>>>>>>>>>>>>>>>");
            int N = countLocalidades(collection);
            System.out.println("Número de localidades distintas: " + N);

            System.out.println();
            Map<String, Integer> RestLocalidades = countRestLocalidades(collection);
            System.out.println("Número de restaurantes por localidade");
            Set<String> chaves = RestLocalidades.keySet(); // ir buscar as keys todas do Map de antes e metê-las num set
            for (String k: chaves) {
                System.out.println(" -> "+ k +" - "+ RestLocalidades.get(k)); // Aqui através do get vai-se buscar o valor inteiro associado
            }   

            System.out.println();
            String NTCompare = "Park";
            List<String> RestNames = getRestWithNameCloserTo(collection, NTCompare);
            System.out.println("Nome de restaurantes contendo '" + NTCompare +"'no nome:");
            for(String n: RestNames){
                System.out.println(" -> "+ n);
            }

        } catch (MongoException me) {
            System.err.println(me);
            System.exit(1);
        }
    }

    //////////////////////////// A) Functions //////////////////////////////
    public static void InsertElement(MongoCollection<Document> col, Document Doc) {
        col.insertOne(Doc);
    }

    public static void EditElement(MongoCollection<Document> col, String param, String oldStr, String newStr) {
        col.updateOne(Filters.eq(param, oldStr), new Document("$set", new Document(param, newStr)));
    }

    public static void SearchElem(MongoCollection<Document> col, Document doc) {
        FindIterable<Document> iterable = col.find(doc);
        for (Document document : iterable) {
            System.out.println(document.toJson());
        }
    }

    //////////////////////////// B) Functions //////////////////////////////
    public static String SingleFieldIndex(MongoCollection<Document> col, String str) {
        String resultCreateIndex = col.createIndex(Indexes.ascending(str));
        System.out.println(String.format("Index created: %s", resultCreateIndex));
        return resultCreateIndex;
    }

    public static String TextIndex(MongoCollection<Document> col, String str) {
        try {
            String resultCreateIndex = col.createIndex(Indexes.text(str));
            System.out.println(String.format("Index created: %s", resultCreateIndex));
            return resultCreateIndex;
        } catch (MongoCommandException e) {
            if (e.getErrorCodeName().equals("IndexOptionsConflict"))
                System.out.println("There's an existing text index with different options");
            return null;
        }
    }

    //////////////////////////// D) Functions //////////////////////////////
    public static int countLocalidades(MongoCollection<Document> col) {
        int count = 0;
        List<Document> pipeline = Arrays.asList(
                new Document("$group", new Document("_id", "$localidade")
                        .append("totalLocalidades", new Document("$sum", 1))));

        AggregateIterable<Document> output = col.aggregate(pipeline);

        for (Document document : output) {
            count += 1;
        }
        return count;
    }

    public static Map<String, Integer> countRestLocalidades(MongoCollection<Document> col) {
        List<Document> pipeline = Arrays.asList(
                new Document("$group", new Document("_id", "$localidade")
                        .append("totalRestaurantes", new Document("$sum", 1))),
                new Document("$project", new Document("_id", 0)
                        .append("localidade", "$_id")
                        .append("totalRestaurantes", 1)));

        AggregateIterable<Document> output = col.aggregate(pipeline);

        Map<String, Integer> result = new HashMap<>();
        for (Document document : output) {
            String localidade = document.getString("localidade");
            int totalRestaurantes = document.getInteger("totalRestaurantes");
            result.put(localidade, totalRestaurantes);
        }
        return result;
    }

    public static List<String> getRestWithNameCloserTo(MongoCollection<Document> col, String name){
        FindIterable<Document> iterable = col.find(Filters.regex("nome", ".*" + name + ".*"))
                    .projection(Projections.include("nome"));
        List<String> result = new ArrayList<>();
        for (Document document : iterable) {
            result.add(document.getString("nome"));
        }
        return result;
    }

}
