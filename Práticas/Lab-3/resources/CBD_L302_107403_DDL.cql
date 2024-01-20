DROP KEYSPACE videotube;

CREATE KEYSPACE videotube WITH replication = {'class':'SimpleStrategy', 'replication_factor' : 2};
-- USE videotube;


CREATE TABLE videotube.user(uid uuid, username text, name text, email text , TSRegister timestamp, PRIMARY KEY(( uid ),TSRegister ))  WITH CLUSTERING ORDER BY (TSRegister DESC);

CREATE TABLE videotube.video(video_id uuid, author_id uuid , V_name text, video_duration int , descr text, tag set<text>, TSShare timestamp, PRIMARY KEY(( video_id ),TSShare ) ) WITH CLUSTERING ORDER BY (TSShare DESC); 

CREATE TABLE videotube.comments_users(comm_auth_id uuid, video_id uuid, author text,comment text, TSComment timestamp, PRIMARY KEY (author, TSComment, comm_auth_id) ) WITH CLUSTERING ORDER BY (TSComment DESC);

CREATE TABLE videotube.comments_videos(comm_auth_id uuid, video_id uuid, author text,comment text, TSComment timestamp, PRIMARY KEY (video_id,TSComment)) WITH CLUSTERING ORDER BY (TSComment DESC);

CREATE TABLE videotube.followers(video_id uuid, follow_usersID set<uuid>, PRIMARY KEY(video_id) );

CREATE TABLE videotube.events(video_id uuid, author_id uuid, type text, moment timestamp, TSEvent timestamp, PRIMARY KEY((video_id, author_id), TSEvent));

CREATE TABLE videotube.rating(video_id uuid, rating_time timestamp, rating tinyint, PRIMARY KEY (video_id, rating_time) ) WITH CLUSTERING ORDER BY (rating_time DESC);

CREATE INDEX on videotube.video (tag);