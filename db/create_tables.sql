create table users (
	id int PRIMARY KEY GENERATED BY DEFAULT AS IDENTITY,
	password text NOT NULL,
	username text NOT NULL UNIQUE,
	email text NOT NULL UNIQUE, 
	birthday date CHECK (birthday <= CURRENT_DATE), 
	registration_timestamp timestamp DEFAULT current_timestamp CHECK (registration_timestamp::date <= CURRENT_DATE),
	surname text NOT NULL,
	name text NOT NULL,
	patronymic text,
	gender varchar(10)
);

create table roles (
	id int PRIMARY KEY GENERATED BY DEFAULT AS IDENTITY,
	title text NOT NULL UNIQUE
);

create table user_role (
	user_id int NOT NULL,
	role_id int NOT NULL,
	PRIMARY KEY (user_id, role_id),
	FOREIGN KEY (user_id) REFERENCES users (id) ON DELETE CASCADE,
	FOREIGN KEY (role_id) REFERENCES roles (id) ON DELETE CASCADE
);

create table patient ( 
	id int PRIMARY KEY,
	surname text NOT NULL,
	name text NOT NULL,
	patronymic text,
	beneficiary boolean DEFAULT FALSE,
	rh_factor varchar(5),
	blood_type varchar(5),
	FOREIGN KEY (id) REFERENCES users(id)
);

create table doctor ( 
	id int PRIMARY KEY,
	surname text NOT NULL,
	name text NOT NULL,
	patronymic text,
	description text,
	image text NOT NULL,
	start_work_date date NOT NULL DEFAULT current_date,
	end_work_date date,
	is_enabled boolean DEFAULT FALSE,
	FOREIGN KEY (id) REFERENCES users(id)
);

create table specialization (
	id int PRIMARY KEY GENERATED BY DEFAULT AS IDENTITY,
	title text NOT NULL UNIQUE,
	description text
);

create table doctor_specialization (
	doctor_id int,
	specialization_id int,
	PRIMARY KEY (doctor_id, specialization_id),
	FOREIGN KEY (doctor_id) REFERENCES doctor(id),
	FOREIGN KEY (specialization_id) REFERENCES specialization(id) ON DELETE CASCADE
);

create table treatment (
	id int PRIMARY KEY GENERATED BY DEFAULT AS IDENTITY,
	specialization_id int,
	title text NOT NULL,
	description text,
	price numeric NOT NULL CHECK (price >= '0'::numeric),
	beneficiary_discount int DEFAULT 0,
	discount int DEFAULT 0,
	FOREIGN KEY (specialization_id) REFERENCES specialization(id) ON DELETE SET NULL
);

create table schedule (
	id int PRIMARY KEY GENERATED BY DEFAULT AS IDENTITY,
	doctor_id int NOT NULL,
	start_work timestamp NOT NULL,
	end_work timestamp NOT NULL,
	is_enabled boolean DEFAULT TRUE,
	FOREIGN KEY (doctor_id) REFERENCES doctor(id)
);

create table appointment (
	id int PRIMARY KEY GENERATED BY DEFAULT AS IDENTITY,
	patient_id int NOT NULL,
	doctor_id int NOT NULL,
	schedule_id int,
	timestamp_start timestamp NOT NULL,
	timestamp_end timestamp NOT NULL,
	price numeric NOT NULL CHECK (price >= '0'::numeric),
	status text NOT NULL,
	diagnosis text,
	comment text,
	FOREIGN KEY (doctor_id) REFERENCES doctor(id),
	FOREIGN KEY (patient_id) REFERENCES patient(id),
	FOREIGN KEY (schedule_id) REFERENCES schedule(id) ON DELETE SET NULL
);

create table appointment_treatment (
	appointment_id int,
	treatment_id int,
	PRIMARY KEY (appointment_id, treatment_id),
	FOREIGN KEY (appointment_id) REFERENCES appointment(id),
	FOREIGN KEY (treatment_id) REFERENCES treatment(id)
);

create table cancelled_appointment (
	id int PRIMARY KEY,
	reason text,
	rejected_by text NOT NULL,
	reject_time timestamp NOT NULL DEFAULT current_timestamp,
	FOREIGN KEY (id) REFERENCES appointment(id)
);

create table review (
	id int PRIMARY KEY GENERATED BY DEFAULT AS IDENTITY,
	doctor_id int NOT NULL,
	patient_id int NOT NULL,
	call_date timestamp,
	appointment_date date,
	score int NOT NULL,
	description text,
	status text NOT NULL,
	FOREIGN KEY (doctor_id) REFERENCES doctor(id),
	FOREIGN KEY (patient_id) REFERENCES patient(id)
);