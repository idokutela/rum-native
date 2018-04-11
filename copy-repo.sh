#!/bin/bash
rsync -av --progress . "$1" --exclude=".*" --exclude=README.md --exclude="*.sh" 
