CREATE TABLE MOIVE (
  ID INTEGER PRIMARY KEY   AUTOINCREMENT,
  chinese_name char(100),
  english_name char(100) UNIQUE ,
  director char(50),
  performer char(500),
  year YEAR,
  country char(50),
  category CHAR(50),
  tag CHAR(100),
  language char(50),
  imdb_score DOUBLE,
  douban_score double,
  Film_length int,
  url CHAR(100)
);