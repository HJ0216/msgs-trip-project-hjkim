package com.msgs.domain.tripplace.domain;

import com.msgs.global.common.model.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@IdClass(ReviewImageId.class)
@Getter @Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class ReviewImage extends BaseEntity {

    @Id @GeneratedValue
    @Column(name = "review_image_id")
    private Integer id;

    @Id
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "review_id", nullable = false)
    private Review review;

    private String imagePath;
}
