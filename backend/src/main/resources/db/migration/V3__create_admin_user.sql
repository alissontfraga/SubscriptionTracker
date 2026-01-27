
-- ADMIN INICIAL
-- username: admin
-- password: admin123 (bcrypt)

INSERT INTO users (username, password)
SELECT
    'admin',
    '$2a$10$KpjRjGhrlRsriv1dH0BvO.yy795T0nE8mQcuoJoN0mj60J9HcvWy6'
WHERE NOT EXISTS (
    SELECT 1 FROM users WHERE username = 'admin'
);

INSERT INTO user_roles (user_id, role)
SELECT u.id, 'ROLE_ADMIN'
FROM users u
WHERE u.username = 'admin'
AND NOT EXISTS (
    SELECT 1
    FROM user_roles ur
    WHERE ur.user_id = u.id
      AND ur.role = 'ROLE_ADMIN'
);
