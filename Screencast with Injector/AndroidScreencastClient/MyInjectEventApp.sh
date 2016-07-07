#!/bin/bash

dx --dex --output=./classes.dex ./MyInjectEventApp.jar
aapt add MyInjectEventApp.jar classes.dex
rm classes.dex
mv MyInjectEventApp.jar ../AndroidScreencast
