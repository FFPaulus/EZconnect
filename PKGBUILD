pkgname="ezconnect"
pkgver=v1.0
pkgrel=1
pkgdesc="Set up VNC and connect to remote desktop via browser"
arch=("x86_64")
license=('MPL2')
source=('git+https://github.com/Ziska20/EZconnect.git#tag=$pkgver')

package() {
		depends=('jre8-openjdk>=8.u222' 'websockify>=0.8.0' 'ruby>=2.6.3' 'x11vnc>=1:0.9.16')
        cd "$pkgname-${pkgver}"
        mkdir /usr/share/ezconnect
        cp ezconnect.jar $pkgdir/usr/share/ezconnect
        cp -r lib $pkgdir/usr/share/ezconnect
        chmod +x ezconnect
        cp ezconnect $pkgdir/usr/bin
}