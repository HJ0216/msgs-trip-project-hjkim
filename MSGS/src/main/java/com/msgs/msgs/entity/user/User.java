package com.msgs.msgs.entity.user;

import com.msgs.msgs.entity.placereview.PlaceReview;
import com.msgs.msgs.entity.tripschedule.TripSchedule;
import com.msgs.msgs.entity.tripstory.StoryComment;
import com.msgs.msgs.entity.tripstory.StoryLikeCount;
import com.msgs.msgs.entity.tripstory.TripStory;

import jakarta.persistence.*;
import lombok.*;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;


@Entity
@Getter @Setter
@Builder
public class User implements UserDetails {

   @Id @GeneratedValue
   @Column(name = "user_id")
   private Integer id;

   @Column(nullable = false, columnDefinition="char(1)")
   private String status;

   @Column(nullable = false, unique = true, length = 50)
   private String email;

   @Column(nullable = false, unique = true, columnDefinition="char(11)")
   private String phone;

   @Column(length = 30)
   private String nickname;

   @Column(length = 30)
   private String password;

   private String imagePath;

   @Column(nullable = false)
   @CreatedDate
   private LocalDateTime createdDate;

   @Column(nullable = false)
   @LastModifiedDate
   private LocalDateTime updatedDate;

   // mapping
   // trip schedule
   @OneToMany(mappedBy = "user")
   private List<TripSchedule> tripSchedule = new ArrayList<>();

   // place review
   @OneToMany(mappedBy = "userPlaceReview")
   private List<PlaceReview> placeReviews = new ArrayList<>();

   // trip story
   @OneToMany(mappedBy = "userTripStory")
   private List<TripStory> tripStories = new ArrayList<>();

   @OneToMany(mappedBy = "userStoryCmnt")
   private List<StoryComment> storyComment = new ArrayList<>();

   @OneToMany(mappedBy = "userStoryLike")
   private List<StoryLikeCount> storyLikeCount = new ArrayList<>();



   //jwt
   @Override
   public Collection<? extends GrantedAuthority> getAuthorities() {
      return null;
   }

   @Override
   public String getUsername() {
      return id+","+email;
   }

   @Override
   public boolean isAccountNonExpired() {
      return true;
   }

   @Override
   public boolean isAccountNonLocked() {
      return true;
   }

   @Override
   public boolean isCredentialsNonExpired() {
      return true;
   }

   @Override
   public boolean isEnabled() {
      return true;
   }
}