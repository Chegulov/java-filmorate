package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.EventType;
import ru.yandex.practicum.filmorate.model.Feed;
import ru.yandex.practicum.filmorate.model.Operation;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.service.FeedService;
import ru.yandex.practicum.filmorate.service.ReviewService;

import javax.validation.Valid;
import java.time.Instant;
import java.util.List;

@Slf4j
@Validated
@RestController
@RequestMapping("/reviews")
@RequiredArgsConstructor
public class ReviewController {
    private final ReviewService reviewService;
    private final FeedService feedService;

    @PostMapping
    public Review create(@Valid @RequestBody Review review) {
        reviewService.create(review);
        feedService.create(Feed.builder()
                .eventType(EventType.REVIEW)
                .operation(Operation.ADD)
                .timestamp(Instant.now().toEpochMilli())
                .userId(review.getUserId())
                .entityId(review.getReviewId())
                .build());

        log.info("отзыв с id={} добавлен", review.getReviewId());
        return review;
    }

    @PutMapping
    public Review update(@Valid @RequestBody Review review) {
        review = reviewService.update(review);
        feedService.create(Feed.builder()
                .eventType(EventType.REVIEW)
                .operation(Operation.UPDATE)
                .timestamp(Instant.now().toEpochMilli())
                .userId(review.getUserId())
                .entityId(review.getReviewId())
                .build());

        return review;
    }

    @DeleteMapping("/{id}")
    public void deleteReviewById(@PathVariable int id) {
        int userId = reviewService.getReviewById(id).getUserId();
        reviewService.deleteReviewById(id);
        feedService.create(Feed.builder()
                .eventType(EventType.REVIEW)
                .operation(Operation.REMOVE)
                .timestamp(Instant.now().toEpochMilli())
                .userId(userId)
                .entityId(id)
                .build());
    }

    @GetMapping("/{id}")
    public Review getReviewById(@PathVariable int id) {
        return reviewService.getReviewById(id);
    }

    @GetMapping
    public List<Review> getReviews(@RequestParam(required = false) Integer filmId,
                                   @RequestParam(defaultValue = "10") Integer count) {
        if (filmId == null) {
            return reviewService.getReviews(count);
        } else {
            return reviewService.getReviewsByFilmId(filmId, count);
        }
    }

    @PutMapping("/{id}/like/{userId}")
    public void addLikeToReview(@PathVariable int id,
                                @PathVariable int userId) {
        reviewService.addLikeDislikeToReview(id, userId, true);
    }

    @PutMapping("/{id}/dislike/{userId}")
    public void addDislikeToReview(@PathVariable int id,
                                @PathVariable int userId) {
        reviewService.addLikeDislikeToReview(id, userId, false);
    }

    @DeleteMapping("/{id}/like/{userId}")
    public void removeLikeFromReview(@PathVariable int id,
                                @PathVariable int userId) {
        reviewService.removeLikeDislikeToReview(id, userId, true);
    }

    @DeleteMapping("/{id}/dislike/{userId}")
    public void removeDislikeFromReview(@PathVariable int id,
                                     @PathVariable int userId) {
        reviewService.removeLikeDislikeToReview(id, userId, false);
    }
}
