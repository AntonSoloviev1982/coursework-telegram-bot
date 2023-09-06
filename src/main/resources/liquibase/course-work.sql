-- liquibase formatted sql

CREATE TABLE state(
   id VARCHAR(30) PRIMARY KEY,
   text VARCHAR(255));
INSERT INTO state(id, text) VALUES
('ShelterList', 'Наши приюты'),
('Info', 'Информация'),
('TimeTable', 'с 10 до 20');

-- changeset pavel:create state_button
CREATE TABLE state_button(
   state_id VARCHAR(30),
   caption VARCHAR(30),
   next_state_id VARCHAR(30)
   );
INSERT INTO state_button(state_id, caption, next_state_id) VALUES
('ShelterList', 'Собаки', 'Info'),
('ShelterList', 'Кошки', 'Info'),
('Info', 'Расписание','TimeTable');



