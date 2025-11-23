#!/bin/bash

set -euo pipefail

# Simple one-line description: build a Java app, package it with jpackage and create an AppImage

# Acquire an exclusive lock so two instances cannot run at the same time
LOCKFILE="/tmp/create_appimage.lock"
exec 200>"$LOCKFILE"
if ! flock -n 200; then
  echo "Another instance of create_appimage.sh is running. Exiting."
  exit 1
fi

# Utility: print and run
log() { echo "=== $*"; }

# Build root: put all generated files into a single directory
BUILD_DIR="build"
BACKUPS_DIR="$BUILD_DIR/backups"
mkdir -p "$BUILD_DIR" "$BACKUPS_DIR"

# Cleanup on exit
TMP_OUT=""
JPKG_OUT=""
cleanup() {
  local rc=$?
  # remove temporary directories if they were created
  if [[ -n "$TMP_OUT" && -d "$TMP_OUT" ]]; then
    rm -rf "$TMP_OUT"
  fi
  if [[ -n "$JPKG_OUT" && -d "$JPKG_OUT" ]]; then
    rm -rf "$JPKG_OUT"
  fi
  exit $rc
}
trap cleanup EXIT

# Check required commands
require_cmd() {
  if ! command -v "$1" &> /dev/null; then
    echo "Required command '$1' not found. Please install it and re-run the script." >&2
    exit 2
  fi
}

require_cmd javac
require_cmd jar
require_cmd jpackage
# wget is needed only if we download appimagetool automatically; prefer curl if available
if ! command -v wget &> /dev/null && ! command -v curl &> /dev/null; then
  echo "Neither 'wget' nor 'curl' found. The script may not be able to download appimagetool automatically." >&2
fi

