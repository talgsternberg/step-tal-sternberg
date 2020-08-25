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
    },
);

// Opens the add-user modal when clicked
document.getElementById('add-user-button').addEventListener(
    'click',
    () => {
      document.getElementById('user-invite-code').value = '';
      addUserModal.style.display = 'flex';
    },
);

/* PAGE FUNCTIONS */

/**
 * Adds a user to the project; called from addUserModal
 *@param {String} projectId id of the project
 */
function addUserToProject(projectId) {
  // Get user input for userName
  const userInviteCode = document.getElementById('user-invite-code').value;

  // If userInviteCode is empty, show error message
  if (userInviteCode === '') {
    addUserModal.style.display = 'none';
    showMessage('Invalid invite code.');
    return;
  }

  // Get user input for userRole
  const userRole = document.getElementById('user-role').value;

  // Generate query string & use it t call the servlet
  const queryString = '/add-user-to-project?project=' + projectId +
  '&userInviteCode=' + userInviteCode + '&userRole=' + userRole;
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
    },
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
const editProjectDetailsModal = document.
    querySelector('.modal.edit-project-details');

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

// Hides the modal when clicked
document.querySelector('.modal-close.edit-project-details').addEventListener(
    'click',
    () => {
      editProjectDetailsModal.style.display = 'none';
    },
);

/**
 * Opens the modal when clicked
 */
function showEditProjectModal() {
  document.getElementById('project-name').value =
  document.getElementById('main-project-name').innerHTML;
  document.getElementById('project-desc').value =
  document.getElementById('main-project-description').innerHTML;
  editProjectDetailsModal.style.display = 'flex';
}

/**
 * Edits the project details
 *@param {String} projectId id of the project
 */
function editProjectDetails(projectId) {
  // Get user inputs
  const projectName = document.getElementById('project-name').value;
  const projectDesc = document.getElementById('project-desc').value;

  if (projectDesc === '' || projectName === '') {
    editProjectDetailsModal.style.display = 'none';
    if (projectName === '' && projectDesc === '') {
      showMessage('Invalid inputs.');
    } else if (projectName === '') {
      showMessage('Invalid name.');
    } else if (projectDesc === '') {
      showMessage('Invalid description.');
    }
    return;
  }

  // Generate query string & use it to call the servlet
  const queryString = '/edit-project-details?project=' + projectId +
  '&projectName=' + projectName + '&projectDesc=' + projectDesc;
  fetch(queryString, {'method': 'POST'}).then((response) => response.json()).
      then((response) => {
        if (response.message === 'Updated project name and description.') {
          updateProjectName(projectName);
          updateProjectDescription(projectDesc);
        } else if (response.message === 'Updated project name.') {
          updateProjectName(projectName);
        } else if (response.message === 'Updated project description.') {
          updateProjectDescription(projectDesc);
        }

        // After call to servlet, show message on page describing outcome
        editProjectDetailsModal.style.display = 'none';
        showMessage(response.message);
      });
}

/**
 * Updates the project name on project page
 *@param {String} newName the new name
 */
function updateProjectName(newName) {
  document.getElementById('main-project-name').innerHTML = newName;
}

/**
 * Updates the project desc on project page
 *@param {String} newDesc the new description
 */
function updateProjectDescription(newDesc) {
  document.getElementById('main-project-description').innerHTML = newDesc;
}

/**
 * Implements basic feature of completing a project.
 * Does not yet fix being able to add tasks or users while a project
 * is marked as incomplete-- might fix later if there's time
 *@param {String} projectId the id of project
 */
function completeProject(projectId) {
  const prompt = document.getElementById('set-project');
  let queryString = '/complete-project?project=' + projectId + '&setComplete=';
  queryString +=
    (prompt.innerHTML === 'Set Project Complete') ? 'true' : 'false';
  fetch(queryString, {'method': 'POST'}).then((response) => response.json()).
      then((response) => {
        if (response.message === 'Project marked as complete.') {
          prompt.innerHTML = 'Set Project Incomplete';
          loadPageElements(/* projectComplete */ true);
        } else if (response.message === 'Project marked as incomplete.') {
          prompt.innerHTML = 'Set Project Complete';
          loadPageElements(/* projectComplete */ false);
        }
        showMessage(response.message);
      });
}

/**
 * Called when a project page loads. Used to handle displaying page elements
 * related to the project's state as complete/incomplete
 *@param {boolean} projectComplete true if the project is complete
 */
function loadPageElements(projectComplete) {
  const addTaskButton = document.querySelector('.deep-button');
  const addUserButton = document.getElementById('add-user-button');
  if (projectComplete === false) {
    addTaskButton.style.display = 'block';
    addUserButton.style.display = 'block';
  } else {
    addTaskButton.style.display = 'none';
    addUserButton.style.display = 'none';
  }
}