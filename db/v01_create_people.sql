CREATE TABLE tb_people (
    id UUID PRIMARY KEY,
    name varchar(100) NOT NULL,
    nick varchar(32) NOT NULL,
    birth_date DATE NOT NULL,
    stack varchar(32)[],
    CONSTRAINT unique_nick UNIQUE (nick)
);
