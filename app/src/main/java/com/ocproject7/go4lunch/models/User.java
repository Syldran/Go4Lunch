package com.ocproject7.go4lunch.models;

import androidx.annotation.Nullable;

import java.util.List;

public class User {
    private String userId;
    private String username;
    private String email;
    @Nullable private String urlPicture;
    @Nullable private String restaurantId;
    @Nullable private String restaurantName;
    @Nullable private String restaurantAddress;
    private List<String> likedRestaurants;

    public User(){}

    public User(String userId, String username, String email, @Nullable String urlPicture) {
        this.userId = userId;
        this.username = username;
        this.email = email;
        this.urlPicture = urlPicture;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    @Nullable
    public String getUrlPicture() {
        return urlPicture;
    }

    public void setUrlPicture(@Nullable String urlPicture) {
        this.urlPicture = urlPicture;
    }

    @Nullable
    public String getRestaurantId() {
        return restaurantId;
    }

    public void setRestaurantId(@Nullable String restaurantId) {
        this.restaurantId = restaurantId;
    }

    @Nullable
    public String getRestaurantName() {
        return restaurantName;
    }

    public void setRestaurantName(@Nullable String restaurantName) {
        this.restaurantName = restaurantName;
    }

    @Nullable
    public String getRestaurantAddress() {
        return restaurantAddress;
    }

    public void setRestaurantAddress(@Nullable String restaurantAddress) {
        this.restaurantAddress = restaurantAddress;
    }

    public List<String> getLikedRestaurants() {
        return likedRestaurants;
    }

    public void setLikedRestaurants(List<String> likedRestaurants) {
        this.likedRestaurants = likedRestaurants;
    }

    @Override
    public String toString() {
        return "User{" +
                "uid='" + userId + '\'' +
                ", username='" + username + '\'' +
                ", email='" + email + '\'' +
                ", urlPicture='" + urlPicture + '\'' +
                ", restaurantUid='" + restaurantId + '\'' +
                ", restaurantName='" + restaurantName + '\'' +
                ", restaurantAddress='" + restaurantAddress + '\'' +
                ", likedRestaurants=" + likedRestaurants +
                '}';
    }
}
