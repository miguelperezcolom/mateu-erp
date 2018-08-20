package io.mateu.erp.model.cms;

import com.google.common.base.Charsets;
import com.google.common.base.Strings;
import com.google.common.io.Files;
import io.mateu.erp.model.config.AppConfig;
import io.mateu.erp.model.organization.Office;
import io.mateu.mdd.core.annotations.Action;
import io.mateu.mdd.core.annotations.SearchFilter;
import io.mateu.mdd.core.annotations.Tab;
import io.mateu.mdd.core.data.Data;
import io.mateu.mdd.core.util.Helper;
import io.mateu.mdd.core.util.JPATransaction;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.exec.CommandLine;
import org.apache.commons.exec.DefaultExecutor;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by miguel on 21/1/17.
 */
@Entity
@Getter@Setter
public class Website {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Tab("General")
    @SearchFilter
    @NotNull
    private String name;

    @NotNull
    private String title;

    @SearchFilter
    private boolean active;

    @ManyToOne
    private Office office;

    @SearchFilter
    private String urls;

    @OneToMany(mappedBy = "website")
    private List<GithubRepository> GitHubRepositories = new ArrayList<>();


    @Override
    public String toString() {
        return getName();
    }


    @Action("Publish")
    public void publish() throws Throwable {
        Helper.transact(new JPATransaction() {
            @Override
            public void run(EntityManager em) throws Throwable {
                String basePath = System.getProperty("cmsdir", "/home/cms/");
                if (!basePath.endsWith("/")) basePath += "/";
                for (String u : getUrls().split("[ ,;]")) {
                    createSite(em, new java.io.File(basePath + getId() + "/" + u), u);
                }
            }
        });
    }


    public Data toData() {
        return new Data();
    }


    public void resetDirectory(java.io.File where) throws Exception {
            DefaultExecutor executor = new DefaultExecutor();
            for (String line : new String[] {
                    "rm -rf " + where.getAbsolutePath()
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



    public void createSite(EntityManager em, java.io.File where, String urlBase) throws Throwable {

        System.out.println("createFiles(" + where.getAbsolutePath() + ", " + urlBase + ")");


        boolean emptyDirectoy = false;

        if (!where.exists()) {
            where.mkdirs();
            emptyDirectoy = true;
        }
        else if (!where.isDirectory()) throw new Exception("" + where.getAbsolutePath() + " is not a directory");

        {

            if (needsReset()) {
                resetDirectory(where);
                emptyDirectoy = true;
            }


            DefaultExecutor executor = new DefaultExecutor();

            List<String> lines = new ArrayList<>();

            lines.add("cd " + where.getAbsolutePath());

            if (emptyDirectoy) {
                addLinesBeforeGitInit(lines, where);


                lines.add("git init");

                for (GithubRepository r : getGitHubRepositories()) {
                    if (Strings.isNullOrEmpty(r.getSubModule())) lines.add("git remote add origin " + r.getUrl());
                    else lines.add("git submodule add " + r.getUrl() + " " + r.getSubModule());
                    lines.add("git pull origin " + ((Strings.isNullOrEmpty(r.getBranch()))?"master":r.getBranch()));
                }

                addLinesAfterGit(lines, where);
            } else {
                lines.add("git pull");
            }


            for (String line : lines) if (!Strings.isNullOrEmpty(line)) {
                System.out.println("executing " + line);
                if (line.startsWith("cd ")) executor.setWorkingDirectory(new java.io.File(line.substring("cd ".length())));
                else {
                    CommandLine cmdLine = CommandLine.parse(line);
                    int exitValue = executor.execute(cmdLine);

                    if (exitValue != 0) throw new Exception(line + " exited with code " + exitValue);
                }
            }
        }


        createFiles(em, where, urlBase);


        // crear fichero config nginx

        java.io.File nginxConfDir = new java.io.File(AppConfig.get(em).getNginxConfigDirectory());
        if (!nginxConfDir.exists()) nginxConfDir.mkdirs();
        else if (!nginxConfDir.isDirectory()) throw new Exception("" + nginxConfDir.getAbsolutePath() + " is not a directory");

        {
            StringBuffer s = new StringBuffer("");
            s.append("#\n" +
                    "# A virtual host created form the mateu.io cms\n" +
                    "#\n" +
                    "\n" +
                    "server {\n" +
                    "    #listen       80;\n" +
                    "    #listen       somename:8080;\n" +
                    "    #server_name  somename  alias  another.alias;\n" +
                    "    server_name " + urlBase + ";\n" +
                    "\n" +
                    "        location / {\n" +
                    "               root " + where.getAbsolutePath() + java.io.File.separator + "public" + ";\n" +
                    "        }\n" +
                    "\n" +
                    "}");

            Files.write(s.toString(), new java.io.File(nginxConfDir.getAbsolutePath() + java.io.File.separator + "cms_" + getId() + ".conf"), Charsets.UTF_8);
        }

        // reload nginx

        {
            String line = AppConfig.get(em).getNginxReloadCommand();
            System.out.println("executing " + line);
            CommandLine cmdLine = CommandLine.parse(line);
            DefaultExecutor executor = new DefaultExecutor();
            int exitValue = executor.execute(cmdLine);

            if (exitValue != 0) throw new Exception(line + " exited with code " + exitValue);
        }

    }

    public boolean needsReset() {
        return false;
    }

    public void createFiles(EntityManager em, java.io.File where, String urlBase) throws Throwable {
    }

    public void addLinesAfterGit(List<String> lines, java.io.File where) {
    }

    public void addLinesBeforeGitInit(List<String> lines, java.io.File where) {
    }

}
