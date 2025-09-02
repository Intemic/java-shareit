CREATE TABLE IF NOT EXISTS users(
    id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    name VARCHAR(50),
    email VARCHAR(100)
);

CREATE TABLE IF NOT EXISTS items(
    id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    owner_id BIGINT,
    name VARCHAR(100),
    description VARCHAR,
    available BOOLEAN,
    FOREIGN KEY (owner_id) REFERENCES users(id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS bookings(
    id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    booker_id BIGINT,
    item_id BIGINT,
    booking_start timestamp,
    booking_end timestamp,
    status VARCHAR,
    FOREIGN KEY(booker_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY(item_id) REFERENCES items(id) ON DELETE CASCADE
);