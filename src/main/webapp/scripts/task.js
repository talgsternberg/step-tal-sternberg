/**
 * Converts description into editable description form.
 * @param {number} taskID for hidden input.
 */
function editDescription(taskID) {
  // Get DOM elements
  const descFormContainer =
      document.getElementById('task-description-container');
  const descContainer = document.getElementById('task-description');
  const editContainer = document.getElementById('edit-description-container');

  // Get attributes
  const desc = descContainer.innerText;

  // Clear DOM elements
  descFormContainer.innerHTML = '';
  descContainer.innerHTML = '';
  editContainer.innerHTML = '';

  // Convert description into a form
  postForm = document.createElement('form');
  postForm.setAttribute('id', 'edit-description-post-form');
  postForm.setAttribute('action', '/task-edit');
  postForm.setAttribute('method', 'POST');
  descFormContainer.appendChild(postForm);

  // Create hidden taskID input
  const taskIDInput = document.createElement('input');
  taskIDInput.setAttribute('type', 'hidden');
  taskIDInput.setAttribute('id', 'edit-comment-task-input');
  taskIDInput.setAttribute('name', 'taskID');
  taskIDInput.setAttribute('value', taskID);
  postForm.appendChild(taskIDInput);

  // Add heading to the top of the comment
  heading = document.createElement('h3');
  heading.innerText = 'Edit Description';
  postForm.appendChild(heading);

  // Fill description element with previous description as input
  descInput = document.createElement('textarea');
  descInput.setAttribute('type', 'text');
  descInput.setAttribute('name', 'description');
  descInput.setAttribute('required', 'true');
  descInput.innerText = desc;
  descContainer.appendChild(descInput);
  postForm.appendChild(descContainer);

  // Fill edit container with Post Edit button
  editContainer.setAttribute('class', 'inline');
  postButton = document.createElement('button');
  postButton.setAttribute('type', 'submit');
  postButton.setAttribute('class', 'deep-button');
  postButton.innerText = 'Post';
  editContainer.appendChild(postButton);
  postForm.appendChild(editContainer);

  // Create reset container with Reset Edit button
  // Might be a Discard Changes button instead in the future
  resetContainer = document.createElement('div');
  resetContainer.setAttribute('id', 'reset-description-container');
  resetContainer.setAttribute('class', 'inline');
  resetButton = document.createElement('button');
  resetButton.setAttribute('type', 'reset');
  resetButton.setAttribute('class', 'deep-button');
  resetButton.innerText = 'Reset';
  resetContainer.appendChild(resetButton);
  postForm.appendChild(resetContainer);
}

/**
 * Converts comment into editable comment form.
 * @param {number} commentID for hidden input.
 * @param {number} taskID for hidden input.
 */
function editComment(commentID, taskID) {
  // Get DOM elements
  const commentContainer =
      document.getElementById('comment-container-' + commentID);
  const titleContainer =
      document.getElementById('comment-title-container-' + commentID);
  const messageContainer =
      document.getElementById('comment-message-container-' + commentID);
  const infoContainer =
      document.getElementById('comment-postinfo-container-' + commentID);
  const editContainer =
      document.getElementById('edit-comment-container-' + commentID);
  const deleteContainer =
      document.getElementById('delete-comment-container-' + commentID);

  // Get attributes
  const title = titleContainer.innerText;
  const message = messageContainer.innerText;

  // Clear DOM elements
  titleContainer.innerHTML = '';
  messageContainer.innerHTML = '';
  editContainer.innerHTML = '';
  deleteContainer.innerHTML = '';

  // Convert comment into a form
  postForm = document.createElement('form');
  postForm.setAttribute('id', 'edit-comment-post-form-' + commentID);
  postForm.setAttribute('action', '/comment-edit');
  postForm.setAttribute('method', 'POST');
  commentContainer.appendChild(postForm);

  // Create hidden taskID input
  const taskIDInput = document.createElement('input');
  taskIDInput.setAttribute('type', 'hidden');
  taskIDInput.setAttribute('id', 'edit-comment-task-input');
  taskIDInput.setAttribute('name', 'taskID');
  taskIDInput.setAttribute('value', taskID);
  postForm.appendChild(taskIDInput);

  // Create hidden commentID input
  const commentIDInput = document.createElement('input');
  commentIDInput.setAttribute('type', 'hidden');
  commentIDInput.setAttribute('id', 'edit-comment-comment-input');
  commentIDInput.setAttribute('name', 'commentID');
  commentIDInput.setAttribute('value', commentID);
  postForm.appendChild(commentIDInput);

  // Add heading to the top of the comment
  heading = document.createElement('h3');
  heading.innerText = 'Edit Your Comment';
  postForm.appendChild(heading);

  // Fill title element with previous title as input
  titleInput = document.createElement('input');
  titleInput.setAttribute('type', 'text');
  titleInput.setAttribute('name', 'title');
  titleInput.setAttribute('required', 'true');
  titleInput.setAttribute('value', title);
  titleInput.setAttribute('maxlength', '40');
  titleContainer.appendChild(titleInput);
  postForm.appendChild(titleContainer);

  // Reuse post info
  postForm.appendChild(infoContainer);

  // Fill message element with previous message as textarea input
  messageInput = document.createElement('textarea');
  messageInput.setAttribute('type', 'text');
  messageInput.setAttribute('name', 'message');
  messageInput.setAttribute('required', 'true');
  messageInput.innerText = message;
  messageContainer.appendChild(messageInput);
  postForm.appendChild(messageContainer);

  // Fill edit container with Post Edit button
  postButton = document.createElement('button');
  postButton.setAttribute('type', 'submit');
  postButton.setAttribute('class', 'inline deep-button');
  postButton.innerText = 'Post';
  editContainer.appendChild(postButton);
  postForm.appendChild(editContainer);

  // Fill delete container with Reset Edit button
  // Might be a Discard Changes button instead in the future
  resetButton = document.createElement('button');
  resetButton.setAttribute('type', 'reset');
  resetButton.setAttribute('class', 'inline deep-button');
  resetButton.innerText = 'Reset';
  deleteContainer.appendChild(resetButton);
  postForm.appendChild(deleteContainer);
}