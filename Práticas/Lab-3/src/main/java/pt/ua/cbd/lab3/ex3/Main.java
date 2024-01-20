package pt.ua.cbd.lab3.ex3;

import com.datastax.oss.driver.api.core.CqlSession;
import com.datastax.oss.driver.api.core.cql.ResultSet;
import com.datastax.oss.driver.api.core.cql.Row;

import java.time.Instant;
import java.util.Set;
import java.util.UUID;

public class Main {
    public static void main(String[] args) {
        try (CqlSession session = CqlSession.builder().build()) {                         // (1)
            ResultSet rs = session.execute("SELECT * FROM videotube.user");              // (2)
            Row rowTest = rs.one();
            if (rowTest != null) {
                System.out.println(rowTest.getString("username"));                  // (3)
            } else {
                System.out.println("No rows found in the result set.");
            }                                   

            System.out.println("INSERT follower");
            session.execute("INSERT INTO videotube.followers (video_id, follow_usersID ) VALUES (1cbf0aeb-7a4f-47c9-9250-42a38896b010 , {e9e20ccd-49ab-4c62-9fa2-94c717f81a3b} )"); // inserir na BD é assim

            System.out.println("EDIT follower");
            session.execute("UPDATE videotube.followers SET follow_usersID={e9e20c0d-49ab-4c62-9fa2-94c717f81a3b , e9e20cee-49ab-4c62-9fa2-94c717f81a3b , e9e20caa-49ab-4c62-9fa2-94c717f81a3b } WHERE video_id=1cbf0aeb-7a4f-47c9-9250-42a38896b010"); // atualizar a BD é assim

            System.out.println("Alinea B)");
            System.out.println("Followers para um video");
            System.out.println("===============");
            ResultSet search = session.execute("SELECT * FROM videotube.followers WHERE video_id=1cbf0aeb-7a4f-47c9-9250-42a38896b010"); //Fiz a search na mesma tabela para ver as alterações
            
            UUID video_id= null;
            for (Row row : search){
                video_id = row.getUuid("video_id");
                Set<UUID> followers = row.getSet("follow_usersID", UUID.class);
                System.out.println("Video_id: " + video_id);
                for (UUID user : followers){
                    System.out.println(user);
                }
            }
        
            System.out.println("Os ultimos 3 comentários introduzidos para um video");
            System.out.println("===============");

            String author = null;
            String comment = null;
            Instant created_at = null;
            UUID comm_auth_id = null;
            UUID video_id2 = null;
            ResultSet search2 = session.execute("SELECT * FROM videotube.comments_videos WHERE video_id=1cbf0aeb-7a4f-47c9-9250-42a38896b007 LIMIT 3"); 
            for (Row row : search2){
                comm_auth_id = row.getUuid("comm_auth_id");
                video_id2 = row.getUuid("video_id");  
                author = row.getString("author");              
                comment = row.getString("comment");
                created_at = row.getInstant("TSComment");
                System.out.println("Comment ID" + comm_auth_id);
                System.out.print("Video (UUID): " + video_id2);                
                System.out.print(" | Author: " + author);
                System.out.print(" | Comment: " + comment);
                System.out.print(" | Created at: " + created_at);
            }
            System.out.println("");

            System.out.println("Rating médio para todos os vídeos");
            System.out.println("===============");

            ResultSet search3 = session.execute("SELECT video_id, AVG(rating) AS avg_rating FROM videotube.rating GROUP BY video_id");
            UUID video_id3 = null;
            Byte avg_rating = null;
            for (Row row : search3){
                video_id3 = row.getUuid("video_id");  
                avg_rating = row.getByte("avg_rating");
                System.out.print("Video (UUID): " + video_id3);                
                System.out.println(" | Comment: " + avg_rating);
            }
            System.out.println("");

            System.out.println("Obter os eventos associados a um vídeo e autor específicos, ordenados pelo timestamp do evento (TSEvent) mais recente:");
            System.out.println("===============");

            ResultSet search4 = session.execute("SELECT video_id, author_id, tsevent, type FROM videotube.events WHERE video_id = 1cbf0aeb-7a4f-47c9-9250-42a38896b000 AND author_id = e9e20c0d-49ab-4c62-9fa2-94c717f81a3b ORDER BY TSEvent DESC");
            UUID video_id4 = null;
            UUID author_id = null;
            Instant tsevent = null;
            String type = null;
            for (Row row : search4){
                video_id4 = row.getUuid("video_id");  
                author_id = row.getUuid("author_id");  
                tsevent = row.getInstant("TSEvent");
                type = row.getString("type");
                System.out.print("Video (UUID): " + video_id4);  
                System.out.print(" | Author (UUID): " + author_id);              
                System.out.print(" | Created at: " + tsevent);
                System.out.println(" | Type: " + type);
            }
            System.out.println("");
        
        }
        catch (Exception e){
            System.out.println("Exception: " + e);
            System.exit(0);
        }
            System.exit(0);
    }
}