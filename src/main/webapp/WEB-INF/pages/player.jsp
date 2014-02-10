<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>

<html>
<head>
    <meta http-equiv='Content-Type' content='text/html; charset=utf-8'>
    <title>Cloud Player</title>

    <meta charset="UTF-8" />

    <script type="text/javascript" src="js/jquery-1.7.2.min.js"></script>
    <script type="text/javascript" src="js/jquery-ui-1.8.20.custom.min.js"></script>
    <script type="text/javascript" src="js/player.js"></script>
    <script type="text/javascript" src="js/jquery.jplayer.min.js"></script>

    <script src="js/id3/binaryfile.js" type="text/javascript"></script>
    <script src="js/id3/bufferedbinaryajax.js" type="text/javascript"></script>
    <script src="js/id3/filereader.js" type="text/javascript"></script>
    <script src="js/id3/id3.js" type="text/javascript"></script>
    <script src="js/id3/id3v1.js" type="text/javascript"></script>
    <script src="js/id3/id3v2.js" type="text/javascript"></script>
    <script src="js/id3/id3v2frames.js" type="text/javascript"></script>
    <script src="js/id3/id4.js" type="text/javascript"></script>
    <script src="js/id3/stringutils.js" type="text/javascript"></script>



    <link type="text/css" rel="stylesheet" href="css/jquery-ui-1.8.20.custom.css"/>
    <link type="text/css" rel="stylesheet" href="css/style.css"/>
</head>
<body>

<div id="successMessage"><c:out value="${successMessage}"/></div>
<div id="errorMessage"><c:out value="${errorMessage}"/></div>
<div id="serverErrorMessage"><c:out value="${serverErrorMessage}"/></div>
<ul id="add-cloud-button">
    <li><a href="addDropbox">ADD DROPOX</a></li>
    <li><a href="addDrive">ADD GOOGLE DRIVE</a></li>
</ul>
<ul id="remove-cloud-button">
    <li><a href="removeDropbox">Disconnect Dropbox account</a></li>
    <li><a href="removeDrive">Disconnect Google Drive account</a></li>
</ul>
<div class="player-body">
    <div id="player-controls">
        <div id="control-buttoms">
            <div id="button-prev" onclick="pagePlayer.prev();"></div>
            <div id="button-play-pause" class="button-play-big" onclick="pagePlayer.playStop();"></div>
            <div id="button-next" onclick="pagePlayer.next();"></div>
            <div id="track-name"></div>
            <div id="progress-bar">
                <div id="progress-indicator"></div>
            </div>
        </div>
    </div>
    <div id="track-list" class="waiting"></div>
</div>

<div id="jquery_jplayer"></div>
<audio controls="controls" id="player" preload="auto" style="display: none;" >
    <source src="" type="audio/mpeg">
</audio>
<script type="text/javascript">

    var proxyURL = "<c:out value="${proxyURL}"/>";

    $(document).ready(function() {
        var audio = document.getElementById('player');
        pagePlayer = new Player();
    });
</script>

<div id="logout-button"><a href="logout">Logout</a></div>
</body>
</html>