package com.msgs.domain.tripstory.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;

import com.msgs.domain.tripschedule.domain.Trip;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter @Setter
public class TripDTO {

	// Trip Entity
	private Integer id;
	private String title;
	private String city;
	private LocalDate startDate;
	private LocalDate endDate;
	private LocalDateTime createdDate;
	private LocalDateTime updatedDate;
	
	// TripDay Entity
	private int dailyId;
	
	// entity 값 DTO 생성자 주입 - Trip
	public TripDTO(Trip trip) {
		this.id = trip.getId();
		this.title = trip.getTitle();
		this.startDate = trip.getStartDate();
		this.endDate = trip.getEndDate();
		this.createdDate = trip.getCreatedDate();
		this.updatedDate = trip.getUpdatedDate();
	}
		
}
