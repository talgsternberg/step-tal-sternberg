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

/**
 * Method that hides the add user form
 */
function hideAddUserForm() {
  document.getElementById('project-add-user-form').style.display = 'none';
  document.getElementById('add-user-button').style.display = 'block';
}

/**
 * Add event listener to toggle tree.
 */
const toggler = document.getElementsByClassName('task-tree-node');
let i;
for (i = 0; i < toggler.length; i++) {
  toggler[i].addEventListener('click', function() {
    this.parentElement.querySelector('.task-tree').classList.toggle('active');
    this.classList.toggle('task-tree-node-down');
  });
}

/**
 * Pop-up
 */
const popup = document.getElementById('project-tasktree-container');
const popupButton = document.getElementById('tasktree-button');
const popupSpan = document.getElementsByClassName('close')[0];

popupButton.onclick = function() {
  popup.style.display = 'block';
};

popupSpan.onclick = function() {
  popup.style.display = 'none';
};

window.onclick = function() {
  if (event.target == popup) {
    popup.style.display = 'none';
  }
};