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
> sonos.sh -c stop -z all
> sonos.sh -c play -z all
> sonos.sh -c play # will play on all zones, like if -z all had been provided

Usage :
> Usage: <main class> [options] Additionnal command parameters
>   Options:
>     --command, -c         Command to be run
>         --control-port    Control port (needed for SSDP discovery, default =
>                           8009)
>         --debug           Debug mode
>                           Default: false
>     -h, --usage, --help   Shows available commands
>                           Default: false
>     --zone, -z            Sonos zone logic name to run command on. Put 'ALL' for
>                           interacting on all zones, or separate zones with comma. Valid
>                           examples are : '-z kitchen', '-z ALL', '-z kitchen,room', '-z
>                           192.168.1.54'.
> 
>   Commands :
>     add
>     discover
>     getxport
>     list
>     move
>     mute
>     next
>     pause
>     play
>     prev
>     remove
>     removeall
>     save
>     setxport
>     shuffle
>     stop
>     track
>     unmute
>     unshuffle
>     volume
>     down
>     up
> 
>   Examples :
>     --command play --zone kitchen,room        Starts music in kitchen and room
>     --command volume 25 --zone kitchen        Set volume to 25 in kitchen
>     --command pause --zone all                Pause all found zones
>     --command pause                           Pause all found zones

**Java API usage**

Check the few junit.

Example :
> new JavaCommander().execute("salon", "play");
