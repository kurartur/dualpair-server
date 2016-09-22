DROP TABLE IF EXISTS test_combinations;
CREATE TABLE test_combinations (
  id         BIGINT PRIMARY KEY,
  sociotype_id INTEGER
);

DROP TABLE IF EXISTS test_combinations_choices_view;
CREATE TABLE test_combinations_choices_view (
  combination_id BIGINT,
  pair1 VARCHAR(50),
  pair2 VARCHAR(50)
);