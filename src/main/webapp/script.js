/**
 * Redirect to User Profile Page.
  * @param {number} userID If not provided, randomize it from 1 to 3 inclusive.

 */
function goToUser(userID=Math.floor(Math.random()*(3))+1) {
  // TODO: fetch for actual user's page
  const url = 'user_profile.html?userID=' + userID;
  location.href = url;
}

/**
 * Redirect to Task Page.
 * @param {number} taskID If not provided, randomize it from 1 to 3 inclusive.
 */
function goToTask(taskID=Math.floor(Math.random()*(3))+1) {
  const url = 'task.html?taskID=' + taskID;
  location.href = url;
}

// Hard coded tasks that are global variables.
const gTasks =
  {task1: {taskID: 1, projectID: 1, name: 'Task 1',
    description: 'Task 1 description...', status: 'incomplete',
    users: [1, 2], subtasks: [3]},
  task2: {taskID: 2, projectID: 1, name: 'Task 2',
    description: 'Task 2 description...', status: 'complete',
    users: [1, 3], subtasks: []},
  task3: {taskID: 3, projectID: 1, name: 'Task 3',
    description: 'Task 3 description...', status: 'incomplete',
    users: [2, 3], subtasks: []}};
const gJSONtasks = JSON.stringify(gTasks);
const gDefaultTask = {taskID: 0, projectID: 0, name: 'Default Task',
  description: 'Default task description...', status: 'none',
  users: [], subtasks: []};
// TODO: fill gUsers and gJSONusers and gDefaultUser (similar to above).
const gUsers =
  {user1: {userID: 1, name: 'User 1', skills: 'Art, Writing',
    major: 'Studio Art', numTaskCompleted: 5, admin: true},
  user2: {userID: 2, name: 'User 2', skills: 'Object Oriented Programming',
    major: 'Computer Science', numTaskCompleted: 8, admin: false},
  user3: {userID: 3, name: 'User 1', skills: 'Leadership, Organization',
    major: 'Chemistry', numTaskCompleted: 4, admin: false}};

const gJSONusers = JSON.stringify(gUsers);
const gDefaultUser = {userID: 0, name: 'Default Username',
  skills: 'Default Skills', major: 'Default Major',
  numTaskCompleted: 'Default Number', admin: false}; // Add attributes

/**
 * When the Task Page loads, get task info. If no taskID is provided in the URL,
 * default values will be shown.
 */
function getTaskInfo() {
  const params = new URLSearchParams(location.search);
  const taskID = params.get('taskID');
  const tasks = JSON.parse(gJSONtasks);
  for (task in tasks) {
    if (tasks[task].taskID == taskID) {
      const title = document.getElementById('task-title-container');
      title.innerHTML = '<h1>' + tasks[task].name + '</h1>';
      const description = document.getElementById('task-description-container');
      description.innerText = tasks[task].description;
      const status = document.getElementById('task-status-container');
      status.innerText = 'Status: ' + tasks[task].status;
      const subtasks = document.getElementById('task-subtasks-container');
      subtasks.appendChild(getSubtasks(tasks[task].subtasks));
      const users = document.getElementById('task-users-container');
      users.appendChild(getUsers(tasks[task].users));
      break;
    }
  }
}

/**
 * When the User Profile Page loads, get user info.
   If no userID is provided in the URL,
 * default values will be shown.
 */
function getUserInfo() {
  const params = new URLSearchParams(location.search);
  const userID = params.get('userID');
  const users = JSON.parse(gJSONusers);
  for (user in users) {
    if (users[user].userID == userID) {
      const title = document.getElementById('user-name-container');
      title.innerHTML = '<h1>' + users[user].name + '</h1>';
      const major = document.getElementById('major-container');
      major.innerText = 'Major: ' + users[user].major;
      const year = document.getElementById('year-container');
      year.innerText = 'Class Year: ' + users[user].year;
      const numTaskCompleted =
        document.getElementById('num-complete-container');
      numTaskCompleted.innerText =
        'Total Tasks Completed: ' + users[user].numTaskCompleted;
      const skills = document.getElementById('skills-container');
      skills.innerText = users[user].skills;
      break;
    }
  }
}

/**
 * Build ul element for subtasks on Task Page.
 * @param {Array} subtasks Array of taskIDs.
 * @return {Element} HTML ul element containing a list of subtasks.
 */
function getSubtasks(subtasks) {
  const ulElement = document.createElement('ul');
  for (subtaskID of subtasks) {
    let subtask = gDefaultTask;
    for (task in gTasks) {
      if (gTasks[task].taskID == subtaskID) {
        subtask = gTasks[task];
        break;
      }
    }
    ulElement.appendChild(createTaskLiElement(subtask));
  }
  return ulElement;
}

/**
 * Build li element for a task or subtask.
 * @param {Hashmap} task Details of the task.
 * @return {Element} HTML li element containing task button and details.
 */
function createTaskLiElement(task) {
  console.log(task);
  // Create HTML elements
  const liElement = document.createElement('li');
  liElement.setAttribute('class', 'task');
  // button element
  const buttonElement = document.createElement('button');
  buttonElement.setAttribute('type', 'button');
  buttonElement.setAttribute('class', 'inline');
  buttonElement.setAttribute('onclick', 'goToTask(' + task.taskID + ')');
  buttonElement.innerText = task.name;
  liElement.appendChild(buttonElement);
  // p element
  const pElement = document.createElement('p');
  pElement.setAttribute('class', 'inline');
  pElement.innerText = task.description;
  liElement.appendChild(pElement);
  return liElement;
}

/**
 * Build ul element for users on Task Page.
 * @param {Array} users Array of userIDs.
 * @return {Element} HTML ul element containing a list of users.
 */
function getUsers(users) {
  const ulElement = document.createElement('ul');
  for (userID of users) {
    let taskUser = gDefaultUser;
    for (user in gUsers) {
      if (gUsers[user].taskID == userID) {
        taskUser = gUsers[user];
        break;
      }
    }
    ulElement.appendChild(createUserLiElement(taskUser));
  }
  return ulElement;
}

/**
 * Build li element for a user.
 * @param {Hashmap} user Details of the user.
 * @return {Element} HTML li element containing user button.
 */
function createUserLiElement(user) {
  console.log(user);
  // Create HTML elements
  const liElement = document.createElement('li');
  liElement.setAttribute('class', 'inline');
  // button element
  const buttonElement = document.createElement('button');
  buttonElement.setAttribute('type', 'button');
  buttonElement.setAttribute('onclick', 'goToUser()');
  buttonElement.innerText = user.name;
  liElement.appendChild(buttonElement);
  return liElement;
}