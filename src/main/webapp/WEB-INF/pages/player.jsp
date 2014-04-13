<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>

<html>
<head>
    <meta http-equiv='Content-Type' content='text/html; charset=utf-8'>
    <title>Cloud Player</title>

    <meta charset="UTF-8" />

    <link href="css/bootstrap/bootstrap.min.css" rel="stylesheet" media="screen">
    <link type="text/css" rel="stylesheet" href="css/jplayer/blue.monday/jplayer.blue.monday.css"/>
    <%--<link type="text/css" rel="stylesheet" href="css/style.css"/>--%>

    <script type="text/javascript" src="js/jquery-1.11.0.min.js"></script>

    <script type="text/javascript" src="js/jplayer/jquery.jplayer.min.js"></script>
    <script type="text/javascript" src="js/jplayer/script.js"></script>

    <script src="js/bootstrap/bootstrap.min.js"></script>

    <%--Metadata retriving and parsing--%>
    <script src="js/id3/binaryfile.js" type="text/javascript"></script>
    <script src="js/id3/bufferedbinaryajax.js" type="text/javascript"></script>
    <script src="js/id3/filereader.js" type="text/javascript"></script>
    <script src="js/id3/id3.js" type="text/javascript"></script>
    <script src="js/id3/id3v1.js" type="text/javascript"></script>
    <script src="js/id3/id3v2.js" type="text/javascript"></script>
    <script src="js/id3/id3v2frames.js" type="text/javascript"></script>
    <script src="js/id3/id4.js" type="text/javascript"></script>
    <script src="js/id3/stringutils.js" type="text/javascript"></script>

</head>
<body>

<div class="container">

    <div class="navbar navbar-default" role="navigation">
        <div class="container-fluid">
            <div class="navbar-header">
                <button type="button" class="navbar-toggle" data-toggle="collapse" data-target=".navbar-collapse">
                    <span class="sr-only">Toggle navigation</span>
                    <span class="icon-bar"></span>
                    <span class="icon-bar"></span>
                    <span class="icon-bar"></span>
                </button>
                <a class="navbar-brand" href="#">Cloud player</a>
            </div>
            <div class="navbar-collapse collapse">
                <ul class="nav navbar-nav">
                    <li class="active"><a href="#">Link</a></li>
                    <li><a href="#">Link</a></li>
                    <li><a href="#">Link</a></li>
                    <li class="dropdown">
                        <a href="#" class="dropdown-toggle" data-toggle="dropdown">Cloud control <b class="caret"></b></a>
                        <ul class="dropdown-menu">
                            <li><a href="addDropbox">ADD DROPOX</a></li>
                            <li><a href="addDrive">ADD GOOGLE DRIVE</a></li>
                            <li class="divider"></li>
                            <li><a href="removeDropbox">Disconnect Dropbox</a></li>
                            <li><a href="removeDrive">Disconnect Google Drive</a></li>
                        </ul>
                    </li>
                </ul>
                <ul class="nav navbar-nav navbar-right">
                    <%--<li class="active"><a href="./">Default</a></li>--%>
                    <%--<li><a href="#">Static top</a></li>--%>
                    <li><a href="logout">logout</a></li>
                </ul>
            </div><!--/.nav-collapse -->
        </div><!--/.container-fluid -->
    </div>

    <div class="jumbotron">
        <h2>Player</h2>
        <div id="jp_container_N" class="jp-video jp-video-360p">
            <div class="jp-type-playlist">
                <div id="jquery_jplayer_N" class="jp-jplayer" style="display:none;"></div>
                <div class="jp-gui">

                    <div class="jp-video-play">
                        <a href="javascript:;" class="jp-video-play-icon" tabindex="1">play</a>
                    </div>

                    <div class="jp-interface">
                        <div class="jp-progress">
                            <div class="jp-seek-bar">
                                <div class="jp-play-bar"></div>
                            </div>
                        </div>
                        <div class="jp-current-time"></div>
                        <div class="jp-duration"></div>
                        <div class="jp-controls-holder">
                            <ul class="jp-controls">
                                <li><a href="javascript:;" class="jp-previous" tabindex="1">previous</a></li>
                                <li><a href="javascript:;" class="jp-play" tabindex="1">play</a></li>
                                <li><a href="javascript:;" class="jp-pause" tabindex="1">pause</a></li>
                                <li><a href="javascript:;" class="jp-next" tabindex="1">next</a></li>
                                <li><a href="javascript:;" class="jp-stop" tabindex="1">stop</a></li>
                                <li><a href="javascript:;" class="jp-mute" tabindex="1" title="mute">mute</a></li>
                                <li><a href="javascript:;" class="jp-unmute" tabindex="1" title="unmute">unmute</a></li>
                                <li><a href="javascript:;" class="jp-volume-max" tabindex="1" title="max volume">max volume</a></li>
                            </ul>
                            <div class="jp-volume-bar">
                                <div class="jp-volume-bar-value"></div>
                            </div>
                            <ul class="jp-toggles">
                                <%--<li><a href="javascript:;" class="jp-full-screen" tabindex="1" title="full screen">full screen</a></li>--%>
                                <%--<li><a href="javascript:;" class="jp-restore-screen" tabindex="1" title="restore screen">restore screen</a></li>--%>
                                <li><a href="javascript:;" class="jp-shuffle" tabindex="1" title="shuffle">shuffle</a></li>
                                <li><a href="javascript:;" class="jp-shuffle-off" tabindex="1" title="shuffle off">shuffle off</a></li>
                                <li><a href="javascript:;" class="jp-repeat" tabindex="1" title="repeat">repeat</a></li>
                                <li><a href="javascript:;" class="jp-repeat-off" tabindex="1" title="repeat off">repeat off</a></li>
                            </ul>
                        </div>
                        <div class="jp-details">
                            <ul>
                                <li><span class="jp-title"></span></li>
                            </ul>
                        </div>
                    </div>
                </div>
                <div class="jp-playlist">
                    <ul>
                        <!-- The method Playlist.displayPlaylist() uses this unordered list -->
                        <li></li>
                    </ul>
                </div>
                <div class="jp-no-solution">
                    <span>Update Required</span>
                    To play the media you will need to either update your browser to a recent version or update your <a href="http://get.adobe.com/flashplayer/" target="_blank">Flash plugin</a>.
                </div>
            </div>
        </div>

    </div>



    </div>

    <!-- Site footer -->
    <div class="footer">
        <p>© Company 2013</p>
    </div>

</div>


</body>
</html>