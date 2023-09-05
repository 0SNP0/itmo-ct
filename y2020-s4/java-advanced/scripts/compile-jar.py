import subprocess

subprocess.Popen(" ".join([
    "javac",
    "--module-path",
    "../../java-advanced-2022/artifacts;../../java-advanced-2022/lib",
    "../java-solutions/module-info.java",
    "../java-solutions/info/kgeorgiy/ja/selivanov/implementor/*.java",
    "-d",
    "../out"
]), shell=True).wait()

subprocess.Popen(" ".join([
    "jar",
    "--module-path",
    "../../../java-advanced-2022/artifacts;../../java-advanced-2022/lib",
    "-c",
    "-v",
    "--file",
    "implementor.jar",
    "--manifest",
    "../java-solutions/info/kgeorgiy/ja/selivanov/implementor/META-INF/MANIFEST.MF",
    "-C",
    "../out",
    "."
]), shell=True).wait()
