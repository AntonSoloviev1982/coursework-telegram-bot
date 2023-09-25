-- liquibase formatted sql

--changeset anton:create_shelter
--может быть у кого-то сделана и заполнена DROP TABLE IF EXISTS message_to_volunteer;
CREATE TABLE shelter(
    id VARCHAR(3) PRIMARY KEY,
    name VARCHAR(30),
    information TEXT,
    timetable TEXT,
    address TEXT,
    security TEXT,
    safety_precautions TEXT,
    rules TEXT,
    documents TEXT,
    transportation TEXT,
    child_accomodation TEXT,
    adult_accomodation TEXT,
    invalid_accomodation TEXT,
    communication TEXT,
    cynologists TEXT,
    refusal_reasons TEXT
    );
INSERT INTO shelter(id, name, information, timetable, address, security, safety_precautions, rules, documents, transportation
, child_accomodation, adult_accomodation, invalid_accomodation, communication, cynologists, refusal_reasons) VALUES
    ('Dog', 'Собаки', 'Текст: информация о приюте собак', 'Текст: расписание работы приюта собак и адрес, схема проезда'
    , 'Текст: адрес приюта собак', 'Текст: контактные данные охраны приюта собак для оформления пропуска на машину'
    , 'Текст: рекомендации по технике безопасности на территории приюта собак', 'Текст: правила знакомства с собаками'
    , 'Текст: список документов, чтобы взять собаку из приюта', 'Текст: транспортировка собаки'
    , 'Текст: обустройство дома для щенка', 'Текст: обустройство дома для взрослой собаки'
    , 'Текст: обустройство дома для собаки-инвалида', 'Текст: советы кинолога по первичному общению с собакой'
    , 'Текст: рекомендации по проверенным кинологам', 'Текст: причины отказа отдать собаку'),

    ('Cat', 'Кошки', 'Текст: информация о приюте кошек', 'Текст: расписание работы приюта кошек и адрес, схему проезда'
    , 'Текст: адрес приюта кошек', 'Текст: контактные данные охраны приюта кошек для оформления пропуска на машину'
    , 'Текст: рекомендации по технике безопасности на территории приюта кошек', 'Текст: правила знакомства с кошкой'
    , 'Текст: список документов, чтобы взять кошку из приюта', 'Текст: транспортировка кошки'
    , 'Текст: обустройство дома для котенка', 'Текст: обустройство дома для взрослой кошки'
    , 'Текст: обустройство дома для кошки-инвалида', ' ', ' ', 'Текст: причины отказа отдать кошку');

-- changeset pavel:create_state
DROP TABLE IF EXISTS state;
CREATE TABLE state(
    id VARCHAR(30) PRIMARY KEY,
    text VARCHAR(60),      --текст, который бот покажет при переходе в это состояние
    text_input BOOLEAN);   --состояние текстового ввода.
    --Если false, то ждем только нажатия кнопок и состояние не требует специальной обработки ответа

INSERT INTO state(id, text, text_input) VALUES
    ('BadChoice', 'Нераспознанная команда. Выберите кнопку:', FALSE),
    ('Shelter', 'Выберите приют:', FALSE),
    ('Stage', 'Выберите этап:', FALSE),
    ('Info', 'Выберите вид информации:', FALSE),
    ('GetAnimal', 'Выберите вид информации:', FALSE),
    ('Report', 'Здесь может быть разный текст. Пришлите/Осталось/Получено', TRUE),

    ('AboutShelter', '@information', FALSE),
    ('TimeTable', '@timetable', FALSE),
    ('Address', '@address', FALSE),
    ('Security', '@security', FALSE),
    ('SafetyPrecautions', '@safetyPrecautions', FALSE),
    ('AnimalList', 'Наши питомцы:', FALSE),
    ('AnimalByNumber', 'Введите номер животного', TRUE),

    ('Rules', '@rules', FALSE),
    ('Documents', '@documents', FALSE),
    ('Tranportation', '@transportation', FALSE),
    ('ChildAccomodation', '@childAccomodation', FALSE),
    ('AdultAccomodation', '@adultAccomodation', FALSE),
    ('InvalidAccomodation', '@invalidAccomodation', FALSE),
    ('DogCommunication', '@communication', FALSE),
    ('Cynologists', '@cynologists', FALSE),
    ('RefusalReasons', '@refusalReasons', FALSE),

    ('MessageToVolonteer', 'Введите сообщение для волонтера', TRUE),
    ('FeedbackRequest', 'Введите контакт для обратной связи', TRUE)
    ;

