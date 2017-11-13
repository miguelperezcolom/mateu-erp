package io.mateu.erp.model.cms;

import com.google.common.base.Strings;
import com.google.common.io.Files;
import io.mateu.erp.model.common.File;
import io.mateu.erp.model.config.AppConfig;
import io.mateu.erp.model.tpv.TPV;
import io.mateu.erp.model.util.Helper;
import io.mateu.erp.model.util.JPATransaction;
import io.mateu.ui.core.shared.Data;
import io.mateu.ui.mdd.server.annotations.Action;
import io.mateu.ui.mdd.server.annotations.Ignored;
import io.mateu.ui.mdd.server.annotations.SearchFilter;
import io.mateu.ui.mdd.server.annotations.Tab;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.exec.CommandLine;
import org.apache.commons.exec.DefaultExecutor;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

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

    @SearchFilter
    private String urls;


    private String gitHubRepositoryUrl;

    private String gitHubRepositoryBranch = "master";


    private String gitHubAPIToken;

    @NotNull
    @ManyToOne
    private Theme theme;


    @ManyToOne
    private TPV tpv;




    @Override
    public String toString() {
        return getName();
    }


    @Action(name = "Publish")
    public void publish() throws Throwable {
        Helper.transact(new JPATransaction() {
            @Override
            public void run(EntityManager em) throws Throwable {
                for (String u : getUrls().split("[ ,;]")) {
                    createFiles(em, new java.io.File(System.getProperty("cmsdir", "/home/cms/") + getId() + "/" + u), u);
                }
            }
        });
    }


    public Data toData() {
        return new Data();
    }

    public void createFiles(EntityManager em, java.io.File where, String urlBase) throws Throwable {

        System.out.println("createFiles(" + where.getAbsolutePath() + ", " + urlBase + ")");

        if (!where.exists()) where.mkdirs();
        else if (!where.isDirectory()) throw new Exception("" + where.getAbsolutePath() + " is not a directory");

        java.io.File contentDir = new java.io.File(where.getAbsolutePath() + java.io.File.separator + "content");
        if (true || !contentDir.exists()) {

            DefaultExecutor executor = new DefaultExecutor();
            for (String line : new String[] {
                    "rm -rf " + where.getAbsolutePath() + "/*"
                    , "cd " + where.getAbsolutePath()
                    , (Strings.isNullOrEmpty(getGitHubRepositoryUrl()))?"hugo new site " + where.getAbsolutePath():""
                    , "git init"
                    , (!Strings.isNullOrEmpty(getGitHubRepositoryUrl()))?"git remote add origin " + getGitHubRepositoryUrl():""
                    , (!Strings.isNullOrEmpty(getGitHubRepositoryUrl()))?"git fetch origin " + ((Strings.isNullOrEmpty(getGitHubRepositoryBranch()))?"master":getGitHubRepositoryBranch()):""
                    , (!Strings.isNullOrEmpty(getGitHubRepositoryUrl()))?"git reset --hard origin/master":""
                    , "cd " + where.getAbsolutePath()
                    //, "git init " + where.getAbsolutePath()
                    , "cd " + where.getAbsolutePath()
                    , "git submodule add " + getTheme().getGitHubRepositoryUrl() + " themes/" + getTheme().getName()
                    //, "git submodule add https://github.com/budparr/gohugo-theme-ananke.git themes/ananke"
            }) if (!Strings.isNullOrEmpty(line)) {
                System.out.println("executing " + line);
                if (line.startsWith("cd ")) executor.setWorkingDirectory(new java.io.File(line.substring("cd ".length())));
                else {
                    CommandLine cmdLine = CommandLine.parse(line);
                    int exitValue = executor.execute(cmdLine);

                    if (exitValue != 0) throw new Exception(line + " exited with code " + exitValue);
                }
            }


            //contentDir.mkdirs();

        }
        else if (!contentDir.isDirectory()) throw new Exception("" + contentDir.getAbsolutePath() + " is not a directory");

        // crear fichero configuración

        {
            StringBuffer s = new StringBuffer("");
            s.append("baseURL = \"http://" + urlBase + "/\"\n" +
                    "languageCode = \"en-us\"\n" +
                    "title = \"" + getTitle() + "\"\n" +
                    "theme = \"" + getTheme().getName() + "\"");

            Files.write(s.toString().getBytes(), new java.io.File(where.getAbsolutePath() + java.io.File.separator + "config.toml"));
        }

        // crear directorio content y ficheros

        createContentFiles(em, contentDir);

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

            Files.write(s.toString().getBytes(), new java.io.File(nginxConfDir.getAbsolutePath() + java.io.File.separator + "cms_" + getId() + ".conf"));
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

    public void createContentFiles(EntityManager em, java.io.File contentDir) throws Throwable {

        // crear indice

        StringBuffer s = new StringBuffer("");
        s.append("---\n" +
                "title: \"My First Post\"\n" +
                "date: 2017-11-10T13:52:59+01:00\n" +
                "draft: false\n" +
                "---");

        Files.write(s.toString().getBytes(), new java.io.File(contentDir.getAbsolutePath() + java.io.File.separator + "index.md"));


    }
}
