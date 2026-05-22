CREATE TABLE inventory_reservations (
    order_id BIGINT NOT NULL,
    product_id BIGINT NOT NULL,
    product_name VARCHAR(255) NOT NULL,
    quantity INT NOT NULL,
    reserved BOOLEAN NOT NULL,
    reason VARCHAR(255) NOT NULL,
    processed_by_instance VARCHAR(255) NOT NULL,
    PRIMARY KEY (order_id)
);