# 1) Compile the project into a build subdirectory
log "1. Compiling the project into $BUILD_DIR"
TMP_OUT="$BUILD_DIR/compiled_$(date +%s)"
mkdir -p "$TMP_OUT"
javac -d "$TMP_OUT" src/Main.java src/Test.java src/View/*.java src/Control/*.java src/Model/*.java src/Model/Rules/*.java

# 2) Create the JAR
log "2. Creating the JAR"
jar --create --file "$TMP_OUT/GameOfLife.jar" --main-class Main -C "$TMP_OUT" .

# 3) Create the jpackage app-image into a build subdirectory to avoid clobbering existing folders
log "3. Running jpackage (app-image) into $BUILD_DIR"
JPKG_OUT="$BUILD_DIR/jpackage_$(date +%s)"
mkdir -p "$JPKG_OUT"
jpackage --input "$TMP_OUT" --name GameOfLife --main-jar GameOfLife.jar --type app-image --dest "$JPKG_OUT"

# At this point jpackage will create a directory named GameOfLife inside JPKG_OUT
JPKG_APP_DIR="$JPKG_OUT/GameOfLife"
if [[ ! -d "$JPKG_APP_DIR" ]]; then
  echo "jpackage did not produce expected output at '$JPKG_APP_DIR'" >&2
  exit 3
fi

# 4) Prepare the AppDir structure inside the build directory. If an AppDir already exists, move it to backups.
APP_DIR="$BUILD_DIR/GameOfLife.AppDir"
if [[ -d "$APP_DIR" ]]; then
  BACKUP="$BACKUPS_DIR/GameOfLife.AppDir_backup_$(date +%s)"
  log "Existing '$APP_DIR' found. Moving it to '$BACKUP' to avoid conflict."
  mv "$APP_DIR" "$BACKUP"
fi

log "4. Preparing AppDir structure at $APP_DIR"
mkdir -p "$APP_DIR/usr/bin"
mkdir -p "$APP_DIR/usr/lib"

# Copy the jpackage output (lib and bin) into the AppDir
if [[ -d "$JPKG_APP_DIR/lib" ]]; then
  cp -r "$JPKG_APP_DIR/lib" "$APP_DIR/usr/"
else
  echo "Expected lib directory not found in jpackage output." >&2
  exit 4
fi

if [[ -f "$JPKG_APP_DIR/bin/GameOfLife" ]]; then
  cp "$JPKG_APP_DIR/bin/GameOfLife" "$APP_DIR/usr/bin/"
else
  echo "Expected executable '$JPKG_APP_DIR/bin/GameOfLife' not found." >&2
  exit 5
fi

# 5) Create the .desktop file
log "5. Creating the .desktop file"
cat > "$APP_DIR/GameOfLife.desktop" << 'EOF'
[Desktop Entry]
Name=Game of Life
Exec=GameOfLife
Icon=icon
Type=Application
Categories=Game;
EOF

# 6) Create a basic icon if none exists. If the repository contains an icon.png, prefer it.
log "6. Creating/adding icon"
if [[ -f "GameOfLife.png" ]]; then
  cp GameOfLife.png "$APP_DIR/icon.png"
elif [[ -f "images/screen.png" ]]; then
  cp images/screen.png "$APP_DIR/icon.png"
else
  # small 1x1 PNG base64 placeholder
  cat > "$APP_DIR/icon.png" << 'EOF'
iVBORw0KGgoAAAANSUhEUgAAAAEAAAABCAYAAAAfFcSJAAAADUlEQVR42mNk+M9QDwADhgGAWjR9awAAAABJRU5ErkJggg==
EOF
fi

# 7) Create AppRun
log "7. Creating AppRun"
cat > "$APP_DIR/AppRun" << 'EOF'
#!/bin/bash
SELF=$(readlink -f "$0")
HERE=${SELF%/*}
export PATH="${HERE}/usr/bin:${PATH}"
export LD_LIBRARY_PATH="${HERE}/usr/lib:${LD_LIBRARY_PATH:-}"
exec "${HERE}/usr/bin/GameOfLife" "$@"
EOF
chmod +x "$APP_DIR/AppRun"

# 8) Check for appimagetool, download into build if necessary
log "8. Checking for appimagetool (will use $BUILD_DIR if downloaded)"
if ! command -v appimagetool &> /dev/null; then
  echo "appimagetool not found on PATH. Will try to use a local copy or download it into $BUILD_DIR."
  APPIMAGETOOL_PATH="$BUILD_DIR/appimagetool-x86_64.AppImage"
  if [[ ! -f "$APPIMAGETOOL_PATH" ]]; then
    echo "Downloading appimagetool-x86_64.AppImage into $BUILD_DIR..."
    if command -v wget &> /dev/null; then
      wget -q --show-progress -O "$APPIMAGETOOL_PATH" "https://github.com/AppImage/AppImageKit/releases/download/continuous/appimagetool-x86_64.AppImage"
    elif command -v curl &> /dev/null; then
      curl -L -o "$APPIMAGETOOL_PATH" "https://github.com/AppImage/AppImageKit/releases/download/continuous/appimagetool-x86_64.AppImage"
    else
      echo "Neither wget nor curl available to download appimagetool. Please install one or install appimagetool on your PATH." >&2
      exit 6
    fi
    chmod +x "$APPIMAGETOOL_PATH"
  fi
  APPIMAGETOOL="$APPIMAGETOOL_PATH"
else
  APPIMAGETOOL="appimagetool"
fi

# 9) Create the AppImage inside the build directory. If an AppImage with the same name exists, move it to backups instead of failing.
OUTPUT_APPIMAGE="$BUILD_DIR/GameOfLife-x86_64.AppImage"
if [[ -f "$OUTPUT_APPIMAGE" ]]; then
  BACKUP_IMG="$BACKUPS_DIR/GameOfLife-x86_64.AppImage_backup_$(date +%s)"
  log "Existing AppImage '$OUTPUT_APPIMAGE' found. Moving it to '$BACKUP_IMG'"
  mv "$OUTPUT_APPIMAGE" "$BACKUP_IMG"
fi

log "9. Creating the AppImage into $OUTPUT_APPIMAGE"
ARCH=x86_64 "$APPIMAGETOOL" "$APP_DIR" "$OUTPUT_APPIMAGE"

log "Done — AppImage created: $OUTPUT_APPIMAGE"

echo "To run: chmod +x $OUTPUT_APPIMAGE && ./$OUTPUT_APPIMAGE"

# exit normally (cleanup trap will run)
exit 0
