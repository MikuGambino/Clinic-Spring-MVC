INSERT INTO roles(id, title) 
VALUES
(1, 'ROLE_ADMIN'),
(2, 'ROLE_PATIENT'),
(3, 'ROLE_DOCTOR');

INSERT INTO users(password, username, email, surname, name, patronymic, gender, birthday)
VALUES ('$2a$10$7ZP5PJuzhGoikMiJM4OOX.HVUuPlwn8DsmZoIXpXuwuhtaypyM04u', 12345, 'dan.k-2011@yandex.ru', 'Кузнецов', 'Даниил', 'Андреевич', 'Мужской', '2001-11-07');

INSERT INTO user_role(user_id, role_id)
VALUES (1, 1), (1, 3);