-- changeset pavel:create_state_button
DROP TABLE IF EXISTS state_button;
CREATE TABLE state_button(
    state_id VARCHAR(30),
    caption VARCHAR(60),
    next_state_id VARCHAR(30),
    button_row TINYINT NOT NULL,  --имя row не разрешает, зарезервировано
    button_col TINYINT NOT NULL,
    shelter_id VARCHAR(3),
    PRIMARY KEY (state_id, caption),
    FOREIGN KEY (state_id) REFERENCES state(id),
    FOREIGN KEY (next_state_id) REFERENCES state(id),
    FOREIGN KEY (shelter_id) REFERENCES shelter(id)
    );
INSERT INTO state_button(state_id, caption, next_state_id, button_row, button_col, shelter_id) VALUES
    ('Shelter', 'Собаки', 'Stage', 1, 1, Null),
    ('Shelter', 'Кошки', 'Stage', 1, 2, Null),

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
    ('GetAnimal', 'Транспортировка животного','Tranportation', 3, 1, Null),
    ('GetAnimal', 'Обустройство дома для щенка','ChildAccomodation', 4, 1, 'Dog'),
    ('GetAnimal', 'Обустройство дома для котенка','ChildAccomodation', 4, 1, 'Cat'),
    ('GetAnimal', 'Обустройство дома для взрослого животного','AdultAccomodation', 5, 1, Null),
    ('GetAnimal', 'Обустройство дома для животного-инвалида','InvalidAccomodation', 6, 1, Null),
    ('GetAnimal', 'Советы кинолога по первичному общению с собакой','DogCommunication', 7, 1, 'Dog'),
    ('GetAnimal', 'Рекомендации по проверенным кинологам','Cynologists', 8, 1, 'Dog'),
    ('GetAnimal', 'Причины отказа отдать животное','RefusalReasons', 9, 1, Null),
    ('GetAnimal', 'Назад к выбору этапа','Stage', 10, 1, Null),
    ('GetAnimal', 'Позвать волонтера','MessageToVolonteer', 11, 1, Null),
    ('GetAnimal', 'Запросить обратную свзь','FeedbackRequest', 11, 2, Null);

--changeset pavel:create_user
DROP TABLE IF EXISTS users;
CREATE TABLE users(     --имя user не разрешает, зарезервировано
    id LONG PRIMARY KEY,
    name VARCHAR(30) NOT NULL,
    shelter_id VARCHAR(3),
    state_id VARCHAR(30) NOT NULL,
    previous_state_id VARCHAR(30),
    state_time TIMESTAMP,
    FOREIGN KEY (state_id) REFERENCES state(id),
    FOREIGN KEY (previous_state_id) REFERENCES state(id)
   );
INSERT INTO users(id, name, shelter_id, state_id) VALUES
    (11, 'User11', 'Dog', 'Shelter'),
    (22, 'User22', 'Cat', 'Shelter');

--changeset anton:create_message_to_volunteer
DROP TABLE IF EXISTS message_to_volunteer;
CREATE TABLE message_to_volunteer(
    id INTEGER PRIMARY KEY AUTO_INCREMENT,
    user_id LONG,
    question_time DATETIME,
    question TEXT,
    answer_time DATETIME,
    answer TEXT,
    FOREIGN KEY (user_id) REFERENCES users(id)
    );

