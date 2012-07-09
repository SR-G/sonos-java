**sonos-java** : Java tools for driving Sonos boxes.

Already available :

* Sonos command line interface

* Sonos java API

To come later :

* Sonos webapp

* Mirror4J integration


http://www.sonos.com/

![sonos](https://github.com/SR-G/sonos-java/raw/master/sonos.jpg)


**Command line usage**

Examples :

	sonos.sh -c stop -z all
	sonos.sh -c play -z all
	sonos.sh -c play # will play on all zones, like if -z all had been provided

Usage :

    Usage: <main class> [options] Additionnal command parameters
      Options:
        --command, -c         Command to be run
            --control-port    Control port (needed for SSDP discovery, default =
                              8009)
            --debug           Debug mode
                              Default: false
        -h, --usage, --help   Shows available commands
                              Default: false
        --zone, -z            Sonos zone logic name to run command on. Put 'ALL' for
                              interacting on all zones, or separate zones with comma. Valid
                              examples are : '-z kitchen', '-z ALL', '-z kitchen,room', '-z
                              192.168.1.54'.
                              Default: 
    
      Commands :
        add                            : Enqueue something in the queue list [additionnal parameters needed]
        discover                       : Discover every Sonos box on the network
        getxport                      
        list                           : Browse the playlist [additionnal parameters needed]
        move                           : Move song to another position in the track list [additionnal parameters needed]
        mute                           : Mute a zone
        next                           : Skip to next song
        pause                          : Pause zone
        play                           : Starts playing
        prev                           : Skip to previous song
        remove                         : Remove the specified song number from the current playlist [additionnal parameters needed]
        removeall                      : Clear the playlist
        save                           : Save playlist [additionnal parameters needed]
        setxport                       : Set the XPort [additionnal parameters needed]
        shuffle                        : Set shuffle on
        stop                           : Pause zone
        track                          : Skip to specified track [additionnal parameters needed]
        unmute                         : Unmute a zone
        unshuffle                      : Set shuffle off
        volume                         : Set volume to a given level [0-100] [additionnal parameters needed]
        down                           : Lower volume by 5
        up                             : Up volume by 5
    
      Examples :
        --command play --zone kitchen,room        Starts music in kitchen and room
        --command volume 25 --zone kitchen        Set volume to 25 in kitchen
        --command pause --zone all                Pause all found zones
        --command pause                           Pause all found zones

**Java API usage**

Check the few junit.

Example :

    new JavaCommander().execute("salon", "play");
