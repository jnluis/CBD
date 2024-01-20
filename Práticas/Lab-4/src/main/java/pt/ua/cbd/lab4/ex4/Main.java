package pt.ua.cbd.lab4.ex4;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import org.neo4j.driver.Driver;
import org.neo4j.driver.GraphDatabase;
import org.neo4j.driver.Session;
import org.neo4j.driver.Values;

// Entidades:

// Cyclist(cyclist_name, age, points, jersey_num)
// Year(year)
// Stage(stage_results_id, edition)
// Team(team_name)

// Relacionamentos:
// BELONGS_TO()
// RUNS_FOR()
// RANKED_ON(rank)

public class Main {

    private static String filepath = "resources/stage_data.csv";
    public static void main(String... args) throws IOException {
        String address = "bolt://localhost:7687";
        Driver driver = GraphDatabase.driver( address);

        loadDatatoNeo4j(filepath, driver);
        
        
        FileWriter out = new FileWriter("CBD_L44c_17403_output.txt");

    }

    public static void removeAllData(Session session) {
        session.run("MATCH (n) DETACH DELETE n");
        System.out.println(">> All data deleted!");
    }

    private static void loadDatatoNeo4j(String Filepath, Driver driver) throws IOException{
        try (BufferedReader br = new BufferedReader(new FileReader(filepath))) {
            br.readLine(); // descartar primeira linha
            String line;
            while((line = br.readLine()) != null) {
                //System.out.println(line);
                String[] values = line.split(",(?=([^\"]*\"[^\"]*\")*[^\"]*$)");// como é .csv, separa-se por vírgulas
                int edition = Integer.parseInt(values[0]);
                int year = Integer.parseInt(values[1]);
                String stage_results_id = values[2];
                String rank = values[3]; // tem problemas com o DNS, que é String, logo não pode ser Int
                String cyclist_name = values[5];
                Double age = Double.parseDouble(values[6]);
                String team_name = values[7];
                if (values[8] == ""){
                    values[8] = "0";
                }
                Double points = Double.parseDouble(values[8]);
                Double jersey_num = Double.parseDouble(values[10]);

                try (Session session = driver.session()) {
                    session.executeWrite(
                        tx -> {
                            tx.run(
                                "CREATE (c:Cyclist {Name: $Name, age: $age, points: $points, jersey_num: $jersey_num })" + 
                                    "MERGE (y:Year {year: $year})" +
                                    "MERGE (st:Stage {stage_num: $stage_results_id, edition: $edition})" + 
                                    "MERGE (T:Team {team_name: $team_name})" +
                                    "CREATE (st)-[:BELONGS_TO]->(y)" + 
                                    "CREATE (c)-[:RUNS_FOR]->(T)" +
                                    "CREATE (c)-[:RANKED_ON {rank: $rank}]->(st)",
                                Values.parameters(
                                    "Name", cyclist_name,
                                    "age", age,
                                    "team_name", team_name,
                                    "points", points,
                                    "jersey_num", jersey_num,
                                    "year", year,
                                    "stage_results_id", stage_results_id,
                                    "edition", edition,
                                    "rank", rank
                                ));
                            return null;
                        }
                    );
                }
            }
        }
    }
}