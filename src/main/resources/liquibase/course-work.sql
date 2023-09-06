-- liquibase formatted sql

-- changeset pavel:create_state
DROP TABLE IF EXISTS state;
CREATE TABLE state(
   id VARCHAR(30) PRIMARY KEY,
   text VARCHAR(255));
INSERT INTO state(id, text) VALUES
('ShelterList', 'Наши приюты'),
('Info', 'Информация'),
('TimeTable', 'с 10 до 20'),
('Rules', 'Так делать нельзя');

-- changeset pavel:create_state_button
DROP TABLE IF EXISTS state_button;
CREATE TABLE state_button(
   state_id VARCHAR(30),
   caption VARCHAR(30),
   next_state_id VARCHAR(30),
   PRIMARY KEY (state_id, caption),
   FOREIGN KEY (state_id) REFERENCES state(id),
   FOREIGN KEY (next_state_id) REFERENCES state(id)
   );
INSERT INTO state_button(state_id, caption, next_state_id) VALUES
('ShelterList', 'Собаки', 'Info'),
('ShelterList', 'Кошки', 'Info'),
('Info', 'Расписание','TimeTable'),
('Info', 'Правила','Rules');



