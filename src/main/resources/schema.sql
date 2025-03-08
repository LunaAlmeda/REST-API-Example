CREATE TABLE IF NOT EXISTS prices (
                                      id BIGINT AUTO_INCREMENT PRIMARY KEY,
                                      brand_id INTEGER NOT NULL,
                                      start_date TIMESTAMP NOT NULL,
                                      end_date TIMESTAMP NOT NULL,
                                      price_list INTEGER NOT NULL,
                                      product_id BIGINT NOT NULL,
                                      priority INTEGER NOT NULL,
                                      price DECIMAL(19, 2) NOT NULL,
                                      currency VARCHAR(3) NOT NULL
    );
