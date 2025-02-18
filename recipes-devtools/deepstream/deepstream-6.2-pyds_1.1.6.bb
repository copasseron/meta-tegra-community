DESCRIPTION = "Python bindings for Deepstream-6.2"
HOMEPAGE = "https://github.com/NVIDIA-AI-IOT/deepstream_python_apps"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://LICENSE;md5=7a01a47514ea2d404b8db41b6cfe6db0"

SRC_REPO = "github.com/NVIDIA-AI-IOT/deepstream_python_apps.git;protocol=https"
SRCBRANCH = "master"
SRC_URI = "git://${SRC_REPO};branch=${SRCBRANCH} \
           file://0001-Fixes-for-cross-building.patch \
           file://0002-Allow-apps-to-be-run-from-other-working-directories.patch \
           "
# v1.1.6 tag
SRCREV = "441b50da01779a2afacc60d40cd666d4bdde628e"

#PV .= "+git${SRCPV}"

COMPATIBLE_MACHINE = "(tegra)"

S = "${WORKDIR}/git"

DEPENDS = "deepstream-6.2 python3-pybind11 gstreamer1.0-python gstreamer1.0 glib-2.0"
DS_PATH = "/opt/nvidia/deepstream/deepstream-6.2"

inherit setuptools3 cmake pkgconfig ptest

OECMAKE_SOURCEPATH = "${S}/bindings"
SETUPTOOLS_SETUP_PATH = "${B}"
EXTRA_OECMAKE = "-DDS_PATH=${DS_PATH}"

do_configure() {
    cmake_do_configure
    cp ${S}/bindings/packaging/setup.py ${B}/    
}

do_compile() {
    cmake_do_compile
    setuptools3_do_compile
}

do_install() {
    setuptools3_do_install
    install -d ${D}${DS_PATH}/sources/deepstream_python_apps
    cp -Rf --preserve=mode,timestamps ${S}/apps ${D}${DS_PATH}/sources/deepstream_python_apps/
    find ${D}${DS_PATH}/sources/deepstream_python_apps/apps -type f -name '*config.txt' |
	xargs sed -i -r -e's,=(\.\./)+samples/,=${DS_PATH}/samples/,' \
	      -e's,^ll-config-file=[[:space:]]*(.*)$,ll-config-file=${DS_PATH}/samples/configs/deepstream-app/\1,' \
	      -e's,^ll-lib-file=[[:space:]]*/opt.*/(lib/.*),ll-lib-file=${DS_PATH}/\1,'
}

PACKAGES += "${PN}-samples"
RDEPENDS:${PN} = "python3-pygobject gstreamer1.0-python"
FILES:${PN}-samples = "${DS_PATH}/sources/deepstream_python_apps"
RDEPENDS:${PN}-samples = "${PN} deepstream-6.2-samples-data python3-opencv python3-numpy gobject-introspection"
PACKAGE_ARCH = "${TEGRA_PKGARCH}"
