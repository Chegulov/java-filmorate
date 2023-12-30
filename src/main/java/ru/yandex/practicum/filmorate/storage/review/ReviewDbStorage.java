package ru.yandex.practicum.filmorate.storage.review;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.DataNotFoundException;
import ru.yandex.practicum.filmorate.exception.DuplicateDataException;
import ru.yandex.practicum.filmorate.model.Review;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Component
public class ReviewDbStorage implements ReviewStorage {
    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public ReviewDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public Review create(Review review) {
        int reviewId = addReviewToDb(review);
        review.setReviewId(reviewId);
        String sqlQuery = "INSERT INTO freviews_likes (review_id, user_id, is_positive) VALUES (?, ?, ?)";
        if (!review.getReviewsLikes().isEmpty()) {
            for (Map.Entry<Integer, Boolean> pair : review.getReviewsLikes().entrySet()) {
                jdbcTemplate.update(sqlQuery, reviewId, pair.getKey(), pair.getValue());
            }
        }
        return review;
    }

    @Override
    public Review update(Review review) {
        String sqlQuery = "UPDATE reviews SET" +
                " content=?, is_positive=? WHERE review_id=?";
        if (jdbcTemplate.update(
                sqlQuery,
                review.getContent(),
                review.getIsPositive(),
                review.getReviewId()) == 0
        ) {
            String msg = String.format("Отзыв с id=%d не найден", review.getReviewId());
            log.info(msg);
            throw new DataNotFoundException(msg);
        }

        String sqlQ = "DELETE FROM reviews_likes WHERE review_id=?";
        jdbcTemplate.update(sqlQ, review.getReviewId());

        if (review.getReviewsLikes() != null && review.getReviewsLikes().size() != 0) {
            String sqlQ2 = "INSERT INTO reviews_likes (review_id, user_id, is_positive) VALUES (?, ?, ?)";
            for (Map.Entry<Integer, Boolean> pair : review.getReviewsLikes().entrySet()) {
                jdbcTemplate.update(sqlQ2, review.getReviewId(), pair.getKey(), pair.getValue());
            }
        }
        return getReviewById(review.getReviewId());
    }

    @Override
    public Review getReviewById(int id) {
        String sqlQuery = "SELECT * FROM reviews WHERE review_id=?";
        Review review;
        try {
            review = jdbcTemplate.queryForObject(sqlQuery, this::makeReview, id);
        } catch (EmptyResultDataAccessException e) {
            String msg = String.format("Отзыв с id=%d не найден", id);
            log.info(msg);
            throw new DataNotFoundException(msg);
        }
        return review;
    }

    @Override
    public void deleteReviewById(int id) {
        if (!dbContainsReview(id)) {
            String msg = String.format("Отзыв с id=%d не найден", id);
            log.info(msg);
            throw new DataNotFoundException(msg);
        }

        String sqlQuery = "DELETE FROM reviews WHERE review_id=?";
        jdbcTemplate.update(sqlQuery, id);
        log.info("Отзыв с id={} удален", id);
    }

    @Override
    public List<Review> getReviewsByFilmId(int id, int count) {
        String sqlQuery = "SELECT * FROM reviews WHERE film_id=?";
        return jdbcTemplate.query(sqlQuery, this::makeReview, id).stream()
                .sorted(Comparator.comparingInt(Review::getUseful).reversed())
                .limit(count)
                .collect(Collectors.toList());
    }

    @Override
    public List<Review> getReviews(int count) {
        String sqlQuery = "SELECT * FROM reviews";
        return jdbcTemplate.query(sqlQuery, this::makeReview).stream()
                .sorted(Comparator.comparingInt(Review::getUseful).reversed())
                .limit(count)
                .collect(Collectors.toList());
    }

    @Override
    public void addLikeDislikeToReview(int id, int userId, boolean isLike) {
        if (!dbContainsReview(id)) {
            String msg = String.format("Отзыв с id=%d не найден", id);
            log.info(msg);
            throw new DataNotFoundException(msg);
        }
        String sqlQuery = "INSERT INTO reviews_likes (review_id, user_id, is_positive) VALUES (?, ?, ?)";
        try {
            jdbcTemplate.update(sqlQuery, id, userId, isLike);
        } catch (DuplicateKeyException e) {
            String msg;
            if (isLike) {
                msg = "Одному отзыву нельзя поставить лайк дважды";
            } else {
                msg = "Одному отзыву нельзя поставить дизлайк дважды";
            }
            log.info(msg);
            throw new DuplicateDataException(msg);
        }
    }

    @Override
    public void removeLikeDislikeToReview(int id, int userId, boolean isLike) {
        if (!dbContainsReview(id)) {
            String msg = String.format("Отзыв с id=%d не найден", id);
            log.info(msg);
            throw new DataNotFoundException(msg);
        }
        String sqlQuery = "DELETE FROM reviews_likes WHERE review_id=?, user_id=?, is_positive=?";
        if (jdbcTemplate.update(sqlQuery, id, userId, isLike) == 0) {
            String msg;
            if (isLike) {
                msg =  String.format("Лайка от пользователя с id=%d у отзыва с id=%d нет", userId, id);
            } else {
                msg =  String.format("Дизлайка от пользователя с id=%d у отзыва с id=%d нет", userId, id);
            }
            log.info(msg);
            throw new DataNotFoundException(msg);
        }
    }

    private int addReviewToDb(Review review) {
        SimpleJdbcInsert simpleJdbcInsert = new SimpleJdbcInsert(jdbcTemplate)
                .withTableName("reviews")
                .usingGeneratedKeyColumns("review_id");
        return simpleJdbcInsert.executeAndReturnKey(review.toMap()).intValue();
    }

    private Review makeReview(ResultSet rs, int rowNum) throws SQLException {
        Review review = Review.builder()
                .reviewId(rs.getInt("review_id"))
                .content(rs.getString("content"))
                .isPositive(rs.getBoolean("is_positive"))
                .userId(rs.getInt("user_id"))
                .filmId(rs.getInt("film_id"))
                .build();

        String sqlQuery = "SELECT user_id, is_positive FROM reviews_likes WHERE review_id=?";
        List<Map<String, Object>> likes = jdbcTemplate.queryForList(sqlQuery,review.getReviewId());
        for (Map<String, Object> m : likes) {
            review.getReviewsLikes().put((Integer) m.get("user_id"), (Boolean) m.get("is_positive"));
        }
        return review;
    }

    private boolean dbContainsReview(int reviewId) {
        String sqlQuery = "SELECT * FROM reviews WHERE review_id=?";
        try {
            jdbcTemplate.queryForObject(sqlQuery, this::makeReview, reviewId);
            return true;
        } catch (EmptyResultDataAccessException e) {
            return false;
        }
    }
}
