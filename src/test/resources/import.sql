INSERT INTO n_locations VALUES ('BGSO','Sofia');
INSERT INTO n_locations VALUES ('BGDB','Dolna bania');

INSERT INTO users VALUES (1, 'TEST@email.com', '$2a$10$7AV/47yRoLFDVGCIxlDyQ.x82cm1CtqyyNs.X8L/tj4PJwfHBcIGW')
INSERT INTO users VALUES (2, 'DRIVER@email.com', '$2a$10$7AV/47yRoLFDVGCIxlDyQ.x82cm1CtqyyNs.X8L/tj4PJwfHBcIGW')
INSERT INTO profile_settings VALUES (1, true);
INSERT INTO profile_settings VALUES (2, true);

INSERT INTO profiles VALUES (1,'TEST', 'TEST', 1, 1);
INSERT INTO profiles VALUES (2,'DRIVER', 'DRIVER', 2, 2);

INSERT INTO user_authorities VALUES (1, 'USER');
INSERT INTO user_authorities VALUES (2, 'USER');
INSERT INTO user_authorities VALUES (2, 'DRIVER');