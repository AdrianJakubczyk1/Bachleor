CREATE TABLE users (
  id              BIGINT PRIMARY KEY AUTO_INCREMENT,
  username        VARCHAR(100) UNIQUE NOT NULL,
  password        VARCHAR(255) NOT NULL,
  role            VARCHAR(20),
  first_name      VARCHAR(100),
  last_name       VARCHAR(100),
  email           VARCHAR(200),
  student_class   BIGINT
);

CREATE TABLE class_signups (
  id               BIGINT PRIMARY KEY AUTO_INCREMENT,
  user_id          BIGINT NOT NULL,
  status           VARCHAR(50),
  created_date     TIMESTAMP,
  school_class_id  BIGINT
);

CREATE TABLE school_class (
  id        BIGINT PRIMARY KEY AUTO_INCREMENT,
  name      VARCHAR(100),
  teacher_id BIGINT
);

CREATE TABLE lesson (
  id              BIGINT PRIMARY KEY AUTO_INCREMENT,
  title           VARCHAR(200),
  school_class_id BIGINT
);

CREATE TABLE lesson_task (
  id         BIGINT PRIMARY KEY AUTO_INCREMENT,
  lesson_id  BIGINT,
  description VARCHAR(500),
  due_date    TIMESTAMP
);

CREATE TABLE task_submission (
  id                 BIGINT PRIMARY KEY AUTO_INCREMENT,
  lesson_task_id     BIGINT,
  user_id            BIGINT,
  solution_text      CLOB,
  solution_filename  VARCHAR(255),
  solution_content_type VARCHAR(100),
  submitted_date     TIMESTAMP,
  grade              INTEGER,
  teacher_comments   CLOB
);

CREATE TABLE app_statistics (
  id         BIGINT PRIMARY KEY AUTO_INCREMENT,
  stat_name  VARCHAR(100),
  stat_value BIGINT,
  timestamp  TIMESTAMP
);

CREATE TABLE posts (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    title VARCHAR(255),
    author VARCHAR(255),
    content TEXT,
    view_count INT DEFAULT 0,
    thumbnail BLOB,
    avg_rating FLOAT,
    attachment BLOB,
    thumbnail_filename VARCHAR(255),
    attachment_filename VARCHAR(255),
    thumbnail_content_type VARCHAR(255),
    attachment_content_type VARCHAR(255)
);