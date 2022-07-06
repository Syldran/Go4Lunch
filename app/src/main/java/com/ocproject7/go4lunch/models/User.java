package com.ocproject7.go4lunch.models;

import androidx.annotation.Nullable;

public class User {
    private String userId;
    private String username;
    private String email;
    @Nullable
    private String urlPicture;
    @Nullable
    private String restaurantId;
    @Nullable
    private String restaurantName;


    public User() {
    }

    public User(String userId, String username, String email, @Nullable String urlPicture, String restaurantId, String restaurantName) {
        this.userId = userId;
        this.username = username;
        this.email = email;
        this.urlPicture = urlPicture;
        this.restaurantId = restaurantId;
        this.restaurantName = restaurantName;
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


    @Override
    public String toString() {
        return "User{" +
                "uid='" + userId + '\'' +
                ", username='" + username + '\'' +
                ", email='" + email + '\'' +
                ", urlPicture='" + urlPicture + '\'' +
                ", restaurantUid='" + restaurantId + '\'' +
                ", restaurantName='" + restaurantName +
                '}';
    }
}
