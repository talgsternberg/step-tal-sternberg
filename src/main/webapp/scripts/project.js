/**
 * This file contains javascript code for the project page
 */

/* PAGE SECTIONS */
const taskSection = document.querySelector('.project-section.tasks');
const usersSection = document.querySelector('.project-section.users');

// Get all tabs
const tabs = document.querySelectorAll('.project-header-tab');
tabs.forEach((clickedTab) => {
  // Add onClick event for each tab
  clickedTab.addEventListener('click', (e) => {
    // Remove the active class from all tabs
    tabs.forEach((tab) => {
      tab.classList.remove('active');
    });
    const clickedClassList = clickedTab.classList;
    // Add the active class on the clicked tab
    clickedClassList.add('active');
    if (clickedClassList.contains('tasks')) {
      showTaskSection();
    }
    if (clickedClassList.contains('users')) {
      showUsersSection();
    }
  });
});

/**
 * Displays the task section
 */
function showTaskSection() {
  hideUsersSection();
  taskSection.style.display = 'block';
}

/**
 * Hides the task section
 */
function hideTaskSection() {
  taskSection.style.display = 'none';
}

/**
 * Displays the users section
 */
function showUsersSection() {
  hideTaskSection();
  usersSection.style.display = 'block';
}

/**
 * Hides the users section
 */
function hideUsersSection() {
  usersSection.style.display = 'none';
}

/**
 * Add event listener to toggle tree.
 */
function treeToggle() {
  const toggler = document.getElementsByClassName('task-tree-node');
  let i;
  for (i = 0; i < toggler.length; i++) {
    toggler[i].addEventListener('click', function() {
      this.parentElement.querySelector('.task-tree').classList.toggle('active');
      this.classList.toggle('task-tree-node-down');
    });
  }
}

/**
 * Pop-up
 */
function popup() {
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
}

/**
 * Body onload function for Project Page.
 */
function initEventListeners() {
  treeToggle();
  popup();
}

/* USER ACTIONS DROP-DOWN */

const projectActions = document.querySelector('.page-header-actions');

/**
 * Displays user actions in header of project page
 */
function showActions() {
  if (projectActions.style.display === 'block') {
    hideActions();
    return;
  }
  projectActions.style.display = 'block';
}

// Close the actions menu when page is clicked
document.addEventListener('click', (event) => {
  if (!document.querySelector('.page-header-actions-selector').
      contains(event.target)) {
    projectActions.style.display = 'none';
  }
});

/**
 * Hides user actions
 */
function hideActions() {
  projectActions.style.display = 'none';
}