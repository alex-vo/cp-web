<!DOCTYPE html>
<html>
<head>
    <meta http-equiv='Content-Type' content='text/html; charset=utf-8'>
    <title>Cloud Player</title>

    <meta charset="UTF-8" />

    <script type="text/javascript" src="js/jquery-1.7.2.min.js"></script>
    <script type="text/javascript" src="js/jquery-ui-1.8.20.custom.min.js"></script>
    <script type="text/javascript" src="js/player.js"></script>
    <script type="text/javascript" src="js/jquery.jplayer.min.js"></script>
    <link type="text/css" rel="stylesheet" href="css/jquery-ui-1.8.20.custom.css"/>
    <link type="text/css" rel="stylesheet" href="css/style.css"/>
</head>
<body>

<div id="add-dropbox-button"><a href="addDropbox">ADD DROPOX</a></div>
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
</div>

<div id="jquery_jplayer"></div>
<audio controls="controls" id="player" preload="auto" style="display: none;" >
    <source src="" type="audio/mpeg">
</audio>
<script type="text/javascript">
    $(document).ready(function() {
        var audio = document.getElementById('player');
        pagePlayer = new Player();
    });
</script>

<div id="logout-button"><a href="logout">Logout</a></div>
</body>
</html>