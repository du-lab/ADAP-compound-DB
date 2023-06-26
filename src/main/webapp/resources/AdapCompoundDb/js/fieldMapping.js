$(document).ready(function () {
    try {
        const mappingCookie = localStorage.getItem("fieldMapping");
        if (mappingCookie) {
            let mapping = JSON.parse(mappingCookie);
            for (let key in mapping) {
                if (mapping.hasOwnProperty(key)) {
                    let value = mapping[key];
                    if (value) {
                        let dragElement = document.querySelector('[data-inputid="' + key + '"]');
                        const fieldElement = document.querySelector('[data-filetype="' + value + '"]');
                        if (fieldElement && fieldElement.firstChild && dragElement) {
                            while (fieldElement.firstChild) {
                                fieldElement.firstChild.remove();
                            }
                            console.log(fieldElement, dragElement)
                            fieldElement.appendChild(dragElement);
                            $("#" + key).val(value.split("_")[1]);
                            // dragElement.parentNode.removeChild(dragElement);
                        }
                    }
                }
            }
        }
    } catch (e) {
        console.log(e);
        localStorage.removeItem("fieldMapping");
        location.reload();
    }

    $(".draggable").on("dragstart", function (event) {
        let id = event.target.id;
        event.originalEvent.dataTransfer.setData("fileId", id.split("_")[1]);
        event.originalEvent.dataTransfer.setData("typeId", id.split("_")[2]);
        event.originalEvent.dataTransfer.setData("draggableId", id.split("_")[3]);
        event.originalEvent.dataTransfer.setData("text/plain", id);
    });
    $(".left, .right").on("dragover", function (event) {
        event.preventDefault();
    });
    $(".left .droppable").on("drop", function (event) {
        event.preventDefault();
        let data = event.originalEvent.dataTransfer.getData("text/plain");
        let fileId = event.originalEvent.dataTransfer.getData("fileId");
        let droppingIntoId = $(this)[0].id.split("_")[1];
        let element = document.getElementById(data);
        let dataForInput = $(this)[0].getAttribute("data-droppable");
        let existingElement = $(this).children().first();
        if (fileId == droppingIntoId) {
            if (existingElement.length) {
                if (existingElement[0].getAttribute("data-inputid") == "Don't Read") {
                    existingElement.remove();
                } else {
                    $("#" + existingElement[0].getAttribute("data-inputid")).val("");
                    $(".right_" + existingElement[0].id.split("_")[1])
                        .find(".field_type_" + existingElement[0].id.split("_")[2]).append(existingElement);
                    saveToLocalStorage(existingElement[0].getAttribute("data-inputid"), "");
                }
            } else {
                $(this).append(createDontReadDiv());
            }
            if (element.innerText != "Don't Read") {
                $(this).append(element);
                addDoubleClickEventListener(element);
                $('#' + element.getAttribute("data-inputid")).val(dataForInput);
                saveToLocalStorage(element.getAttribute("data-inputid"), $(this)[0].getAttribute("data-filetype"));
            } else {
                let clonedElement = $(element).clone();
                clonedElement[0].id = "draggable_" + fileId + "_-1_-2";
                clonedElement.on("dragstart", function (event) {
                    let id = event.target.id;
                    event.originalEvent.dataTransfer.setData("fileId", id.split("_")[1]);
                    event.originalEvent.dataTransfer.setData("typeId", id.split("_")[2]);
                    event.originalEvent.dataTransfer.setData("draggableId", id.split("_")[3]);
                    event.originalEvent.dataTransfer.setData("text/plain", id);
                });
                clonedElement.innerText = "Don't Read";
                $(this).append(clonedElement);
                addDoubleClickEventListener(clonedElement);
            }
            resetDroppable(fileId);
        }

        function addDoubleClickEventListener(element) {
            $(element).on("dblclick", function () {
                if (element.innerText == "Don't Read") {
                    $(this).remove();
                } else {
                    let parentContainer = $(this).closest(".field-container_" + element.id.split("_")[1]);
                    $(this).detach();
                    $(this).unbind("dblclick")
                    $(this).find("input").val("")
                    resetDroppable(element.id.split("_")[1]);
                    parentContainer.find(".right_" + element.id.split("_")[1])
                        .find(".field_type_" + element.id.split("_")[2]).append($(this));
                }
            });
        }
    });

    function saveToLocalStorage(key, value) {
        let jsonData = JSON.parse(localStorage.getItem("fieldMapping"));
        if (jsonData == null) {
            jsonData = {};
        }
        jsonData[key] = value;
        localStorage.setItem("fieldMapping", JSON.stringify(jsonData));
    }

    function resetDroppable(id) {
        $(".droppable_" + id).each(function () {
            if ($(this).children().length == 0) {
                $(this).append(createDontReadDiv(id));
            }
        });
    }

    $(".right").on("drop", function (event) {
        event.preventDefault();
        let droppingIntoId = $(this)[0].id.split("_")[1];
        let data = event.originalEvent.dataTransfer.getData("text/plain");
        let element = document.getElementById(data);
        let fileId = element.id.split("_")[1];
        let typeId = element.id.split("_")[2];
        let draggableId = element.id.split("_")[3];
        if (fileId == droppingIntoId) {
            if (element.innerText == "Don't Read" && draggableId != -1) {
                element.remove();
            } else {
                $(".right_" + fileId).find(".field_type_" + typeId).append(element);
                $("#" + element.getAttribute("data-inputid")).val("");
            }
        }
        saveToLocalStorage(element.getAttribute("data-inputid"), "");
        resetDroppable(fileId);
    });

    function createDontReadDiv(id) {
        let div = document.createElement("div");
        div.id = "draggable_" + id + "_-1_-2";
        div.className = "draggable bg-secondary";
        div.draggable = false;
        div.setAttribute("data-inputid", "Don't Read");
        div.textContent = "Don't Read";
        return div;
    }

    $("#resetMetadataButton").on("click", function (event) {
        event.preventDefault(); // Prevent form submission
        resetMetadataForm();
    });
    function resetMetadataForm() {
        localStorage.removeItem("fieldMapping");
        location.reload();
    }

});
