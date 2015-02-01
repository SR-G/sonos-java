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

**How to build**

You need [Maven](maven.apache.org) (2 or 3) as a build tool. Just run from the sonos-root project a maven command like "mvn clean install -Dmaven.test.skip=true" to build the various jars.

Most dependencies are available in the [maven central repository](http://search.maven.org/), excepted the cling one. To have this one, if you don't own a Maven repository on your own (which would allow to deploy the missing jars there), your can just add this maven configuration :

How To Build

To enable the required maven repositories add the following profile to your settings.xml

	<profiles>
		<profile>
		  <id>4thline</id>
		  <repositories>
			<repository>
			  <id>4thline.org-repo</id>
			  <url>http://4thline.org/m2</url>
			  <snapshots>
				<enabled>false</enabled>
			  </snapshots>
			</repository>
		  </repositories>
		</profile>
	</profiles>

And to activate by default add:

	<activeProfiles>
		<activeProfile>4thline</activeProfile>
	</activeProfiles>

More information on mavens settings.xml can be found at http://maven.apache.org/settings.html
