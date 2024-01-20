-- 1. Lista de todos os atletas de um determinado país
SELECT * FROM WorldCup.athletes_by_team 
WHERE team = 'France';

-- 2. Todas as equipas patrocinadas pela marca Coca-cola
SELECT * FROM WorldCup.teams
WHERE sponsors CONTAINS 'Coca-cola'
ALLOW FILTERING;

-- 3. Os jogos ganhos por França
SELECT * FROM WorldCup.results
WHERE winner = 26a08301-fd61-49f1-aecf-2c39c7e48249;

-- 4. Classificação de um grupo, ordenados pelo número de pontos em ordem decrescente
SELECT * FROM WorldCup.classifications 
WHERE group = 'Grupo F'
ALLOW FILTERING; 

-- 5. Todos os atletas com mais de 30 anos, ordenados do mais velho para o mais novo
SELECT * FROM WorldCup.athletes_by_age
WHERE age > 30 
ORDER By team;

-- 6. Seleciona todos os jogos que terminaram empatados.
SELECT * FROM WorldCup.results
WHERE winner = null -- Não funciona, porque o Cassandra ainda não suporta qualquer tipo de comparação com valores null
-- Usar '' não resulta pois winner é do tipo UUID

-- 7. Os 2 jogadores mais velhos da seleção portuguesa;
SELECT * FROM athletes_by_team
WHERE team = 'Portugal'
ORDER BY age DESC
LIMIT 2;

-- 8. Apresentar a média de idades para uma determinada equipa
SELECT AVG(age) FROM WorldCup.athletes_by_team 
WHERE team = 'France';

-- 9. Retornar todos os resultados das rondas de Quarto de Final, ordenadas inversamente pela data
SELECT * FROM WorldCup.results 
WHERE round = 'QF' 
ORDER BY date DESC;

-- 10. Selecionar todas as equipas que terminaram em 1º lugar do seu grupo
SELECT *
FROM WorldCup.classifications
GROUP BY group;