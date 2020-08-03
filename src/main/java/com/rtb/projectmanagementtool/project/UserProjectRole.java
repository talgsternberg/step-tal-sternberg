// UserProjectRole.java - enum representing role of user in a project
//
// REGULAR - regular user with limited capabilities within project.
// ADMIN - user with more administrative capabilities within project
// CREATER - the creator of the project. Does everything an admin can do
// and more, including being able to add/remove admins and delete the project

package com.rtb.projectmanagementtool.project;

public enum UserProjectRole {
  CREATOR,
  ADMIN,
  REGULAR;
}