--changeset alexander:create_feedback_request
DROP TABLE IF EXISTS feedback_request;
CREATE TABLE feedback_request(
    id INTEGER PRIMARY KEY AUTO_INCREMENT,
    user_id LONG NOT NULL,
    request_time TIMESTAMP NOT NULL,
    contact TEXT,
    execution_time TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id)
    );

--changeset salavat:create_cat
DROP TABLE IF EXISTS cat;
CREATE TABLE cat(
    id INTEGER PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(30),
    breed VARCHAR(30),
    age INTEGER,
    photo BLOB,
    is_adopted BOOLEAN
    );
INSERT INTO cat(name, breed, age, is_adopted) VALUES
    ('Кот', 'Дворовый', 5, FALSE),
    ('Котяра', 'Кошка', 10, TRUE);

--changeset alexander:create_dog
CREATE TABLE dog(
    id INTEGER PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(30),
    breed VARCHAR(30),
    age INTEGER,
    photo BLOB,
    is_adopted BOOLEAN
    );
INSERT INTO dog(name, breed, age, is_adopted) VALUES
    ('Пес', 'Дворняга', 5, FALSE),
    ('Барбос', 'Собака', 10, TRUE);

--changeset salavat:create_cat_adobtion
CREATE TABLE cat_adoption(
    id INTEGER PRIMARY KEY AUTO_INCREMENT,
    user_id LONG,
    pet_id INTEGER,
    date DATE,
    trial_date DATE,
    FOREIGN KEY (user_id) REFERENCES users(id),
    FOREIGN KEY (pet_id) REFERENCES cat(id)
    );
--за один день один пользователь может усыновить только одно животное
CREATE UNIQUE INDEX user_cat_date ON cat_adoption (user_id, pet_id, date)

--changeset salavat:create_dog_adobtion
CREATE TABLE dog_adoption(
    id INTEGER PRIMARY KEY AUTO_INCREMENT,
    user_id LONG,
    pet_id INTEGER,
    date DATE,
    trial_date DATE,
    FOREIGN KEY (user_id) REFERENCES users(id),
    FOREIGN KEY (pet_id) REFERENCES dog(id)
    );
--за один день один пользователь может усыновить только одно животное
--проверки, что у разных усыновлений не пересекаются испытательные строки в базе нет. Это сделает сервис
CREATE UNIQUE INDEX user_dog_date ON dog_adoption (user_id, pet_id, date)

--changeset alexander:create_cat_report
DROP TABLE IF EXISTS cat_report;
CREATE TABLE cat_report(
    id INTEGER PRIMARY KEY AUTO_INCREMENT,
    adoption_id INTEGER NOT NULL,
    date DATE NOT NULL,
    photo BLOB,
    text BLOB,
    FOREIGN KEY (adoption_id) REFERENCES cat_adoption(id)
    );
--за один день по одному усыновлению может прийти только один отчет
CREATE UNIQUE INDEX cat_report_adoption_date ON cat_report (adoption_id, date)

--changeset alexander:create_dog_report
DROP TABLE IF EXISTS dog_report;
CREATE TABLE dog_report(
    id INTEGER PRIMARY KEY AUTO_INCREMENT,
    adoption_id INTEGER NOT NULL,
    date DATE NOT NULL,
    photo BLOB,
    text BLOB,
    FOREIGN KEY (adoption_id) REFERENCES dog_adoption(id)
    );
--за один день по одному усыновлению может прийти только один отчет
CREATE UNIQUE INDEX dog_report_adoption_date ON dog_report (adoption_id, date)

--changeset pavel:create_sequence
--CREATE SEQUENCE my_sequence
--   START WITH 1
--   INCREMENT BY 1;
--оказалось, последовательности надо просто поставить у ключа AUTO_INCREMENT

