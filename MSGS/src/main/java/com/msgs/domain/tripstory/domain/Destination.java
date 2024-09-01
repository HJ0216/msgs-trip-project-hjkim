package com.msgs.domain.tripstory.domain;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;

import java.time.LocalDateTime;

@Entity
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Destination {

    @Id @GeneratedValue
    @Column(name = "destination_id")
    private Integer id;

    @Column(length = 100, nullable = false)
    private String name;

    @Column(length = 15, nullable = false)
    private String type;

    @Column(length = 30, nullable = false)
    private String city;

    private String address;

    @Column(columnDefinition = "decimal(10, 6)")
    private Double longitude;

    @Column(columnDefinition = "decimal(10, 6)")
    private Double latitude;

    private String comments;
}
