package com.example.cmp354project;

import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.List;

public class UserProfile {

     private String fname;
     private String lname;
     private String email;
     private String password;
     private String dormBlock;
     private String dormNum;
     private String points;

    // public static List<UserProfile> AllUsers;

     public UserProfile(String fname, String lname, String email, String password, String dormBlock, String dormNum, String points) {
          this.fname = fname;
          this.lname = lname;
          this.email = email;
          this.password = password;
          this.dormBlock = dormBlock;
          this.dormNum = dormNum;
          this.points = points;
     }

     public String getFname() {
          return fname;
     }

     public void setFname(String fname) {
          this.fname = fname;
     }

     public String getLname() {
          return lname;
     }

     public void setLname(String lname) {
          this.lname = lname;
     }

     public String getEmail() {
          return email;
     }

     public void setEmail(String email) {
          this.email = email;
     }

     public String getPassword() {
          return password;
     }

     public void setPassword(String password) {
          this.password = password;
     }

     public String getDormBlock() {
          return dormBlock;
     }

     public void setDormBlock(String dormBlock) {
          this.dormBlock = dormBlock;
     }

     public String getDormNum() {
          return dormNum;
     }

     public void setDormNum(String dormNum) {
          this.dormNum = dormNum;
     }

     public String getPoints() {
          return points;
     }

     public void setPoints(String points) {
          this.points = points;
     }



}
