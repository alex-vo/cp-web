$(document).ready(function(){

    var myPlaylist = new jPlayerPlaylist({
        jPlayer: "#jquery_jplayer_N",
        cssSelectorAncestor: "#jp_container_N"
    }, [ ], {
        playlistOptions: {
            enableRemoveControls: true
        },
        swfPath: "js",
        supplied: "wav, ogg, mp3",
        smoothPlayBar: true,
        keyEnabled: true,
        audioFullScreen: true
    });

    ServerAPI.getPlayList(myPlaylist, function(playList){
        myPlaylist.setPlaylist(playList);
    });

});
//]]>