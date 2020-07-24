/**
 * Redirect to Main Hub Page.
 */
function goToHub() {
  const url = 'main_hub.html';
  location.href = url;
}

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
 * Redirect to Project Page.
 * @param {number} projectID If not provided, randomize it from 1-3 inclusive.
 */
function goToProject(projectID=Math.floor(Math.random()*2)+1) {
  const url = 'project.html?projectID=' + projectID;
  location.href = url;
}

/**
 * Redirect to Task Page.
 * @param {number} taskID If not provided, randomize it from 1-7 inclusive.
 */
function goToTask(taskID=Math.floor(Math.random()*7)+1) {
  const url = 'task.html?taskID=' + taskID;
  location.href = url;
}

// Hard coded projects that are global variables.
const gProjects =
  {project1: {projectID: 1, name: 'Project 1',
    description: 'Project 1 description...', admins: [1],
    tasks: [1, 2], allowedUsers: [1, 2]},
  project2: {projectID: 2, name: 'Project 2',
    description: 'Project 2 description...', admins: [2],
    tasks: [4, 5], allowedUsers: [1, 2, 3]}};
const gJSONprojects = JSON.stringify(gProjects);
const gDefaultProject = {projectID: 0, name: 'Default Project',
  description: 'Default project description...', admins: [],
  tasks: [], allowedUsers: []};

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
    users: [2], subtasks: []},
  task4: {taskID: 4, projectID: 2, name: 'Task 4',
    description: 'Task 4 description...', status: 'incomplete',
    users: [2, 3], subtasks: [6, 7]},
  task5: {taskID: 5, projectID: 2, name: 'Task 5',
    description: 'Task 5 description...', status: 'complete',
    users: [1], subtasks: []},
  task6: {taskID: 6, projectID: 2, name: 'Task 6',
    description: 'Task 6 description...', status: 'complete',
    users: [3], subtasks: []},
  task7: {taskID: 7, projectID: 2, name: 'Task 7',
    description: 'Task 7 description...', status: 'incomplete',
    users: [2], subtasks: []}};
const gJSONtasks = JSON.stringify(gTasks);
const gDefaultTask = {taskID: 0, projectID: 0, name: 'Default Task',
  description: 'Default task description...', status: 'none',
  users: [], subtasks: []};
// TODO: fill gUsers and gJSONusers and gDefaultUser (similar to above).
const gUsers =
  {user1: {userID: 1, name: 'User 1', skills: [{skill: 'Art', priority: true},
    {skill: 'Writing', priority: true}], major: ['Studio Art'],
  numTaskCompleted: 5, year: 2021},
  user2: {userID: 2, name: 'User 2', skills:
    [{skill: 'Object Oriented Programming',
      priority: true}], major: ['Computer Science'],
  numTaskCompleted: 8, year: 2023},
  user3: {userID: 3, name: 'User 1', skills:
    [{skill: 'Leadership', priority: true},
      {skill: 'Organization', priority: false}],
  major: ['Chemistry', 'English'], numTaskCompleted: 4, year: 2024}};

const gJSONusers = JSON.stringify(gUsers);
const gDefaultUser = {userID: 0, name: 'Default Username',
  skills: 'Default Skills', major: 'Default Major',
  numTaskCompleted: 'Default Number'}; // Add attributes
/**
 * When the Project Page loads, get project info. If no projectID is provided in
 * the URL, default values will be shown.
 */
function getProjectInfo() {
  const params = new URLSearchParams(location.search);
  const projectID = params.get('projectID');
  const projects = JSON.parse(gJSONprojects);
  for (project in projects) {
    if (projects[project].projectID == projectID) {
      const title = document.getElementById('project-title-container');
      title.innerHTML = '<h1>' + projects[project].name + '</h1>';
      const description =
        document.getElementById('project-description-container');
      description.innerText = projects[project].description;
      const admins = document.getElementById('project-admins-container');
      admins.appendChild(getUsers(projects[project].admins));
      const tasks = document.getElementById('project-tasks-container');
      tasks.appendChild(getTasks(projects[project].tasks));
      const users = document.getElementById('project-users-container');
      users.appendChild(getUsers(projects[project].allowedUsers));
      break;
    }
  }
}
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
      const project = document.getElementById('task-project-container');
      project.appendChild(getProjectReturn(tasks[task]));
      const subtasks = document.getElementById('task-subtasks-container');
      subtasks.appendChild(getTasks(tasks[task].subtasks));
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
      const major = document.getElementById('user-major-container');
      major.innerText = 'Major: ' + users[user].major;
      const year = document.getElementById('user-year-container');
      year.innerText = 'Class Year: ' + users[user].year;
      const numTaskCompleted =
        document.getElementById('user-num-complete-container');
      numTaskCompleted.innerText =
        'Total Tasks Completed: ' + users[user].numTaskCompleted;
      const prioritySkills =
        document.getElementById('user-prskills-container');
      pskills = '';
      for (skill of users[user].skills) {
        if (skill.priority == true) {
          if (pskills == '') {
            pskills = skill.skill;
          }
          else {
            pskills = (pskills + ', ' + skill.skill);
          }
        }
      }
      prioritySkills.innerText = 'Priority Skills: ' + pskills;
      const skills = document.getElementById('user-skills-container');
      skillString = '';
      for (skill of users[user].skills) {
        if (skillString == '') {
          skillString = skill.skill;
         }
        else {
          skillString = skillString + ', ' + skill.skill;
         }
      }
      skills.innerText = skillString;
      break;
    }
  }
}

/**
 * Build ul element for subtasks on Task Page.
 *@ param {Array} subtasks Array of taskIDs.
 * @return {Element} HTML ul element containing a list of subtasks.
 * Build return to project button.
 * @param {Hashmap} task Array of taskIDs.
 * @return {Element} HTML ul element containing a list of tasks.
 */
function getProjectReturn(task) {
  const pElement = document.createElement('p');
  for (project in gProjects) {
    if (gProjects[project].projectID == task.projectID) {
      pElement.innerText = task.name + ' is part of ' + gProjects[project].name;
      pElement.innerHTML += '<br>';
      const buttonElement = document.createElement('button');
      buttonElement.setAttribute('type', 'button');
      buttonElement.setAttribute(
          'onclick', 'goToProject(' + task.projectID + ')');
      buttonElement.innerText = 'Go to ' + gProjects[project].name;
      pElement.appendChild(buttonElement);
    }
  }
  return pElement;
}

/**
 * Build ul element for tasks.
 * @param {Array} tasks Array of taskIDs.
 * @return {Element} HTML ul element containing a list of tasks.
>>>>>>> b4f09dae8dfcd261e6c6d77936c559e97132651d
 */
function getTasks(tasks) {
  const ulElement = document.createElement('ul');
  for (taskID of tasks) {
    let actualTask = gDefaultTask;
    for (task in gTasks) {
      if (gTasks[task].taskID == taskID) {
        actualTask = gTasks[task];
        break;
      }
    }
    ulElement.appendChild(createTaskLiElement(actualTask));
  }
  return ulElement;
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