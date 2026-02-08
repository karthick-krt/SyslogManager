-- Insert a default admin user. Adjust domain and role_id as needed.
INSERT INTO users (user_name, domain_name, role_id)
VALUES ('admin', 'local', 1)
ON CONFLICT DO NOTHING;
