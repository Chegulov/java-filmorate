package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
import ru.yandex.practicum.filmorate.storage.review.ReviewStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.List;

@Slf4j
@Service
public class ReviewService {
    private final ReviewStorage reviewStorage;
    private final FilmStorage filmStorage;
    private final UserStorage userStorage;

    @Autowired
    public ReviewService(@Qualifier("filmDbStorage") FilmStorage filmStorage,
                       @Qualifier("userDbStorage") UserStorage userStorage,
                         ReviewStorage reviewStorage) {
        this.filmStorage = filmStorage;
        this.userStorage = userStorage;
        this.reviewStorage = reviewStorage;
    }

    public ReviewStorage getReviewStorage() {
        return reviewStorage;
    }

    public Review create(Review review) {
        filmStorage.getFilmById(review.getFilmId());
        userStorage.getUserById(review.getUserId());
        return reviewStorage.create(review);
    }

    public Review update(Review review) {
        filmStorage.getFilmById(review.getFilmId());
        userStorage.getUserById(review.getUserId());
        return reviewStorage.update(review);
    }

    public Review getReviewById(int id) {
        return reviewStorage.getReviewById(id);
    }

    public void deleteReviewById(int id) {
        reviewStorage.deleteReviewById(id);
    }

    public List<Review> getReviews(Integer count) {
        return reviewStorage.getReviews(count);
    }

    public List<Review> getReviewsByFilmId(Integer filmId, Integer count) {
        return reviewStorage.getReviewsByFilmId(filmId, count);
    }

    public void addLikeDislikeToReview(int id, int userId, boolean b) {
        reviewStorage.addLikeDislikeToReview(id, userId, b);
    }

    public void removeLikeDislikeToReview(int id, int userId, boolean b) {
        reviewStorage.removeLikeDislikeToReview(id, userId, b);
    }
}
