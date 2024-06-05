package com.example.cmp354project;

public class dormService {
     private String OwnerEmail; //person offering
     private String serviceTitle;
     private String desc;
     private String cost;
     private String dormBlock;
     private String dormNum;
     private String status; //0:still up, 1:taken/claimed
     private String ClaimEmail;

     public void setServiceTitle(String serviceTitle) {
          this.serviceTitle = serviceTitle;
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

     @Override
     public String toString() {
          return "dormService{" +
                  "\nOwnerEmail = " + OwnerEmail +
                  "\nServiceTitle = " + serviceTitle +
                  "\ndesc = " +  desc +
                  "\ncost = " + cost +
                  "\nstatus = " + status +
                  "\nClaimEmail = " + ClaimEmail +
                  "}";
     }

     public dormService(String ownerEmail, String serviceTitle, String desc, String cost, String dormBlock, String dormNum, String status, String claimEmail) {
          OwnerEmail = ownerEmail;
          this.serviceTitle = serviceTitle;
          this.desc = desc;
          this.cost = cost;
          this.dormBlock = dormBlock;
          this.dormNum = dormNum;
          this.status = status;
          ClaimEmail = claimEmail;
     }

     public String getOwnerEmail() {
          return OwnerEmail;
     }

     public void setOwnerEmail(String ownerEmail) {
          OwnerEmail = ownerEmail;
     }

     public String getServiceTitle() {
          return serviceTitle;
     }

     public void setServicesTitle(String serviceTitle) {
          this.serviceTitle = serviceTitle;
     }

     public String getDesc() {
          return desc;
     }

     public void setDesc(String desc) {
          this.desc = desc;
     }

     public String getCost() {
          return cost;
     }

     public void setCost(String cost) {
          this.cost = cost;
     }


     public String getStatus() {
          return status;
     }

     public void setStatus(String status) {
          this.status = status;
     }


     public String getClaimEmail() {
          return ClaimEmail;
     }

     public void setClaimEmail(String claimEmail) {
          ClaimEmail = claimEmail;
     }
}
