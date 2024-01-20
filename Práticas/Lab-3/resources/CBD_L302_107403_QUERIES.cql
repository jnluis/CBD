7    // 7. Permitir a pesquisa de todos os videos de determinado autor;
SELECT * FROM video
WHERE author_id = 26e20c0d-49ab-4c62-9fa2-94c717f81a3b
ALLOW FILTERING ;

// 8. Permitir a pesquisa de comentarios por utilizador, ordenado inversamente pela data;

SELECT * FROM comments_users
WHERE author = 'jl'
;

// 9. Permitir a pesquisa de comentarios por videos, ordenado inversamente pela data;
SELECT * FROM comments_videos
WHERE video_id = 1cbf0aeb-7a4f-47c9-9250-42a38896b010
;

// 10. Permitir a pesquisa do rating medio de um video e quantas vezes foi votado;
SELECT COUNT(*) AS "Votes",
AVG(rating) AS "Average Rating"
FROM rating
WHERE video_id = 1cbf0aeb-7a4f-47c9-9250-42a38896b005;

// 1. Os ultimos 3 comentarios introduzidos para um video;

SELECT comment FROM comments_videos
WHERE video_id = 1cbf0aeb-7a4f-47c9-9250-42a38896b007
LIMIT 3;

// 2. Lista das tags de determinado video;

SELECT tag from video
WHERE video_id = 1cbf0aeb-7a4f-47c9-9250-42a38896b000;

// 3. Todos os videos com a tag Aveiro;

SELECT V_name FROM video
WHERE tag 
CONTAINS 'Aveiro' // Não tenho nenhum vídeo com a tag Aveiro, mas se substituirmos por Portugal, já funciona

// 4. Os ultimos 5 eventos de determinado video realizados por um utilizador;

SELECT * FROM events
WHERE video_id = 1cbf0aeb-7a4f-47c9-9250-42a38896b000
AND author_id = e9e20c0d-49ab-4c62-9fa2-94c717f81a3b
LIMIT 5;

// 5. Videos partilhados por determinado utilizador (maria1987, por exemplo) num determinado periodo de tempo (Agosto de 2017, por exemplo);

SELECT V_name FROM video
WHERE author_id = 34e20c0d-49ab-4c62-9fa2-94c717f81a3b
AND TSShare >= '2022-05-02'
ALLOW FILTERING;

// Foi preciso colocar o allow filtering uma vez que author_id não é PK da tabela video

// 6. Os ultimos 10 videos, ordenado inversamente pela data da partilhada;

SELECT V_name, TSShare FROM video
ORDER BY TSShare
LIMIT 10;

// Como não restringimos à chave de partição, para resolver, deveríamos criar uma tabela auxiliar para resolver o problema

// 7. Todos os seguidores (followers) de determinado video;

SELECT follow_usersID FROM followers
WHERE video_id = 1cbf0aeb-7a4f-47c9-9250-42a38896b003;

// 8. Todos os comentarios (dos videos) que determinado utilizador esta a seguir (following);

//Seria necessário criar mais tabelas para resolver esta querie, pois Cassandra não suporta operações JOIN

// 9. Os 5 videos com maior rating;

// Não consigo fazer esta query, porque o ORDER BY não funciona sem especificar a partition key,
e nós neste caso queríamos qualquer vídeo

// 10. Uma query que retorne todos os videos e que mostre claramente a forma pela qual estao ordenados;

SELECT video_id, author_id, TSShare FROM video
ORDER BY TSShare; // Não funciona, pois author_id é a partition key da tabela video, mas não estamos a especificar um valor específico para ela

// 11. Lista com as Tags existentes e o numero de videos catalogados com cada uma delas;
// Não consigo, pois seria necessário contar quantos vídeos têm a tag que fosse pedida na query e para além disso, era preciso ir buscar o autor
// pelo que não é possível, já que precisaríamos de agrupar vídeos através das suas tags


// 12. Calcular o rating médio para todos os vídeos;

SELECT video_id, AVG(rating) AS avg_rating
FROM videotube.rating
GROUP BY video_id;

// 13. Consulta para obter os eventos associados a um vídeo e autor específicos, ordenados pelo timestamp do evento (TSEvent) mais recente:

SELECT video_id, author_id, tsevent, type 
FROM videotube.events 
WHERE video_id = 1cbf0aeb-7a4f-47c9-9250-42a38896b000 AND author_id = e9e20c0d-49ab-4c62-9fa2-94c717f81a3b
ORDER BY TSEvent DESC;