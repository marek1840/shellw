#!/bin/bash

dir=$(dirname "$(readlink -f "$0")")
source="$dir/build/libs/shellw.jar"
target="${HOME}/libs/shellw.jar"

build()(
  cd "$dir" && ./gradlew jar
)

copy_nar(){
  mkdir -p "${HOME}/libs" && cp "$source" "$target" && echo "updated shellw"
}

build || exit 1 && [ "$source" -nt "$target" ] && copy_nar

for binary in "$dir/src/main/shell/"* ; do
    target_bin="${HOME}/bin/$(basename $binary)"
    cp "$binary" "$target_bin" && chmod +x "$target_bin"
done
