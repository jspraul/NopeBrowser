#!/usr/bin/python

from datetime import datetime
from os import system
from socket import socket, timeout, AF_INET, SOCK_DGRAM
from sys import platform, stdout

toSay = "NopeBrowser is not running"
waitSecondsWhenRunning = 60.0
waitSecondsWhenNotRunning = 10.0

monitor = socket(AF_INET,SOCK_DGRAM)
monitor.bind(('',2119))
monitor.settimeout(waitSecondsWhenNotRunning - 1)

def say(toSay):
 #stdout.write("\a") # ding did not work
 if platform == "win32":
  system("PowerShell.exe -Command \"Add-Type -AssemblyName System.Speech; $speak=New-Object System.Speech.Synthesis.SpeechSynthesizer; $speak.Speak('{0}')\"".format(toSay))
 else:
  system("/usr/bin/say '{0}'".format(toSay))
  

print(("\n[{0}] Begin monitoring NopeBrowser app\n"
 +"           (complaining if not heard from for 1 minute)\n\n"
 +"           Press Ctrl+C to quit\n")
 .format(datetime.now().strftime("%H:%M:%S")))

while True:
 try:
  message, address = monitor.recvfrom(1024)
  if monitor.gettimeout() != waitSecondsWhenRunning:
   print("[{0}] back online".format(datetime.now().strftime("%H:%M:%S")))
   monitor.settimeout(waitSecondsWhenRunning)
 except timeout as e:
  if monitor.gettimeout() != waitSecondsWhenNotRunning:
   print("[{0}] offline".format(datetime.now().strftime("%H:%M:%S")))
   monitor.settimeout(waitSecondsWhenNotRunning)
  say(toSay)
