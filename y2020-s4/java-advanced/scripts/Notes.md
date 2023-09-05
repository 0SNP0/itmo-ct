Сгенерированный не мной, к слову, тоже не запускается

PS D:\university\java2022\java-advanced-private\test\__current-repo\scripts> .\compile-jar.bat

D:\university\java2022\java-advanced-private\test\__current-repo\scripts>javac --module-path ..\..\java-advanced-2022\artifacts;..\..\java-advanced-2022\lib ..\java-solutions\module-info.java ..\java-solutions\info\kgeorgiy\ja\seliv
anov\implementor\*.java -d ..\out

D:\university\java2022\java-advanced-private\test\__current-repo\scripts>jar --module-path ..\..\..\java-advanced-2022\artifacts;..\..\java-advanced-2022\lib -c -v --file implementor.jar --manifest ..\java-solutions\info\kgeorgiy\ja
\selivanov\implementor\META-INF\MANIFEST.MF -C ../out .
added manifest
added module-info: module-info.class
adding: info/(in = 0) (out= 0)(stored 0%)
adding: info/kgeorgiy/(in = 0) (out= 0)(stored 0%)
adding: info/kgeorgiy/ja/(in = 0) (out= 0)(stored 0%)
adding: info/kgeorgiy/ja/selivanov/(in = 0) (out= 0)(stored 0%)
adding: info/kgeorgiy/ja/selivanov/implementor/(in = 0) (out= 0)(stored 0%)
adding: info/kgeorgiy/ja/selivanov/implementor/CodeGen$MethodWrapper.class(in = 1961) (out= 968)(deflated 50%)
adding: info/kgeorgiy/ja/selivanov/implementor/CodeGen.class(in = 8483) (out= 3735)(deflated 55%)
adding: info/kgeorgiy/ja/selivanov/implementor/Implementor$Deleter.class(in = 1315) (out= 560)(deflated 57%)
adding: info/kgeorgiy/ja/selivanov/implementor/Implementor.class(in = 9072) (out= 4182)(deflated 53%)
PS D:\university\java2022\java-advanced-private\test\__current-repo\scripts> java -jar .\implementor.jar
Error: Could not find or load main class info.kgeorgiy.ja.selivanov.implementor.Implementor
Caused by: java.lang.NoClassDefFoundError: info/kgeorgiy/java/advanced/implementor/JarImpler
PS D:\university\java2022\java-advanced-private\test\__current-repo\scripts> ls


    Directory: D:\university\java2022\java-advanced-private\test\__current-repo\scripts


Mode                 LastWriteTime         Length Name
----                 -------------         ------ ----
-a----          4/1/2022   7:31 PM            410 compile-jar.bat
-a----          4/1/2022   7:31 PM            687 compile-jar.py
-a----          4/1/2022   6:48 PM            721 gen-javadoc.bat
-a----          4/1/2022   6:48 PM            931 gen-javadoc.py
-a----          4/8/2022   7:13 PM          11649 implementor.jar
-a----          4/8/2022   7:12 PM           2235 Notes.md
