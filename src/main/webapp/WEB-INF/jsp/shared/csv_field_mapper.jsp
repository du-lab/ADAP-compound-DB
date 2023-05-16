<%@ page import="java.util.List" %>
<%@ page import="java.util.ArrayList" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<%
    List<String> leftFields = new ArrayList<>();
    List<String> rightFields = new ArrayList<>();
    String[] leftFieldsArray = request.getParameterValues("leftFields");
    String[] rightFieldsArray = request.getParameterValues("rightFields");
    if (leftFieldsArray != null) {
        for (String field : leftFieldsArray) {
            leftFields.add(field);
        }
    }
    if (rightFieldsArray != null) {
        for (String field : rightFieldsArray) {
            rightFields.add(field);
        }
    }
%>
<script src="https://code.jquery.com/jquery-3.6.0.min.js"></script>
<style>
    .field-container {
        display: flex;
        justify-content: space-between;
        margin: 20px;
        user-select: none;
    }
    .left {
        width: 55%;
        padding: 10px;
    }
    .right {
        width:45%;
        padding: 10px;
        display: flex;
        flex-wrap: wrap;
    }
    .draggable {
        cursor: pointer;
        margin: 5px;
        padding: 5px 10px;
        border-radius: 5px;
        min-width: 150px;
        box-shadow: rgba(0, 0, 0, 0.02) 0px 1px 3px 0px, rgba(27, 31, 35, 0.15) 0px 0px 0px 1px;
    }
    .drop-container {
        border: 1px solid #dadada;
        display: flex;
        margin-bottom: 10px;
        justify-content: space-between;
        align-items: center;
        padding: 5px;
    }
    .droppable {
        min-height: 50px;
        text-decoration: none;
        align-items: center;
        background: none;
        display: flex;
        padding-left: 15px;
        /*justify-content: center;*/
    }
</style>
<script>
    $(document).ready(function() {
        $(".draggable").on("dragstart", function(event) {
            console.log("dragstart")
            event.originalEvent.dataTransfer.setData("text/plain", event.target.id);
        });

        $(".left, .right").on("dragover", function(event) {
            event.preventDefault();
        });

        $(".left .droppable").on("drop", function(event) {
            event.preventDefault();
            console.log("dropped")
            var data = event.originalEvent.dataTransfer.getData("text/plain");
            var element = document.getElementById(data);
            var existingElement = $(this).children().first();
            console.log(element.id)
            if (existingElement.length) {
                if (existingElement.innerText == "Don't Read") {
                    existingElement.remove();
                } else {
                    $(".right").append(existingElement);
                }
            }
            if (element.id !== "draggabledontread") {
                $(this).append(element);
                addDoubleClickEventListener(element);
            } else {
                console.log(element)
                var clonedElement = $(element).clone();
                clonedElement.id="draggabledontread";
                clonedElement.innerText = "Don't Read";
                $(this).append(clonedElement);
                addDoubleClickEventListener(clonedElement);
            }
        });

        $(".right").on("drop", function(event) {
            event.preventDefault();
            var data = event.originalEvent.dataTransfer.getData("text/plain");
            var element = document.getElementById(data);
            console.log(element)
            if (element.innerText == "Don't Read") {
                element.remove();
            } else {
                $(".right").append(element);
            }

        });

        function addDoubleClickEventListener(element) {
            $(element).on("dblclick", function() {
                console.log(element.innerText)
                if (element.innerText == "Don't Read") {
                    $(this).remove();
                } else {
                    var parentContainer = $(this).closest(".field-container");
                    $(this).detach();
                    $(this).unbind("dblclick")
                    parentContainer.find(".right").append($(this));
                }
            });
        }
    });

</script>

<div class="field-container">
    <div class="left">
        <c:forEach items="${leftFields}" varStatus="loop" var="field">
            <div id="droppable${loop.index}" class="drop-container">
                <div style="min-width: 30%;">${field}:</div>
                <div class="droppable" style="width:70%;max-width: 70%;">

                </div>
            </div>
        </c:forEach>
    </div>
    <div class="right">
        <c:forEach items="${rightFields}" varStatus="loop" var="field">
            <div id="draggable${loop.index}" class="draggable" draggable="true">
                    ${field}
            </div>
        </c:forEach>
        <div id="draggabledontread" class="draggable" draggable="true">
            Don't Read
        </div>
    </div>
</div>