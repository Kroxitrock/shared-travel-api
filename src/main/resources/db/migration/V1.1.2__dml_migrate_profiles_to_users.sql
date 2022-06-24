INSERT INTO users (email, "password")
SELECT p.email, p."password"
FROM profiles p;