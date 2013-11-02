#!/bin/sh
rsvg-convert ic_launcher.svg -w 72 -h 72 -o ../drawable-hdpi/ic_launcher.png 
rsvg-convert ic_launcher.svg -w 48 -h 48 -o ../drawable-mdpi/ic_launcher.png 
rsvg-convert ic_launcher.svg -w 96 -h 96 -o ../drawable-xhdpi/ic_launcher.png 
rsvg-convert ic_launcher.svg -w 144 -h 144 -o ../drawable-xxhdpi/ic_launcher.png
