// playerObj should be standart JS DOM instance
var songData = new Array();
Player = function() {
    this.list = new Array("Kalimba.mp3", "Maid with the Flaxen Hair.mp3");
    this.current = null;
    this.paused = true;
    this.seeking = false;
    $("#jquery_jplayer").jPlayer({
        swfPath: "js/Jplayer.swf",
        supplied: "mp3",
        volume: 1,
        wmode:"window",
        solution: "html,flash",
        errorAlerts: true,
        warningAlerts: false,
        ended: function () {
            pagePlayer.next();
        }
    });

    $("#jquery_jplayer").bind($.jPlayer.event.timeupdate, function() {
        var left = (($("#jquery_jplayer").data("jPlayer").status.currentTime
            / $("#jquery_jplayer").data("jPlayer").status.duration) * $("#progress-indicator").parent().width()) - 4;
        $("#progress-indicator").css('left', left);
    });
    var dragStart = $("#progress-indicator").parent().offset().left - 4;
    var dragFinish = dragStart + $("#progress-indicator").parent().width();
    //TODO draggable - incorrect progress
    $("#progress-indicator").draggable({ axis: "x", containment: [dragStart,0,dragFinish,0],
        stop: function() {
            var offset = ($("#progress-indicator").offset().left
                - $("#progress-indicator").parent().offset().left + 4) / $("#progress-indicator").parent().width();
            var time = $("#jquery_jplayer").data("jPlayer").status.duration * offset;
            $("#jquery_jplayer").jPlayer("play", time);
        }
    });

    this.getPlayList = function () {
        var url = 'api/getPlayList';
        $.ajax({
            url: url,
            cache: false,
            dataType: 'json',
            success: function (data) {
                console.log(data);
                songData = data; // global
                renderPlayList(data);
            }
        });
    };

    this.playStop = function(){
        if(!this.current){
            this.current = this.list[0];

            selectSongByElement(this.current);
        }
        if(this.paused){
            this.playTrack();
        }else{
            this.pauseTrack();
        }
    }

    this.getPlayList();

    this.next = function(){
        for(var i = 0; i < this.list.length; i++){
            if(this.current == this.list[i]){
                if(i == this.list.length - 1){
                    this.current = this.list[0];
                }else{
                    this.current = this.list[i + 1];
                }
                break;
            }
        }
        selectSongByElement(this.current);

        this.playTrack();
    }

    this.prev = function(){
        for(var i = 0; i < this.list.length; i++){
            if(this.current == this.list[i]){
                if(i == 0){
                    this.current = this.list[this.list.length - 1];
                }else{
                    this.current = this.list[i - 1];
                }
                break;
            }
        }
        selectSongByElement(this.current);

        this.playTrack();
    }

    this.playTrack = function(){
        this.paused = false;
        $("#button-play-pause").attr('class', 'button-pause-big');
        $(".pause-small").attr("class", "play-small");
        var songName = getSongTitle(this.current);
        $(".listed-track").each(function () {
            if ($(this).find(".track-title").text() == songName) {
                $(this).find(".play-small").attr("class", "pause-small");
            }
        });
        if(songName){
            $("#track-name").text(songName);
            $("#jquery_jplayer").jPlayer("play");
        }
    }

    this.pauseTrack = function(){
        this.paused = true;
        $("#button-play-pause").attr('class', 'button-play-big');
        $(".pause-small").attr("class", "play-small");
        if($("#jquery_jplayer").data("jPlayer").status.src){
            $("#jquery_jplayer").jPlayer("pause");
        }
    }

    this.playSong = function (obj) {
        var trackNumber = $(".listed-track").index($(obj).closest(".listed-track"));
        if (this.current == this.list[trackNumber]) {
            this.playStop();
        } else {
            clickedObject = this.list[trackNumber];
            selectSongByElement(clickedObject);
            if (clickedObject["url"]) {
                this.current = clickedObject;
                this.playTrack();
            }
        }
    }

    this.getMetadata = function (songHtmlElement) {
        var trackNumber = $(".listed-track").index($(songHtmlElement).closest(".listed-track"));

        this.current = this.list[trackNumber];

        requestSongMetadata(this.current, function (songObj) {
            // need to white while metadata not get
            updateVisibleMetadata(songObj, songHtmlElement);
            saveMetadataToServer(songObj);
        });

    }

    updateVisibleMetadata = function (songObj, songHtmlElement) {

        $metadataHtml = renderSongMetadata(songObj);
        $(songHtmlElement).closest(".listed-track").find(".metadata").html($metadataHtml);

    }

    saveMetadataToServer = function (songObj) {
        var url = 'api/saveSongMetadata?jsonSongObject=' + JSON.stringify(songObj);
        $.ajax({
            url: url,
            cache: false,
            dataType: 'json',
            success: function (data) {
                // TODO process error
                console.log(data);
            }
        });
    };

    renderPlayList = function (data) {
        if (data) {
            pagePlayer.list = data;
            $('#track-list').empty();
            $('#track-list').removeClass("waiting");
            for (var i = 0; i < data.length; i++) {
                var songElement = renderSongElement(data[i]);
                $('#track-list').append(songElement);
            }
        }else if(data && data["errorMessage"]){
            $("#errorMessage").text(data["errorMessage"]);
        }
    }
    renderSongElement = function (song) {

        var title = getSongTitle(song);
        var songElement =
            '<div class="listed-track">' +
                '<div class="play-small" onclick="pagePlayer.playSong(this)"></div>' +
                '<span class="track-title">' + title + '</span>' +
                '<span class="metadata"> ' + renderSongMetadata(song) + '</span>' +
                '<span class="metadataRefreshButton" onclick="pagePlayer.getMetadata(this)">refresh</span>' +
                '<br/>' +
                '</div>';
        return  songElement;
    }

    renderSongMetadata = function (song) {

        var metadataHtml = '';

        if (song["metadata"] != null) {
            var metadata = song["metadata"];
            metadataHtml = 'title:' + metadata["title"] +
                ' artist:' + metadata["artist"] +
                ' album:' + metadata["album"];
        }

        return metadataHtml;
    }

    getSongTitle = function (song) {
        var title = '';
        /* TODO: refactor some combination title + artist
         if(    song['metadata'] !== null
         && song['metadata']['title'] !== null
         && song['metadata']['title'] != ''
         ){
         title = song['metadata']['title'];
         }else{
         title = song['fileName'];
         }
         */
        title = song['fileName'];
        return title;
    }

    selectSongByElement = function (element) {
        songUrl = getSongURL(element);
        if(songUrl){
            element["url"] = songUrl;
            selectSongByUrl(songUrl);
        }
    }


    selectSongByUrl = function(songUrl){
        $("#jquery_jplayer").jPlayer("setMedia", {
            mp3: songUrl
        } );        
    }


    getSongURL = function(songObject){
        var srcURL = "";

        if( songObject["url"]){
            srcURL = songObject["url"];
        } else {
            srcURL = requestSongUrl(songObject);
            songObject["url"] = srcURL;
        }
        console.log(srcURL);
        return srcURL;
    }

    requestSongUrl = function (songObject) {
        var url = "";
        $.ajax({
            // TODO: do not send driveID if it is Dropbox
            //url: "api/getLink?path=" + songObject["filePath"] + "&cloud_id=" + songObject["cloudId"] + "&file_id=" + songObject["driveId"],
            url: "api/getLink?cloud_id=" + songObject["cloudId"] + "&file_id=" + songObject["fileId"] ,
            async: false,
            cache: false,
            success: function (data) {
                if (data == "error") {
                    $("#errorMessage").text("Failed to connect the server");
                } else {
                    url = data;
                }
            },
            error: function (data) {
                $("#errorMessage").text("Failed to connect the server");
            }
        });
        return url;
    }

    requestSongMetadata = function (songObj, callback) {
        var songURL = getSongURL(songObj);
        console.log(songObj);
        ID3.loadTags(songURL, function () {
            metadata = ID3.getAllTags(songURL);
            songObj["metadata"] = metadata;
            console.log(songObj["metadata"]);
            callback(songObj);
        });
    }
};

