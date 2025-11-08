INSERT INTO public.doctor(id, surname, name, patronymic, description, image, start_work_date, end_work_date, is_enabled)
VALUES (1, 'Иванов', 'Алексей', 'Петрович', 'Врач-терапевт', 'default-doc.png', '2025-11-07', null, true);

INSERT INTO public.patient(id, surname, name, patronymic, beneficiary, rh_factor, blood_type)
VALUES  (1, 'Иванов', 'Алексей', 'Петрович', true, true, 'A(II)');

INSERT INTO public.specialization(id, title, description)
VALUES (1, 'Терапевт', 'Терапевт — это врач широкого профиля, который занимается выявлением, лечением и профилактикой заболеваний внутренних органов.');

INSERT INTO public.treatment(id, specialization_id, title, description, price, beneficiary_discount, discount)
VALUES 
    (1, 1, 'Первичный осмотр', 'Прием терапевта включает первичный осмотр с использованием методов пальпации, перкуссии и аускультации, сбор сведений о симптомах и истории болезни.', 1000, 10, 10),
    (2, 1, 'Повторный приём', 'Повторный прием терапевта требуется для наблюдения пациента и корректировки ранее назначенного лечения.', 700, 0, 0);

INSERT INTO public.doctor_specialization(doctor_id, specialization_id)
	VALUES (1, 1);

INSERT INTO public.schedule(id, doctor_id, start_work, end_work, is_enabled)
VALUES (1, 1, '2028-06-30 09:00:00', '2028-06-30 09:30:00', true);