package com.msgs.msgs.entity.tripstory;

import com.msgs.msgs.entity.tripschedule.Trip;
import com.msgs.msgs.entity.user.User;
import jakarta.persistence.*;
import java.time.LocalDateTime;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "trip_story")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TripStory {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "story_id")
	private int id;

	// join with trip schedule
	@OneToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "schedule_id", nullable = false)
	private Trip trip;

	// join with user
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "user_id", nullable = false)
	private User userTripStory;

	@Column(length = 100)
	private String title;

	@Column(columnDefinition = "int(1)")
	private int rating;

	@Column(columnDefinition = "text")
	private String comment;

	@Column(name = "date_list", length = 500, nullable = false)
	private String dateList;

	@Column(name = "city_name", length = 30)
	private String cityName;

	@Column(name = "reg_date", nullable = false)
	private LocalDateTime regDate;
	@Column(name = "mod_date")
	private LocalDateTime modDate;

	// mapping
	@OneToMany(mappedBy = "tripStoryImg")
	private List<StoryImg> storyImgs = new ArrayList<>();

	@OneToMany(mappedBy = "tripStoryCmnt")
	private List<StoryComment> storyComments = new ArrayList<>();

	@OneToMany(mappedBy = "tripLikeCnt")
	private List<StoryLikeCount> storyLikeCounts = new ArrayList<>();

	@PrePersist
	public void setRegDate() {
		this.regDate = LocalDateTime.now();
	}

}
