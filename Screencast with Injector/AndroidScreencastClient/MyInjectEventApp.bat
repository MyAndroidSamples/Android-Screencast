dx --dex --output=./classes.dex ./MyInjectEventApp.jar
aapt add MyInjectEventApp.jar classes.dex
del classes.dex
move MyInjectEventApp.jar ../AndroidScreencast
