<!DOCTYPE html>
<html>
<head>
    <meta http-equiv='Content-Type' content='text/html; charset=utf-8'>
    <title>LeD player</title>

    <meta charset="UTF-8" />

    <script type="text/javascript" src="js/jquery-1.7.2.min.js"></script>
    <script type="text/javascript" src="js/jquery-ui-1.8.20.custom.min.js"></script>
    <script type="text/javascript" src="js/player.js"></script>
    <link type="text/css" rel="stylesheet" href="css/jquery-ui-1.8.20.custom.css"/>
    <link type="text/css" rel="stylesheet" href="css/style.css"/>
</head>
<body>


<ul class="ui-player-body">
    <!--track list will be inserted here-->
</ul>
<div id="add-dropbox-button"><a href="addDropbox">ADD DROPOX</a></div>
<div id="logout-button"><a href="logout">Logout</a></div>

<div class="ui-player-header">
    <input type="text" onkeyup="pagePlayer.chk_me($(this).val());" id="musicSearchInput" class="ui-track-header" onblur="pagePlayer.endFilterMode();"/>

    <div class="ui-track-header" id="trackHeaderContainer" onclick="pagePlayer.startFilterMode();">
        <span class="track-title"></span>
        <span class="track-artist"></span>
    </div>

    <div style="float: left">
        <div class="ui-icon ui-icon-prev"
             style="position: absolute; top: 16px; margin-left: 20px;"
             onclick="pagePlayer.prev();"   >
        </div>
        <div class="ui-icon ui-icon-pause-big"
             style="position: absolute; top: 16px; margin-left: 52px;"
             onclick="pagePlayer.pause();"   >

        </div>
        <div class="ui-icon ui-icon-play-big"
             style="position: absolute; top: 16px; margin-left: 54px;"
             onclick="pagePlayer.bigButtonClick();"   >

        </div>
        <div class="ui-icon ui-icon-next"
             style="position: absolute; top: 16px; margin-left: 84px;"
             onclick="pagePlayer.next();"   >

        </div>


        <div id="loadingBar" class="ui-icon ui-loading-bar"
             style="position: absolute; right: 40px; top: 16px;" >
        </div>

        <div class="ui-slider"
             style="position: absolute; right: 20px; top: 16px; height: 32px;"  >
        </div>

        <div id="slider-vertical" style="height:30px;"></div>
    </div>

</div>
<div class="ui-progress-bar ui-progress-played"> </div>
<div class="ui-progress-bar ui-progress-estimated"> </div>
<div class="ui-progress-current"> </div>

<div class="ui-loading-progress"> </div>

<audio controls="controls" id="player" preload="auto" style="display: none;" >
    <source src="https://www.dropbox.com/s/qumfp51p9e264wi/Kalimba.mp3" type="audio/mpeg">
</audio>

<form id="hiddenForm" action="uploadFiles" method="post" target="tFrame" enctype="multipart/form-data">
    <input type="file" name="file1" id="fileUpload" accept="audio/mp3" onchange="startUpload('hiddenForm');" style="position: absolute; top: -150px; left: -1000px;"/>
</form>

<div id="fileUploadModal" class="ui-modal-form">
    <div>
        <form id="IEForm" action="uploadFiles" method="post" target="tFrame" enctype="multipart/form-data">
            <input type="file" name="file1" accept="audio/mp3" />
            <input type="submit" onclick="$('#fileUploadModal').css('display', 'none'); startUpload('IEForm');" value="Загрузить"/>
            <input type="button" onclick="$('#fileUploadModal').css('display', 'none');" value="Отмена"/>
        </form>
    </div>
</div>

<iframe id="tFrame" name="tFrame" style="display: none; height: 1px; width: 1px; border: 0;" onload="stopUpload();">
</iframe>

<script>
    $(document).ready(function() {
        var audio = document.getElementById('player');
        pagePlayer = new Player(audio);

        $('.ui-listed-track').live("mouseover",
            function () {
                if ((pagePlayer.current == null)
                        || (pagePlayer.current != null && pagePlayer.current.id != this.id)) {
                    $(this).find('.ui-icon-delete').css('display', 'block');
                }

                if ((pagePlayer.current == null)
                        || (pagePlayer.current != null && pagePlayer.current.id != this.id)
                        || pagePlayer.paused) {
                    $(this).find('.ui-icon-play').css('display', 'block');
                }
            }
        );
        $('.ui-listed-track').live("mouseout",
            function () {
                $(this).find('.ui-icon-play').css('display', 'none');
                $(this).find('.ui-icon-delete').css('display', 'none');
            }
        );
        $( "#slider" ).slider();

        $(document).keyup(function(evt) {
            //console.debug(evt.keyCode);

            if (pagePlayer != null) {
                if (evt.keyCode == 32) {
                    // space
                    if (pagePlayer.paused) {
                        pagePlayer.resume();
                    } else {
                        pagePlayer.pause();
                    }

                } else
                if (evt.keyCode == 37 || evt.keyCode == 38) {
                    // left and up buttons
                    pagePlayer.prev();
                } else
                if (evt.keyCode == 39 || evt.keyCode == 40) {
                  // right and down buttons
                    pagePlayer.next();
                }
/*
                else
                if (evt.keyCode == 107) {
                    // add button

                } else
                if (evt.keyCode == 109) {
                    // minus button
                }
*/
            }

        });
    });

    $(window).resize(function() {
        pagePlayer.redrawProgress();
    });

    function stopUpload() {
        $('#loadingBar').css('display', 'none');

        if (pagePlayer != null) pagePlayer.getTrackList($('#musicSearchInput').val());

        $('.ui-loading-progress').css('display', 'none');
    }

    function startUpload(parentFormId) {
        $('#loadingBar').css('display', 'block');

        if (parentFormId == 'IEForm') return;
        var xhr = new XMLHttpRequest();

        var fd;
        if (document.getElementById(parentFormId).getFormData != undefined &&
            document.getElementById(parentFormId).getFormData != 'undefined' &&
            document.getElementById(parentFormId).getFormData != null) {
            fd = document.getElementById(parentFormId).getFormData();
        } else
        if (FormData != undefined &&
            FormData != 'undefined' &&
            FormData != null) {
            fd = new FormData(document.getElementById(parentFormId));
        } else {
            // for old opera versions
            document.getElementById(parentFormId).submit();
            return;
        }


        /* event listners */
        xhr.upload.addEventListener("progress", uploadProgress, false);

        xhr.addEventListener("load", stopUpload, false);
/*
        xhr.addEventListener("error", uploadFailed, false);
        xhr.addEventListener("abort", uploadCanceled, false);
*/
        xhr.open("POST", "uploadFiles");
        xhr.send(fd);
    }

    function uploadProgress(evt) {
        if (evt.lengthComputable) {
            $('.ui-loading-progress').css('display', 'block');
            $('.ui-loading-progress').css('left', ($('.ui-player-header').position().left - 300) + 'px');
            $('.ui-loading-progress').css('width', (evt.loaded / evt.total * 600) + 'px');
        }
    }
</script>

</body>
</html>