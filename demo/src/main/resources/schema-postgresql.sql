CREATE TABLE users (
    id SERIAL PRIMARY KEY,
    username VARCHAR(255) NOT NULL,
    password VARCHAR(255) NOT NULL,
    role VARCHAR(50) NOT NULL
);

CREATE TABLE posts (
    id SERIAL PRIMARY KEY,
    title VARCHAR(255) NOT NULL,
    content TEXT NOT NULL,
    attachment bytea,
    attachment_filename VARCHAR(255),
    attachment_content_type VARCHAR(100),
    author VARCHAR(255) NOT NULL
);

ALTER TABLE users
  ADD COLUMN first_name VARCHAR(255),
  ADD COLUMN last_name VARCHAR(255),
  ADD COLUMN student_class VARCHAR(100);

  CREATE TABLE comments (
      id SERIAL PRIMARY KEY,
      post_id BIGINT NOT NULL,
      username VARCHAR(255) NOT NULL,
      text TEXT NOT NULL,
      likes INTEGER DEFAULT 0,
      created_date TIMESTAMP NOT NULL,
      FOREIGN KEY (post_id) REFERENCES posts(id)
  );

  CREATE TABLE comment_likes (
      id SERIAL PRIMARY KEY,
      comment_id BIGINT NOT NULL,
      username VARCHAR(255) NOT NULL,
      CONSTRAINT fk_comment
         FOREIGN KEY (comment_id)
         REFERENCES comments(id)
  );

  CREATE TABLE school_classes (
      id SERIAL PRIMARY KEY,
      name VARCHAR(255) NOT NULL,
      description VARCHAR(1024),
      content TEXT,
      auto_approve BOOLEAN NOT NULL DEFAULT FALSE
  );

  CREATE TABLE class_signups (
      id SERIAL PRIMARY KEY,
      school_class_id BIGINT NOT NULL,
      user_id BIGINT NOT NULL,
      status VARCHAR(20) NOT NULL,
      created_date TIMESTAMP NOT NULL,
      FOREIGN KEY (school_class_id) REFERENCES school_classes(id)
      ON DELETE CASCADE
  );

  CREATE TABLE lessons (
      id SERIAL PRIMARY KEY,
      school_class_id BIGINT NOT NULL,
      title VARCHAR(255) NOT NULL,
      description VARCHAR(1024),
      content TEXT,
      attachment BYTEA,
      attachment_filename VARCHAR(255),
      attachment_content_type VARCHAR(100),
      CONSTRAINT fk_school_class
          FOREIGN KEY (school_class_id)
          REFERENCES school_classes(id)
  );


  CREATE TABLE lesson_ratings (
      id SERIAL PRIMARY KEY,
      lesson_id BIGINT NOT NULL,
      user_id BIGINT NOT NULL,
      rating INTEGER NOT NULL CHECK (rating BETWEEN 1 AND 5),
      created_date TIMESTAMP NOT NULL,
      CONSTRAINT fk_lesson
          FOREIGN KEY (lesson_id)
          REFERENCES lessons(id)

  );

  ALTER TABLE school_classes ADD COLUMN teacher_id BIGINT;

  CREATE TABLE class_signups (
      id SERIAL PRIMARY KEY,
      school_class_id BIGINT NOT NULL,
      user_id BIGINT NOT NULL,
      status VARCHAR(20) NOT NULL,
      created_date TIMESTAMP NOT NULL,
      CONSTRAINT fk_school_class FOREIGN KEY (school_class_id) REFERENCES school_classes(id)
  );

  -- Drop the existing foreign key constraint (name might vary)
  ALTER TABLE lessons DROP CONSTRAINT fk_school_class;

  -- Add a new foreign key constraint with cascading delete
  ALTER TABLE lessons
    ADD CONSTRAINT fk_school_class
    FOREIGN KEY (school_class_id)
    REFERENCES school_classes(id)
    ON DELETE CASCADE;


    CREATE TABLE post_rating (
        id SERIAL PRIMARY KEY,
        post_id INTEGER NOT NULL,
        user_id INTEGER NOT NULL,
        rating SMALLINT NOT NULL,
        CONSTRAINT post_rating_unique UNIQUE(post_id, user_id),
        CONSTRAINT post_rating_fk_post FOREIGN KEY (post_id)
             REFERENCES posts(id) ON DELETE CASCADE,
        CONSTRAINT post_rating_fk_user FOREIGN KEY (user_id)
             REFERENCES users(id) ON DELETE CASCADE
    );

    ALTER TABLE posts ADD COLUMN avg_rating numeric(3,1) DEFAULT 0;

    CREATE TABLE task_submissions (
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