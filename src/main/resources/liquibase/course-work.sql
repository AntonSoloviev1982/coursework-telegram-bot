-- liquibase formatted sql

-- changeset pavel:create_state
DROP TABLE IF EXISTS state;
CREATE TABLE state(
   id VARCHAR(30) PRIMARY KEY,
   text VARCHAR(30)),
   text_input BOOLEAN
   ;
INSERT INTO state(id, text, text_input) VALUES
    ('BadChoice', 'Нераспознанная команда. Выберите кнопку:', FALSE),
    ('Shelter', 'Выберите приют:', FALSE),
    ('Stage', 'Выберите этап:', FALSE),
    ('Info', 'Выберите вид информации:', FALSE),
    ('GetAnimal', 'Выберите вид информации:', FALSE),
    ('Report', 'Пришлите фото и текстовый отчет', TRUE),

    ('AboutShelter', '@information', FALSE),
    ('TimeTable', '@timetable', FALSE),
    ('Security', '@security', FALSE),
    ('SafetyPrecautions', '@safety_precautions', FALSE),
    ('AnimalList', 'Наши питомцы:'),
    ('AnimalByNumber', 'Введите номер животного', TRUE),

    ('Rules', '@rules', FALSE),
    ('Documents', '@documents', FALSE),
    ('Tranportation', '@tranportation', FALSE),
    ('ChildAccomodation', '@child_accomodation', FALSE),
    ('AdultAccomodation', '@adult_accomodation', FALSE),
    ('InvalidAccomodation', '@invalid_accomodation', FALSE),
    ('DogCommunication', '@dog_communication', FALSE),
    ('Cynologists', '@cynologists', FALSE),
    ('RefusalReasons', '@refusal_reasons', FALSE),

    ('MessageToVolonteer', 'Введите сообщение для волонтера', TRUE),
    ('FeedbackRequest', 'Введите контакт для обратной связи', TRUE)
    ;

-- changeset pavel:create_state_button
DROP TABLE IF EXISTS state_button;
CREATE TABLE state_button(
   state_id VARCHAR(30),
   caption VARCHAR(60),
   next_state_id VARCHAR(30),
   button_row TINYINT NOT NULL,
   button_col TINYINT NOT NULL,
   shelter_id VARCHAR(3),
   PRIMARY KEY (state_id, caption),
   FOREIGN KEY (state_id) REFERENCES state(id),
   FOREIGN KEY (next_state_id) REFERENCES state(id)
   );
INSERT INTO state_button(state_id, caption, next_state_id, button_row, button_col, shelter_id) VALUES
    ('Shelter', 'Собаки', 'Info', 1, 1, Null),
    ('Shelter', 'Кошки', 'Info', 1, 2, Null),

    ('Stage', 'Узнать информацию о приюте (этап 1)','Info', 1, 1, Null ),
    ('Stage', 'Как взять животное из приюта (этап 2)','GetAnimal', 2, 1, Null),
    ('Stage', 'Прислать отчет о питомце (этап 3)','Report', 3, 1, Null),
    ('Stage', 'Назад к выбору приюта','Shelter', 4, 1, Null),
    ('Stage', 'Позвать волонтера','MessageToVolonteer', 5, 1, Null),
    ('Stage', 'Запросить обратную свзь','FeedbackRequest', 5, 2, Null),

    ('Info', 'Рассказать о приюте','AboutShelter', 1, 1, Null),
    ('Info', 'Расписание работы приюта и адрес, схема проезда','TimeTable', 2, 1, Null),
    ('Info', 'Контактные данные охраны для оформления пропуска на машину','Security', 3, 1, Null),
    ('Info', 'Рекомендации по технике безопасности на территории приюта','SafetyPrecautions', 4, 1, Null),
    ('Info', 'Наши питомцы','AnimalList', 5, 1, Null),
    ('Info', 'О питомце  (по номеру)','AnimalByNumber', 5, 2, Null),
    ('Info', 'Назад к выбору этапа','Stage', 6, 1, Null),
    ('Info', 'Позвать волонтера','MessageToVolonteer', 7, 1, Null),
    ('Info', 'Запросить обратную свзь','FeedbackRequest', 7, 2, Null),

    ('GetAnimal', 'Правила знакомства с животным','Rules', 1, 1, Null),
    ('GetAnimal', 'Список документов, чтобы взять животное из приюта','Documents', 2, 1, Null),
    ('GetAnimal', 'Рекомендации по транспортировке животного','Tranportation', 3, 1, Null),
    ('GetAnimal', 'Рекомендации по обустройству дома для щенка','ChildAccomodation', 4, 1, 'Dog'),
    ('GetAnimal', 'Рекомендации по обустройству дома для котенка','ChildAccomodation', 4, 1, 'Cat'),
    ('GetAnimal', 'Рекомендации по обустройству дома для взрослого животного','AdultAccomodation', 5, 1, Null),
    ('GetAnimal', 'Рекомендации по обустройству дома для животного-инвалида','InvalidAccomodation', 6, 1, Null),
    ('GetAnimal', 'Советы кинолога по первичному общению с собакой','DogCommunication', 7, 1, 'Dog'),
    ('GetAnimal', 'Рекомендации по проверенным кинологам','Cynologists', 8, 1, 'Dog'),
    ('GetAnimal', 'Причины отказа отдать животное','RefusalReasons', 9, 1, Null),
    ('GetAnimal', 'Назад к выбору этапа','Stage', 10, 1, Null),
    ('GetAnimal', 'Позвать волонтера','MessageToVolonteer', 11, 1, Null),
    ('GetAnimal', 'Запросить обратную свзь','FeedbackRequest', 11, 2, Null),

    ('MessageToVolonteer', 'Возврат к боту', Null, 1, 1, Null),
    ('FeedbackRequest', 'Возврат к боту', Null, 1, 1, Null),
    ('Report', 'Возврат к боту', Null, 1, 1, Null),
    ('AnimalByNumber', 'Возврат к боту', Null, 1, 1, Null);

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



