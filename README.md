EZconnect
===

EZconnect aims to make remote desktop access easy. It is an application that configures and starts a remote desktop that can then be accessed using a browser from anywhere in the local network. EZconnect uses the noVNC JavaScript library (available on https://github.com/novnc/noVNC). EZconnect is currently only available for Linux.

Installation on Arch-based systems
---

The easiest way to install EZconnect on Arch-based systems, is by downloading the PKGBUILD file. EZconnect can then be installed using the following instructions in the command line.
1. move PKGBUILD to an empty directory
2. navigate into that directory
3. execute ```makepkg -s```
4. execute ```makepkg --install ezconnect-v1.0-1-x86\_64.tar.xz``` and follow the installation prompts. The app is now installed and can be started using the bash command ```ezconnect```.

Installation on other Linux systems:
---

It is also possible to install EZconnect on other Linux systems, however this is currently more complicated, as dependencies have to be installed. This method also works on Arch-based systems. The following packages are needed:

- jre8-openjdk (tested with version 8.u222)
- websockify (tested with version 0.8.0)
- ruby (tested with version 2.6.3)
- x11vnc (tested with version 1:0.9.16)
- ruby-bundler (tested with version 2.0.2)

When these dependencies are installed, download EZconnect.jar and the lib folder and place them into an empty directory. Navigate to this directory, then:

1.execute ```cd lib/webserver```
2. execute ```bundle install```. If you do not have the correct permissions to write, give the requested directory the permission or use sudo.
3. execute ```cd ../..```
4. execute ```java -jar EZconnect.jar```

Instructions
---

1. Start EZconnect as described above.
1. Enter desired Username and Password.
1. Hit **Start**
3. Open your browser and go to the address shown on the bottom of EZconnect
1. Enter your credentials and hit **Login**
1. Hit **Connect**

License
---

This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0. If a copy of the MPL was not distributed with this file, You can obtain one at https://mozilla.org/MPL/2.0/.

Contributors:
---

Patrick Hein and Franziska Paulus
