DROP TABLE IF EXISTS todo;
DROP TABLE IF EXISTS todo_user;

CREATE TABLE todo_user (
    id      BIGINT AUTO_INCREMENT PRIMARY KEY,
    name    VARCHAR(255) NOT NULL
);

CREATE TABLE todo (
    id          BIGINT AUTO_INCREMENT PRIMARY KEY,
    task        VARCHAR(255) NOT NULL,
    user_id     BIGINT NOT NULL,
    FOREIGN KEY (user_id) REFERENCES todo_user(id)
);
