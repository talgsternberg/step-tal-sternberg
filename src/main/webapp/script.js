/**
 * Redirect to User Profile Page.
 */
function goToUser() {
  // TODO: fetch for actual user's page
  const url = 'user_profile.html';
  location.href = url;
}

/**
 * Redirect to Task Page. If no taskID is provided, randomize it from 1 to 3 inclusive.
 */
function goToTask(taskID=Math.floor(Math.random()*(3))+1) {
  const url = 'task.html?taskID=' + taskID;
  location.href = url;
}

// Hard coded tasks that are global variables.
const g_tasks = {task1:{taskID:1, projectID:1, name:"Task 1", description:"This is task 1", status:"incomplete"},
                task2:{taskID:2, projectID:1, name:"Task 2", description:"This is task 2", status:"complete"},
                task3:{taskID:3, projectID:1, name:"Task 3", description:"This is task 3", status:"incomplete"}};
const g_JSONtasks = JSON.stringify(g_tasks);

/**
 * When the Task Page loads, get task info. If no taskID is provided in the URL,
 * default values will be shown.
 */
function getTaskInfo() {
  const params = new URLSearchParams(location.search);
  let taskID = params.get("taskID");
  let tasks = JSON.parse(g_JSONtasks);
  for (task in tasks){
    if (tasks[task].taskID == taskID){
      let title = document.getElementById("task-title-container");
      title.innerHTML = "<h1>" + tasks[task].name + "</h1>";
      let description = document.getElementById("task-description-container");
      description.innerText = tasks[task].description;
      let status = document.getElementById("task-status-container");
      status.innerText = "Status: " + tasks[task].status;
      break;
    }
  }
}
