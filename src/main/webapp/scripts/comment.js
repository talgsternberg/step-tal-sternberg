/**
 * Converts comment into editable comment form.
 * @param {number} commentID for hidden input.
 * @param {number} taskID for hidden input.
 */
function editComment(commentID, taskID) {
  // Get DOM elements
  const commentContainer = document.getElementById('comment-container');
  const titleContainer = document.getElementById('comment-title-container');
  const messageContainer = document.getElementById('comment-message-container');
  const infoContainer = document.getElementById('comment-postinfo-container');
  const editContainer = document.getElementById('edit-comment-container');
  const deleteContainer = document.getElementById('delete-comment-container');

  // Get attributes
  const title = titleContainer.getElementsByTagName("h3")[0].innerText;
  const message = messageContainer.getElementsByTagName("p")[0].innerText;

  // Clear DOM elements
  titleContainer.innerHTML = '';
  messageContainer.innerHTML = '';
  editContainer.innerHTML = '';
  deleteContainer.innerHTML = '';

  // Convert comment into a form
  postForm = document.createElement('form');
  postForm.setAttribute('id', 'edit-comment-post-form');
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