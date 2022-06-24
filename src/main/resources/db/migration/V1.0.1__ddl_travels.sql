CREATE TABLE n_locations (
	code varchar(5) NOT NULL,
	name varchar(50) NOT NULL,
	CONSTRAINT n_locations_pk PRIMARY KEY (code)
);
COMMENT ON TABLE n_locations IS 'Nomenclature table for the locations from/to which the user can travel';

-- Column comments

COMMENT ON COLUMN n_locations.code IS 'Short code for the city';
COMMENT ON COLUMN n_locations.name IS 'Name of the city';

CREATE TABLE travels (
	id serial8 NOT NULL,
	from_code varchar(5) NOT NULL,
	to_code varchar(5) NOT NULL,
	departure_date timestamp NOT NULL,
	CONSTRAINT travels_pk PRIMARY KEY (id),
	CONSTRAINT travels_fk FOREIGN KEY (from_code) REFERENCES n_locations(code),
	CONSTRAINT travels_fk_1 FOREIGN KEY (to_code) REFERENCES n_locations(code)
);

-- Column comments

COMMENT ON COLUMN travels.from_code IS 'Location from where the travel comences';
COMMENT ON COLUMN travels.to_code IS 'Location where the travel ends';
COMMENT ON COLUMN travels.departure_date IS 'Timestamp of the date the travel will start';
