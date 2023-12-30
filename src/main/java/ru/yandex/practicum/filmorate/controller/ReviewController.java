package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.service.ReviewService;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/reviews")
@RequiredArgsConstructor
public class ReviewController {
    private final ReviewService reviewService;

    @PostMapping
    public Review create(@RequestBody Review review) {
        validate(review);
        reviewService.create(review);

        log.info("отзыв с id={} добавлен", review.getReviewId());
        return review;
    }

    @PutMapping
    public Review update(@RequestBody Review review) {
        validate(review);

        return reviewService.update(review);
    }

    @DeleteMapping("/{id}")
    public void deleteReviewById(@PathVariable int id) {
        reviewService.getReviewStorage().deleteReviewById(id);
    }

    @GetMapping("/{id}")
    public Review getReviewById(@PathVariable int id) {
        return reviewService.getReviewStorage().getReviewById(id);
    }

    @GetMapping
    public List<Review> getReviews(@RequestParam(required = false) Integer filmId,
                                   @RequestParam(defaultValue = "10") Integer count) {
        if (filmId == null) {
            return reviewService.getReviewStorage().getReviews(count);
        } else {
            return reviewService.getReviewStorage().getReviewsByFilmId(filmId, count);
        }
    }

    @PutMapping("/{id}/like/{userId}")
    public void addLikeToReview(@PathVariable int id,
                                @PathVariable int userId) {
        reviewService.getReviewStorage().addLikeDislikeToReview(id, userId, true);
    }

    @PutMapping("/{id}/dislike/{userId}")
    public void addDislikeToReview(@PathVariable int id,
                                @PathVariable int userId) {
        reviewService.getReviewStorage().addLikeDislikeToReview(id, userId, false);
    }

    @DeleteMapping("/{id}/like/{userId}")
    public void removeLikeFromReview(@PathVariable int id,
                                @PathVariable int userId) {
        reviewService.getReviewStorage().removeLikeDislikeToReview(id, userId, true);
    }

    @DeleteMapping("/{id}/dislike/{userId}")
    public void removeDislikeFromReview(@PathVariable int id,
                                     @PathVariable int userId) {
        reviewService.getReviewStorage().removeLikeDislikeToReview(id, userId, false);
    }

    private void validate(Review review) {
        String msg;

        if (review.getContent() == null || review.getContent().isBlank()) {
            msg = "Содержание отзыва не может быть пустым.";
            log.error(msg);
            throw new ValidationException(msg);
        }

        if (review.getUserId() == 0) {
            msg = "Id пользователя не может быть равен нулю.";
            log.error(msg);
            throw new ValidationException(msg);
        }

        if (review.getFilmId() == 0) {
            msg = "Id фильма не может быть равен нулю.";
            log.error(msg);
            throw new ValidationException(msg);
        }

        if (review.getIsPositive() == null) {
            msg = "Полезность отзыва должна быть проинициализирована.";
            log.error(msg);
            throw new ValidationException(msg);
        }
    }
}
