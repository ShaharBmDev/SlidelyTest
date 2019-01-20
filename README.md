Slidely Test for android developers
=================================

This is a simple app as Slidely specified, loading images from the web (pixabay service of top photos) and displaying them.
In case it fails, it switches to loading images and videos from local storage and displaying them in a chronological order.
It shows the media on a full screen with no gaps, with animations to the showing and disappearing images of fade and zoom in and fade and zoom out accordingly.
Also, it shows an analog clock overlay on top of the media.

The app tries to show correct usage of Andriod architectural components as well as third party libraries.

* The app architectural guideline is MVP. 
* The app is written in Kotlin.
* The app checks for read permissions.
* The app loads remote data in pixabay servers.
* The app loads local media in the devices storage.
* The app has a settings button to alter the display time of a media object before it is replaced.
* The app plays the videos in a loop in case of short videos.
* In case of app going to the background the media display mechanism recognizes it and pauses the time interval and resumes when app is in foreground again.
* In case of app going to the background the media display mechanism reconizes it and pauses the video and resumes when app is in foreground again.
* RxJava is used.
* Retrofit is used.
* Picasso is used.
* Chaching mechanism is used.
* Butterknife is used
* Dagger 2 is used.

### Future work:
* Saving data to persistant storage using room for offline usage.
* Adding search feature i.e.: specific content from pixabay.
