function handleFileSelection(fileInput) {
    const files = Array.from(fileInput.files);
    const selectedExtensions = [];
    const allowedExtensions = ["msp", "msl", "csv", "cdf", "mzml", "mzxml", "mgf"];

    const fileErrorMessage = document.getElementById("fileErrorMessage");
    fileErrorMessage.textContent = "";

    for (let i = 0; i < files.length; i++) {
        const file = files[i];
        const fileExtension = file.name.split(".").pop();
        if (!allowedExtensions.includes(fileExtension)) {
            fileErrorMessage.textContent = "Invalid file type: " + file.name;
            fileInput.value = "";
            return;
        }
        if (!selectedExtensions.includes(fileExtension)) {
            selectedExtensions.push(fileExtension);
        } else {
            fileErrorMessage.textContent = "Only one file of each kind is allowed.";
            fileInput.value = "";
            return;
        }
    }
}