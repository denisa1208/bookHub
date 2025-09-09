package org.example;

public class UserSession {
    private static UserSession instance;
    private User currentUser;

    private UserSession() {}

    public static UserSession getInstance() {
        if (instance == null) {
            instance = new UserSession();
        }
        return instance;
    }

    public void setCurrentUser(User user) {
        this.currentUser = user;
        System.out.println("DEBUG: UserSession - User set to: " + user.getUsername());
    }

    public String getCurrentUsername() {
        if (currentUser != null) {
            System.out.println("DEBUG: UserSession - Getting username: " + currentUser.getUsername());
            return currentUser.getUsername();
        }
        System.out.println("DEBUG: UserSession - No user logged in");
        return null;
    }

    public long getCurrentUserId() {
        return currentUser != null ? currentUser.getId() : -1;
    }

    public User getCurrentUser() {
        return currentUser;
    }

    public void logout() {
        currentUser = null;
    }
}
