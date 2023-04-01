
HOWTO
- setup a site embedding the videos to show
  - maybe a main page linking to a few per sub-page rather than 300 on one page
- fork this project
- edit app/src/main/res/layout/activity_main.xml buttons
  - id (if desired) and/or add new buttons
  - emoji
- edit app/src/main/java/net/spraul/nopebrowser/MainActivity.kt
  - allowUrlContains (denyUrlContains processed first)
  - button.button*.setOnClickListener urls
- build using GitHub action
- sideload release .apk
- troubleshooting
  - debug button dumps count per yes/no hostname list to Android log

TODO
- status indicating still loading
- sound played when initial URL is blocked (OverrideURL, not InterceptRequest)
- normal Android stuff
  - rotate currently restarts the activity
  - scroll the buttons horizontally?
- a network broadcast packet indicating the app is alive
  - also include the last time something was loaded and filetype
  - purpose: monitor on some other device
- not hardcoding buttons, filter list, and URLs
- clear storage (logins, etc.)
- fix based on log messages (some may be normal)
  - Render process's crash wasn't handled by all associated webviews, triggering application crash.
  - E/BufferQueueProducer: \[ImageReader-1x1f23u2304m3-3043-0](id:be300000001,api:3,p:3043,c:3043) detachBuffer: slot 0 is not owned by the producer (state = FREE)
  - E/BufferQueueProducer: \[MediaCodec.release](id:be300000002,api:0,p:-1,c:3043) detachBuffer: BufferQueue has no connected producer
  - I/SurfaceComposerClient: popReleaseBufferCallbackLocked: callback not found for bufferId:### framenumber:###
