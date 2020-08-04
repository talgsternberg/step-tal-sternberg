/**
 * When the Task Page loads, fetch from server to get task info. If no taskID
 * is provided in the URL, default values will be shown.
 */
function getTaskInfo() {
  // Can get taskID from param, or maybe just get it from the button that was
  // pressed to arrive on task page (if we keep onclick() functions instead of
  // <a href=''> route).
  const params = new URLSearchParams(location.search);
  const taskID = params.get('taskID');
  // const tasks = JSON.parse(gJSONtasks);
  // Create variables to create the URL
  console.log('/task?taskID=' + taskID);
  fetch('/task?taskID=' + taskID)
      .then((response) => response.json())
      .then((response) => {
        const task = response.task[0];
        // const project = response.project[0];
        // const subtasks = response.subtasks;
        // const users = response.users;
        // const comments = response.comments;
        // Fill up task page
        const title = document.getElementById('task-title-container');
        title.innerHTML = '<h1>' + task.name + '</h1>';
        const description =
          document.getElementById('task-description-container');
        description.innerText = task.description;
        const status = document.getElementById('task-status-container');
        status.innerText = 'Status: ' + task.status;
        const toProject = document.getElementById('task-project-container');
        // toProject.appendChild(getProjectReturn(project));
        const subtaskList = document.getElementById('task-subtasks-container');
        // subtaskList.appendChild(getTasks(subtasks));
        const addSubtask = document.getElementById('task-addsubtask-container');
        // addSubtask.appendChild(getCreateTaskButton(
        //     project.projectID,
        //     project.projectName,
        //     task.taskID,
        //     task.name));
        addSubtask.appendChild(createTaskButton(1, 'Project Name 1'));
        const userList = document.getElementById('task-users-container');
        // userList.appendChild(getUsers(users));
        // const commentList =
        //   document.getElementById('task-comments-container');
        // commentList.appendChild(getComments(comments));
        doStuff(); // Test calling a function in another file
      });
}

/**
 * Build create task button.
 * @param {number} projectID
 * @param {String} projectName
 * @param {number} taskID If not provided, set to 0.
 * @param {String} taskName If not provided, set to 'null'.
 * @return {Element} HTML button element containing goToAddTask() function.
 */
function createTaskButton(projectID, projectName, taskID=0, taskName='null') {
  buttonElement = document.createElement('button');
  buttonElement.setAttribute('type', 'button');
  buttonElement.setAttribute('onclick', 'goToAddTask(' +
      projectID + ', \'' +
      projectName + '\', ' +
      taskID + ', \'' +
      taskName + '\')');
  if (taskID === 0) {
    buttonElement.innerText = 'Add Task';
  } else {
    buttonElement.innerText = 'Add Subtask';
  }
  console.log(buttonElement);
  return buttonElement;
}

/**
 * Redirect to Add Task Page.
 * @param {number} projectID
 * @param {String} projectName
 * @param {number} taskID If not provided, set to 0.
 * @param {String} taskName If not provided, set to 'null'.
 */
function goToAddTask(projectID, projectName, taskID=0, taskName='null') {
  const url =
      'add_task.html?projectID=' + projectID +
      '&projectName=' + projectName +
      '&taskID=' + taskID +
      '&taskName=' + taskName;
  console.log(url);
  location.href = url;
}

/**
 * Build Add Task Page
 */
function getAddTaskInfo() {
  const params = new URLSearchParams(location.search);
  const projectID = params.get('projectID');
  const projectName = params.get('projectName');
  const taskID = params.get('taskID');
  const taskName = params.get('taskName');
  const parentProject = document.getElementById('addtask-project-container');
  parentProject.innerText = 'This task will be under project: ' + projectName;
  const parentTask = document.getElementById('addtask-task-container');
  if (taskID != 0) {
    parentTask.innerText = 'This task will be under task: ' + taskName;
  } else {
    parentTask.innerText = '';
  }
  const inputProjectID = document.getElementById('projectID');
  inputProjectID.setAttribute('value', projectID);
}