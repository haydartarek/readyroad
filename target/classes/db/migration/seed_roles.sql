USE readyroad_prod;

INSERT IGNORE INTO user_roles (role_name, description)
VALUES
  ('USER',       'Standard learner user'),
  ('ADMIN',      'Full system administrator'),
  ('INSTRUCTOR', 'Lesson content manager');

SELECT id, role_name, description FROM user_roles;
SELECT id, email, username, role FROM users;
