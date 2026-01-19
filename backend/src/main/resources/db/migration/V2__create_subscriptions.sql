CREATE TABLE subscriptions (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(80) NOT NULL,
    price NUMERIC(30,2) NOT NULL,
    currency VARCHAR(20) NOT NULL,
    frequency VARCHAR(20) NOT NULL,
    category VARCHAR(20) NOT NULL,
    status VARCHAR(20) NOT NULL,
    start_date DATE NOT NULL,
    renewal_date DATE NOT NULL,
    owner_id BIGINT NOT NULL,
    CONSTRAINT fk_subscription_owner
        FOREIGN KEY (owner_id)
        REFERENCES users(id)
        ON DELETE CASCADE
);
