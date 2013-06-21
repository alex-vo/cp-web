
function uploadFile(fileInput) {
    $.ajax({
        type: 'POST',
        contentType: 'multipart/form-data, boundary=xxxxxxxxxxx',
        url: 'uploadFiles',
        data: $(fileInput).val()
    });
}