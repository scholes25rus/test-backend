CREATE TABLE IF NOT EXISTS author
(
    id SERIAL primary key,
    fullname TEXT NOT NULL,
    created_date TIMESTAMPTZ NOT NULL DEFAULT now()
);

ALTER TABLE budget ADD COLUMN author_id INT,
ADD CONSTRAINT fk_author_id
FOREIGN KEY (author_id)
REFERENCES author(id);
