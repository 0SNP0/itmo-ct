import subprocess

subprocess.Popen(" ".join([
    "javadoc",
    "-d",
    "../javadoc",
    "-link",
    "https://docs.oracle.com/en/java/javase/17/docs/api",
    "-cp",
    "../../java-advanced-2022/lib/hamcrest-core-1.3.jar;"
    "../../java-advanced-2022/lib/jsoup-1.8.1.jar;"
    "../../java-advanced-2022/lib/junit-4.11.jar;"
    "../../java-advanced-2022/lib/quickcheck-0.6.jar",
    "../java-solutions/info/kgeorgiy/ja/selivanov/implementor/*.java",
    "../../java-advanced-2022/modules/info.kgeorgiy.java.advanced.implementor/info/kgeorgiy/java/advanced/implementor"
    "/Impler.java",
    "../../java-advanced-2022/modules/info.kgeorgiy.java.advanced.implementor/info/kgeorgiy/java/advanced/implementor"
    "/JarImpler.java",
    "../../java-advanced-2022/modules/info.kgeorgiy.java.advanced.implementor/info/kgeorgiy/java/advanced/implementor"
    "/ImplerException.java"
]), shell=True).wait()
