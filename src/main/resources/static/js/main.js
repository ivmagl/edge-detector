'use strict';

var uploadForm = document.querySelector('#uploadForm');
var fileUploadInput = document.querySelector('#fileUploadInput');
var fileUploadError = document.querySelector('#fileUploadError');
var fileUploadSuccess = document.querySelector('#fileUploadSuccess');

function uploadFile(file) {
    var formData = new FormData();
    formData.append("file", file);

    var xhr = new XMLHttpRequest();
    xhr.open("POST", "/upload");

    xhr.onload = function() {
        console.log(xhr.responseText);
        var response = JSON.parse(xhr.responseText);
        if(xhr.status == 200) {
            fileUploadError.style.display = "none";
            fileUploadSuccess.innerHTML = "<p>File Uploaded Successfully.</p><p>Download Url : <a href='" + response.fileDownloadUri + "' target='_blank'>" + response.fileDownloadUri + "</a></p>";
            fileUploadSuccess.style.display = "block";
        } else {
            fileUploadSuccess.style.display = "none";
            fileUploadError.innerHTML = (response && response.message) || "Some Error Occurred";
			fileUploadError.style.display = "block";
        }
    };

    xhr.send(formData);
}

uploadForm.addEventListener('submit', function(event){
    var files = fileUploadInput.files;
    if(files.length === 0) {
        fileUploadError.innerHTML = "Please select a file";
        fileUploadError.style.display = "block";
    }
    uploadFile(files[0]);
    event.preventDefault();
}, true);

