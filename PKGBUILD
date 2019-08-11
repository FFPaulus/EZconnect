pkgname="ezconnect"
pkgver=v1.0
pkgrel=1
pkgdesc="Set up VNC and connect to remote desktop via browser"
arch=("x86_64")
license=('MPL2')
source=('git+https://github.com/Ziska20/EZconnect.git#tag=50352f058ea4e95ddac1df80b5e7b783620d4d3d')
md5sums=('SKIP')

package() {
		cd $srcdir/EZconnect
		depends=('jre8-openjdk>=8.u222' 'websockify>=0.8.0' 'ruby>=2.6.3' 'x11vnc>=1:0.9.16')
		echo $srcdir
		echo $pkgdir
		sudo mkdir /usr/share/ezconnect
		sudo cp EZconnect.jar /usr/share/ezconnect
		sudo cp -r lib /usr/share/ezconnect
		chmod +x ezconnect
		sudo cp ezconnect /usr/bin
}