package org.gradle.wrapper;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.Arrays;
import java.util.Properties;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

/** Lightweight bootstrap launcher used when the upstream Gradle wrapper JAR is unavailable. */
public final class GradleWrapperMain {
    private static final int BUFFER_SIZE = 128 * 1024;

    private GradleWrapperMain() { }

    public static void main(String[] args) throws Exception {
        Path project = Path.of(System.getProperty("grom.wrapper.root", System.getProperty("user.dir"))).toAbsolutePath().normalize();
        Path propertiesFile = project.resolve("gradle/wrapper/gradle-wrapper.properties");
        if (!Files.exists(propertiesFile)) throw new IllegalStateException("Missing " + propertiesFile);
        Properties properties = new Properties();
        try (InputStream in = Files.newInputStream(propertiesFile)) { properties.load(in); }
        String distributionUrl = properties.getProperty("distributionUrl");
        if (distributionUrl == null || distributionUrl.isBlank()) throw new IllegalStateException("distributionUrl is missing in " + propertiesFile);

        String filename = distributionUrl.substring(distributionUrl.lastIndexOf('/') + 1);
        String distributionName = filename.endsWith(".zip") ? filename.substring(0, filename.length() - 4) : filename;
        String baseName = distributionName.replaceFirst("-(bin|all|src)$", "");
        String userGradleHome = System.getProperty("gradle.user.home", Path.of(System.getProperty("user.home"), ".gradle").toString());
        Path cache = Path.of(userGradleHome).resolve("wrapper/dists").resolve(distributionName).resolve("gromvisuals-bootstrap");
        Path distribution = cache.resolve(baseName);
        Path executable = distribution.resolve("bin").resolve(isWindows() ? "gradle.bat" : "gradle");

        if (!Files.isRegularFile(executable)) {
            Files.createDirectories(cache);
            Path zip = cache.resolve(filename);
            if (!Files.isRegularFile(zip) || Files.size(zip) == 0) download(distributionUrl, zip);
            extract(zip, cache);
            if (!Files.isRegularFile(executable)) throw new IOException("Gradle distribution extracted, but executable is missing: " + executable);
        }

        if (!isWindows()) executable.toFile().setExecutable(true, false);
        String[] command;
        if (isWindows()) {
            command = new String[args.length + 3];
            command[0] = "cmd.exe";
            command[1] = "/c";
            command[2] = "\"" + executable + "\"";
            System.arraycopy(args, 0, command, 3, args.length);
        } else {
            command = new String[args.length + 1];
            command[0] = executable.toString();
            System.arraycopy(args, 0, command, 1, args.length);
        }
        Process process = new ProcessBuilder(command).directory(project.toFile()).inheritIO().start();
        System.exit(process.waitFor());
    }

    private static void download(String url, Path target) throws IOException {
        System.out.println("[GromVisuals Gradle bootstrap] Downloading " + url);
        Path temporary = target.resolveSibling(target.getFileName() + ".part");
        HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();
        connection.setInstanceFollowRedirects(true);
        connection.setConnectTimeout(30_000);
        connection.setReadTimeout(60_000);
        connection.setRequestProperty("User-Agent", "GromVisuals-Gradle-Wrapper/1.0");
        try {
            int status = connection.getResponseCode();
            if (status < 200 || status >= 300) throw new IOException("Gradle download returned HTTP " + status);
            try (InputStream in = new BufferedInputStream(connection.getInputStream());
                 BufferedOutputStream out = new BufferedOutputStream(Files.newOutputStream(temporary))) {
                byte[] buffer = new byte[BUFFER_SIZE];
                int read;
                while ((read = in.read(buffer)) != -1) out.write(buffer, 0, read);
            }
            Files.move(temporary, target, StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException exception) {
            Files.deleteIfExists(temporary);
            throw new IOException("Could not download Gradle. Check internet access, then run ./gradlew again.", exception);
        } finally {
            connection.disconnect();
        }
    }

    private static void extract(Path archive, Path destination) throws IOException {
        Path normalizedRoot = destination.toAbsolutePath().normalize();
        System.out.println("[GromVisuals Gradle bootstrap] Extracting Gradle distribution...");
        try (ZipInputStream zip = new ZipInputStream(new BufferedInputStream(Files.newInputStream(archive)))) {
            ZipEntry entry;
            byte[] buffer = new byte[BUFFER_SIZE];
            while ((entry = zip.getNextEntry()) != null) {
                Path output = destination.resolve(entry.getName()).toAbsolutePath().normalize();
                if (!output.startsWith(normalizedRoot)) throw new IOException("Unsafe path in Gradle distribution: " + entry.getName());
                if (entry.isDirectory()) {
                    Files.createDirectories(output);
                } else {
                    Files.createDirectories(output.getParent());
                    try (BufferedOutputStream out = new BufferedOutputStream(Files.newOutputStream(output))) {
                        int read;
                        while ((read = zip.read(buffer)) != -1) out.write(buffer, 0, read);
                    }
                }
                zip.closeEntry();
            }
        }
    }

    private static boolean isWindows() {
        return System.getProperty("os.name", "").toLowerCase().contains("win");
    }
}
