# beat-saber-custom-song-repair-tool

Since returning to Beat Saber lately, I noticed that when playing without mods some custom songs won't load.
They just put up an endless spinning animation.

While looking at the log files i found a common problem that the version is not in the correct place in the Json data.
And since modifying Json by hand is tiresome i created this tool to automate the process.

This tool will try to repair a custom song by moving the version information to the start of the files.
This has proven to work for the test songs i have used it on.
But i can't promise it will work for all songs.

Tool is written as a GUI in Java, and bundled with a launcher, that currently requires windows.