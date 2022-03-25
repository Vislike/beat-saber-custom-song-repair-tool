@echo off
cd /D "%~dp0"
start "repair-tool" "bin\javaw" -jar "repair-tool-${project.version}.jar" %*
