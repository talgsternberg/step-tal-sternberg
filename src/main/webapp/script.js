/**
 * Redirect to User Profile Page
 */
function goToUser() {
  // TODO: fetch for actual user's page
  const url = 'user_profile.html';
  location.href = url;
}

const g_tasks = {task1:{taskID:1, projectID:1, name:"Task 1", description:"This is task 1", status:"incomplete"},
                task2:{taskID:2, projectID:1, name:"Task 2", description:"This is task 2", status:"complete"},
                task3:{taskID:3, projectID:1, name:"Task 3", description:"This is task 3", status:"incomplete"}};
const g_JSONtasks = JSON.stringify(g_tasks);
console.log(g_tasks);
console.log(g_JSONtasks);

/**
 * Redirect to Task Page
 */
function goToTask() {
  const taskID = Math.floor(Math.random() * (4 - 1) ) + 1;
  const url = 'task.html?taskID=' + taskID;
  location.href = url;
//   let taskID = Math.floor(Math.random() * (4 - 1) ) + 1;
//   console.log(taskID);
  //document.body.onload = getTaskInfo(1);
}

function getTaskInfo() {
  //let taskID = 1;
  const params = new URLSearchParams(location.search);
  let taskID = params.get("taskID");
  console.log(taskID);
  let tasks = JSON.parse(g_JSONtasks);
  // TODO: fetch for actual task's page
  for (task in tasks){
    console.log(task);
    console.log(tasks[task].description);
    if (tasks[task].taskID == taskID){
      console.log("Entered if statement")
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
