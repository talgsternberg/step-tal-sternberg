/**
 * Redirect to User Profile Page.
 */
function goToUser() {
  // TODO: fetch for actual user's page
  const url = 'user_profile.html';
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
console.log(gTasks);

// TODO: fill gUsers and gJSONusers and gDefaultUser (similar to above).
const gUsers = {};
const gJSONusers = {};
const gDefaultUser = {userID: 0, name: 'Default Username'}; // Add attributes

/**
 * When the Task Page loads, get task info. If no taskID is provided in the URL,
 * default values will be shown.
 */
function getTaskInfo() {
  const params = new URLSearchParams(location.search);
  const taskID = params.get('taskID');
  const tasks = JSON.parse(gJSONtasks);
  console.log(tasks);
  for (task in tasks) {
    if (tasks[task].taskID == taskID) {
      const title = document.getElementById('task-title-container');
      title.innerHTML = '<h1>' + tasks[task].name + '</h1>';
      const description = document.getElementById('task-description-container');
      description.innerText = tasks[task].description;
      const status = document.getElementById('task-status-container');
      status.innerText = 'Status: ' + tasks[task].status;
      const subtasks = document.getElementById('task-subtasks-container');
      if (tasks[task].subtasks.length != 0) {
        console.log(tasks[task].subtasks);
        const ulSubtaskElement = document.createElement('ul');
        for (subtaskID of tasks[task].subtasks) {
          // Get subtask info
          let subtask = gDefaultTask;
          for (task in gTasks) {
            if (gTasks[task].taskID == subtaskID) {
              subtask = gTasks[task];
              break;
            }
          }
          ulSubtaskElement.appendChild(createTaskLiElement(subtask));
        }
        subtasks.appendChild(ulSubtaskElement);
      }
      const users = document.getElementById('task-users-container');
      if (tasks[task].users.length != 0) {
        console.log(tasks[task].users);
        const ulUserElement = document.createElement('ul');
        for (userID of tasks[task].users) {
          // Get user info
          let taskUser = gDefaultUser;
          for (user in gUsers) {
            if (gUsers[user].taskID == userID) {
              taskUser = gUsers[user];
              break;
            }
          }
          ulUserElement.appendChild(createUserLiElement(taskUser));
        }
        users.appendChild(ulUserElement);
      } 
      break;
    }
  }
}

/**
 * Build li element for a task or subtask.
 * @param {Hashmap} task Details of the task.
 * @returns {Element} HTML li element containing task button and details.
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
 * Build li element for a task or subtask.
 * @param {Hashmap} user Details of the user.
 * @returns {Element} HTML li element containing user button.
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
