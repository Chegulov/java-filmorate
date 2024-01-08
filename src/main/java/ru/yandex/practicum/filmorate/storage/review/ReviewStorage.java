package ru.yandex.practicum.filmorate.storage.review;

import ru.yandex.practicum.filmorate.model.Review;

import java.util.List;

public interface ReviewStorage {
    Review create(Review review);

    Review update(Review review);

    Review getReviewById(int id);

    void deleteReviewById(int id);

    List<Review> getReviewsByFilmId(int id, int count);

    List<Review> getReviews(int count);

    void addLikeDislikeToReview(int id, int userId, boolean isLike);

    void removeLikeDislikeToReview(int id, int userId, boolean isLike);
}
