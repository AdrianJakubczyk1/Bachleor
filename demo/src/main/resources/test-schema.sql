DROP TABLE IF EXISTS "comment_likes";
DROP TABLE IF EXISTS "comments";
DROP TABLE IF EXISTS "posts";
DROP TABLE IF EXISTS "app_statistics";
DROP TABLE IF EXISTS "task_submissions";
DROP TABLE IF EXISTS "lesson_ratings";
DROP TABLE IF EXISTS "lesson_tasks";
DROP TABLE IF EXISTS "lessons";
DROP TABLE IF EXISTS "class_signups";
DROP TABLE IF EXISTS "school_classes";
DROP TABLE IF EXISTS "users";

-- Create the users table
CREATE TABLE "users" (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  username VARCHAR(100) UNIQUE NOT NULL,
  password VARCHAR(255) NOT NULL,
  role VARCHAR(20),
  first_name VARCHAR(100),
  last_name VARCHAR(100),
  email VARCHAR(200),
  student_class BIGINT
);

CREATE TABLE "class_signups" (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  user_id BIGINT NOT NULL,
  status VARCHAR(50),
  created_date TIMESTAMP,
  school_class_id BIGINT
);

-- Create the school_class table
CREATE TABLE "school_classes" (
  id               BIGSERIAL    PRIMARY KEY,
  name             VARCHAR(255) NOT NULL,
  description      TEXT,
  content          TEXT,
  auto_approve     BOOLEAN      DEFAULT FALSE,
  teacher_id       BIGINT       NOT NULL,
  signup_deadline  TIMESTAMP,

  CONSTRAINT fk_school_classes_teacher
    FOREIGN KEY (teacher_id)
    REFERENCES "users"(id)
);

-- Create the lesson table
CREATE TABLE "lessons" (
id BIGSERIAL PRIMARY KEY,
  school_class_id BIGINT NOT NULL,
  title VARCHAR(255) NOT NULL,
  description TEXT,
  content TEXT NOT NULL,
  attachment BYTEA,
  attachment_filename VARCHAR(255),
  attachment_content_type VARCHAR(100),
  solution_required BOOLEAN,
  solution_due_date TIMESTAMP,

  CONSTRAINT fk_lessons_school_class
    FOREIGN KEY (school_class_id)
    REFERENCES "school_classes"(id)
);

-- Create the lesson_task table
CREATE TABLE "lesson_tasks" (
  id                BIGSERIAL    PRIMARY KEY,
  lesson_id         BIGINT       NOT NULL
                       CONSTRAINT fk_lesson_tasks_lesson
                         REFERENCES "lessons"(id)
                         ON DELETE CASCADE,
  title             VARCHAR(255) NOT NULL,
  description       TEXT,
  due_date          TIMESTAMP,
  solution_required BOOLEAN      NOT NULL DEFAULT FALSE
);

-- Create the task_submission table
CREATE TABLE "task_submissions" (
       id SERIAL PRIMARY KEY,
        lesson_task_id BIGINT NOT NULL,
        user_id BIGINT NOT NULL,
        solution_text TEXT,
        solution_file BYTEA,
        solution_filename VARCHAR(255),
        solution_content_type VARCHAR(255),
        submitted_date TIMESTAMP WITHOUT TIME ZONE,
        grade NUMERIC,
        teacher_comments TEXT
);

-- Create the app_statistics table
CREATE TABLE "app_statistics" (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  stat_name VARCHAR(100),
  stat_value BIGINT,
  timestamp TIMESTAMP
);

-- Create the posts table
CREATE TABLE "posts" (
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

CREATE TABLE IF NOT EXISTS "school_classes" (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  name VARCHAR(100),
  description VARCHAR(500),
  content TEXT,
  auto_approve BOOLEAN,
  teacher_id BIGINT,
  signup_deadline TIMESTAMP
);

CREATE TABLE IF NOT EXISTS "comments" (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  post_id BIGINT NOT NULL,
  username VARCHAR(100) NOT NULL,
  text TEXT NOT NULL,
  likes INT DEFAULT 0,
  created_date TIMESTAMP
);

CREATE TABLE IF NOT EXISTS "comment_likes" (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  comment_id BIGINT NOT NULL,
  username VARCHAR(100) NOT NULL
);

  CREATE TABLE "lesson_ratings" (
      id SERIAL PRIMARY KEY,
      lesson_id BIGINT NOT NULL,
      user_id BIGINT NOT NULL,
      rating INTEGER NOT NULL CHECK (rating BETWEEN 1 AND 5),
      created_date TIMESTAMP NOT NULL,
      CONSTRAINT fk_lesson
          FOREIGN KEY (lesson_id)
          REFERENCES "lessons"(id)

  );