-- liquibase formatted sql

-- changeset pavel:create_state
DROP TABLE IF EXISTS state;
CREATE TABLE state(
   id VARCHAR(30) PRIMARY KEY,
   text VARCHAR(255));
INSERT INTO state(id, text) VALUES
    ('ShelterChoice', 'Наши приюты'),
    ('Info', 'Информация'),
    ('TimeTable', 'с 10 до 20'),
    ('Rules', 'Так делать нельзя');

-- changeset pavel:create_state_button
DROP TABLE IF EXISTS state_button;
CREATE TABLE state_button(
   state_id VARCHAR(30),
   caption VARCHAR(30),
   next_state_id VARCHAR(30) NOT NULL,
   button_row TINYINT NOT NULL,
   button_col TINYINT NOT NULL,
   PRIMARY KEY (state_id, caption),
   FOREIGN KEY (state_id) REFERENCES state(id),
   FOREIGN KEY (next_state_id) REFERENCES state(id)
   );
INSERT INTO state_button(state_id, caption, next_state_id, button_row, button_col) VALUES
    ('ShelterChoice', 'Собаки', 'Info', 1, 1),
    ('ShelterChoice', 'Кошки', 'Info', 1, 2),
    ('Info', 'Расписание','TimeTable', 1, 1),
    ('Info', 'Правила','Rules', 1, 2);

--changeset pavel:create_user
DROP TABLE IF EXISTS users;
CREATE TABLE users(
   id LONG PRIMARY KEY,
   name VARCHAR(30) NOT NULL,
   shelter_id VARCHAR(3),
   state_id VARCHAR(30) NOT NULL,
   previous_state_id VARCHAR(30),
   state_time DATETIME,
   FOREIGN KEY (state_id) REFERENCES state(id),
   FOREIGN KEY (previous_state_id) REFERENCES state(id)
   );

--changeset pavel:create_animals
CREATE TABLE Dog(
   id INTEGER PRIMARY KEY
   );
CREATE TABLE Cat(
   id INTEGER PRIMARY KEY
   );



