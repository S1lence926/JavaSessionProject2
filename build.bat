@echo off
echo === Компиляция исходников ===
mkdir build\classes 2>nul
dir /s /b src\*.java > sources.txt
javac --release 17 -cp lib\sqlite-jdbc.jar -d build\classes @sources.txt

echo === Упаковка JAR ===
cd build\classes
jar xf ..\..\lib\sqlite-jdbc.jar
cd ..\..
echo Main-Class: library.Main > manifest.txt
jar cfm library-system.jar manifest.txt -C build\classes .

echo === Готово! Запуск: ===
echo   java -jar library-system.jar
