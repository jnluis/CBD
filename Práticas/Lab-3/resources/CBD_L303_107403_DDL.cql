CREATE KEYSPACE IF NOT EXISTS WorldCup
WITH replication = {'class': 'SimpleStrategy', 'replication_factor': 1};

CREATE TABLE WorldCup.athletes_by_team (
    id UUID ,
    name text,
    age int,
    team text,
    titles MAP<text, timestamp>,
    PRIMARY KEY(team, age, id)
);

CREATE TABLE WorldCup.athletes_by_age (
    id UUID ,
    name text,
    age int,
    team text,
    titles MAP<text, timestamp>,
    PRIMARY KEY(age, team, id)
);

CREATE TABLE WorldCup.teams (
    id UUID PRIMARY KEY,
    name text,
    athletes SET<UUID>,
    coach text,
    sponsors LIST<text>,
);

CREATE TABLE WorldCup.results (
    id UUID,
    round text,
    winner UUID,
    results MAP<UUID, int>,
    date timestamp,
    PRIMARY KEY((round), date, id)
);

CREATE TABLE WorldCup.classifications (
    group text,
    team UUID,
    points int,
    PRIMARY KEY (group, points, team)
);

CREATE INDEX ON WorldCup.teams (coach);
CREATE INDEX ON WorldCup.results (winner);
