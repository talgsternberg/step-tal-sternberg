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

/* PAGE MODALS */

const addUserModal = document.querySelector('.modal.add-user-to-project');
const messageModal = document.querySelector('.modal.message');
const messageModalMessage = document.getElementById('message-modal-message');

// Hides the add-user modal when clicked
document.querySelector('.modal-close.add-user-to-project').addEventListener(
'click',
    () => {
      addUserModal.style.display = 'none';
    }
);

// Opens the add-user modal when clicked
document.getElementById('add-user-button').addEventListener(
'click',
    () => {
      document.getElementById('user-email').value = '';
      addUserModal.style.display = 'flex';
    }
);

/* PAGE FUNCTIONS */

/**
 * Adds a user to the project; called from addUserModal
 *@param {String} projectId id of the project
 */
function addUserToProject(projectId) {
  // Get user input for userName
  const userEmail = document.getElementById('user-email').value;

  // If userEmail is empty, show error message
  if (userEmail === '') {
    addUserModal.style.display = 'none';
    showMessage('Invalid email');
    return;
  }

  // Get user input for userRole
  const userRole = document.getElementById('user-role').value;

  // Generate query string & use it t call the servlet
  const queryString = '/add-user-to-project?project=' + projectId +
  '&userEmail=' + userEmail + '&userRole=' + userRole;
  fetch(queryString, {'method': 'POST'}).then((response) => response.json()).
  then((response) => {
    if (response.hasOwnProperty('userId') &&
    response.hasOwnProperty('userName')) {
      addUserToProjectPage(response.userName, userRole, response.userId);
    }

    // After call to servlet, show message on page describing outcome
    addUserModal.style.display = 'none';
    showMessage(response.message);
  });
}

/**
 * Adds the user to the page so that a page reload isn't necessary
 *@param {String} userName name of the user
 *@param {String} userRole role of the user
 *@param {String} userId id of user to generate link to their page
 */
function addUserToProjectPage(userName, userRole, userId) {
  const a = document.createElement('a');
  const p = document.createElement('p');
  p.innerHTML = userRole + ': ' + userName;
  a.appendChild(p);
  a.href = '/user-profile?userID=' + userId;
  usersSection.appendChild(a);
}

/**
 * Displays the message modal
 *@param {String} message the message to display
 */
function showMessage(message) {
  messageModalMessage.innerHTML = message;
  messageModal.style.display = 'flex';
}

// Closes the message modal
document.getElementById('message-modal-close').addEventListener(
'click',
    () => {
      messageModal.style.display = 'none';
    }
);

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
const projectDescription = document.
querySelector('.page-header-description');

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
 * Toggles the state of the description text of a project to shown or hidden
 */
function toggleDescription() {
  if (projectDescription.style.display === 'none' ||
  projectDescription.style.display === '') {
    projectDescription.style.display = 'block';
  } else {
    projectDescription.style.display = 'none';
  }
}

/**
 * Hides user actions
 */
function hideActions() {
  projectActions.style.display = 'none';
}

