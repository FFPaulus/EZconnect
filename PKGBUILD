pkgname="ezconnect"
pkgver=v1.0
pkgrel=1
pkgdesc="Set up VNC and connect to remote desktop via browser"
arch=("x86_64")
license=('MPL2')
source=('git+https://github.com/Ziska20/EZconnect.git#tag=448187cddd2566ce5de1db73876a8f780f77317f')
md5sums=('SKIP')
depends=('jre8-openjdk>=8.u222' 'websockify>=0.8.0' 'ruby>=2.6.3' 'x11vnc>=1:0.9.16' 'ruby-bundler>=2.0.2')

package() {
		cd "$srcdir/EZconnect"
		sudo mkdir /usr/share/ezconnect
		sudo bundle install --quiet --gemfile lib/webserver/Gemfile
		sudo cp EZconnect.jar /usr/share/ezconnect
		sudo cp -r lib /usr/share/ezconnect
		sudo chmod 666 /usr/share/ezconnect/lib/webserver/settings.yml
		sudo chmod 666 /usr/share/ezconnect/lib/webserver/storage/user.yml
		chmod +x ezconnect
		sudo cp ezconnect /usr/bin
}