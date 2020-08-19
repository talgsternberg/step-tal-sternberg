/**
 * This file contains javascript code for the project page
 */

/**
 * Method that shows the add user form
 */
function showAddUserForm() {
  document.getElementById('project-add-user-form').style.display = 'block';
  document.getElementById('add-user-button').style.display = 'none';
}

/*
 * Method that hides the add user form
 */
function hideAddUserForm() {
  document.getElementById('project-add-user-form').style.display = 'none';
  document.getElementById('add-user-button').style.display = 'block';
}