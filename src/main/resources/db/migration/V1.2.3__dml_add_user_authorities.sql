INSERT INTO user_authorities
SELECT u.id, 'USER'
FROM users u;