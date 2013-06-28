// playerObj should be standart JS DOM instance
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
    $("#progress-indicator").draggable({ axis: "x", containment: [dragStart,0,dragFinish,0],
        stop: function() {
            var offset = ($("#progress-indicator").offset().left
                - $("#progress-indicator").parent().offset().left + 4) / $("#progress-indicator").parent().width();
            var time = $("#jquery_jplayer").data("jPlayer").status.duration * offset;
            $("#jquery_jplayer").jPlayer("play", time);
        }
    });

    this.getTrackList = function() {
        var url = 'getTrackList';
        $.ajax({
            url: url,
            cache: false,
            dataType: 'json',
            success: function(data) {
                self.list = data;
                $('.ui-player-body').empty();
                for (var i = 0; i < data["songs"].length; i++) {
                    var title = data["songs"][i];

                    if (title.length > 45) {
                        title = title.substr(0, 45) + ' ...';
                    }

                    var s =
                        '<div class="listed-track">'+
                            '<div class="play-small"></div>' +
                            '<span class="track-title">' + title + '</span>  <br/>'+
                        '</div>';
                    $('.player-body').append(s);
                }
            }
        });
    };

    this.playStop = function(){
        if(!this.current){
            this.current = this.list[0];
            getSongSource(this.current);
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
        getSongSource(this.current);
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
        getSongSource(this.current);
        this.playTrack();
    }

    getSongSource = function(songName){
        $.ajax({
            url: "getSongSrc?name=" + songName,
            async: false,
            cache: false,
            success: function(data){
                $("#jquery_jplayer").jPlayer("setMedia", {
                    mp3: data
                } );
            }
        });
    }

    this.playTrack = function(){
        this.paused = false;
        $("#button-play-pause").attr('class', 'button-pause-big');
        $(".pause-small").attr("class", "play-small");
        $(".listed-track").each(function(){
            if($(this).find(".track-title").text() == pagePlayer.current){
                $(this).find(".play-small").attr("class", "pause-small");
            }
        });
        $("#track-name").text(this.current);
        $("#jquery_jplayer").jPlayer("play");
    }

    this.pauseTrack = function(){
        this.paused = true;
        $("#button-play-pause").attr('class', 'button-play-big');
        $(".pause-small").attr("class", "play-small");
        $("#jquery_jplayer").jPlayer("pause");
    }
};

