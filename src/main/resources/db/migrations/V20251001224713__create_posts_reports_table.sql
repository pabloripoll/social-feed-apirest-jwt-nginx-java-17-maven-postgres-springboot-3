CREATE TABLE IF NOT EXISTS posts_reports (
  id BIGSERIAL PRIMARY KEY,
  type_id BIGINT NOT NULL,
  reporter_user_id BIGINT NOT NULL,
  reporter_message VARCHAR(256),
  in_review BOOLEAN NOT NULL DEFAULT FALSE,
  in_review_since TIMESTAMP WITHOUT TIME ZONE NOT NULL,
  is_closed BOOLEAN NOT NULL DEFAULT FALSE,
  closed_at TIMESTAMP WITHOUT TIME ZONE NOT NULL,
  moderation_id BIGINT NOT NULL,
  member_user_id BIGINT NOT NULL,
  member_post_id BIGINT NOT NULL,
  created_at TIMESTAMP WITHOUT TIME ZONE NOT NULL,
  updated_at TIMESTAMP WITHOUT TIME ZONE NOT NULL,
  CONSTRAINT fk_posts_reports_type FOREIGN KEY (type_id) REFERENCES posts_report_types(id) ON DELETE CASCADE,
  CONSTRAINT fk_posts_reports_reporter_user FOREIGN KEY (reporter_user_id) REFERENCES users(id) ON DELETE CASCADE,
  CONSTRAINT fk_posts_reports_member_user FOREIGN KEY (member_user_id) REFERENCES users(id) ON DELETE CASCADE,
  CONSTRAINT fk_posts_reports_moderation FOREIGN KEY (moderation_id) REFERENCES members_moderations(id) ON DELETE CASCADE,
  CONSTRAINT fk_posts_reports_member_post FOREIGN KEY (member_post_id) REFERENCES posts(id) ON DELETE CASCADE
);

CREATE INDEX IF NOT EXISTS idx_posts_reports_created_at ON posts_reports (created_at);
