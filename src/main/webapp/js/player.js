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

    this.getTrackList = function() {
        var url = 'api/getMusicList?path=/';
        $.ajax({
            url: url,
            cache: false,
            dataType: 'json',
            success: function(data) {
                renderTrackList(data);
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

    this.getTrackList();

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
        var songName = this.current["name"].substr(this.current["name"].lastIndexOf("/") + 1, this.current["name"].length);
        $(".listed-track").each(function(){
            if($(this).find(".track-title").text() == songName){
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

    this.playSong = function(obj){
        if(this.current == this.list[$(".listed-track").index($(obj).closest(".listed-track"))]){
            this.playStop();
        }else{
            clickedObject = this.list[$(".listed-track").index($(obj).closest(".listed-track"))];
            selectSongByElement(clickedObject);
            if(clickedObject["url"]){
                this.current = clickedObject;
                this.getMetadata(obj);
                console.log(this.current);
                this.playTrack();
            }
        }
    }

    this.getMetadata = function(songObject){
        this.current = this.list[$(".listed-track").index($(songObject).closest(".listed-track"))];
        var songURL = getSongURL(this.current);
        if(songURL){
            getSongMetadataByURL(songURL);
        }
    }

    renderTrackList = function(data){
        if(data && data["songs"]){
            pagePlayer.list = data["songs"];
            $('#track-list').empty();
            $('#track-list').removeClass("waiting");
            for (var i = 0; i < data["songs"].length; i++) {                
                var title = data["songs"][i]["name"].substr(data["songs"][i]["name"].lastIndexOf("/") + 1, data["songs"][i]["name"].length);
                var s =
                    '<div class="listed-track">'+
                        '<div class="play-small" onclick="pagePlayer.playSong(this)"></div>' +
                        '<span class="track-title">' + title + '</span>  <br/>'+
                        '</div>';
                $('#track-list').append(s);
            }
        }else if(data && data["errorMessage"]){
            $("#errorMessage").text(data["errorMessage"]);
        }
    }

    selectSongByElement = function(element){
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
        }else{
            $.ajax({
                url: "api/getLink?path=" + songObject["name"] + "&cloud_id="
                    + songObject["cloudId"] + "&file_id=" + songObject["id"],
                async: false,
                cache: false,
                success: function(data){
                    if(data == "error"){
                        $("#errorMessage").text("Failed to connect the server");
                    }else{
                        srcURL = data;
                    }
                },
                error: function(data){
                   $("#errorMessage").text("Failed to connect the server");
                }
            });
        }
        console.log(srcURL);
        return srcURL;
    }

    getSongMetadataByURL = function(songUrl){
        var songUrlThroughProxy = proxyURL + songUrl;
        var tags = null;

        ID3.loadTags(songUrlThroughProxy, function(){
            tags = ID3.getAllTags(songUrlThroughProxy);
            console.log(tags);
        });

        return tags;
    }
};

