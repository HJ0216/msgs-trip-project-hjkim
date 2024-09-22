package com.msgs.domain.tripstory.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Destination {

  @Id
  @GeneratedValue
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
