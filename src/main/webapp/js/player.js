// playerObj should be standart JS DOM instance
Player = function(playerObj) {
    var self = this;
    this.list = null;
    this.current = null;
    this.player = playerObj;
    this.paused = false;
    this.seeking = false;

    this.prev = function() {
        if (self.current == null) {
            self.playTrack(self.list[self.list.length - 1].id);
        } else {
            for (var i = 0; i < self.list.length; i++) {
                if (self.list[i].id == self.current.id) {
                    // match found;
                    self.playTrack(self.list[((i - 1) + self.list.length) % self.list.length].id);
                    break;
                }
            }
        }
    };
    this.next = function() {
        if (self.current == null) {
            self.playTrack(self.list[0].id);
        } else {
            for (var i = 0; i < self.list.length; i++) {
                if (self.list[i].id == self.current.id) {
                    // match found;
                    self.playTrack(self.list[((i + 1) + self.list.length) % self.list.length].id);
                    break;
                }
            }
        }
    };
    this.pause = function() {
        self.player.pause();
        self.paused = true;
        $('.ui-listed-track#'+self.current.id+' .ui-icon-pause-small').css('display', 'none');

        $('.ui-player-header .ui-icon-play-big').css('display', 'block');
        $('.ui-player-header .ui-icon-pause-big').css('display', 'none');
    };
    this.playTrack = function(trackId) {
        // if paused and clicked to play on same song
        if (self.paused && trackId == self.current.id) {
            self.resume();
            return;
        }

        for (var i = 0; i < self.list.length; i++) {
            if (self.list[i].id == trackId) {
                self.player.pause();

                self.player.src = "/audioDisc/" + self.list[i].source;
                //self.player.src = 'audio/34.mp3';  //dummy song for testing

                self.player.play();

                self.current = self.list[i];
                self.paused = false;
                self.drawCurrentSongData();
                break;
            }
        }
    };
    this.resume = function() {
        if (self.current != null) {
            self.player.play();
            self.paused = false;
            self.drawCurrentSongData();
        } else {
            self.next();
        }
    };
    this.drawCurrentSongData = function() {
        $('.ui-track-header .track-title').html(self.current.title);
        $('.ui-track-header .track-artist').html(self.current.artist);

        $('.ui-listed-track .ui-icon-pause-small').css('display', 'none');
        $('.ui-listed-track#'+self.current.id+' .ui-icon-pause-small').css('display', 'block');

        $('.ui-player-header .ui-icon-play-big').css('display', 'none');
        $('.ui-player-header .ui-icon-pause-big').css('display', 'block');
    };

    this.getTrackList = function(searchCriteria) {
        if(searchCriteria == null || searchCriteria == undefined){
            searchCriteria = "";
        }
        var url = 'getTrackList?search=' + searchCriteria;
        $.ajax({
            url: url,
            cache: false,
            dataType: 'json',
            success: function(data) {
                self.list = data;
                $('.ui-player-body').empty();
                for (var i = 0; i < data.length; i++) {
                    var title = data[i].title;
                    var artist = data[i].artist;

                    if (title.length > 45) {
                        title = title.substr(0, 45) + ' ...';
                    }
                    if (artist.length > 70) {
                        artist = artist.substr(0, 70) + ' ...';
                    }

                    var s =
                        '<li class="ui-listed-track" id="' + data[i].id + '">'+
                            '<div class="ui-icon ui-move-handler"></div>'+
                            '<div class="ui-icon ui-icon-pause-small" onclick="pagePlayer.pause();"></div>'+
                            '<div class="ui-icon ui-icon-play" onclick="$(\'.ui-icon-play\').css(\'display\', \'none\');$(\'.ui-icon-delete\').css(\'display\', \'none\');pagePlayer.playTrack(' + data[i].id + ');"></div>' +
                            '<div class="ui-icon ui-icon-delete" onclick="pagePlayer.deleteTrack(' + data[i].id + ');"></div>'+
                            '<span class="track-title">' + title + '</span>  <br/>'+
                            '<span class="track-artist">' + artist + '</span>'+
                        '</li>';
                    $('.ui-player-body').append(s);
                }
            }
        });
    };
    this.bigButtonClick = function() {
        if (self.paused) {
            self.resume();
        } else {
            self.next();
        }
    };
    this.deleteTrack = function(trackId) {
        $.ajax({
            url: 'deleteTrack?id='+trackId,
            cache: false,
            success: function(data) {
                self.getTrackList($('#musicSearchInput').val());
            }
        });
    };

    // redraw progres bar
    this.redrawProgress = function() {
        var totalWidth = $('.ui-player-header').width();
        var playedWidth = (self.player.currentTime / self.player.duration) * totalWidth;
        if (playedWidth == null || playedWidth == undefined || playedWidth == 'undefined' || isNaN(playedWidth)) {
            playedWidth = 0;
        }

        var leftStart = $('.ui-player-header').position().left - ($('.ui-player-header').width() / 2) ;

        var played = $('.ui-progress-played');
        played.width(playedWidth + 'px');
        played.css('left', leftStart + 'px');

        var estimated = $('.ui-progress-estimated');
        estimated.width((totalWidth - playedWidth) + 'px');
        estimated.css('left', (leftStart + playedWidth) + 'px');

        if (! self.seeking) {
            var current = $('.ui-progress-current').css('left', (leftStart + playedWidth) + 'px');
        }

        $('.ui-loading-progress').css('left', leftStart + 'px');

    };

    this.triggerFileUpload = function() {
        // setting alternate file upload for IE
        if ($.browser.msie) {
            $('#fileUploadModal').css('display', 'block');
        } else {
            $('#fileUpload').click();
        }
    };


    this.timer = null;
    this.chk_me = function (searchCriteria){
        clearTimeout(self.timer);
        self.timer=setTimeout(function (){self.getTrackList(searchCriteria);}, 500);
        //this.getTrackList(searchCriteria);
    };

    this.startFilterMode = function() {
        $('#musicSearchInput').css('display', 'block');
        $('#trackHeaderContainer').css('display', 'none');
        $('#musicSearchInput').focus();
    };
    this.endFilterMode = function() {
        $('#musicSearchInput').css('display', 'none');
        $('#trackHeaderContainer').css('display', 'block');
    };

    $(self.player).bind('timeupdate', function() {
        self.redrawProgress();
    });

    // seek handler init
    $('.ui-progress-current').draggable({
        axis: 'x',
        start: function(event, ui) {
            self.seeking = true;
        },
        stop: function(event, ui) {
            if (self.current != null) {
                var startPos = $('.ui-player-header').position().left - ($('.ui-player-header').width() / 2);
                var totalWidth = $('.ui-player-header').width();

                var percentageToSeek = (ui.position.left - startPos) / totalWidth;

                self.player.currentTime = self.player.duration * percentageToSeek;
            } else {
                $('.ui-progress-current').css('left', ($('.ui-player-header').position().left - ($('.ui-player-header').width() / 2)) + 'px');
            }
            self.seeking = false;
        },
        drag: function(event, ui) {
            if (ui.position.left < $('.ui-player-header').position().left - ($('.ui-player-header').width() / 2)) {
                ui.position.left = $('.ui-player-header').position().left - ($('.ui-player-header').width() / 2);
            } else
            if (ui.position.left > $('.ui-player-header').position().left + ($('.ui-player-header').width() / 2)) {
                ui.position.left = $('.ui-player-header').position().left + ($('.ui-player-header').width() / 2);
            }
        }
    });
    //volume control init
    $('.ui-slider').slider({
        orientation: "vertical",
        range: "min",
        min: 0,
        max: 10,
        value: 6,
        slide: function( event, ui ) {
            self.player.volume = ui.value / 10;
        }
    });
    self.player.volume = 0.6;

    //setting event listener on song end
    self.player.addEventListener("ended", function() {
         self.next();
    });



    $('.ui-progress-bar').click(function(evt) {
        console.debug('aaaaaaaaa');
        if (self.current != null) {
            var leftStart = $('.ui-player-header').position().left - ($('.ui-player-header').width() / 2);
            var percentageToSeek = (evt.clientX - leftStart) / $('.ui-player-header').width();

            self.player.currentTime = self.player.duration * percentageToSeek;
        }

    });


    // init darg&drop file upload!!!
    // drag&drop file upload end!!!


    //

    this.sortableList = $(".ui-player-body");
    this.sortableList.sortable({
        handle : '.ui-move-handler',
        stop: function(event, ui) {
            var movedSongId = $(event.srcElement).parent().attr('id');
            var list = self.sortableList.sortable( "toArray");

            var prevSongIndex = list.indexOf(''+movedSongId) - 1;
            var prevSongId = prevSongIndex >= 0 ? list[prevSongIndex] : -1;

            var url = 'moveTrack?id='+ movedSongId +'&after='+ prevSongId;
            $.ajax({
                url: url,
                cache: false,
                success: function(data) {
                    self.getTrackList($('#musicSearchInput').val());
                }
            });

        }

    });



    this.getTrackList();
    this.redrawProgress();
};


var pagePlayer = null;


function stateChange(event) {
    if (event.target.readyState == 4) {
        if (event.target.status == 200) {
            stopUpload();
//            dropZone.text('Загрузка успешно завершена!');
        } else {
/*
            dropZone.text('Произошла ошибка!');
            dropZone.addClass('error');
*/
        }
    }
}

