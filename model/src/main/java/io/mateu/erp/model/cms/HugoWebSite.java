package io.mateu.erp.model.cms;

import com.google.common.base.Charsets;
import com.google.common.io.Files;
import io.mateu.mdd.core.annotations.Tab;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.exec.CommandLine;
import org.apache.commons.exec.DefaultExecutor;

import javax.persistence.Entity;
import javax.persistence.EntityManager;
import javax.persistence.ManyToOne;
import javax.validation.constraints.NotNull;
import java.io.File;
import java.util.List;

@Entity
@Getter
@Setter
public class HugoWebSite extends Website {


    @Tab("Theming")
    @NotNull
    @ManyToOne
    private Theme theme;


    @Override
    public void addLinesBeforeGitInit(List<String> lines, File where) {
        lines.add("hugo new site " + where.getAbsolutePath());
    }

    @Override
    public void addLinesAfterGit(List<String> lines, java.io.File where) {
        lines.add("git submodule add " + getTheme().getGitHubRepositoryUrl() + " themes/" + getTheme().getName());
    }

    public void createFiles(EntityManager em, java.io.File where, String urlBase) throws Throwable {


        java.io.File contentDir = new java.io.File(where.getAbsolutePath() + java.io.File.separator + "content");
        if (!contentDir.exists()) {
            contentDir.mkdirs();
        }
        else if (!contentDir.isDirectory()) throw new Exception("" + contentDir.getAbsolutePath() + " is not a directory");



        java.io.File dataDir = new java.io.File(where.getAbsolutePath() + java.io.File.separator + "data");
        if (!dataDir.exists()) {
            dataDir.mkdirs();
        }
        else if (!dataDir.isDirectory()) throw new Exception("" + dataDir.getAbsolutePath() + " is not a directory");

        // crear fichero configuración

        {
            StringBuffer s = new StringBuffer("");
            s.append("baseURL = \"http://" + urlBase + "/\"\n" +
                    "languageCode = \"en-us\"\n" +
                    "title = \"" + getTitle() + "\"\n" +
                    "theme = \"" + getTheme().getName() + "\"");

            java.io.File c = new java.io.File(where.getAbsolutePath() + java.io.File.separator + "config.toml");
            if (true || !c.exists()) Files.write(s.toString(), c, Charsets.UTF_8);
        }

        // crear directorio content y ficheros

        createContentFiles(em, contentDir, dataDir);

        {
            DefaultExecutor executor = new DefaultExecutor();
            for (String line : new String[] {
                    "cd " + where.getAbsolutePath()
                    , "hugo"
            }) {
                System.out.println("executing " + line);
                if (line.startsWith("cd ")) executor.setWorkingDirectory(new java.io.File(line.substring("cd ".length())));
                else {
                    CommandLine cmdLine = CommandLine.parse(line);
                    int exitValue = executor.execute(cmdLine);

                    if (exitValue != 0) throw new Exception(line + " exited with code " + exitValue);
                }
            }
        }


    }

    public void createContentFiles(EntityManager em, java.io.File contentDir, java.io.File dataDir) throws Throwable {

        // crear indice

        StringBuffer s = new StringBuffer("");
        s.append("---\n" +
                "title: \"My First Post\"\n" +
                "date: 2017-11-10T13:52:59+01:00\n" +
                "draft: false\n" +
                "---");

        java.io.File f = new java.io.File(contentDir.getAbsolutePath() + java.io.File.separator + "index.md");

        if (!f.exists()) Files.write(s.toString(), f, Charsets.UTF_8);


    }

}
