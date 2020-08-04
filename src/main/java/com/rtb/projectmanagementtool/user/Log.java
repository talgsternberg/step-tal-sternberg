package com.rtb.projectmanagementtool.user;

/** A Login Info Collection */
public final class Log {

  private final String status;
  private final String loginUrl;
  private final String logoutUrl;
  private final String userEmail;
  private final String AuthID;

  public Log(String status, String loginUrl, String logoutUrl, String userEmail, String AuthID) {
    this.status = status;
    this.loginUrl = loginUrl;
    this.logoutUrl = logoutUrl;
    this.userEmail = userEmail;
    this.AuthID = AuthID;
  }
}
