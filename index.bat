echo off
echo NUL>_.class&&del /s /f /q *.class
cls
javac com/krzem/jwt_auth/*.java&&java com/krzem/jwt_auth/Main
start /min cmd /c "echo NUL>_.class&&del /s /f /q *.class"