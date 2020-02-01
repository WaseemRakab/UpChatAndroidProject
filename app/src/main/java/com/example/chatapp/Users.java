package com.example.chatapp;

public class Users {
    private String Email;
    private String Name;

    public Users() {
    }

    public Users(String email, String name) {
        Email = email;
        Name = name;
    }

    public String getEmail() {
        return Email;
    }

    public void setEmail(String email) {
        Email = email;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public String toString() {
        return "Name is: " + this.Name + "Email is: " + this.Email;
    }
}