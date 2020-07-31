import {
    goToHub as importGoToHub,
    goToSettings as importGoToSettings,
    goToUser as importGoToUser,
    goToProject as importGoToProject,
    goToTask as importGoToTask} from './script.js';

window.goToHub = function goToHub() {
  importGoToHub();
}
window.goToSettings = function goToSettings() {
  importGoToSettings();
}
window.goToUser = function goToUser() {
  importGoToUser();
}
window.goToProject = function goToProject() {
  importGoToProject();
}
window.goToTask = function goToTask() {
  importGoToTask();
}


/**
 * When the Task Page loads, get task info. If no taskID is provided in the URL,
 * default values will be shown.
 */
window.getTaskInfo = function getTaskInfo() {
  // Can get taskID from param, or maybe just get it from the button that was
  // pressed to arrive on task page.
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
        // const subtasks = response.subtask;
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
        const userList = document.getElementById('task-users-container');
        // userList.appendChild(getUsers(users));
        // const commentList =
        //   document.getElementById('task-comments-container');
        // commentList.appendChild(getComments(comments));
      });
}