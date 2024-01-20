UPDATE WorldCup.athletes_by_team SET name = 'Kepler Ferreira' WHERE id = 8da6fbe7-0395-49f4-b064-5d5a0dc19b22 and team='Portugal' and age=39;
UPDATE WorldCup.athletes_by_team SET titles = titles + {'The best': toTimestamp(now())} WHERE id = 8da6fbe7-1095-49f4-b064-5d5a0dc19b22 and team='Portugal' and age=36;
UPDATE WorldCup.teams SET coach = 'Roberto Martinez' WHERE id = 26a08300-fd61-49f1-aecf-2c39c7e48249;
UPDATE WorldCup.teams SET sponsors = sponsors + ['SuperBock'] WHERE id = 26a08300-fd61-49f1-aecf-2c39c7e48249;
UPDATE WorldCup.teams SET sponsors = sponsors - ['Sagres'] WHERE id = 26a08300-fd61-49f1-aecf-2c39c7e48249;


DELETE FROM WorldCup.athletes_by_team WHERE id = 8da6fbe7-0195-49f4-b064-5d5a0dc19b22  and team='Portugal' and age=39; -- Eliminar o CR7 da BD
DELETE FROM WorldCup.teams WHERE id = 26a08304-fd61-49f1-aecf-2c39c7e48249;
DELETE FROM WorldCup.classifications WHERE team = 26a08303-fd61-49f1-aecf-2c39c7e48249 and points = 6 and group= 'Grupo D'; 
DELETE winner FROM WorldCup.results WHERE round = 'MF' and id = 26a08301-fd61-49f1-aecf-2c39c7e48249 and date= '2023-11-29'; 
DELETE titles FROM WorldCup.athletes_by_team WHERE id = 8da6fbe7-2095-49f4-b064-5d5a0dc19b22 and age=29 and team='Brazil'; 