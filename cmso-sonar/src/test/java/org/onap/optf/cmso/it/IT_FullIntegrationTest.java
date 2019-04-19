/*
 * ============LICENSE_START============================================== Copyright (c) 2019 AT&T
 * Intellectual Property. =======================================================================
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License. ============LICENSE_END=================================================
 */

package org.onap.optf.cmso.it;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import org.apache.commons.io.IOUtils;
import org.junit.Test;

public class IT_FullIntegrationTest {

    private Properties env = new Properties();

    @Test
    public void runTest() throws IOException {
        InputStream is = new FileInputStream(new File("src/test/resources/integration.properties"));
        env.load(is);
        Process process = null;
        try {
            ProcessBuilder processBuilder = buildCommand();
            process = processBuilder.start();
            // debug.debug("engine command=" + commandString);
            String stdout = IOUtils.toString(process.getInputStream(), "UTF-8");
            String stderr = IOUtils.toString(process.getErrorStream(), "UTF-8");
            System.out.println("stdout=" + stdout);
            System.out.println("stderr=" + stderr);
            copyJacocoFiles();
            copyClassFiles();
            copyForSonar();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (process.isAlive()) {
                process.destroyForcibly();
            }
        }
    }

    private void copyForSonar() throws IOException {
        String[] jacocoFiles = env.getProperty("copy.jacoco.for.sonar").split(",");
        for (String jacocoFile : jacocoFiles) {
            String[] parts = jacocoFile.split("\\|");
            if (parts.length == 2) {
                File source = new File(parts[0]);
                File dest = new File(parts[1]);
                if (source.exists() && source.isFile() && dest.getParentFile().isDirectory()) {
                    Path srcFile = Paths.get(source.getAbsolutePath());
                    Path dstFile = Paths.get(dest.getAbsolutePath());
                    Files.copy(srcFile, dstFile, StandardCopyOption.REPLACE_EXISTING);
                } else {
                    System.out.println("Skipping " + jacocoFile);
                }
            } else {
                System.out.println("Skipping " + jacocoFile);
            }

        }
    }

    private void copyClassFiles() throws IOException {
        File dest = new File(env.getProperty("jacoco.exec.classes"));
        dest.mkdirs();
        if (dest.isDirectory()) {

            String[] sourceFolders = env.getProperty("source.classes.folders").split(",");
            for (String source : sourceFolders) {
                String[] parts = source.split("\\|");
                if (parts.length == 2) {
                    Path destPath = Paths.get(dest.getAbsolutePath(), parts[0]);
                    destPath.toFile().mkdirs();
                    File sourceFolder = new File(parts[1]);
                    if (sourceFolder.exists() && sourceFolder.isDirectory()) {
                        Path srcPath = Paths.get(sourceFolder.getAbsolutePath());
                        copyFolder(srcPath, destPath);
                    }
                }
            }
        }
    }

    private void copyJacocoFiles() throws IOException {
        File dest = new File(env.getProperty("jacoco.exec.dest"));
        dest.mkdirs();
        if (dest.isDirectory()) {

            String[] sourceFiles = env.getProperty("jacoco.exec.source.files").split(",");
            for (String source : sourceFiles) {
                File sourceFile = new File(source);
                if (sourceFile.exists()) {
                    Path destPath = Paths.get(dest.getAbsolutePath(), sourceFile.getName());
                    Path srcPath = Paths.get(sourceFile.getAbsolutePath());
                    Files.copy(srcPath, destPath, StandardCopyOption.REPLACE_EXISTING);
                }
            }
        }
    }

    private static void copyFolder(Path src, Path dest) {
        try {
            Files.walk(src).forEach(s -> {
                try {
                    Path d = dest.resolve(src.relativize(s));
                    if (Files.isDirectory(s)) {
                        if (!Files.exists(d))
                            Files.createDirectory(d);
                        return;
                    }
                    Files.copy(s, d);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private ProcessBuilder buildCommand() {
        ProcessBuilder processBuilder = new ProcessBuilder();
        List<String> command = new ArrayList<>();
        String basepath = env.getProperty("base.path", "./");
        File workdir = new File(env.getProperty("workdir", "./docker/integration"));
        command.add("/bin/bash");
        command.add("-x");
        command.add(basepath + "ete_test.sh");
        Map<String, String> environment = processBuilder.environment();
        processBuilder.directory(workdir);
        processBuilder.command(command);
        return processBuilder;
    }
}
