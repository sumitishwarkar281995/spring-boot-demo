DROP TABLE IF EXISTS customer;

CREATE TABLE customer (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255)
);


DROP TABLE IF EXISTS transaction;

CREATE TABLE transaction (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    amount DOUBLE,
    date DATE,
    customer_id BIGINT,
    FOREIGN KEY (customer_id) REFERENCES customer(id)
);