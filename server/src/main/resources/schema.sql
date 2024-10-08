drop table IF EXISTS users CASCADE;
drop table IF EXISTS items CASCADE;
drop table IF EXISTS bookings CASCADE;
drop table IF EXISTS requests CASCADE;
drop table IF EXISTS comments CASCADE;

create TABLE IF NOT EXISTS users (
                                     id        BIGINT GENERATED BY DEFAULT AS IDENTITY          NOT NULL,
                                     name      VARCHAR(255)    NOT NULL,
                                     email     VARCHAR(512)    NOT NULL,
                                     CONSTRAINT pk_user PRIMARY KEY (id),
                                     CONSTRAINT UQ_USER_EMAIL UNIQUE (email)
);

create TABLE IF NOT EXISTS items (
                                     id BIGINT GENERATED BY DEFAULT AS IDENTITY NOT NULL,
                                     name VARCHAR(255) NOT NULL,
                                     description VARCHAR NOT NULL,
                                     is_available BOOLEAN NOT NULL,
                                     owner_id BIGINT NOT NULL,
                                     request_id BIGINT,
                                     CONSTRAINT pk_item PRIMARY KEY (id)
);

create TABLE IF NOT EXISTS bookings (
                                        id BIGINT GENERATED BY DEFAULT AS IDENTITY NOT NULL,
                                        start_date TIMESTAMP WITHOUT TIME ZONE,
                                        end_date TIMESTAMP WITHOUT TIME ZONE,
                                        item_id BIGINT NOT NULL,
                                        booker_id BIGINT NOT NULL,
                                        status VARCHAR(20) NOT NULL,
                                        CONSTRAINT pk_booking PRIMARY KEY (id)
);

create TABLE IF NOT EXISTS comments (
                                        id BIGINT GENERATED BY DEFAULT AS IDENTITY NOT NULL,
                                        text VARCHAR NOT NULL,
                                        item_id BIGINT NOT NULL,
                                        author_id BIGINT NOT NULL,
                                        created TIMESTAMP WITHOUT TIME ZONE,
                                        CONSTRAINT pk_comment PRIMARY KEY (id)
);

create TABLE IF NOT EXISTS requests (
                                        id BIGINT GENERATED BY DEFAULT AS IDENTITY NOT NULL,
                                        description VARCHAR NOT NULL,
                                        requestor_id BIGINT NOT NULL,
                                        created TIMESTAMP WITHOUT TIME ZONE,
                                        CONSTRAINT pk_request PRIMARY KEY (id)
);
